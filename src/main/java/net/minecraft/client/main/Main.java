package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("fullscreen");
        optionParser.accepts("checkGlErrors");
        OptionSpec<String> serverOption = optionParser.accepts("server").withRequiredArg();
        OptionSpec<Integer> portOption = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> gameDirOption = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> assetsDirOption = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> resourcePackDirOption = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        OptionSpec<String> proxyHostOption = optionParser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> proxyPortOption = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        OptionSpec<String> proxyUserOption = optionParser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> proxyPassOption = optionParser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> usernameOption = optionParser.accepts("username").withRequiredArg().defaultsTo("mrflashstudio");
        OptionSpec<String> uuidOption = optionParser.accepts("uuid").withRequiredArg();
        OptionSpec<String> accessTokenOption = optionParser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> versionOption = optionParser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> widthOption = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> heightOption = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<String> userPropertiesOption = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> profilePropertiesOption = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> assetIndexOption = optionParser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> userTypeOption = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        OptionSpec<String> remainingOptions = optionParser.nonOptions();
        OptionSet optionSet = optionParser.parse(args);
        List<String> remainingArgs = optionSet.valuesOf(remainingOptions);

        if (!remainingArgs.isEmpty()) {
            System.out.println("Completely ignored arguments: " + remainingArgs);
        }

        String proxyHost = optionSet.valueOf(proxyHostOption);
        Proxy proxy = Proxy.NO_PROXY;

        if (proxyHost != null) {
            try {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(proxyHost, optionSet.valueOf(proxyPortOption)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        final String proxyUser = optionSet.valueOf(proxyUserOption);
        final String proxyPass = optionSet.valueOf(proxyPassOption);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(proxyUser) && isNullOrEmpty(proxyPass)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
                }
            });
        }

        int width = optionSet.valueOf(widthOption);
        int height = optionSet.valueOf(heightOption);
        boolean fullscreen = optionSet.has("fullscreen");
        boolean checkGlErrors = optionSet.has("checkGlErrors");
        boolean demoMode = optionSet.has("demo");
        String version = optionSet.valueOf(versionOption);
        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        PropertyMap userProperties = gson.fromJson(optionSet.valueOf(userPropertiesOption), PropertyMap.class);
        PropertyMap profileProperties = gson.fromJson(optionSet.valueOf(profilePropertiesOption), PropertyMap.class);
        File gameDir = optionSet.valueOf(gameDirOption);
        File assetsDir = optionSet.has(assetsDirOption) ? optionSet.valueOf(assetsDirOption) : new File(gameDir, "assets/");
        File resourcePackDir = optionSet.has(resourcePackDirOption) ? optionSet.valueOf(resourcePackDirOption) : new File(gameDir, "resourcepacks/");
        String uuid = optionSet.has(uuidOption) ? optionSet.valueOf(uuidOption) : optionSet.valueOf(usernameOption);
        String assetIndex = optionSet.has(assetIndexOption) ? optionSet.valueOf(assetIndexOption) : null;
        String server = optionSet.valueOf(serverOption);
        Integer port = optionSet.valueOf(portOption);
        Session session = new Session(optionSet.valueOf(usernameOption), uuid, optionSet.valueOf(accessTokenOption), optionSet.valueOf(userTypeOption));
        GameConfiguration gameConfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, userProperties, profileProperties, proxy), new GameConfiguration.DisplayInformation(width, height, fullscreen, checkGlErrors), new GameConfiguration.FolderInformation(gameDir, resourcePackDir, assetsDir, assetIndex), new GameConfiguration.GameInformation(demoMode, version), new GameConfiguration.ServerInformation(server, port));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        (new Minecraft(gameConfiguration)).run();
    }

    private static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}