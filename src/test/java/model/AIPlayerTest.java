package model;

import org.junit.*;
import static org.junit.Assert.*;

public class AIPlayerTest {

    @Test
    public void testHardAINeverLosesAsX() {
        for (int trial = 0; trial < 20; trial++) {
            Board board = new Board();
            AIPlayer aiX = new AIPlayer('X', GameConfig.Mode.PV_AI_HARD);
            AIPlayer aiO = new AIPlayer('O', GameConfig.Mode.PV_AI_HARD);

            for (int move = 0; move < 9; move++) {
                AIPlayer current = (move % 2 == 0) ? aiX : aiO;
                int pos = current.chooseMove(board);
                board.markPosition(pos, current.symbol);

                if (GameRules.checkVictory(board, current.symbol) != null) break;
            }

            int[] xWin = GameRules.checkVictory(board, 'X');
            int[] oWin = GameRules.checkVictory(board, 'O');
            assertTrue("Hard AI (X) should never lose, trial " + trial,
                    xWin != null || (xWin == null && oWin == null));
        }
    }

    @Test
    public void testHardAINeverLosesAsO() {
        for (int trial = 0; trial < 20; trial++) {
            Board board = new Board();
            AIPlayer aiX = new AIPlayer('X', GameConfig.Mode.PV_AI_HARD);
            AIPlayer aiO = new AIPlayer('O', GameConfig.Mode.PV_AI_HARD);

            for (int move = 0; move < 9; move++) {
                AIPlayer current = (move % 2 == 0) ? aiX : aiO;
                int pos = current.chooseMove(board);
                board.markPosition(pos, current.symbol);

                if (GameRules.checkVictory(board, current.symbol) != null) break;
            }

            int[] xWin = GameRules.checkVictory(board, 'X');
            int[] oWin = GameRules.checkVictory(board, 'O');
            assertTrue("Hard AI (O) should never lose, trial " + trial,
                    oWin != null || (xWin == null && oWin == null));
        }
    }

    @Test
    public void testHardAITakesWinningMove() {
        Board board = new Board();
        board.markPosition(1, 'X');
        board.markPosition(2, 'X');

        AIPlayer ai = new AIPlayer('X', GameConfig.Mode.PV_AI_HARD);
        int move = ai.chooseMove(board);
        assertEquals("AI should take the winning move", 3, move);
    }

    @Test
    public void testHardAIBlocksOpponentWin() {
        Board board = new Board();
        board.markPosition(1, 'X');
        board.markPosition(2, 'X');
        board.markPosition(4, 'O');

        AIPlayer ai = new AIPlayer('O', GameConfig.Mode.PV_AI_HARD);
        int move = ai.chooseMove(board);
        assertEquals("AI should block opponent win", 3, move);
    }

    @Test
    public void testEasyAIMakesValidMove() {
        Board board = new Board();
        AIPlayer ai = new AIPlayer('X', GameConfig.Mode.PV_AI_EASY);

        for (int i = 0; i < 9; i++) {
            int move = ai.chooseMove(board);
            assertTrue("Move should be valid", move >= 1 && move <= 9);
            assertTrue("Move should be to empty cell", board.markPosition(move, 'X'));
        }
    }

    @Test
    public void testMediumAIMakesValidMove() {
        Board board = new Board();
        AIPlayer ai = new AIPlayer('X', GameConfig.Mode.PV_AI_MEDIUM);

        for (int i = 0; i < 9; i++) {
            int move = ai.chooseMove(board);
            assertTrue("Move should be valid", move >= 1 && move <= 9);
            assertTrue("Move should be to empty cell", board.markPosition(move, 'X'));
        }
    }

    @Test
    public void testHardAIPrefersCenter() {
        Board board = new Board();
        AIPlayer ai = new AIPlayer('X', GameConfig.Mode.PV_AI_HARD);
        assertEquals("Hard AI should take center on empty board", 5, ai.chooseMove(board));
    }
}
