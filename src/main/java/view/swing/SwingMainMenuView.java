package view.swing;

import controller.GameController;
import controller.GameEventListener;
import model.*;
import persistence.FileGameStateRepository;
import persistence.FileScoreRepository;
import view.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SwingMainMenuView extends JFrame {

    private final Scoreboard scoreboard;
    private final FileScoreRepository scoreRepo;
    private final FileGameStateRepository gameStateRepo;
    private Theme currentTheme;

    public SwingMainMenuView() {
        this.scoreboard = new Scoreboard();
        this.scoreRepo = new FileScoreRepository();
        this.gameStateRepo = new FileGameStateRepository();
        this.currentTheme = Theme.classic();
        scoreRepo.load(scoreboard);
        initUI();
    }

    private void initUI() {
        setTitle("Jogo da Velha");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        showMenuPanel();
        pack();
        setLocationRelativeTo(null);
    }

    private void showMenuPanel() {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panel.setBackground(currentTheme.getBackground());

        JLabel title = new JLabel("JOGO DA VELHA", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(currentTheme.getXColor());
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        if (gameStateRepo.exists()) {
            centerPanel.add(makeButton("Continuar Partida", e -> continueGame()), gbc);
            centerPanel.add(Box.createVerticalStrut(10), gbc);
        }

        centerPanel.add(makeButton("Jogador vs Jogador", e -> startGame(GameConfig.Mode.PVP)), gbc);
        centerPanel.add(makeButton("vs IA Facil", e -> startGame(GameConfig.Mode.PV_AI_EASY)), gbc);
        centerPanel.add(makeButton("vs IA Media", e -> startGame(GameConfig.Mode.PV_AI_MEDIUM)), gbc);
        centerPanel.add(makeButton("vs IA Dificil", e -> startGame(GameConfig.Mode.PV_AI_HARD)), gbc);
        centerPanel.add(Box.createVerticalStrut(10), gbc);
        centerPanel.add(makeButton("Tema: " + currentTheme.getName(), e -> cycleTheme()), gbc);
        centerPanel.add(makeButton("Placar e Ranking", e -> showRanking()), gbc);
        centerPanel.add(makeButton("Sair", e -> exit()), gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        if (scoreboard.hasGames()) {
            JLabel scoreLabel = new JLabel(scoreSummary(), SwingConstants.CENTER);
            scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            scoreLabel.setForeground(currentTheme.getTextColor());
            scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            panel.add(scoreLabel, BorderLayout.SOUTH);
        }

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JButton makeButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBackground(currentTheme.getCellBackground());
        btn.setForeground(currentTheme.getTextColor());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(currentTheme.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addActionListener(action);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(currentTheme.getCellHover()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(currentTheme.getCellBackground()); }
        });
        return btn;
    }

    private void startGame(GameConfig.Mode mode) {
        String p1 = JOptionPane.showInputDialog(this, "Nome do Jogador 1 (X):", "Jogador 1");
        if (p1 == null) return;
        if (p1.trim().isEmpty()) p1 = "Jogador 1";

        String p2;
        if (mode == GameConfig.Mode.PVP) {
            p2 = JOptionPane.showInputDialog(this, "Nome do Jogador 2 (O):", "Jogador 2");
            if (p2 == null) return;
            if (p2.trim().isEmpty()) p2 = "Jogador 2";
        } else {
            p2 = mode == GameConfig.Mode.PV_AI_EASY ? "IA Facil"
                    : mode == GameConfig.Mode.PV_AI_MEDIUM ? "IA Media" : "IA Dificil";
        }

        int timerOpt = JOptionPane.showConfirmDialog(this,
                "Ativar timer por jogada (" + GameConfig.DEFAULT_TIMER_SECONDS + "s)?",
                "Timer", JOptionPane.YES_NO_OPTION);

        GameConfig config = new GameConfig.Builder()
                .mode(mode).player1Name(p1.trim()).player2Name(p2.trim())
                .timerEnabled(timerOpt == JOptionPane.YES_OPTION).build();

        openGameView(config);
    }

    private void continueGame() {
        GameState state = gameStateRepo.load();
        if (state == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma partida salva encontrada.");
            return;
        }

        GameConfig config = new GameConfig.Builder()
                .mode(state.getMode())
                .player1Name(state.getPlayer1Name())
                .player2Name(state.getPlayer2Name())
                .timerEnabled(state.isTimerEnabled())
                .timerSeconds(state.getTimerSeconds())
                .build();

        openGameView(config, state);
    }

    private void openGameView(GameConfig config) {
        openGameView(config, null);
    }

    private void openGameView(GameConfig config, GameState state) {
        SwingGameView gameView = new SwingGameView(this, config, scoreboard, currentTheme, gameStateRepo);
        gameView.setVisible(true);
        if (state != null) {
            gameView.resumeGame(state);
        }
        this.setVisible(false);
    }

    private void cycleTheme() {
        Theme[] themes = Theme.allThemes();
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].getName().equals(currentTheme.getName())) {
                currentTheme = themes[(i + 1) % themes.length];
                break;
            }
        }
        showMenuPanel();
    }

    private void showRanking() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><h2>Placar</h2>");
        sb.append(String.format("<b>%s (X)</b>: %d vitoria(s) (%.1f%%)<br>",
                scoreboard.getPlayer1Name(), scoreboard.getPlayer1Wins(), scoreboard.getPlayer1WinRate()));
        sb.append(String.format("<b>%s (O)</b>: %d vitoria(s) (%.1f%%)<br>",
                scoreboard.getPlayer2Name(), scoreboard.getPlayer2Wins(), scoreboard.getPlayer2WinRate()));
        sb.append(String.format("<b>Empates</b>: %d<br>", scoreboard.getDraws()));
        sb.append(String.format("<b>Total de jogos</b>: %d", scoreboard.getTotalGames()));
        sb.append("</html>");

        Object[] options = {"OK", "Resetar Placar"};
        int choice = JOptionPane.showOptionDialog(this, sb.toString(), "Placar e Ranking",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 1) {
            scoreboard.reset();
            scoreRepo.delete();
            showMenuPanel();
        }
    }

    private void exit() {
        scoreRepo.save(scoreboard);
        System.exit(0);
    }

    public void returnToMenu() {
        showMenuPanel();
        this.setVisible(true);
    }

    private String scoreSummary() {
        return String.format("%s %d x %d %s | Emp: %d | Jogos: %d",
                scoreboard.getPlayer1Name(), scoreboard.getPlayer1Wins(),
                scoreboard.getPlayer2Wins(), scoreboard.getPlayer2Name(),
                scoreboard.getDraws(), scoreboard.getTotalGames());
    }

    public FileScoreRepository getScoreRepo() { return scoreRepo; }
    public FileGameStateRepository getGameStateRepo() { return gameStateRepo; }
}
