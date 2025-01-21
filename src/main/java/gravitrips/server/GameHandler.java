package gravitrips.server;

import java.util.HashMap;
import java.util.Map;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import gravitrips.client.Settings;

class gameHandler implements Runnable {
    private SequentialSpace gameSpace;
    private String gameID;
    private String spaceID;
    private Map<String, String> players = new HashMap<String, String>();
    private SequentialSpace playerOneChannel;
    private SequentialSpace playerTwoChannel;

    public gameHandler(String gameID, String spaceID, String uri, SpaceRepository repository, Settings settings)
            throws InterruptedException {
        this.gameID = gameID;
        this.spaceID = spaceID;

        gameSpace = new SequentialSpace();
        playerOneChannel = new SequentialSpace();
        playerTwoChannel = new SequentialSpace();

        repository.add(this.spaceID, gameSpace);
        repository.add(this.gameID + "playerOne", playerOneChannel);
        repository.add(this.gameID + "playerTwo", playerOneChannel);
        new Thread(new ServerChatHandler(gameSpace)).start();
        gameSpace.put("Server", "Welcome to " + gameID);
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 2; i++) {
                Object[] joinMessage = gameSpace.get(new ActualField("joining"), new FormalField(String.class));
                players.put((String) joinMessage[0], (String) joinMessage[1]);
                // String playerURI = "tcp://" + host + ":" + port + "/game" + gameC + "?keep";
                gameSpace.put("player" + (String) joinMessage[0], playerOneChannel);
            }
            while (true) {

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}