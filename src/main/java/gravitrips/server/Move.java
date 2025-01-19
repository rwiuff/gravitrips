package gravitrips.server;

public class Move {
    private Piece piece;
    private int row;
    private int column;

    public Move(Piece piece, int row, int column) {
        this.piece = piece;
        this.row = row;
        this.column = column;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
