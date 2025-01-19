package gravitrips.server;

import javafx.scene.paint.Color;

public class Board {
    private int rows;
    private int columns;
    private Piece[][] board;
    private Piece empty;
    private Piece player1;
    private Piece player2;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new Piece[rows][columns];
        this.empty = new Piece("empty", Color.rgb(237, 28, 36));
        reset();
    }

    public Piece getPlayer1() {
        return player1;
    }

    public Piece getPlayer2() {
        return player2;
    }

    public Piece getEmpty() {
        return empty;
    }

    public void reset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = empty;
            }
        }
    }

    public Piece[][] getBoard() {
        return board;
    }

    public boolean columnFull(int column) {
        return (board[0][column] != null);
    }

    public Move dropPiece(Piece player, int column) throws Exception {
        if (column >= columns) {
            throw new IndexOutOfBoundsException(column);
        } else if (lookUp(0, column) != empty) {
            throw new Exception("Column occupied");
        } else if (lookUp(rows - 1, column) == empty) {
            board[rows - 1][column] = player;
            return new Move(player, rows - 1, column);
        } else {
            for (int i = 0; i < rows; i++) {
                if (lookUp(i + 1, column) != empty) {
                    board[i][column] = player;
                    return new Move(player, i, column);
                }
            }
        }
        return null;
    }

    public Piece lookUp(int row, int column) {
        if (row < 0 || row >= rows) {
            return null;
        } else if (column < 0 || column >= columns) {
            return null;
        } else {
            return board[row][column];
        }
    }
}
