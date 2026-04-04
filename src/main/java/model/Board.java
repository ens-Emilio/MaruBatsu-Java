package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Board {

    private final char[][] cells;
    private final Stack<MoveRecord> moveHistory;
    private final Stack<MoveRecord> redoStack;
    private int moveCount;

    public Board() {
        this.cells = new char[GameConfig.BOARD_SIZE][GameConfig.BOARD_SIZE];
        this.moveHistory = new Stack<>();
        this.redoStack = new Stack<>();
        reset();
    }

    public Board(Board other) {
        this.cells = new char[GameConfig.BOARD_SIZE][GameConfig.BOARD_SIZE];
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConfig.BOARD_SIZE; j++) {
                this.cells[i][j] = other.cells[i][j];
            }
        }
        this.moveHistory = new Stack<>();
        this.moveHistory.addAll(other.moveHistory);
        this.redoStack = new Stack<>();
        this.moveCount = other.moveCount;
    }

    public void reset() {
        moveCount = 0;
        moveHistory.clear();
        redoStack.clear();
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConfig.BOARD_SIZE; j++) {
                cells[i][j] = GameConfig.EMPTY_CELL;
            }
        }
    }

    public boolean markPosition(int position, char symbol) {
        if (position < 1 || position > GameConfig.TOTAL_CELLS) return false;
        int row = (position - 1) / GameConfig.BOARD_SIZE;
        int col = (position - 1) % GameConfig.BOARD_SIZE;
        if (cells[row][col] != GameConfig.EMPTY_CELL) return false;

        cells[row][col] = symbol;
        moveCount++;
        moveHistory.push(new MoveRecord(position, symbol, moveCount));
        redoStack.clear();
        return true;
    }

    public MoveRecord undoLastMove() {
        if (moveHistory.isEmpty()) return null;
        MoveRecord record = moveHistory.pop();
        int row = (record.getPosition() - 1) / GameConfig.BOARD_SIZE;
        int col = (record.getPosition() - 1) % GameConfig.BOARD_SIZE;
        cells[row][col] = GameConfig.EMPTY_CELL;
        moveCount--;
        redoStack.push(record);
        return record;
    }

    public MoveRecord redoLastMove() {
        if (redoStack.isEmpty()) return null;
        MoveRecord record = redoStack.pop();
        int row = (record.getPosition() - 1) / GameConfig.BOARD_SIZE;
        int col = (record.getPosition() - 1) % GameConfig.BOARD_SIZE;
        cells[row][col] = record.getPlayerSymbol();
        moveCount++;
        moveHistory.push(record);
        return record;
    }

    public boolean canUndo() { return !moveHistory.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public boolean isPositionOccupied(int position) {
        if (position < 1 || position > GameConfig.TOTAL_CELLS) return true;
        int row = (position - 1) / GameConfig.BOARD_SIZE;
        int col = (position - 1) % GameConfig.BOARD_SIZE;
        return cells[row][col] != GameConfig.EMPTY_CELL;
    }

    public char getCell(int row, int col) { return cells[row][col]; }
    public int getMoveCount() { return moveCount; }
    public boolean isFull() { return moveCount == GameConfig.TOTAL_CELLS; }

    public List<MoveRecord> getMoveHistory() {
        return Collections.unmodifiableList(new ArrayList<>(moveHistory));
    }

    public void restoreFrom(char[][] savedCells, List<MoveRecord> savedHistory, int savedMoveCount) {
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConfig.BOARD_SIZE; j++) {
                cells[i][j] = savedCells[i][j];
            }
        }
        moveHistory.clear();
        moveHistory.addAll(savedHistory);
        redoStack.clear();
        moveCount = savedMoveCount;
    }
}
