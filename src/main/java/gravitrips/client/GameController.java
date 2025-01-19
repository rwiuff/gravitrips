package gravitrips.client;

import org.jspace.RemoteSpace;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class GameController {
    @FXML
    TextField gamemessage;
    private String userName;
    private RemoteSpace gameSpace;

    public void setup(Settings settings, RemoteSpace game_space) {
        this.gameSpace = game_space;
        this.userName = settings.getUserName();
    }

    @FXML
    private void send(ActionEvent event) throws InterruptedException {
        String message = gamemessage.getText();
        gamemessage.clear();
        gameSpace.put(userName, message);
    }

    @FXML
    private void onEnter(ActionEvent event) throws InterruptedException {
        send(event);
    }
}
