package gravitrips.server;

public class Move {
    private int piece;
    private int row;
    private int column;

    public Move(int piece, int row, int column) {
        this.piece = piece;
        this.row = row;
        this.column = column;
    }

    public int getPiece() {
        return piece;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
