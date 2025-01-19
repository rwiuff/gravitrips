package gravitrips.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javafx.scene.control.TreeItem;
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
    TreeView<String> tree = new TreeView<>();

    private String host;
    private String port;
    private String userName;

    private RemoteSpace lobby;

    private RemoteSpace globalChat;

    private RemoteSpace game_space;

    private TreeItem<String> root;

    public void setup(Settings settings) throws InterruptedException {
        this.host = settings.getHost();
        this.port = settings.getPort();
        this.userName = settings.getUserName();
        openLobby();
    }

    private void openLobby() {
        try {
            String uri = "tcp://" + host + ":" + port + "/lobby?keep";
            System.out.println("Connecting to lobby " + uri + "...");
            this.lobby = new RemoteSpace(uri);
            String chatUri = "tcp://" + host + ":" + port + "/global_chat?keep";
            System.out.println("Connecting to chat " + chatUri + "...");
            this.globalChat = new RemoteSpace(chatUri);
            Thread chatThread = new Thread(new ClientChatHandler(globalChat, messages));
            globalChat.put(userName, "Joined the chat");
            chatThread.start();
            this.root = new TreeItem<>("Games");
            root.setExpanded(true);
            tree.setRoot(root);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void create(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("Game name");
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
    private void refresh(ActionEvent event) throws InterruptedException {
        root.getChildren().clear();
        Map<String, String> userList = new HashMap<String, String>();
        List<String> gameList = new ArrayList<String>();
        List<Object[]> games = lobby.queryAll(new ActualField("games"), new FormalField(String.class),
                new FormalField(String.class),
                new FormalField(String.class));
        for (Object[] entry : games) {
            userList.put((String) entry[1], (String) entry[2]);
            gameList.add((String) entry[2]);
        }
        for (String game : gameList) {
            TreeItem<String> aGame = new TreeItem<String>(game);
            for (Map.Entry<String, String> user : userList.entrySet()) {
                if (user.getValue().equals(game)) {
                    aGame.getChildren().add(new TreeItem<>(user.getKey()));
                }
                aGame.setExpanded(true);
            }
            root.getChildren().add(aGame);
        }
    }

    @FXML
    private void join(ActionEvent event) {
        String game = tree.getSelectionModel().getSelectedItem().getValue();
        try {
            lobby.put("enter", userName, game);
            Object[] response = lobby.get(new ActualField("gameURI"), new ActualField(userName),
                    new ActualField(game), new FormalField(String.class));
            String game_uri = (String) response[3];
            System.out.println("Connecting to chat space " + game_uri);
            this.game_space = new RemoteSpace(game_uri);
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            Client.game(stage, game_space);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void send(ActionEvent event) throws InterruptedException {
        refresh(event);
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
