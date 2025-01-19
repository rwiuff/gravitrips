package gravitrips.server;

public class Driver {
    static Game game;
    private static int n = 6;
    private static int m = 6;

    public static void main(String[] args) {
        String player1 = "Player1";
        String player2 = "Player2";
        game = new Game(n, m, player1, player2);
        printGameState();
        try {
            game.putPiece(game.getPlayerTwo(), 1);
            game.putPiece(game.getPlayerTwo(), 2);
            game.putPiece(game.getPlayerTwo(), 2);
            game.putPiece(game.getPlayerOne(), 0);
            game.putPiece(game.getPlayerOne(), 1);
            game.putPiece(game.getPlayerOne(), 2);
            game.putPiece(game.getPlayerTwo(), 3);
            game.putPiece(game.getPlayerOne(), 3);
            game.putPiece(game.getPlayerOne(), 3);
            System.out.println(game.checkState());
            printGameState();
            game.putPiece(game.getPlayerOne(), 3);
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
