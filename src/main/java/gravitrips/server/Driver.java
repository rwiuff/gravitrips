package gravitrips.server;

import java.util.Arrays;

import javafx.scene.paint.Color;

public class Driver {
    static Game game;
    private static int n = 6;
    private static int m = 6;

    public static void main(String[] args) {
        Piece player1 = new Piece("Player1", Color.BEIGE);
        Piece player2 = new Piece("Player2", Color.ALICEBLUE);
        game = new Game(n, m);
        printGameState();
        try {
            game.putPiece(player2, 1);
            game.putPiece(player2, 2);
            game.putPiece(player2, 2);
            game.putPiece(player1, 0);
            game.putPiece(player1, 1);
            game.putPiece(player1, 2);
            game.putPiece(player2, 3);
            game.putPiece(player1, 3);
            game.putPiece(player1, 3);
            System.out.println(game.checkState());
            printGameState();
            game.putPiece(player1, 3);
            System.out.println(game.checkState());
            printGameState();
            System.out.println("Won by player " + game.getLastMove().getPiece().getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printGameState() {
        Piece[][] field = game.getBoard();
        for (Piece[] row : field) {
            for(Piece piece : row){
                System.out.print(piece.getPlayer());
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
