package persistence;

import model.MoveRecord;
import model.Scoreboard;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileScoreRepository implements ScoreRepository {

    private static final String FILE = "scoreboard.dat";

    @Override
    public void save(Scoreboard sb) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println("p1Name=" + sb.getPlayer1Name());
            pw.println("p2Name=" + sb.getPlayer2Name());
            pw.println("p1Wins=" + sb.getPlayer1Wins());
            pw.println("p2Wins=" + sb.getPlayer2Wins());
            pw.println("draws=" + sb.getDraws());
            pw.println("totalGames=" + sb.getTotalGames());
        } catch (IOException e) {
            System.err.println("Erro ao salvar placar: " + e.getMessage());
        }
    }

    @Override
    public void load(Scoreboard sb) {
        File file = new File(FILE);
        if (!file.exists()) return;

        java.util.Map<String, String> data = new java.util.HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) data.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            return;
        }

        sb.setPlayers(
                data.getOrDefault("p1Name", "Jogador 1"),
                data.getOrDefault("p2Name", "Jogador 2"));

        int p1w = Integer.parseInt(data.getOrDefault("p1Wins", "0"));
        int p2w = Integer.parseInt(data.getOrDefault("p2Wins", "0"));
        int dr = Integer.parseInt(data.getOrDefault("draws", "0"));

        for (int i = 0; i < p1w; i++) sb.player1Won();
        for (int i = 0; i < p2w; i++) sb.player2Won();
        for (int i = 0; i < dr; i++) sb.draw();
    }

    @Override
    public void delete() { new File(FILE).delete(); }

    @Override
    public boolean exists() { return new File(FILE).exists(); }
}
