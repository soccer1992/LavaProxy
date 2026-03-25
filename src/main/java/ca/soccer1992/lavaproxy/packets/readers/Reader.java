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
    public Packet read(ByteBuf buf, int ver) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int id = readVarInt(buf);
        Class<? extends Packet> clazz = getPacketFromInfo(MinecraftVersions.ID_TO_PROTOCOL_CONSTANT.get(ver), id);
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
    public Class<? extends Packet> _getPacketFromInfo(MinecraftVersions ver1, int packetID, Map<Class<? extends Packet>, List<DefinitionPair>> definitions){
        DefinitionPair oldEntry = null;
        int ver = ver1.getProtocol();
        Map. Entry<Class<? extends Packet>, List<DefinitionPair>> correct = null;
        for (var e : definitions.entrySet()){
            for (DefinitionPair pair : e.getValue()){
                if (pair.packetID() != packetID) continue;
                if (oldEntry != null){
                    if (pair.protocol().getProtocol()>ver){
                        return correct.getKey();
                    }
                }
                correct = e;

                oldEntry = pair;

            }
        }
        if (correct == null){
            return null; // to prevent a error
        }
        return correct.getKey();
    }

}
