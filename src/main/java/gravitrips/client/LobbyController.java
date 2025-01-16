package gravitrips.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LobbyController {

    @FXML
    TextField lobbymessage;

    @FXML
    TextFlow messages;

    private String host;
    private String port;
    private Settings settings;
    private String userName;

    private RemoteSpace lobby;

    private RemoteSpace globalChat;

    public void setup(Settings settings) throws InterruptedException {
        this.settings = settings;
        this.host = settings.getHost();
        this.port = settings.getPort();
        this.userName = settings.getUserName();
        openLobby();
    }

    private void openLobby() throws InterruptedException {
        try {
            String uri = "tcp://" + host + ":" + port + "/lobby?keep";
            System.out.println("Connecting to lobby " + uri + "...");
            this.lobby = new RemoteSpace(uri);
            String chatUri = "tcp://" + host + ":" + port + "/global_chat?keep";
            this.globalChat = new RemoteSpace(chatUri);
            Thread chatThread = new Thread(new chatHandler(chatUri, messages));
            globalChat.put(userName, "Joined the chat");
            chatThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void create(ActionEvent event) {
        System.out.println("create");
    }

    @FXML
    private void join(ActionEvent event) {
        System.out.println("join");
    }

    @FXML
    private void send(ActionEvent event) throws InterruptedException {
        String message = lobbymessage.getText();
        lobbymessage.clear();
        globalChat.put(userName, message);
    }

}

class chatHandler implements Runnable {

    private RemoteSpace chatroom;
    private int localInc;

    @FXML
    TextFlow messages;

    public chatHandler(String chatUri, TextFlow messages) throws UnknownHostException, IOException {
        this.messages = messages;
        this.chatroom = new RemoteSpace(chatUri);
        System.out.println("Connecting to chat space " + chatUri);
        this.localInc = 0;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int inc = (int) chatroom.queryp(new FormalField(Integer.class))[0];
                if (inc > localInc) {
                    List<Object[]> history = chatroom.queryAll(new FormalField(Integer.class),
                            new FormalField(String.class), new FormalField(String.class));
                    for (int i = localInc; i < inc; i++) {
                        Object[] message = history.get(i);
                        Text user = new Text();
                        user.setText(message[1] + ": ");
                        user.setStyle("-fx-font-weight: bold");
                        Text print = new Text();
                        print.setText(message[2] + "\n");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                messages.getChildren().add(user);
                                messages.getChildren().add(print);
                            }
                        });
                    }
                    localInc = inc;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}