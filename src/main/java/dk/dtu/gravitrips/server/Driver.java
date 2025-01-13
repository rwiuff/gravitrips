package dk.dtu.gravitrips.server;

import java.util.Arrays;

public class Driver {
    static Game game;
    private static int n = 6;
    private static int m = 6;

    public static void main(String[] args) {
        game = new Game(n, m);
        printGameState();
        try {
            game.putPiece(2, 1);
            game.putPiece(2, 2);
            game.putPiece(2, 2);
            game.putPiece(1, 0);
            game.putPiece(1, 1);
            game.putPiece(1, 2);
            game.putPiece(2, 3);
            game.putPiece(1, 3);
            game.putPiece(1, 3);
            System.out.println(game.checkState());
            printGameState();
            game.putPiece(1, 3);
            System.out.println(game.checkState());
            printGameState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printGameState() {
        int[][] field = game.getField();
        for (int[] row : field) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println("");
    }
}
