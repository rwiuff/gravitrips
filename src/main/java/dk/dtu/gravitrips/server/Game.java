package dk.dtu.gravitrips.server;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private int[][] field;
    private int n;
    private int m;
    private List<Move> moves = new ArrayList();

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

    public void checkState() {
        Move latestMove = moves.getLast();
        int player = latestMove.getPlayer();
        int row = latestMove.getRow();
        int column = latestMove.getColumn();
        int connectFour = connected(player, row, column);
    }

    private int connected(int player, int row, int column) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connected'");
    }
}
