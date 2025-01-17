package gravitrips.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class LobbyController {

    @FXML
    TextField lobbymessage;

    @FXML
    TextFlow messages;

    @FXML
    TreeView<String> tree;

    private String host;
    private String port;
    private Settings settings;
    private String userName;

    private RemoteSpace lobby;

    private RemoteSpace globalChat;

    private RemoteSpace game_space;

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
            new Thread(new LobbyHandler(lobby)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void create(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("Username");
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        dialog.initOwner(stage);
        dialog.setTitle("Gravitrips");
        dialog.setHeaderText("Create new game");
        dialog.setContentText("Input game name");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(gameName -> {
            try {
                lobby.put("enter", userName, gameName);
                Object[] response = lobby.get(new ActualField("gameURI"), new ActualField(userName),
                        new ActualField(gameName), new FormalField(String.class));
                String game_uri = (String) response[3];
                System.out.println("Connecting to chat space " + game_uri);
                this.game_space = new RemoteSpace(game_uri);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
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

    @FXML
    private void menu(ActionEvent event) throws InterruptedException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setHeaderText("You are about to exit the lobby");
        alert.setContentText("Go to main menu?");
        alert.setTitle("Gravitrips");
        if (alert.showAndWait().get() == ButtonType.OK) {
            globalChat.put(userName, "Left the chat");
            Client.mainMenu(stage);
        } else {
            System.out.println("Cancel");
        }
    }

    @FXML
    private void onEnter(ActionEvent event) throws InterruptedException {
        send(event);
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

class LobbyHandler implements Runnable {

    private RemoteSpace lobby;

    public LobbyHandler(RemoteSpace lobby) {
        this.lobby = lobby;
    }

    @Override
    public void run() {

    }

}