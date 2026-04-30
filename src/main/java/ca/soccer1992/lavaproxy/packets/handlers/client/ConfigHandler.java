package ca.soccer1992.lavaproxy.packets.handlers.client;

import ca.soccer1992.lavaproxy.Connection;
import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.packets.ConnectionTypes;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.client.NBTKick;
import ca.soccer1992.lavaproxy.packets.client.RegistryData;
import ca.soccer1992.lavaproxy.packets.client.Transfer;
import ca.soccer1992.lavaproxy.packets.client.UpdateTags;
import ca.soccer1992.lavaproxy.packets.clientserver.FinishConfiguration;
import ca.soccer1992.lavaproxy.packets.clientserver.KeepAlive;
import ca.soccer1992.lavaproxy.packets.clientserver.KnownPacks;
import ca.soccer1992.lavaproxy.packets.clientserver.PluginMessage;
import ca.soccer1992.lavaproxy.packets.handlers.Handler;
import ca.soccer1992.lavaproxy.packets.server.FeatureFlags;
import ca.soccer1992.lavaproxy.types.RegistryPart;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.querz.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.fromJSON;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readString;
import static ca.soccer1992.lavaproxy.utils.PacketHelpers.writeString;

public class ConfigHandler extends Handler {
    public boolean handle(Packet p, Connection c){
        Connection backendConnection = c.backendConnection;
        //System.out.println(p.getClass());
        //System.out.println(c.isClosed);
        //System.out.println(c.hasDisconnected);
        if (p instanceof final NBTKick packet){
            try {
                backendConnection.backendDisconnect(fromJSON(packet.reasonJSON()));
            } catch (Exception e){
                backendConnection.disconnect(e.toString(), true);
            }
            return true;
        }
        if (p instanceof final Transfer packet){
            System.out.println(backendConnection.fillPlaceholders("backend.transfer", "", "", packet.host, packet.port,""));
            backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof final RegistryData packet){
            if (Objects.equals(packet.id.name(), "dimension_type")){
                CompoundTag codec = new CompoundTag();
                Map<String, Integer> dimMap = new HashMap<>();
                for (RegistryPart part : packet.RegistryData.values()){
                    if (!part.hasNBT()){
                        continue;
                        //throw new IllegalArgumentException("dimension_type does not have NBT attached to RegistryPart");
                    }
                    CompoundTag t = part.nbt();
                    codec.put(part.entry(),t); // confirmed to have nbt
                    dimMap.put(part.entry(), part.id());

                }
                c._dimensionMap = dimMap;
                c._dimensionCodec = codec;
            }
            backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof final KeepAlive packet){

            try {
                backendConnection.writePacket(packet);
            } catch (Exception e){
                return false;
            }
            return true;
        }
        if (p instanceof FeatureFlags packet){
            c.plr.enabled_features = packet.enabled_features;
            backendConnection.plr.enabled_features = packet.enabled_features;
            return true;
        }
        if (p instanceof KnownPacks packet){
            backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof FinishConfiguration){
            backendConnection.conType = ConnectionTypes.POST_CONFIG;
            backendConnection.writePacket(new FinishConfiguration());
            c.conType = ConnectionTypes.PLAY;
            return true;
        }
        if (p instanceof final UpdateTags packet){
            backendConnection.writePacket(packet);
            return true;
        }
        if (p instanceof final PluginMessage packet) {
            ByteBuf dataBuf = Unpooled.copiedBuffer(packet.data);

            if (packet.channel.equalsIgnoreCase("minecraft:brand")){
                // rewrite
                PluginMessage rewrite = new PluginMessage();
                String rewritten = readString(dataBuf);
                rewritten = backendConnection.fillPlaceholders(Main.translations.get("backend.brand"),"",backendConnection.plr.brand, "", 0, rewritten);
                ByteBuf buf = Unpooled.buffer();
                writeString(rewritten, buf);
                byte[] out = new byte[buf.readableBytes()];
                buf.readBytes(out);
                rewrite.setChannel(packet.channel);
                rewrite.setData(out);
                backendConnection.writePacket(rewrite);
                buf.release();
            }
            dataBuf.release();
            return true;
        }

        return false;
    }
}
