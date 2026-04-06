package ca.soccer1992.lavaproxy.packets;


import java.util.HashMap;
import java.util.Map;

public enum HandshakeIntent {
    STATUS(1),
    LOGIN(2),
    TRANSFER(3);
    private final int id;

    HandshakeIntent(int id){
        this.id = id;
    }
    public static HandshakeIntent getIntentFromID(int id){
        return switch (id){
            case 1 -> STATUS;
            case 2 -> LOGIN;
            case 3 -> TRANSFER;
            default -> null;
        };
    }
    private static final Map<Integer, HandshakeIntent> BY_ID = new HashMap<>();

    static {
        for (HandshakeIntent type : values()) {
            BY_ID.put(type.id, type);
        }
    }
    public int getId() {
        return (int) id;
    }
}
