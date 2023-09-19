package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanServerDetector {

    private static final AtomicInteger field_148551_a = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();

    public static class LanServer {
        private final String lanServerMotd;
        private final String lanServerIpPort;
        private long timeLastSeen;

        public LanServer(String motd, String address) {
            lanServerMotd = motd;
            lanServerIpPort = address;
            timeLastSeen = Minecraft.getSystemTime();
        }

        public String getServerMotd() {
            return lanServerMotd;
        }

        public String getServerIpPort() {
            return lanServerIpPort;
        }

        public void updateLastSeen() {
            timeLastSeen = Minecraft.getSystemTime();
        }
    }

    public static class LanServerList {
        private final List<LanServer> listOfLanServers = Lists.newArrayList();
        private boolean wasUpdated;

        public synchronized boolean getWasUpdated() {
            return wasUpdated;
        }

        public synchronized void setWasNotUpdated() {
            wasUpdated = false;
        }

        public synchronized List<LanServer> getLanServers() {
            return Collections.unmodifiableList(listOfLanServers);
        }

        public synchronized void func_77551_a(String pingResponse, InetAddress address) {
            String motd = ThreadLanServerPing.getMotdFromPingResponse(pingResponse);
            String ipPort = ThreadLanServerPing.getAdFromPingResponse(pingResponse);

            if (ipPort != null) {
                ipPort = address.getHostAddress() + ":" + ipPort;
                boolean found = false;

                for (LanServer lanServer : listOfLanServers) {
                    if (lanServer.getServerIpPort().equals(ipPort)) {
                        lanServer.updateLastSeen();
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    listOfLanServers.add(new LanServer(motd, ipPort));
                    wasUpdated = true;
                }
            }
        }
    }

    public static class ThreadLanServerFind extends Thread {
        private final LanServerList localServerList;
        private final InetAddress broadcastAddress;
        private final MulticastSocket socket;

        public ThreadLanServerFind(LanServerList serverList) throws IOException {
            super("LanServerDetector #" + field_148551_a.incrementAndGet());
            localServerList = serverList;
            setDaemon(true);
            socket = new MulticastSocket(4445);
            broadcastAddress = InetAddress.getByName("224.0.2.60");
            socket.setSoTimeout(5000);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

            socket.joinGroup(new InetSocketAddress(broadcastAddress, 4445), networkInterface);
        }

        public void run() {
            byte[] buffer = new byte[1024];

            while (!isInterrupted()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException ignored) {
                    continue;
                } catch (IOException ioException) {
                    logger.error("Couldn't ping server", ioException);
                    break;
                }

                String pingResponse = new String(packet.getData(), packet.getOffset(), packet.getLength());
                logger.debug(packet.getAddress() + ": " + pingResponse);
                localServerList.func_77551_a(pingResponse, packet.getAddress());
            }

            try {
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
                socket.leaveGroup(new InetSocketAddress(broadcastAddress, 4445), networkInterface);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            socket.close();
        }
    }
}
