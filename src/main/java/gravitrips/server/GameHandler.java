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
            game = new Game(rows, columns, players.get(0), players.get(1));
            playerOneChannel.put("setup", rows, columns, 1);
            playerTwoChannel.put("setup", rows, columns, 2);
            sendBoard();
            while (true) {
                playerOneChannel.get(new ActualField("status"), new ActualField(players.get(0)),
                        new ActualField("ready"));
                playerTwoChannel.get(new ActualField("status"), new ActualField(players.get(1)),
                        new ActualField("ready"));
                int playerTurn = 1;
                while (game.checkState() == false) {
                    if (checkplayerTurn(playerTurn) == 1) {
                        playerOneChannel.put("status", "turn");
                        playerTwoChannel.put("status", "wait");
                        int column = (int) playerOneChannel.get(new FormalField(Integer.class))[0];
                        while (validMove(playerTurn % 2, column) == false) {
                            playerOneChannel.put("status", "invalid");
                            column = (int) playerOneChannel.get(new FormalField(Integer.class))[0];
                        }
                    } else if (checkplayerTurn(playerTurn) == 0) {
                        playerOneChannel.put("status", "wait");
                        playerTwoChannel.put("status", "turn");
                        int column = (int) playerTwoChannel.get(new FormalField(Integer.class))[0];
                        while (validMove(playerTurn % 2, column) == false) {
                            playerTwoChannel.put("status", "invalid");
                            column = (int) playerTwoChannel.get(new FormalField(Integer.class))[0];
                        }
                    }
                    sendBoard();
                }
                playerOneChannel.put("status", "winner");
                playerOneChannel.put("winner", game.getLastMove().getPiece());
                playerTwoChannel.put("status", "winner");
                playerTwoChannel.put("winner", game.getLastMove().getPiece());
            }
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

    private int checkplayerTurn(int playerTurn) {
        return playerTurn % 2;
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