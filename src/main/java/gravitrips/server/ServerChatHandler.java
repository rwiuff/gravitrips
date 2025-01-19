package gravitrips.server;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;

public class ServerChatHandler implements Runnable {
    private SequentialSpace globalChat;
    private int inc;

    public ServerChatHandler(SequentialSpace globalChat) throws InterruptedException {
        this.globalChat = globalChat;
        globalChat.put(0);
        this.inc = 0;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] message = globalChat.get(new FormalField(String.class), new FormalField(String.class));
                inc++;
                globalChat.put(inc, message[0], message[1]);
                globalChat.put(inc);
                globalChat.get(new ActualField(inc - 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}