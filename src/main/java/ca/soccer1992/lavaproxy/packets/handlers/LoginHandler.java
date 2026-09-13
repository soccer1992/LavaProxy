package ca.soccer1992.lavaproxy.packets.handlers;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.cipher.EncryptionDecoder;
import ca.soccer1992.lavaproxy.cipher.EncryptionEncoder;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.login.EncryptionRequest;
import ca.soccer1992.lavaproxy.packets.client.login.LoginSuccess;
import ca.soccer1992.lavaproxy.packets.readers.ConfigReader;
import ca.soccer1992.lavaproxy.packets.readers.PlayReader;
import ca.soccer1992.lavaproxy.packets.server.EncryptionResponse;
import ca.soccer1992.lavaproxy.packets.server.LoginAck;
import ca.soccer1992.lavaproxy.packets.server.LoginStart;
import ca.soccer1992.lavaproxy.packets.server.PluginResponse;
import ca.soccer1992.lavaproxy.types.GameProfile;
import ca.soccer1992.lavaproxy.types.GameProperty;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static ca.soccer1992.lavaproxy.utils.EncryptionUtils.RSADecrypt;
import static ca.soccer1992.lavaproxy.utils.EncryptionUtils.twosComplementHexdigest;


public class LoginHandler extends Handler{
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static CompletableFuture<GameProfile> checkHasJoined(String username, String serverIdHash) {
        String url = String.format(
                "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s",
                URLEncoder.encode(username, StandardCharsets.UTF_8),
                URLEncoder.encode(serverIdHash, StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200 || response.body().isEmpty()) {
                        return null; // auth failed
                    }
                    JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                    UUID uuid = UUID.fromString(
                            json.get("id").getAsString().replaceFirst(
                                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                    "$1-$2-$3-$4-$5"
                            )
                    );
                    String name = json.get("name").getAsString();
                    ArrayList<GameProperty> propList = new ArrayList<>();
                    for (JsonElement prop : json.getAsJsonArray("properties")) {
                        if (prop.isJsonObject()) {
                            JsonObject obj = prop.getAsJsonObject();
                            String signature = obj.has("signature") ? obj.get("signature").getAsString() : null;
                            propList.add(new GameProperty(
                                    obj.get("name").getAsString(),
                                    obj.get("value").getAsString(),
                                    signature
                            ));
                        }
                    }
                    return new GameProfile(uuid, name, propList);
                });
    }
    public void continueSuccess(Connection c){
        if (Main.players.isEmpty()){
            Main.session_id = UUID.randomUUID();
        }
        LoginSuccess success = new LoginSuccess();
        success.setName(c.plr.name);
        success.setUUID(c.plr.uuid);
        success.setSessionUUID(Main.session_id);
        //if (c.protocol.getProtocol()<MinecraftVersions.MINECRAFT_1_20_2.getProtocol()){
        //    //c.disconnect(ComponentUtils.parser.deserialize("<rainbow>Testing (<1.20.2 LOGIN KICK)</rainbow>"),false);
        //    //c.connect(c.tryIter.next());
        //    c.disconnect("<1.20.2 is currently not supported, sorry!", false);
        //    return true;
        //}
        c.writePacket(success);
        if (c.protocol.getProtocol()< MinecraftVersions.MINECRAFT_1_20_2.getProtocol()){
            // instantly change it xd
            c.conType = ConnectionTypes.PLAY;
            c.setReader(new PlayReader());
            c.setHandler(new PlayHandler());
            c.connect(c.tryIter.next());

            return;

        } else {
            c.conType = ConnectionTypes.POST_SUCCESS;
        }
    }
    public boolean handle(Packet p, Connection c){
        if (p instanceof LoginStart packet){
            if (!packet.playerName.matches("[a-zA-Z0-9\\p{Punct}]+")){
                c.noLogDisconnect("\"Invalid player name\"");
                return true;
            }
            c.plr.setName(packet.playerName);
            c.plr.setUUID(packet.uuid);
            c.sendCompression(Main.compressionThreshold);

            System.out.println(c.fillPlaceholders("log.connect", "", ""));
            if (Main.onlineMode){
                EncryptionRequest encryptionRequest = new EncryptionRequest();
                encryptionRequest.setAuthenticate(true);
                encryptionRequest.setServerID("");
                byte[] token = new byte[4];
                new SecureRandom().nextBytes(token);
                c.verifyToken = token;
                encryptionRequest.setPublicKey(Main.encryptionKey.getPublic().getEncoded());
                encryptionRequest.setVerifyToken(c.verifyToken);
                c.writePacket(encryptionRequest);
                return true;
            }
            continueSuccess(c);
            //System.out.printf("Player %s (%s) has started login%n",c.plr, c.addr.getHostString());


            //c.disconnect(ComponentUtils.parser.deserialize("<rainbow>Testing</rainbow>"),false);
            return true;

        } else if (p instanceof LoginAck){
            if (c.conType != ConnectionTypes.POST_SUCCESS){
                c.disconnect("LoginAck sent before LoginSuccess", true);
                return true;
            }
            //System.out.println("switched to config");
            if (c.protocol.isGreaterEquals(MinecraftVersions.MINECRAFT_1_20_2)) {
                c.setReader(new ConfigReader());
                c.conType = ConnectionTypes.CONFIG;
                c.setHandler(new ConfigHandler());
            } else {
                c.disconnect("LoginAck somehow sent on <1.20.2? Please report this!",true);
                throw new UnsupportedOperationException("LoginAck somehow sent on <1.20.2? Please report this!");
            }
            //new ServerConnection().connect(c, HandshakeIntent.LOGIN, "127.0.0.1",25565);

            c.connect(c.tryIter.next());
            return true;
        }

        if (p instanceof PluginResponse){
            c.backendConnection.writePacketServer(p);
            return true;
        }
        if (p instanceof EncryptionResponse response){
            if (c.verifyToken == null || c.sharedSecret != null) {
                c.noLogDisconnect("\"EncryptionResponse sent without a request\"");
                return true;
            }

            try {
                byte[] token = RSADecrypt(Main.encryptionKey, response.verifyToken);
                c.sharedSecret = RSADecrypt(Main.encryptionKey, response.sharedSecret);
                if (!MessageDigest.isEqual(token, c.verifyToken)) {
                    c.close(); // protocol violation
                    return true;
                }
                // wait! we need to check the session data before continuing
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                // server id ignored since its empty
                digest.update(c.sharedSecret);
                digest.update(Main.encryptionKey.getPublic().getEncoded());
                byte[] hash = digest.digest();
                checkHasJoined(c.plr.name, twosComplementHexdigest(hash)).thenAcceptAsync(profile -> {
                    if (profile == null) {
                        c.close();
                        return;
                    }
                    c.plr.setUUID(profile.uuid);
                    c.plr.properties = profile.properties;
                    // gotta apply the encryption

                    try {
                        c.nChannel.pipeline().addFirst("decrypt", new EncryptionDecoder(c.sharedSecret));
                        c.nChannel.pipeline().addFirst("encrypt", new EncryptionEncoder(c.sharedSecret));
                        continueSuccess(c);
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    }
                }, c.nChannel.eventLoop());


            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
            return true;


        }
        return false;
    }
}
