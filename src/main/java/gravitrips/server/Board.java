package gravitrips.server;

public class Board {
    private int rows;
    private int columns;
    private int[][] board;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new int[rows][columns];
        reset();
    }

    public void reset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = 0;
            }
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public Move dropPiece(int player, int column) throws Exception {
        if (column >= columns) {
            throw new IndexOutOfBoundsException(column);
        } else if (lookUp(0, column) != 0) {
            throw new Exception("Column occupied");
        } else if (lookUp(rows - 1, column) == 0) {
            board[rows - 1][column] = player;
            return new Move(player, rows - 1, column);
        } else {
            for (int i = 0; i < rows; i++) {
                if (lookUp(i + 1, column) != 0) {
                    board[i][column] = player;
                    return new Move(player, i, column);
                }
            }
        }
        return null;
    }

    public int lookUp(int row, int column) {
        if (row < 0 || row >= rows) {
            return 0;
        } else if (column < 0 || column >= columns) {
            return 0;
        } else {
            return board[row][column];
        }
    }
}
