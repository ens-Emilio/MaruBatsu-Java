package model;

import org.junit.*;
import static org.junit.Assert.*;

public class BoardTest {

    private Board board;

    @Before
    public void setUp() { board = new Board(); }

    @Test
    public void testNewBoardIsEmpty() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                assertEquals(' ', board.getCell(i, j));
        assertEquals(0, board.getMoveCount());
        assertFalse(board.isFull());
    }

    @Test
    public void testMarkPosition() {
        assertTrue(board.markPosition(5, 'X'));
        assertEquals('X', board.getCell(1, 1));
        assertEquals(1, board.getMoveCount());
    }

    @Test
    public void testMarkInvalidPosition() {
        assertFalse(board.markPosition(0, 'X'));
        assertFalse(board.markPosition(10, 'X'));
        assertFalse(board.markPosition(-1, 'X'));
    }

    @Test
    public void testMarkOccupiedPosition() {
        assertTrue(board.markPosition(1, 'X'));
        assertFalse(board.markPosition(1, 'O'));
    }

    @Test
    public void testIsPositionOccupied() {
        assertFalse(board.isPositionOccupied(1));
        board.markPosition(1, 'X');
        assertTrue(board.isPositionOccupied(1));
        assertTrue(board.isPositionOccupied(0));
        assertTrue(board.isPositionOccupied(10));
    }

    @Test
    public void testUndo() {
        board.markPosition(1, 'X');
        board.markPosition(5, 'O');
        assertEquals(2, board.getMoveCount());

        board.undoLastMove();
        assertEquals(1, board.getMoveCount());
        assertEquals(' ', board.getCell(1, 1));
        assertEquals('X', board.getCell(0, 0));
        assertTrue(board.canRedo());
    }

    @Test
    public void testRedo() {
        board.markPosition(1, 'X');
        board.undoLastMove();
        assertTrue(board.canRedo());

        board.redoLastMove();
        assertEquals('X', board.getCell(0, 0));
        assertEquals(1, board.getMoveCount());
        assertFalse(board.canRedo());
    }

    @Test
    public void testUndoClearsRedo() {
        board.markPosition(1, 'X');
        board.markPosition(5, 'O');
        board.undoLastMove();
        assertTrue(board.canRedo());

        board.markPosition(3, 'O');
        assertFalse(board.canRedo());
    }

    @Test
    public void testIsFull() {
        for (int i = 1; i <= 9; i++) {
            char sym = (i % 2 == 1) ? 'X' : 'O';
            board.markPosition(i, sym);
        }
        assertTrue(board.isFull());
        assertEquals(9, board.getMoveCount());
    }

    @Test
    public void testReset() {
        board.markPosition(1, 'X');
        board.markPosition(5, 'O');
        board.reset();
        assertEquals(0, board.getMoveCount());
        assertFalse(board.canUndo());
        assertFalse(board.canRedo());
    }

    @Test
    public void testCopyConstructor() {
        board.markPosition(1, 'X');
        board.markPosition(5, 'O');
        Board copy = new Board(board);
        assertEquals('X', copy.getCell(0, 0));
        assertEquals('O', copy.getCell(1, 1));
        assertEquals(2, copy.getMoveCount());
    }

    @Test
    public void testMoveHistory() {
        board.markPosition(5, 'X');
        board.markPosition(1, 'O');
        assertEquals(2, board.getMoveHistory().size());
        assertEquals(5, board.getMoveHistory().get(0).getPosition());
        assertEquals(1, board.getMoveHistory().get(1).getPosition());
    }
}
