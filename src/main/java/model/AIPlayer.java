package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer {

    private final char symbol;
    private final char opponentSymbol;
    private final GameConfig.Mode difficulty;
    private final Random random;

    public AIPlayer(char symbol, GameConfig.Mode difficulty) {
        this.symbol = symbol;
        this.opponentSymbol = (symbol == GameConfig.PLAYER_X_SYMBOL)
                ? GameConfig.PLAYER_O_SYMBOL : GameConfig.PLAYER_X_SYMBOL;
        this.difficulty = difficulty;
        this.random = new Random();
    }

    public int chooseMove(Board board) {
        switch (difficulty) {
            case PV_AI_EASY: return easyMove(board);
            case PV_AI_MEDIUM: return mediumMove(board);
            case PV_AI_HARD: return minimaxMove(board);
            default: return easyMove(board);
        }
    }

    private int easyMove(Board board) {
        int blocking = findWinningMove(board, symbol);
        if (blocking != -1) return blocking;
        return randomMove(board);
    }

    private int mediumMove(Board board) {
        int winning = findWinningMove(board, symbol);
        if (winning != -1) return winning;

        int blocking = findWinningMove(board, opponentSymbol);
        if (blocking != -1) return blocking;

        if (!board.isPositionOccupied(5)) return 5;

        if (random.nextDouble() < 0.6) {
            return minimaxMove(board);
        }

        int[] corners = {1, 3, 7, 9};
        for (int c : corners) {
            if (!board.isPositionOccupied(c)) return c;
        }

        return randomMove(board);
    }

    private int findWinningMove(Board board, char sym) {
        for (int pos = 1; pos <= GameConfig.TOTAL_CELLS; pos++) {
            if (!board.isPositionOccupied(pos)) {
                Board copy = new Board(board);
                copy.markPosition(pos, sym);
                if (GameRules.checkVictory(copy, sym) != null) return pos;
            }
        }
        return -1;
    }

    private int minimaxMove(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;
        for (int pos = 1; pos <= GameConfig.TOTAL_CELLS; pos++) {
            if (!board.isPositionOccupied(pos)) {
                Board copy = new Board(board);
                copy.markPosition(pos, symbol);
                int score = alphabeta(copy, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = pos;
                }
            }
        }
        return bestMove;
    }

    private int alphabeta(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        if (GameRules.checkVictory(board, symbol) != null) return 10 - depth;
        if (GameRules.checkVictory(board, opponentSymbol) != null) return depth - 10;
        if (board.isFull()) return 0;

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int pos = 1; pos <= GameConfig.TOTAL_CELLS; pos++) {
                if (!board.isPositionOccupied(pos)) {
                    Board copy = new Board(board);
                    copy.markPosition(pos, symbol);
                    int eval = alphabeta(copy, depth + 1, alpha, beta, false);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int pos = 1; pos <= GameConfig.TOTAL_CELLS; pos++) {
                if (!board.isPositionOccupied(pos)) {
                    Board copy = new Board(board);
                    copy.markPosition(pos, opponentSymbol);
                    int eval = alphabeta(copy, depth + 1, alpha, beta, true);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break;
                }
            }
            return minEval;
        }
    }

    private int randomMove(Board board) {
        List<Integer> available = getAvailablePositions(board);
        return available.get(random.nextInt(available.size()));
    }

    private List<Integer> getAvailablePositions(Board board) {
        List<Integer> positions = new ArrayList<>();
        for (int pos = 1; pos <= GameConfig.TOTAL_CELLS; pos++) {
            if (!board.isPositionOccupied(pos)) positions.add(pos);
        }
        return positions;
    }
}
