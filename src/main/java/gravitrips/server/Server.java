package gravitrips.server;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import gravitrips.client.Settings;

public class Server implements Runnable {
    private String host;
    private String port;
    private int rows;
    private int columns;
    private SequentialSpace lobby;
    private SequentialSpace games;
    private SequentialSpace globalChat;
    private SpaceRepository repository;

    public Server(Settings settings) throws InterruptedException {
        this.host = settings.getHost();
        this.port = settings.getPort();
        this.rows = settings.getRows();
        this.columns = settings.getColumns();
        this.repository = new SpaceRepository();
        this.lobby = new SequentialSpace();
        repository.add("lobby", lobby);
        String uri = "tcp://" + host + ":" + port + "/lobby?keep";
        repository.addGate(uri);
        System.out.println("Opening repository gate at " + uri + "...");
        this.games = new SequentialSpace();
        this.globalChat = new SequentialSpace();
        repository.add("global_chat", globalChat);
        new Thread(new ServerChatHandler(globalChat)).start();
    }

    @Override
    public void run() {
        while (true) {
            Integer gameC = 0;

            String gameURI;

            while (true) {
                Object[] request;
                try {
                    request = lobby.get(new ActualField("enter"), new FormalField(String.class),
                            new FormalField(String.class));
                    String userName = (String) request[1];
                    String gameID = (String) request[2];

                    System.out.println(userName + " requesting to enter " + gameID + "...");

                    Object[] game = games.queryp(new ActualField(gameID), new FormalField(Integer.class));

                    if (game != null) {
                        gameURI = "tcp://" + host + ":" + port + "/game" + game[1] + "?keep";
                    } else {
                        System.out.println("Creating game " + gameID + " for " + userName + "...");
                        gameURI = "tcp://" + host + ":" + port + "/game" + gameC + "?keep";
                        System.out.println("Setting up game " + gameURI + "...");
                        new Thread(new gameHandler(gameID, "game" + gameC, gameURI, repository)).start();
                        games.put(gameID, gameC);
                        gameC++;
                    }
                    System.out.println("Telling " + userName + " to go for room " + gameID + " at " + gameURI + "...");
                    lobby.put("gameURI", userName, gameID, gameURI);
                    lobby.put("games", userName, gameID, gameURI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class gameHandler implements Runnable {
    private SequentialSpace game;
    private String gameID;
    private String spaceID;

    public gameHandler(String gameID, String spaceID, String uri, SpaceRepository repository) throws InterruptedException {
        this.gameID = gameID;
        this.spaceID = spaceID;

        game = new SequentialSpace();

        repository.add(this.spaceID, game);
        new Thread(new ServerChatHandler(game)).start();;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object[] message = game.get(new FormalField(String.class), new FormalField(String.class));
                System.out.println(message[0] + " : " + message[1]);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}