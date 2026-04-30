package ca.soccer1992.lavaproxy.types;
public enum Difficulty {
    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3);

    private final int value;

    Difficulty(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public static Difficulty fromValue(int value) {
        for (Difficulty d : Difficulty.values()) {
            if (d.value == value) {
                return d;
            }
        }
        throw new IllegalArgumentException("Invalid difficulty: " + value);
    }
}