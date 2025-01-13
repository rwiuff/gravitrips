package dk.dtu.gravitrips.server;

import java.util.ArrayList;

public class Game {

    private Field field;
    private ArrayList<Move> moves = new ArrayList<Move>();

    public Game(int rows, int columns) {
        this.field = new Field(rows, columns);
    }

    public int[][] getField() {
        return field.getField();
    }

    public void putPiece(int player, int column) throws Exception {
        Move lastMove = field.dropPiece(player, column);
        moves.add(lastMove);
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
                if (field.lookUp(row - 1, column - 1) == player)
                    points += connected(direction, player, row - 1, column - 1);
                break;
            case 1:
                if (field.lookUp(row - 1, column) == player)
                    points += connected(direction, player, row - 1, column);
                break;
            case 2:
                if (field.lookUp(row - 1, column + 1) == player)
                    points += connected(direction, player, row - 1, column + 1);
                break;
            case 3:
                if (field.lookUp(row, column - 1) == player)
                    points += connected(direction, player, row, column - 1);
                break;
            case 4:
                if (field.lookUp(row, column + 1) == player)
                    points += connected(direction, player, row, column + 1);
                break;
            case 5:
                if (field.lookUp(row + 1, column - 1) == player)
                    points += connected(direction, player, row + 1, column - 1);
                break;
            case 6:
                if (field.lookUp(row + 1, column) == player)
                    points += connected(direction, player, row + 1, column);
                break;
            case 7:
                if (field.lookUp(row + 1, column + 1) == player)
                    points += connected(direction, player, row + 1, column + 1);
                break;
            default:
                break;
        }
        return points;
    }

    public Move getLastMove() {
        return moves.get(moves.size() - 1);
    }

}
