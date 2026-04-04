package view.swing;

import controller.GameController;
import controller.GameEventListener;
import model.*;
import persistence.FileGameStateRepository;
import view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SwingGameView extends JFrame implements GameEventListener {

    private final SwingMainMenuView mainMenu;
    private final GameController controller;
    private final Theme theme;
    private final GameConfig config;
    private final FileGameStateRepository gameStateRepo;

    private JButton[][] cellButtons;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JButton undoButton;
    private JButton redoButton;
    private JButton forfeitButton;
    private JButton saveButton;
    private JList<String> historyList;
    private DefaultListModel<String> historyModel;

    public SwingGameView(SwingMainMenuView mainMenu, GameConfig config,
                         Scoreboard scoreboard, Theme theme,
                         FileGameStateRepository gameStateRepo) {
        this.mainMenu = mainMenu;
        this.config = config;
        this.theme = theme;
        this.gameStateRepo = gameStateRepo;
        this.controller = new GameController(config, scoreboard, this, gameStateRepo);

        setTitle("Jogo da Velha - " + config.getPlayer1Name() + " vs " + config.getPlayer2Name());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) { backToMenu(); }
        });

        initUI();
        controller.startNewGame();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(theme.getBackground());

        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createBoardPanel(), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(createStatusPanel(), BorderLayout.NORTH);
        southPanel.add(createButtonPanel(), BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        mainPanel.add(createHistoryPanel(), BorderLayout.EAST);

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        scoreLabel = new JLabel("", SwingConstants.LEFT);
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        scoreLabel.setForeground(theme.getTextColor());
        updateScoreDisplay();
        top.add(scoreLabel, BorderLayout.WEST);

        if (config.isTimerEnabled()) {
            timerLabel = new JLabel("", SwingConstants.RIGHT);
            timerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
            timerLabel.setForeground(theme.getXColor());
            top.add(timerLabel, BorderLayout.EAST);
        }
        return top;
    }

    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(GameConfig.BOARD_SIZE, GameConfig.BOARD_SIZE, 4, 4));
        boardPanel.setOpaque(false);
        cellButtons = new JButton[GameConfig.BOARD_SIZE][GameConfig.BOARD_SIZE];

        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConfig.BOARD_SIZE; j++) {
                JButton btn = new JButton("");
                btn.setFont(new Font("SansSerif", Font.BOLD, 48));
                btn.setFocusPainted(false);
                btn.setBackground(theme.getCellBackground());
                btn.setForeground(theme.getTextColor());
                btn.setBorder(BorderFactory.createLineBorder(theme.getBorderColor(), 2));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setOpaque(true);
                btn.setPreferredSize(new Dimension(110, 110));

                final int row = i, col = j;
                btn.addActionListener((ActionEvent e) -> {
                    controller.makeMove(row * GameConfig.BOARD_SIZE + col + 1);
                });

                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        if (btn.getText().isEmpty() && controller.isGameActive())
                            btn.setBackground(theme.getCellHover());
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (btn.getText().isEmpty())
                            btn.setBackground(theme.getCellBackground());
                    }
                });

                cellButtons[i][j] = btn;
                boardPanel.add(btn);
            }
        }
        return boardPanel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        statusLabel = new JLabel("Vez de " + config.getPlayer1Name(), SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(theme.getXColor());
        statusLabel.setBorder(new EmptyBorder(8, 0, 5, 0));
        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        panel.setOpaque(false);

        undoButton = makeControlButton("Desfazer", e -> controller.undoMove());
        redoButton = makeControlButton("Refazer", e -> controller.redoMove());
        saveButton = makeControlButton("Salvar", e -> controller.saveGame());
        forfeitButton = makeControlButton("Desistir", e -> {
            int c = JOptionPane.showConfirmDialog(this,
                    controller.getCurrentPlayer().getName() + " deseja desistir?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) controller.forfeit();
        });
        JButton menuButton = makeControlButton("Menu", e -> backToMenu());

        panel.add(undoButton);
        panel.add(redoButton);
        panel.add(saveButton);
        panel.add(forfeitButton);
        panel.add(menuButton);
        return panel;
    }

    private JButton makeControlButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBackground(theme.getCellBackground());
        btn.setForeground(theme.getTextColor());
        btn.addActionListener(action);
        return btn;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(150, 0));

        JLabel histTitle = new JLabel("Historico", SwingConstants.CENTER);
        histTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        histTitle.setForeground(theme.getTextColor());
        panel.add(histTitle, BorderLayout.NORTH);

        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setFont(new Font("Monospaced", Font.PLAIN, 11));
        historyList.setBackground(theme.getCellBackground());
        historyList.setForeground(theme.getTextColor());
        JScrollPane scroll = new JScrollPane(historyList);
        scroll.setPreferredSize(new Dimension(140, 300));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // --- GameEventListener implementation ---

    @Override
    public void onBoardUpdated(Board board, int[] highlightPositions) {
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConfig.BOARD_SIZE; j++) {
                char symbol = board.getCell(i, j);
                JButton btn = cellButtons[i][j];
                int pos = i * GameConfig.BOARD_SIZE + j + 1;

                if (symbol == GameConfig.EMPTY_CELL) {
                    btn.setText("");
                    btn.setForeground(theme.getTextColor());
                    btn.setBackground(theme.getCellBackground());
                    btn.setFont(btn.getFont().deriveFont(Font.BOLD, 48f));
                } else {
                    btn.setText(String.valueOf(symbol));
                    btn.setFont(btn.getFont().deriveFont(Font.BOLD, 48f));
                    boolean hl = highlightPositions != null && contains(highlightPositions, pos);
                    if (hl) {
                        btn.setBackground(theme.getWinColor());
                        btn.setForeground(Color.WHITE);
                    } else {
                        btn.setBackground(theme.getCellBackground());
                        btn.setForeground(symbol == GameConfig.PLAYER_X_SYMBOL
                                ? theme.getXColor() : theme.getOColor());
                    }
                }
            }
        }
    }

    @Override
    public void onPlayerSwitched(Player currentPlayer) {
        statusLabel.setText("Vez de " + currentPlayer.getName());
        statusLabel.setForeground(currentPlayer.getSymbol() == GameConfig.PLAYER_X_SYMBOL
                ? theme.getXColor() : theme.getOColor());
        undoButton.setEnabled(controller.canUndo());
        redoButton.setEnabled(controller.canRedo());

        boolean aiTurn = controller.isAITurn();
        for (int i = 0; i < GameConfig.BOARD_SIZE; i++)
            for (int j = 0; j < GameConfig.BOARD_SIZE; j++)
                cellButtons[i][j].setEnabled(!aiTurn);
        if (aiTurn) statusLabel.setText(currentPlayer.getName() + " esta pensando...");
    }

    @Override
    public void onGameOver(Player winner, int[] winningPositions) {
        statusLabel.setText(winner.getName() + " venceu!");
        statusLabel.setForeground(theme.getWinColor());
        disableBoard();
        updateScoreDisplay();
        showPlayAgainDialog(winner.getName() + " venceu!");
    }

    @Override
    public void onDraw() {
        statusLabel.setText("Deu velha! Empate!");
        statusLabel.setForeground(theme.getOColor());
        disableBoard();
        updateScoreDisplay();
        showPlayAgainDialog("Deu velha! Empate!");
    }

    @Override
    public void onInvalidMove(String reason) {
        statusLabel.setText(reason);
        java.util.Timer t = new java.util.Timer("Flash", true);
        t.schedule(new java.util.TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (controller.isGameActive())
                        statusLabel.setText("Vez de " + controller.getCurrentPlayer().getName());
                });
            }
        }, 1500);
    }

    @Override public void onTimerTick(int secondsLeft) {
        if (timerLabel != null) {
            timerLabel.setText(secondsLeft + "s");
            timerLabel.setForeground(secondsLeft <= 5 ? theme.getOColor() : theme.getXColor());
        }
    }

    @Override public void onMoveUndone(Board board, Player currentPlayer) {
        onBoardUpdated(board, null);
        onPlayerSwitched(currentPlayer);
        if (!historyModel.isEmpty()) historyModel.removeElementAt(historyModel.size() - 1);
    }

    @Override public void onMoveRedone(Board board, Player currentPlayer) {
        onBoardUpdated(board, null);
        onPlayerSwitched(currentPlayer);
        List<MoveRecord> hist = board.getMoveHistory();
        if (!hist.isEmpty()) historyModel.addElement(hist.get(hist.size() - 1).toString());
    }

    @Override public void onMoveMade(int position, Player player) {
        historyModel.addElement("#" + board().getMoveCount() + " " + player.getSymbol() + " -> " + position);
        historyList.ensureIndexIsVisible(historyModel.size() - 1);
    }

    @Override public void onScoreUpdated(Scoreboard scoreboard) { updateScoreDisplay(); }
    @Override public void onGameSaved() { JOptionPane.showMessageDialog(this, "Jogo salvo com sucesso!"); }
    @Override public void onGameLoaded(GameState gameState) { /* handled in resume */ }

    public void resumeGame(GameState state) { controller.resumeGame(state); }

    private Board board() { return controller.getBoard(); }

    private void showPlayAgainDialog(String message) {
        int choice = JOptionPane.showConfirmDialog(this,
                message + "\nJogar novamente?", "Fim de Jogo", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            historyModel.clear();
            controller.startNewGame();
            enableBoard();
        } else {
            backToMenu();
        }
    }

    private void backToMenu() {
        mainMenu.getScoreRepo().save(controller.getScoreboard());
        mainMenu.returnToMenu();
        this.dispose();
    }

    private void disableBoard() {
        for (JButton[] row : cellButtons) for (JButton b : row) b.setEnabled(false);
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        saveButton.setEnabled(false);
        forfeitButton.setEnabled(false);
    }

    private void enableBoard() {
        for (JButton[] row : cellButtons) for (JButton b : row) b.setEnabled(true);
        saveButton.setEnabled(true);
        forfeitButton.setEnabled(true);
    }

    private void updateScoreDisplay() {
        Scoreboard sb = controller.getScoreboard();
        scoreLabel.setText(String.format("%s %d x %d %s | Emp: %d",
                sb.getPlayer1Name(), sb.getPlayer1Wins(),
                sb.getPlayer2Wins(), sb.getPlayer2Name(), sb.getDraws()));
    }

    private boolean contains(int[] arr, int val) {
        for (int v : arr) if (v == val) return true;
        return false;
    }
}
