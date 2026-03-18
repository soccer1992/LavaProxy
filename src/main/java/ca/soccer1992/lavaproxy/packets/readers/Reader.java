package ca.soccer1992.lavaproxy.packets.readers;

import ca.soccer1992.lavaproxy.MinecraftVersions;
import ca.soccer1992.lavaproxy.packets.DefinitionPair;
import ca.soccer1992.lavaproxy.packets.Packet;
import ca.soccer1992.lavaproxy.packets.server.HandshakePacket;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static ca.soccer1992.lavaproxy.PacketHelpers.readVarInt;

public abstract class Reader {
    public static Map<Class<? extends Packet>, List<DefinitionPair>> serverDefinitions = Map.of();
    protected abstract Map<Class<? extends Packet>, List<DefinitionPair>> serverDefinitions();
    protected abstract Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions();

    public static Map<Class<? extends Packet>, List<DefinitionPair>> clientDefinitions = Map.of();
    public Packet read(ByteBuf buf, int ver) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int id = readVarInt(buf);
        Packet p = getPacketFromInfo(MinecraftVersions.ID_TO_PROTOCOL_CONSTANT.get(ver), id).getDeclaredConstructor().newInstance();

        p.decode(buf);
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
                    return pair.packetID();
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
                if (oldEntry != null){
                    if (pair.protocol().getProtocol()>ver){
                        return e.getKey();
                    }
                }
                correct = e;

                oldEntry = pair;

            }
        }
        return correct.getKey();
    }

}
