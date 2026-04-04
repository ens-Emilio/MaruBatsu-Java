import model.GameConfig;
import view.console.ConsoleGameAdapter;
import view.swing.SwingMainMenuView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--console")) {
            runConsole();
        } else {
            runGUI();
        }
    }

    private static void runGUI() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { /* default */ }
        SwingUtilities.invokeLater(() -> new SwingMainMenuView().setVisible(true));
    }

    private static void runConsole() {
        new ConsoleGameAdapter().run();
    }
}
