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
            while (true) {
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
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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