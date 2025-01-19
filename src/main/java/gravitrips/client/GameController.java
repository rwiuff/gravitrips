package gravitrips.client;

import java.io.IOException;
import java.net.UnknownHostException;
import org.jspace.RemoteSpace;

import gravitrips.server.Game;
import gravitrips.server.Piece;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;

public class GameController {
    @FXML
    TextField gamemessage;
    @FXML
    TextFlow messages;
    @FXML
    BorderPane gamefield;
    private String userName;
    private RemoteSpace gameSpace;
    private Settings settings;

    public void setup(Settings settings, RemoteSpace game_space)
            throws UnknownHostException, IOException, InterruptedException {
        this.gameSpace = game_space;
        this.userName = settings.getUserName();
        this.settings = settings;
        Thread chatThread = new Thread(new ClientChatHandler(gameSpace, messages));
        gameSpace.put(userName, "Joined the game");
        chatThread.start();
        drawField();
    }

    private void drawField() {
        GridPane gridPane = new GridPane();
        // Local test driver
        Game game = new Game(16,16);
        Piece[][] field = game.getBoard();
        for(int i = 0; i < settings.getRows(); i++){
            for(int j = 0; j < settings.getColumns(); j++){
                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(20);
                rectangle.setHeight(20);
                rectangle.setFill(Color.CRIMSON);
                gridPane.add(rectangle, i, j);
            }
        }
        gridPane.setAlignment(Pos.CENTER);
        gamefield.setCenter(gridPane);
        // Ends here
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