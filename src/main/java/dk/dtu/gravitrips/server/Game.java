package dk.dtu.gravitrips.server;

import java.util.ArrayList;

public class Game {

    private int[][] field;
    private int n;
    private int m;
    private ArrayList<Move> moves = new ArrayList<Move>();

    public Game(int n, int m) {
        this.n = n;
        this.m = m;
        this.field = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                this.field[i][j] = 0;
            }
        }
    }

    public int[][] getField() {
        return field;
    }

    public void putPiece(int i, int j) throws Exception {
        if (j >= m) {
            throw new IndexOutOfBoundsException(j);
        } else if (field[0][j] != 0) {
            throw new Exception("Column is occupied");
        } else if (field[n - 1][j] == 0) {
            field[n - 1][j] = i;
            moves.add(new Move(i, n - 1, j));
        } else {
            for (int k = 0; k < n - 1; k++) {
                if (field[k + 1][j] != 0) {
                    field[k][j] = i;
                    moves.add(new Move(i, k, i));
                }
            }
        }
    }

    public boolean checkState() {
        Move latestMove = moves.get(moves.size() - 1);
        int player = latestMove.getPlayer();
        int row = latestMove.getRow();
        int column = latestMove.getColumn();
        if (connectedUp(player, row, column) >= 4) {
            return true;
        } else if (connectedDown(player, row, column) >= 4) {
            return true;
        } else if (connectedLeft(player, row, column) >= 4) {
            return true;
        } else if (connectedRight(player, row, column) >= 4) {
            return true;
        } else if (connectedUpLeft(player, row, column) >= 4) {
            return true;
        } else if (connectedUpRight(player, row, column) >= 4) {
            return true;
        } else if (connectedDownLeft(player, row, column) >= 4) {
            return true;
        } else if (connectedDownRight(player, row, column) >= 4) {
            return true;
        } else {
            return false;
        }
    }

    private int connectedDownRight(int player, int row, int column) {
        if (field[row + 1][column+1] == player) {
            return 1 + connectedDownRight(player, row - 1, column);
        } else {
            return 1;
        }
    }

    private int connectedDownLeft(int player, int row, int column) {
        if (field[row + 1][column-1] == player) {
            return 1 + connectedDownLeft(player, row - 1, column);
        } else {
            return 1;
        }
    }

    private int connectedUpRight(int player, int row, int column) {
        if (field[row - 1][column+1] == player) {
            return 1 + connectedUpRight(player, row - 1, column);
        } else {
            return 1;
        }
    }

    private int connectedUpLeft(int player, int row, int column) {
        if (field[row-1][column-1] == player) {
            return 1 + connectedUpLeft(player, row - 1, column);
        } else {
            return 1;
        }
    }

    private int connectedRight(int player, int row, int column) {
        if (field[row][column+1] == player) {
            return 1 + connectedRight(player, row - 1, column);
        } else {
            return 1;
        }
    }

    private int connectedLeft(int player, int row, int column) {
        if (field[row][column-1] == player) {
            return 1 + connectedLeft(player, row - 1, column);
        } else {
            return 1;
        }
    }

    private int connectedDown(int player, int row, int column) {
        if (field[row + 1][column] == player) {
            return 1 + connectedDown(player, row - 1, column);
        } else {
            return 1;
        }
    }

    private int connectedUp(int player, int row, int column) {
        if (field[row - 1][column] == player) {
            return 1 + connectedUp(player, row - 1, column);
        } else {
            return 1;
        }
    }

}
