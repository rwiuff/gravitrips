package gravitrips.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

class ClientChatHandler implements Runnable {

    private RemoteSpace chatroom;
    private int localInc;

    @FXML
    TextFlow messages;

    public ClientChatHandler(RemoteSpace globalChat, TextFlow messages) throws UnknownHostException, IOException {
        this.messages = messages;
        this.chatroom = globalChat;
        System.out.println("Connecting to chat space " + globalChat);
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