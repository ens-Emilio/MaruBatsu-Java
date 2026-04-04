package model;

public class Scoreboard {

    private String player1Name = "Jogador 1";
    private String player2Name = "Jogador 2";
    private int player1Wins;
    private int player2Wins;
    private int draws;
    private int totalGames;

    public void setPlayers(String p1, String p2) {
        this.player1Name = p1;
        this.player2Name = p2;
    }

    public void player1Won() { player1Wins++; totalGames++; }
    public void player2Won() { player2Wins++; totalGames++; }
    public void draw() { draws++; totalGames++; }

    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public int getPlayer1Wins() { return player1Wins; }
    public int getPlayer2Wins() { return player2Wins; }
    public int getDraws() { return draws; }
    public int getTotalGames() { return totalGames; }

    public boolean hasGames() {
        return totalGames > 0;
    }

    public double getPlayer1WinRate() {
        return totalGames == 0 ? 0 : (double) player1Wins / totalGames * 100;
    }

    public double getPlayer2WinRate() {
        return totalGames == 0 ? 0 : (double) player2Wins / totalGames * 100;
    }

    public void reset() {
        player1Wins = 0;
        player2Wins = 0;
        draws = 0;
        totalGames = 0;
    }

    public void setFrom(Scoreboard other) {
        this.player1Name = other.player1Name;
        this.player2Name = other.player2Name;
        this.player1Wins = other.player1Wins;
        this.player2Wins = other.player2Wins;
        this.draws = other.draws;
        this.totalGames = other.totalGames;
    }
}
