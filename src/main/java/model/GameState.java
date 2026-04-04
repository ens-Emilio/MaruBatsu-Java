package model;

public class GameState {

    private final char[][] cells;
    private final char currentPlayerSymbol;
    private final GameConfig.Mode mode;
    private final String player1Name;
    private final String player2Name;
    private final int moveCount;
    private final java.util.List<MoveRecord> moveHistory;
    private final boolean timerEnabled;
    private final int timerSeconds;

    public GameState(char[][] cells, char currentPlayerSymbol, GameConfig.Mode mode,
                     String player1Name, String player2Name, int moveCount,
                     java.util.List<MoveRecord> moveHistory,
                     boolean timerEnabled, int timerSeconds) {
        this.cells = cells;
        this.currentPlayerSymbol = currentPlayerSymbol;
        this.mode = mode;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.moveCount = moveCount;
        this.moveHistory = moveHistory;
        this.timerEnabled = timerEnabled;
        this.timerSeconds = timerSeconds;
    }

    public char[][] getCells() { return cells; }
    public char getCurrentPlayerSymbol() { return currentPlayerSymbol; }
    public GameConfig.Mode getMode() { return mode; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public int getMoveCount() { return moveCount; }
    public java.util.List<MoveRecord> getMoveHistory() { return moveHistory; }
    public boolean isTimerEnabled() { return timerEnabled; }
    public int getTimerSeconds() { return timerSeconds; }
}
