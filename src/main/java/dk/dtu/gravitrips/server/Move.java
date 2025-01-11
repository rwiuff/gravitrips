package dk.dtu.gravitrips.server;

public class Move {
    private int player;
    private int row;
    private int column;

    public Move(int player, int row, int column) {
        this.player = player;
        this.row = row;
        this.column = column;
    }

    public int getPlayer() {
        return player;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
