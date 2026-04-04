package controller;

import model.Board;
import model.GameState;
import model.MoveRecord;
import model.Player;
import model.Scoreboard;

public interface GameEventListener {
    void onBoardUpdated(Board board, int[] highlightPositions);
    void onPlayerSwitched(Player currentPlayer);
    void onGameOver(Player winner, int[] winningPositions);
    void onDraw();
    void onInvalidMove(String reason);
    void onTimerTick(int secondsLeft);
    void onMoveUndone(Board board, Player currentPlayer);
    void onMoveRedone(Board board, Player currentPlayer);
    void onMoveMade(int position, Player player);
    void onScoreUpdated(Scoreboard scoreboard);
    void onGameSaved();
    void onGameLoaded(GameState gameState);
}
