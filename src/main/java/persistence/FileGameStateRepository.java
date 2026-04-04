package persistence;

import model.GameConfig;
import model.GameState;
import model.MoveRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileGameStateRepository implements GameStateRepository {

    private static final String FILE = "gamestate.dat";

    @Override
    public void save(GameState state) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println("mode=" + state.getMode().name());
            pw.println("p1Name=" + state.getPlayer1Name());
            pw.println("p2Name=" + state.getPlayer2Name());
            pw.println("currentPlayer=" + state.getCurrentPlayerSymbol());
            pw.println("moveCount=" + state.getMoveCount());
            pw.println("timerEnabled=" + state.isTimerEnabled());
            pw.println("timerSeconds=" + state.getTimerSeconds());

            StringBuilder cells = new StringBuilder("cells=");
            for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
                for (int j = 0; j < GameConfig.BOARD_SIZE; j++) {
                    cells.append(state.getCells()[i][j]);
                }
            }
            pw.println(cells);

            StringBuilder history = new StringBuilder("history=");
            for (MoveRecord mr : state.getMoveHistory()) {
                history.append(mr.serialize()).append(";");
            }
            pw.println(history);

        } catch (IOException e) {
            System.err.println("Erro ao salvar jogo: " + e.getMessage());
        }
    }

    @Override
    public GameState load() {
        File file = new File(FILE);
        if (!file.exists()) return null;

        java.util.Map<String, String> data = new java.util.HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) data.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            return null;
        }

        char[][] cells = new char[GameConfig.BOARD_SIZE][GameConfig.BOARD_SIZE];
        String cellsStr = data.getOrDefault("cells", "         ");
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConfig.BOARD_SIZE; j++) {
                int idx = i * GameConfig.BOARD_SIZE + j;
                cells[i][j] = idx < cellsStr.length() ? cellsStr.charAt(idx) : ' ';
            }
        }

        List<MoveRecord> history = new ArrayList<>();
        String histStr = data.getOrDefault("history", "");
        if (!histStr.isEmpty()) {
            for (String entry : histStr.split(";")) {
                if (!entry.isEmpty()) {
                    try { history.add(MoveRecord.deserialize(entry)); }
                    catch (Exception e) { /* skip invalid */ }
                }
            }
        }

        return new GameState(
                cells,
                data.getOrDefault("currentPlayer", "X").charAt(0),
                GameConfig.Mode.valueOf(data.getOrDefault("mode", "PVP")),
                data.getOrDefault("p1Name", "Jogador 1"),
                data.getOrDefault("p2Name", "Jogador 2"),
                Integer.parseInt(data.getOrDefault("moveCount", "0")),
                history,
                Boolean.parseBoolean(data.getOrDefault("timerEnabled", "false")),
                Integer.parseInt(data.getOrDefault("timerSeconds", "15"))
        );
    }

    @Override
    public void delete() { new File(FILE).delete(); }

    @Override
    public boolean exists() { return new File(FILE).exists(); }
}
