package model;

public class Player {

    private final String name;
    private final char symbol;

    public Player(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() { return name; }
    public char getSymbol() { return symbol; }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player other = (Player) obj;
        return symbol == other.symbol;
    }

    @Override
    public int hashCode() {
        return Character.hashCode(symbol);
    }
}
