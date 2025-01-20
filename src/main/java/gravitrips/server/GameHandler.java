package gravitrips.server;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import gravitrips.client.Settings;

class gameHandler implements Runnable {
    private SequentialSpace gameSpace;
    private String gameID;
    private String spaceID;

    public gameHandler(String gameID, String spaceID, String uri, SpaceRepository repository, Settings settings) throws InterruptedException {
        this.gameID = gameID;
        this.spaceID = spaceID;

        gameSpace = new SequentialSpace();

        repository.add(this.spaceID, gameSpace);
        new Thread(new ServerChatHandler(gameSpace)).start();
        gameSpace.put("Server", "Welcome to " + gameID);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object[] message = gameSpace.get(new FormalField(String.class), new FormalField(String.class));
                System.out.println(message[0] + " : " + message[1]);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}