package dk.dtu.gravitrips.server;

import java.util.ArrayList;

public class Game {

    private int[][] field;
    private int n;
    private int m;
    private ArrayList<Move> moves = new ArrayList<Move>();

    public Game(int n, int m) {
        this.n = n+1;
        this.m = m+1;
        this.field = new int[n+1][m+1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                this.field[i][j] = 0;
            }
        }
    }

    public int[][] getField() {
        return field;
    }

    public void putPiece(int player, int column) throws Exception {
        if (column >= m) {
            throw new IndexOutOfBoundsException(column);
        } else if (field[1][column] != 0) {
            throw new Exception("Column is occupied");
        } else if (field[n - 1][column] == 0) {
            field[n - 1][column] = player;
            moves.add(new Move(player, n - 1, column));
        } else {
            for (int k = 0; k < n - 1; k++) {
                if (field[k + 1][column] != 0) {
                    field[k][column] = player;
                    moves.add(new Move(player, k, column));
                }
            }
        }
    }

    public boolean checkState() {
        Move latestMove = moves.get(moves.size() - 1);
        int player = latestMove.getPlayer();
        int row = latestMove.getRow();
        int column = latestMove.getColumn();
        boolean connect = false;
        for (int direction = 0; direction < 8; direction++) {
            connect = (connected(direction, player, row, column) >= 4) ? true : false;
            if (connect)
                return connect;
        }
        return connect;
    }

    private int connected(int direction, int player, int row, int column) {
        int points = 1;
        switch (direction) {
            case 0:
                if (field[row - 1][column - 1] == player)
                    points += connected(direction, player, row - 1, column - 1);
                break;
            case 1:
                if (field[row - 1][column] == player)
                    points += connected(direction, player, row - 1, column);
                break;
            case 2:
                if (field[row - 1][column + 1] == player)
                    points += connected(direction, player, row - 1, column + 1);
                break;
            case 3:
                if (field[row][column - 1] == player)
                    points += connected(direction, player, row, column - 1);
                break;
            case 4:
                if (field[row][column + 1] == player)
                    points += connected(direction, player, row, column + 1);
                break;
            case 5:
                if (field[row + 1][column - 1] == player)
                    points += connected(direction, player, row + 1, column - 1);
                break;
            case 6:
                if (field[row + 1][column] == player)
                    points += connected(direction, player, row + 1, column);
                break;
            case 7:
                if (field[row + 1][column + 1] == player)
                    points += connected(direction, player, row + 1, column + 1);
                break;
            default:
                break;
        }
        return points;
    }

}
