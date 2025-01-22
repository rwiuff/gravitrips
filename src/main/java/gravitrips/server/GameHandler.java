package gravitrips.server;

import java.util.ArrayList;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import com.google.gson.Gson;

import gravitrips.client.Settings;

class gameHandler implements Runnable {
    private SequentialSpace gameSpace;
    private String gameID;
    private String spaceID;
    private SequentialSpace playerOneChannel;
    private SequentialSpace playerTwoChannel;
    private ArrayList<String> players = new ArrayList<String>();
    private Game game;
    private int rows;
    private int columns;
    private Gson gson = new Gson();

    public gameHandler(String gameID, String spaceID, String uri, SpaceRepository repository, Settings settings)
            throws InterruptedException {
        this.gameID = gameID;
        this.spaceID = spaceID;
        this.rows = settings.getRows();
        this.columns = settings.getColumns();

        gameSpace = new SequentialSpace();
        playerOneChannel = new SequentialSpace();
        playerTwoChannel = new SequentialSpace();

        repository.add(this.spaceID, gameSpace);
        repository.add(this.gameID + "player1", playerOneChannel);
        repository.add(this.gameID + "player2", playerTwoChannel);
        new Thread(new ServerChatHandler(gameSpace)).start();
    }

    @Override
    public void run() {
        try {
            Object[] join1Message = gameSpace.get(new ActualField("channel"), new ActualField("request"),
                    new FormalField(String.class));
            Object[] join2Message = gameSpace.get(new ActualField("channel"), new ActualField("request"),
                    new FormalField(String.class));
            gameSpace.put("channel", "response", (String) join1Message[2], this.gameID + "player1");
            gameSpace.put("channel", "response", (String) join2Message[2], this.gameID + "player2");
            players.add((String) join1Message[2]);
            players.add((String) join2Message[2]);
            game = new Game(rows, columns);
            playerOneChannel.put("setup", rows, columns, 1);
            playerTwoChannel.put("setup", rows, columns, 2);
            sendBoard();
            playerOneChannel.get(new ActualField("status"), new ActualField(players.get(0)), new ActualField("ready"));
            playerTwoChannel.get(new ActualField("status"), new ActualField(players.get(1)), new ActualField("ready"));
            gameSpace.put("server", players.get(0) + " starts");
            playerOneChannel.put("begin");
            playerTwoChannel.put("begin");
            int playerTurn = 1;
            while (game.checkState() == false) {
                playerOneChannel.put("continue");
                playerTwoChannel.put("continue");
                if (checkPlayerTurn(playerTurn)) {
                    playerOneChannel.put("turn", 1);
                    playerTwoChannel.put("turn", 1);
                    int column = (int) playerOneChannel.get(new FormalField(Integer.class))[0];
                    while (validMove(1, column) == false) {
                        playerOneChannel.put("continue");
                        column = (int) playerOneChannel.get(new FormalField(Integer.class))[0];
                    }
                    playerOneChannel.put("break");
                } else {
                    playerOneChannel.put("turn", 2);
                    playerTwoChannel.put("turn", 2);
                    int column = (int) playerTwoChannel.get(new FormalField(Integer.class))[0];
                    while (validMove(2, column) == false) {
                        playerTwoChannel.put("continue");
                        column = (int) playerTwoChannel.get(new FormalField(Integer.class))[0];
                    }
                    playerTwoChannel.put("break");
                }
                sendBoard();
                playerTurn++;
                gameSpace.put("server", players.get(playerTurn % 2) + "'s turn");
            }
            playerOneChannel.put("break");
            playerTwoChannel.put("break");
            playerOneChannel.put(game.getLastMove().getPiece());
            playerTwoChannel.put(game.getLastMove().getPiece());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean validMove(int player, int column) {
        try {
            game.putPiece(player, column);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkPlayerTurn(int playerTurn) {
        return playerTurn % 2 == 1;
    }

    private void sendBoard() {
        String send = gson.toJson(game.getBoard());
        try {
            playerOneChannel.put("board", send);
            playerTwoChannel.put("board", send);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}