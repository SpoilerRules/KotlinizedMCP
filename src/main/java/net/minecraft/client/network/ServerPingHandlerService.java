package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;

import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ServerPingHandlerService {
    private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
    private static final Logger logger = LogManager.getLogger();
    private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

    public void ping(final ServerData server) throws UnknownHostException {
        ServerAddress serverAddress = ServerAddress.fromString(server.serverIP);
        final NetworkManager networkManager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serverAddress.getIP()), serverAddress.getPort(), false);
        this.pingDestinations.add(networkManager);
        server.serverMOTD = "Pinging...";
        server.pingToServer = -1L;
        server.playerList = null;
        networkManager.setNetHandler(new INetHandlerStatusClient() {
            private boolean receivedStatus = false;
            private boolean unexpectedStatus = false;
            private long pingTimestamp = 0L;

            public void handleServerInfo(S00PacketServerInfo packetIn) {
                if (unexpectedStatus) {
                    networkManager.closeChannel(new ChatComponentText("Received unrequested status"));
                } else {
                    unexpectedStatus = true;
                    ServerStatusResponse serverStatusResponse = packetIn.getResponse();

                    if (serverStatusResponse.getServerDescription() != null) {
                        server.serverMOTD = serverStatusResponse.getServerDescription().getFormattedText();
                    } else {
                        server.serverMOTD = "";
                    }

                    if (serverStatusResponse.getProtocolVersionInfo() != null) {
                        server.gameVersion = serverStatusResponse.getProtocolVersionInfo().getName();
                        server.version = serverStatusResponse.getProtocolVersionInfo().getProtocol();
                    } else {
                        server.gameVersion = "Old";
                        server.version = 0;
                    }

                    if (serverStatusResponse.getPlayerCountData() != null) {
                        server.populationInfo = EnumChatFormatting.GRAY + String.valueOf(serverStatusResponse.getPlayerCountData().getOnlinePlayerCount()) + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + serverStatusResponse.getPlayerCountData().getMaxPlayers();

                        if (ArrayUtils.isNotEmpty(serverStatusResponse.getPlayerCountData().getPlayers())) {
                            StringBuilder stringBuilder = new StringBuilder();

                            for (GameProfile gameProfile : serverStatusResponse.getPlayerCountData().getPlayers()) {
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append("\n");
                                }

                                stringBuilder.append(gameProfile.getName());
                            }

                            if (serverStatusResponse.getPlayerCountData().getPlayers().length < serverStatusResponse.getPlayerCountData().getOnlinePlayerCount()) {
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append("\n");
                                }

                                stringBuilder.append("... and ").append(serverStatusResponse.getPlayerCountData().getOnlinePlayerCount() - serverStatusResponse.getPlayerCountData().getPlayers().length).append(" more ...");
                            }

                            server.playerList = stringBuilder.toString();
                        }
                    } else {
                        server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                    }

                    if (serverStatusResponse.getFavicon() != null) {
                        String s = serverStatusResponse.getFavicon();

                        if (s.startsWith("data:image/png;base64,")) {
                            server.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
                        } else {
                            ServerPingHandlerService.logger.error("Invalid server icon (unknown translate)");
                        }
                    } else {
                        server.setBase64EncodedIconData(null);
                    }

                    pingTimestamp = Minecraft.getSystemTime();
                    networkManager.sendPacket(new C01PacketPing(pingTimestamp));
                    receivedStatus = true;
                }
            }

            public void handlePong(S01PacketPong packetIn) {
                long startTime = pingTimestamp;
                long endTime = Minecraft.getSystemTime();
                server.pingToServer = endTime - startTime;
                networkManager.closeChannel(new ChatComponentText("Finished"));
            }

            public void onDisconnect(IChatComponent reason) {
                if (!receivedStatus) {
                    ServerPingHandlerService.logger.error("Can't ping " + server.serverIP + ": " + reason.getUnformattedText());
                    server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                    server.populationInfo = "";
                    ServerPingHandlerService.this.tryCompatibilityPing(server);
                }
            }
        });

        try {
            networkManager.sendPacket(new C00Handshake(47, serverAddress.getIP(), serverAddress.getPort(), EnumConnectionState.STATUS));
            networkManager.sendPacket(new C00PacketServerQuery());
        } catch (Throwable throwable) {
            logger.error(throwable);
        }
    }

    private void tryCompatibilityPing(final ServerData server) {
        final ServerAddress serverAddress = ServerAddress.fromString(server.serverIP);
        (new Bootstrap()).group(NetworkManager.CLIENT_NIO_EVENTLOOP.getValue()).handler(new ChannelInitializer<>() {
            protected void initChannel(@NotNull Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
                } catch (ChannelException ignored) {
                }

                channel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    public void channelActive(@NotNull ChannelHandlerContext context) throws Exception {
                        super.channelActive(context);
                        ByteBuf byteBuf = Unpooled.buffer();

                        try {
                            byteBuf.writeByte(254);
                            byteBuf.writeByte(1);
                            byteBuf.writeByte(250);
                            char[] characters = "MC|PingHost".toCharArray();
                            byteBuf.writeShort(characters.length);

                            for (char character : characters) {
                                byteBuf.writeChar(character);
                            }

                            byteBuf.writeShort(7 + 2 * serverAddress.getIP().length());
                            byteBuf.writeByte(127);
                            characters = serverAddress.getIP().toCharArray();
                            byteBuf.writeShort(characters.length);

                            for (char character : characters) {
                                byteBuf.writeChar(character);
                            }

                            byteBuf.writeInt(serverAddress.getPort());
                            context.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        } finally {
                            byteBuf.release();
                        }
                    }

                    protected void channelRead0(ChannelHandlerContext context, ByteBuf byteBuf) {
                        short value = byteBuf.readUnsignedByte();

                        if (value == 255) {
                            String response = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), Charsets.UTF_16BE);
                            String[] array = Iterables.toArray(ServerPingHandlerService.PING_RESPONSE_SPLITTER.split(response), String.class);

                            if ("§1".equals(array[0])) {
                                MathHelper.parseIntWithDefault(array[1], 0);
                                String version = array[2];
                                String serverMOTD = array[3];
                                int playerCount = MathHelper.parseIntWithDefault(array[4], -1);
                                int maxPlayers = MathHelper.parseIntWithDefault(array[5], -1);
                                server.version = -1;
                                server.gameVersion = version;
                                server.serverMOTD = serverMOTD;
                                server.populationInfo = EnumChatFormatting.GRAY + String.valueOf(playerCount) + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + maxPlayers;
                            }
                        }

                        context.close();
                    }

                    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) {
                        context.close();
                    }
                });
            }
        }).channel(NioSocketChannel.class).connect(serverAddress.getIP(), serverAddress.getPort());
    }

    public void pingPendingNetworks() {
        synchronized (pingDestinations) {
            Iterator<NetworkManager> iterator = pingDestinations.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkManager = iterator.next();

                if (networkManager.isChannelOpen()) {
                    networkManager.processReceivedPackets();
                } else {
                    iterator.remove();
                    networkManager.checkDisconnected();
                }
            }
        }
    }

    public void clearPendingNetworks() {
        synchronized (pingDestinations) {
            Iterator<NetworkManager> iterator = pingDestinations.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkManager = iterator.next();

                if (networkManager.isChannelOpen()) {
                    iterator.remove();
                    networkManager.closeChannel(new ChatComponentText("Cancelled"));
                }
            }
        }
    }
}
