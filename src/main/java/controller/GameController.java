package controller;

import model.*;

public class GameController {

    private final Board board;
    private final Player player1;
    private final Player player2;
    private final AIPlayer aiPlayer;
    private final Scoreboard scoreboard;
    private final GameConfig config;
    private final GameEventListener listener;
    private final persistence.GameStateRepository gameStateRepo;

    private Player currentPlayer;
    private boolean gameOver;
    private boolean gameActive;
    private boolean aiThinking;

    private java.util.Timer turnTimer;
    private int secondsLeft;

    public GameController(GameConfig config, Scoreboard scoreboard, GameEventListener listener,
                          persistence.GameStateRepository gameStateRepo) {
        this.config = config;
        this.board = new Board();
        this.player1 = new Player(config.getPlayer1Name(), GameConfig.PLAYER_X_SYMBOL);
        this.player2 = new Player(config.getPlayer2Name(), GameConfig.PLAYER_O_SYMBOL);
        this.scoreboard = scoreboard;
        this.listener = listener;
        this.gameStateRepo = gameStateRepo;
        this.currentPlayer = player1;
        this.gameOver = false;
        this.gameActive = false;
        this.aiThinking = false;

        scoreboard.setPlayers(config.getPlayer1Name(), config.getPlayer2Name());

        if (config.isAIMode()) {
            this.aiPlayer = new AIPlayer(GameConfig.PLAYER_O_SYMBOL, config.getMode());
        } else {
            this.aiPlayer = null;
        }
    }

    public void startNewGame() {
        board.reset();
        currentPlayer = player1;
        gameOver = false;
        gameActive = true;
        aiThinking = false;
        listener.onBoardUpdated(board, null);
        listener.onPlayerSwitched(currentPlayer);
        startTurnTimer();
    }

    public void resumeGame(GameState state) {
        board.restoreFrom(state.getCells(), state.getMoveHistory(), state.getMoveCount());
        currentPlayer = state.getCurrentPlayerSymbol() == GameConfig.PLAYER_X_SYMBOL ? player1 : player2;
        gameOver = false;
        gameActive = true;
        aiThinking = false;
        listener.onBoardUpdated(board, null);
        listener.onPlayerSwitched(currentPlayer);
        startTurnTimer();
        if (isAITurn()) scheduleAIMove();
    }

    public void makeMove(int position) {
        if (gameOver || !gameActive || aiThinking) return;

        if (board.isPositionOccupied(position)) {
            listener.onInvalidMove("Posicao ocupada!");
            return;
        }

        board.markPosition(position, currentPlayer.getSymbol());
        listener.onMoveMade(position, currentPlayer);
        stopTurnTimer();

        int[] winLine = GameRules.checkVictory(board, currentPlayer.getSymbol());
        if (winLine != null) {
            gameOver = true;
            gameActive = false;
            listener.onBoardUpdated(board, winLine);
            listener.onGameOver(currentPlayer, winLine);
            updateScoreboard(currentPlayer);
            return;
        }

        if (GameRules.checkDraw(board)) {
            gameOver = true;
            gameActive = false;
            listener.onBoardUpdated(board, null);
            listener.onDraw();
            scoreboard.draw();
            listener.onScoreUpdated(scoreboard);
            return;
        }

        switchPlayer();
        listener.onBoardUpdated(board, null);
        listener.onPlayerSwitched(currentPlayer);
        startTurnTimer();

        if (isAITurn()) scheduleAIMove();
    }

    public void undoMove() {
        if (!gameActive || aiThinking) return;

        stopTurnTimer();

        if (config.isAIMode() && board.canUndo()) {
            MoveRecord rec = board.undoLastMove();
            if (rec != null) switchPlayer();
        }

        if (board.canUndo()) {
            MoveRecord rec = board.undoLastMove();
            if (rec != null) switchPlayer();
        }

        listener.onMoveUndone(board, currentPlayer);
        startTurnTimer();
    }

    public void redoMove() {
        if (!gameActive || aiThinking) return;

        stopTurnTimer();

        if (config.isAIMode() && board.canRedo()) {
            MoveRecord rec = board.redoLastMove();
            if (rec != null) switchPlayer();
        }

        if (board.canRedo()) {
            MoveRecord rec = board.redoLastMove();
            if (rec != null) switchPlayer();
        }

        listener.onMoveRedone(board, currentPlayer);
        startTurnTimer();
    }

    public void forfeit() {
        if (!gameActive) return;
        stopTurnTimer();
        gameOver = true;
        gameActive = false;
        Player winner = currentPlayer.equals(player1) ? player2 : player1;
        listener.onGameOver(winner, null);
        updateScoreboard(winner);
    }

    public void saveGame() {
        GameState state = new GameState(
                snapshotCells(), currentPlayer.getSymbol(), config.getMode(),
                config.getPlayer1Name(), config.getPlayer2Name(),
                board.getMoveCount(), board.getMoveHistory(),
                config.isTimerEnabled(), config.getTimerSeconds());
        gameStateRepo.save(state);
        listener.onGameSaved();
    }

    public char getCurrentPlayerSymbol() { return currentPlayer.getSymbol(); }
    public Player getCurrentPlayer() { return currentPlayer; }
    public Board getBoard() { return board; }
    public Scoreboard getScoreboard() { return scoreboard; }
    public GameConfig getConfig() { return config; }
    public boolean isGameActive() { return gameActive; }
    public boolean isAiThinking() { return aiThinking; }

    public boolean canUndo() {
        if (!gameActive || aiThinking) return false;
        if (config.isAIMode()) return board.getMoveCount() >= 2;
        return board.canUndo();
    }

    public boolean canRedo() {
        if (!gameActive || aiThinking) return false;
        return board.canRedo();
    }

    public boolean isAITurn() {
        return aiPlayer != null && currentPlayer.equals(player2);
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
    }

    private void scheduleAIMove() {
        aiThinking = true;
        java.util.Timer aiTimer = new java.util.Timer("AI-Timer", true);
        aiTimer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                if (!gameActive) { aiThinking = false; return; }
                int move = aiPlayer.chooseMove(board);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    aiThinking = false;
                    makeMove(move);
                });
            }
        }, 600);
    }

    private void startTurnTimer() {
        stopTurnTimer();
        if (!config.isTimerEnabled() || isAITurn()) return;
        secondsLeft = config.getTimerSeconds();
        turnTimer = new java.util.Timer("Turn-Timer", true);
        turnTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                secondsLeft--;
                javax.swing.SwingUtilities.invokeLater(() -> {
                    listener.onTimerTick(secondsLeft);
                    if (secondsLeft <= 0) { stopTurnTimer(); timeUp(); }
                });
            }
        }, 0, 1000);
    }

    private void stopTurnTimer() {
        if (turnTimer != null) { turnTimer.cancel(); turnTimer = null; }
    }

    private void timeUp() {
        gameOver = true;
        gameActive = false;
        Player winner = currentPlayer.equals(player1) ? player2 : player1;
        listener.onGameOver(winner, null);
        updateScoreboard(winner);
    }

    private void updateScoreboard(Player winner) {
        if (winner.equals(player1)) scoreboard.player1Won();
        else scoreboard.player2Won();
        listener.onScoreUpdated(scoreboard);
    }

    private char[][] snapshotCells() {
        char[][] snap = new char[GameConfig.BOARD_SIZE][GameConfig.BOARD_SIZE];
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConfig.BOARD_SIZE; j++) {
                snap[i][j] = board.getCell(i, j);
            }
        }
        return snap;
    }
}
