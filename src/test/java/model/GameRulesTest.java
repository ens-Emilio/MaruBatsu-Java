package model;

import org.junit.*;
import static org.junit.Assert.*;

public class GameRulesTest {

    private Board board;

    @Before
    public void setUp() { board = new Board(); }

    @Test
    public void testNoVictoryOnEmptyBoard() {
        assertNull(GameRules.checkVictory(board, 'X'));
        assertNull(GameRules.checkVictory(board, 'O'));
    }

    @Test
    public void testRowVictory() {
        board.markPosition(1, 'X');
        board.markPosition(4, 'O');
        board.markPosition(2, 'X');
        board.markPosition(5, 'O');
        board.markPosition(3, 'X');

        int[] result = GameRules.checkVictory(board, 'X');
        assertNotNull(result);
        assertArrayEquals(new int[]{1, 2, 3}, result);
    }

    @Test
    public void testColumnVictory() {
        board.markPosition(1, 'X');
        board.markPosition(2, 'O');
        board.markPosition(4, 'X');
        board.markPosition(5, 'O');
        board.markPosition(7, 'X');

        int[] result = GameRules.checkVictory(board, 'X');
        assertNotNull(result);
        assertArrayEquals(new int[]{1, 4, 7}, result);
    }

    @Test
    public void testMainDiagonalVictory() {
        board.markPosition(1, 'X');
        board.markPosition(2, 'O');
        board.markPosition(5, 'X');
        board.markPosition(3, 'O');
        board.markPosition(9, 'X');

        int[] result = GameRules.checkVictory(board, 'X');
        assertNotNull(result);
        assertArrayEquals(new int[]{1, 5, 9}, result);
    }

    @Test
    public void testAntiDiagonalVictory() {
        board.markPosition(3, 'O');
        board.markPosition(1, 'X');
        board.markPosition(5, 'O');
        board.markPosition(2, 'X');
        board.markPosition(7, 'O');

        int[] result = GameRules.checkVictory(board, 'O');
        assertNotNull(result);
        assertArrayEquals(new int[]{3, 5, 7}, result);
    }

    @Test
    public void testNoVictoryForOpponent() {
        board.markPosition(1, 'X');
        board.markPosition(2, 'X');
        board.markPosition(4, 'O');
        assertNull(GameRules.checkVictory(board, 'O'));
    }

    @Test
    public void testDrawDetection() {
        board.markPosition(1, 'X'); board.markPosition(2, 'O');
        board.markPosition(3, 'X'); board.markPosition(4, 'X');
        board.markPosition(5, 'O'); board.markPosition(6, 'O');
        board.markPosition(7, 'O'); board.markPosition(8, 'X');
        board.markPosition(9, 'X');

        assertTrue(GameRules.checkDraw(board));
        assertNull(GameRules.checkVictory(board, 'X'));
        assertNull(GameRules.checkVictory(board, 'O'));
    }

    @Test
    public void testNotDrawWhenEmpty() {
        assertFalse(GameRules.checkDraw(board));
    }

    @Test
    public void testAllVictoryCombinations() {
        int[][] winCombos = {
                {1,2,3}, {4,5,6}, {7,8,9},
                {1,4,7}, {2,5,8}, {3,6,9},
                {1,5,9}, {3,5,7}
        };

        for (int[] combo : winCombos) {
            Board b = new Board();
            for (int pos : combo) b.markPosition(pos, 'X');
            assertNotNull("Should detect win for " + java.util.Arrays.toString(combo),
                    GameRules.checkVictory(b, 'X'));
        }
    }
}
