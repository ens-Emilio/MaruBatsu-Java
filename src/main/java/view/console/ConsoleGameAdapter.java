package view.console;

import controller.GameController;
import controller.GameEventListener;
import model.*;

import java.util.List;
import java.util.Scanner;

public class ConsoleGameAdapter implements GameEventListener {

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";
    private static final String CLEAR = "\u001B[2J\u001B[H";

    private final Scanner scanner = new Scanner(System.in);
    private GameController controller;
    private Scoreboard scoreboard;

    @Override public void onBoardUpdated(Board board, int[] highlight) { printBoard(board, highlight); }
    @Override public void onPlayerSwitched(Player p) {
        String c = p.getSymbol() == GameConfig.PLAYER_X_SYMBOL ? BLUE : RED;
        System.out.println(colorize("Vez de " + p.getName() + " (" + p.getSymbol() + ")", c, BOLD));
    }
    @Override public void onGameOver(Player winner, int[] hl) {
        if (hl != null) printBoard(controller.getBoard(), hl);
        String c = winner.getSymbol() == GameConfig.PLAYER_X_SYMBOL ? BLUE : RED;
        System.out.println(colorize("Parabens! " + winner.getName() + " venceu!", c, BOLD));
    }
    @Override public void onDraw() { System.out.println(colorize("Deu velha! Empate!", YELLOW, BOLD)); }
    @Override public void onInvalidMove(String reason) { System.out.println(colorize(reason, RED)); }
    @Override public void onTimerTick(int s) {}
    @Override public void onMoveUndone(Board b, Player p) {
        printBoard(b, null);
        System.out.println(colorize("Jogada desfeita! Vez de " + p.getName(), YELLOW));
    }
    @Override public void onMoveRedone(Board b, Player p) {
        printBoard(b, null);
        System.out.println(colorize("Jogada refeita! Vez de " + p.getName(), YELLOW));
    }
    @Override public void onMoveMade(int pos, Player p) {}
    @Override public void onScoreUpdated(Scoreboard sb) { this.scoreboard = sb; }
    @Override public void onGameSaved() { System.out.println(colorize("Jogo salvo!", GREEN)); }
    @Override public void onGameLoaded(GameState gs) {}

    public void run() {
        System.out.print(CLEAR);
        System.out.println(colorize("=== JOGO DA VELHA ===", CYAN, BOLD));
        System.out.println();

        GameConfig config = readConfig();
        scoreboard = new Scoreboard();
        scoreboard.setPlayers(config.getPlayer1Name(), config.getPlayer2Name());

        controller = new GameController(config, scoreboard, this,
                new persistence.FileGameStateRepository());

        boolean playing = true;
        while (playing) {
            controller.startNewGame();

            while (controller.isGameActive()) {
                Player current = controller.getCurrentPlayer();
                if (config.isAIMode() && current.getSymbol() == GameConfig.PLAYER_O_SYMBOL) {
                    try { Thread.sleep(500); } catch (InterruptedException e) { break; }
                    continue;
                }

                String c = current.getSymbol() == GameConfig.PLAYER_X_SYMBOL ? BLUE : RED;
                System.out.print(colorize(current.getName() + " (" + current.getSymbol() + "), escolha (1-9): ", c));
                String input = scanner.nextLine().trim();

                if (input.equals("u") || input.equals("undo")) { controller.undoMove(); continue; }
                if (input.equals("r") || input.equals("redo")) { controller.redoMove(); continue; }
                if (input.equals("s") || input.equals("save")) { controller.saveGame(); continue; }

                try {
                    int pos = Integer.parseInt(input);
                    controller.makeMove(pos);
                } catch (NumberFormatException e) {
                    System.out.println(colorize("Entrada invalida! Use 1-9, 'u' undo, 'r' redo, 's' save", RED));
                }
            }

            printScoreboard();
            System.out.print(colorize("Jogar novamente? (s/n): ", YELLOW));
            playing = scanner.nextLine().trim().toLowerCase().matches("s|sim");
        }
        System.out.println(colorize("Obrigado por jogar!", GREEN, BOLD));
    }

    private GameConfig readConfig() {
        System.out.println(colorize("[1]", GREEN) + " PvP");
        System.out.println(colorize("[2]", YELLOW) + " vs IA Facil");
        System.out.println(colorize("[3]", CYAN) + " vs IA Media");
        System.out.println(colorize("[4]", RED) + " vs IA Dificil");
        int choice = readInt("Escolha: ", 1, 4);

        System.out.print(colorize("Jogador 1 (X): ", CYAN));
        String p1 = scanner.nextLine().trim();
        if (p1.isEmpty()) p1 = "Jogador 1";

        GameConfig.Mode mode; String p2;
        if (choice == 1) {
            mode = GameConfig.Mode.PVP;
            System.out.print(colorize("Jogador 2 (O): ", CYAN));
            p2 = scanner.nextLine().trim();
            if (p2.isEmpty()) p2 = "Jogador 2";
        } else if (choice == 2) { mode = GameConfig.Mode.PV_AI_EASY; p2 = "IA Facil";
        } else if (choice == 3) { mode = GameConfig.Mode.PV_AI_MEDIUM; p2 = "IA Media";
        } else { mode = GameConfig.Mode.PV_AI_HARD; p2 = "IA Dificil"; }

        return new GameConfig.Builder().mode(mode).interfaceMode(GameConfig.InterfaceMode.CONSOLE)
                .player1Name(p1).player2Name(p2).build();
    }

    private void printBoard(Board board, int[] hl) {
        System.out.println();
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConfig.BOARD_SIZE; j++) {
                char c = board.getCell(i, j);
                int pos = i * GameConfig.BOARD_SIZE + j + 1;
                boolean isHl = hl != null && contains(hl, pos);
                if (c == GameConfig.EMPTY_CELL) {
                    System.out.print(colorize(" " + pos + " ", isHl ? YELLOW : DIM));
                } else {
                    String color = isHl ? GREEN : (c == GameConfig.PLAYER_X_SYMBOL ? BLUE : RED);
                    System.out.print(colorize(" " + c + " ", BOLD, color));
                }
                if (j < GameConfig.BOARD_SIZE - 1) System.out.print("|");
            }
            System.out.println();
            if (i < GameConfig.BOARD_SIZE - 1) System.out.println("-----------");
        }
        System.out.println();
    }

    private void printScoreboard() {
        System.out.println(colorize("=== PLACAR ===", CYAN, BOLD));
        System.out.println(colorize("  " + scoreboard.getPlayer1Name() + " (X): " + scoreboard.getPlayer1Wins(), BLUE));
        System.out.println(colorize("  " + scoreboard.getPlayer2Name() + " (O): " + scoreboard.getPlayer2Wins(), RED));
        System.out.println(colorize("  Empates: " + scoreboard.getDraws(), YELLOW));
        System.out.println();
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(colorize(prompt, YELLOW));
            try { int v = Integer.parseInt(scanner.nextLine().trim()); if (v >= min && v <= max) return v; }
            catch (NumberFormatException e) { /* fall through */ }
            System.out.println(colorize("Opcao invalida!", RED));
        }
    }

    private String colorize(String text, String... codes) {
        StringBuilder sb = new StringBuilder();
        for (String c : codes) sb.append(c);
        return sb.append(text).append(RESET).toString();
    }

    private boolean contains(int[] arr, int val) { for (int v : arr) if (v == val) return true; return false; }
}
