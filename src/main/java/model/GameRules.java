package model;

public class GameRules {

    public static int[] checkVictory(Board board, char symbol) {
        int[] result;
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            result = checkRow(board, i, symbol);
            if (result != null) return result;
            result = checkColumn(board, i, symbol);
            if (result != null) return result;
        }
        result = checkMainDiagonal(board, symbol);
        if (result != null) return result;
        result = checkAntiDiagonal(board, symbol);
        if (result != null) return result;
        return null;
    }

    public static boolean checkDraw(Board board) {
        return board.isFull();
    }

    private static int[] checkRow(Board board, int row, char symbol) {
        for (int col = 0; col < GameConfig.BOARD_SIZE; col++) {
            if (board.getCell(row, col) != symbol) return null;
        }
        int[] positions = new int[GameConfig.BOARD_SIZE];
        for (int col = 0; col < GameConfig.BOARD_SIZE; col++) {
            positions[col] = row * GameConfig.BOARD_SIZE + col + 1;
        }
        return positions;
    }

    private static int[] checkColumn(Board board, int col, char symbol) {
        for (int row = 0; row < GameConfig.BOARD_SIZE; row++) {
            if (board.getCell(row, col) != symbol) return null;
        }
        int[] positions = new int[GameConfig.BOARD_SIZE];
        for (int row = 0; row < GameConfig.BOARD_SIZE; row++) {
            positions[row] = row * GameConfig.BOARD_SIZE + col + 1;
        }
        return positions;
    }

    private static int[] checkMainDiagonal(Board board, char symbol) {
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            if (board.getCell(i, i) != symbol) return null;
        }
        int[] positions = new int[GameConfig.BOARD_SIZE];
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            positions[i] = i * GameConfig.BOARD_SIZE + i + 1;
        }
        return positions;
    }

    private static int[] checkAntiDiagonal(Board board, char symbol) {
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            if (board.getCell(i, GameConfig.BOARD_SIZE - 1 - i) != symbol) return null;
        }
        int[] positions = new int[GameConfig.BOARD_SIZE];
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            positions[i] = i * GameConfig.BOARD_SIZE + (GameConfig.BOARD_SIZE - 1 - i) + 1;
        }
        return positions;
    }
}
