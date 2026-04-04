package view;

import java.awt.Color;

public class Theme {

    private final String name;
    private final Color background;
    private final Color cellBackground;
    private final Color cellHover;
    private final Color xColor;
    private final Color oColor;
    private final Color winColor;
    private final Color textColor;
    private final Color borderColor;

    public Theme(String name, Color background, Color cellBackground, Color cellHover,
                 Color xColor, Color oColor, Color winColor, Color textColor, Color borderColor) {
        this.name = name;
        this.background = background;
        this.cellBackground = cellBackground;
        this.cellHover = cellHover;
        this.xColor = xColor;
        this.oColor = oColor;
        this.winColor = winColor;
        this.textColor = textColor;
        this.borderColor = borderColor;
    }

    public String getName() { return name; }
    public Color getBackground() { return background; }
    public Color getCellBackground() { return cellBackground; }
    public Color getCellHover() { return cellHover; }
    public Color getXColor() { return xColor; }
    public Color getOColor() { return oColor; }
    public Color getWinColor() { return winColor; }
    public Color getTextColor() { return textColor; }
    public Color getBorderColor() { return borderColor; }

    public static Theme classic() {
        return new Theme("Classico",
                new Color(240, 240, 240), Color.WHITE, new Color(220, 230, 255),
                new Color(41, 98, 255), new Color(213, 0, 0),
                new Color(0, 200, 83), Color.BLACK, Color.GRAY);
    }

    public static Theme dark() {
        return new Theme("Escuro",
                new Color(30, 30, 40), new Color(45, 45, 60), new Color(60, 60, 80),
                new Color(100, 149, 237), new Color(255, 99, 71),
                new Color(50, 205, 50), Color.WHITE, new Color(80, 80, 100));
    }

    public static Theme neon() {
        return new Theme("Neon",
                new Color(15, 15, 25), new Color(25, 25, 40), new Color(40, 40, 70),
                new Color(0, 200, 255), new Color(255, 50, 150),
                new Color(57, 255, 20), Color.WHITE, new Color(80, 0, 120));
    }

    public static Theme retro() {
        return new Theme("Retro",
                new Color(56, 142, 60), new Color(76, 175, 80), new Color(102, 187, 106),
                new Color(255, 255, 0), new Color(255, 152, 0),
                new Color(244, 67, 54), Color.BLACK, new Color(27, 94, 32));
    }

    public static Theme[] allThemes() {
        return new Theme[]{ classic(), dark(), neon(), retro() };
    }
}
