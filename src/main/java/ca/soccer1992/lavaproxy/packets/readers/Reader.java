package ca.soccer1992.lavaproxy.packets.readers;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.DefinitionPair;
import ca.soccer1992.lavaproxy.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static ca.soccer1992.lavaproxy.utils.PacketHelpers.readVarInt;

public abstract class Reader {
    protected abstract Map<Class<? extends Packet>, List<DefinitionPair>> serverDefinitions();
    protected abstract Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions();
    public Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions = Map.of();
    public Packet read(ByteBuf buf, int ver, boolean forceClient) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int id = readVarInt(buf);
        Class<? extends Packet> clazz;
        System.out.printf("Attempting to read ID %s from protocol %s as %s (IsClient: %s)%n",id,ver,this.getClass(),forceClient);
        if (!forceClient) {
            clazz = getPacketFromInfo(MinecraftVersions.ID_TO_PROTOCOL_CONSTANT.get(ver), id);
        } else {
            clazz = getPacketFromInfoClient(MinecraftVersions.ID_TO_PROTOCOL_CONSTANT.get(ver), id);

        }
        if (clazz == null){
            return null;
        }

        Packet p = clazz.getDeclaredConstructor().newInstance();

        p.decode(buf, MinecraftVersions.ID_TO_PROTOCOL_CONSTANT.get(ver));
        return p;
    }
    public int getPacketFromInfo(MinecraftVersions ver, Class<? extends Packet> packet){
        return _getPacketFromInfo(ver, packet, serverDefinitions());
    }
    public Class<? extends Packet> getPacketFromInfo(MinecraftVersions ver, int packetID){
        return _getPacketFromInfo(ver, packetID, serverDefinitions());
    }

    public int getPacketFromInfoClient(MinecraftVersions ver, Class<? extends Packet> packet){
        return _getPacketFromInfo(ver, packet, clientDefinitions());
    }
    public Class<? extends Packet> getPacketFromInfoClient(MinecraftVersions ver, int packetID){
        return _getPacketFromInfo(ver, packetID, clientDefinitions());
    }

    public int _getPacketFromInfo(MinecraftVersions ver1, Class<? extends Packet> packet, Map<Class<? extends Packet>, List<DefinitionPair>> definitions){
        DefinitionPair oldEntry = null;
        int correct = 0;
        int ver = ver1.getProtocol();
        if (definitions.get(packet) == null) return 0xffff;
        for (DefinitionPair pair : definitions.get(packet)){
            if (oldEntry != null){
                if (pair.protocol().getProtocol()>ver){
                    return oldEntry.packetID();
                }
            }
            correct = pair.packetID();
            oldEntry = pair;
        }
        return correct;
    }

    public static DefinitionPair _nearestLower(List<DefinitionPair> items, int target) {
        DefinitionPair result = null;
        for (DefinitionPair item : items) {

            int value = item.protocol().getProtocol();
            //System.out.println(value);
            //System.out.println(target);
            //System.out.println(item);
            if (value <= target && (result == null || value > result.protocol().getProtocol())) {
                result = item;
            }
        }

        return result;
    }

    public Class<? extends Packet> _getPacketFromInfo(MinecraftVersions ver1, int packetID, Map<Class<? extends Packet>, List<DefinitionPair>> definitions){
        int ver = ver1.getProtocol();
        DefinitionPair correctPair = null;
        Map. Entry<Class<? extends Packet>, List<DefinitionPair>> correct = null;
        for (var e : definitions.entrySet()){
            List<DefinitionPair> filtered = e.getValue().stream().filter(definition -> definition.packetID()==packetID).toList();

            DefinitionPair out = _nearestLower(filtered, ver);

            if (out != null){
                if (correct == null){
                    correct = e;
                    correctPair = out;
                } else if (out.protocol().getProtocol()>correctPair.protocol().getProtocol()){
                    correct = e;
                    correctPair = out;
                }
            }
        }
        if (correct == null){
            return null; // to prevent a error
        }
        return correct.getKey();
    }

}
