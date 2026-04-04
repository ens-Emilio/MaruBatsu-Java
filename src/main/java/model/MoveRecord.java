package model;

public class MoveRecord {

    private final int position;
    private final char playerSymbol;
    private final int moveNumber;

    public MoveRecord(int position, char playerSymbol, int moveNumber) {
        this.position = position;
        this.playerSymbol = playerSymbol;
        this.moveNumber = moveNumber;
    }

    public int getPosition() { return position; }
    public char getPlayerSymbol() { return playerSymbol; }
    public int getMoveNumber() { return moveNumber; }

    public int getRow() { return (position - 1) / GameConfig.BOARD_SIZE; }
    public int getCol() { return (position - 1) % GameConfig.BOARD_SIZE; }

    @Override
    public String toString() {
        return "#" + moveNumber + " " + playerSymbol + " -> " + position;
    }

    public String serialize() {
        return position + "," + playerSymbol + "," + moveNumber;
    }

    public static MoveRecord deserialize(String data) {
        String[] parts = data.split(",");
        return new MoveRecord(
                Integer.parseInt(parts[0]),
                parts[1].charAt(0),
                Integer.parseInt(parts[2]));
    }
}
