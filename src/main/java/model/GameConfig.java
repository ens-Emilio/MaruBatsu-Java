package model;

public class GameConfig {

    public static final int BOARD_SIZE = 3;
    public static final int TOTAL_CELLS = BOARD_SIZE * BOARD_SIZE;
    public static final char EMPTY_CELL = ' ';
    public static final char PLAYER_X_SYMBOL = 'X';
    public static final char PLAYER_O_SYMBOL = 'O';
    public static final int DEFAULT_TIMER_SECONDS = 15;

    public enum Mode { PVP, PV_AI_EASY, PV_AI_MEDIUM, PV_AI_HARD }
    public enum InterfaceMode { GUI, CONSOLE }

    private final Mode mode;
    private final InterfaceMode interfaceMode;
    private final String player1Name;
    private final String player2Name;
    private final int timerSeconds;
    private final boolean timerEnabled;

    private GameConfig(Builder builder) {
        this.mode = builder.mode;
        this.interfaceMode = builder.interfaceMode;
        this.player1Name = builder.player1Name;
        this.player2Name = builder.player2Name;
        this.timerSeconds = builder.timerSeconds;
        this.timerEnabled = builder.timerEnabled;
    }

    public Mode getMode() { return mode; }
    public InterfaceMode getInterfaceMode() { return interfaceMode; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public int getTimerSeconds() { return timerSeconds; }
    public boolean isTimerEnabled() { return timerEnabled; }
    public boolean isAIMode() { return mode != Mode.PVP; }
    public boolean isHardAI() { return mode == Mode.PV_AI_HARD; }
    public boolean isMediumAI() { return mode == Mode.PV_AI_MEDIUM; }

    public static class Builder {
        private Mode mode = Mode.PVP;
        private InterfaceMode interfaceMode = InterfaceMode.GUI;
        private String player1Name = "Jogador 1";
        private String player2Name = "Jogador 2";
        private int timerSeconds = DEFAULT_TIMER_SECONDS;
        private boolean timerEnabled = false;

        public Builder mode(Mode mode) { this.mode = mode; return this; }
        public Builder interfaceMode(InterfaceMode im) { this.interfaceMode = im; return this; }
        public Builder player1Name(String name) { this.player1Name = name; return this; }
        public Builder player2Name(String name) { this.player2Name = name; return this; }
        public Builder timerSeconds(int seconds) { this.timerSeconds = seconds; return this; }
        public Builder timerEnabled(boolean enabled) { this.timerEnabled = enabled; return this; }
        public GameConfig build() { return new GameConfig(this); }
    }
}
