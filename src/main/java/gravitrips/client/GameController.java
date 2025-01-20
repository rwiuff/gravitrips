package gravitrips.client;

import java.io.IOException;
import java.net.UnknownHostException;
import org.jspace.RemoteSpace;

import gravitrips.server.Game;
import gravitrips.server.Piece;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class GameController {
    @FXML
    TextField gamemessage;
    @FXML
    TextFlow messages;
    @FXML
    StackPane gameField = new StackPane();
    @FXML
    Label label = new Label();
    private String userName;
    private RemoteSpace gameSpace;
    private Settings settings;
    private Color fieldcolor;

    public void setup(Settings settings, RemoteSpace game_space)
            throws UnknownHostException, IOException, InterruptedException {
        this.gameSpace = game_space;
        this.userName = settings.getUserName();
        this.settings = settings;
        Thread chatThread = new Thread(new ClientChatHandler(gameSpace, messages));
        label.setVisible(false);
        gameSpace.put(userName, "Joined the game");
        chatThread.start();
        drawBoard();
    }

    private void drawBoard() {
        GridPane gridPane = new GridPane();
        // Local test driver
        Game game = new Game(16, 16, "player1", "playerTwo");
        Piece[][] board = game.getBoard();
        fieldcolor = Color.rgb(237, 28, 36);
        for (int i = 0; i < settings.getColumns(); i++) {
            Rectangle rectangle = new Rectangle();
            rectangle.setWidth(20);
            rectangle.setHeight(20);
            rectangle.setFill(fieldcolor);
            rectangle.setId(i + ";" + 0);
            gridPane.add(rectangle, i, 0);
            if (board[0][i].getPlayer().equals("empty")) {
                Piece piece = game.getPlayerOne();
                Circle c = new Circle(10, piece.getColour());
                c.setCenterX(rectangle.getWidth() / 2);
                c.setCenterY(rectangle.getHeight() / 2);
                gridPane.add(c, i, 0);
            }
        }
        for (int i = 1; i <= settings.getRows(); i++) {
            for (int j = 0; j < settings.getColumns(); j++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(20);
                rectangle.setHeight(20);
                rectangle.setFill(fieldcolor);
                rectangle.setId(i + ";" + j);
                gridPane.add(rectangle, i-1, j);
                Piece piece = board[i-1][j];
                Circle c = new Circle(10, piece.getColour());
                c.setCenterX(rectangle.getWidth() / 2);
                c.setCenterY(rectangle.getHeight() / 2);
                gridPane.add(c, i, j);
            }
        }
        gridPane.setAlignment(Pos.CENTER);
        gameField.getChildren().add(gridPane);
        StackPane.setAlignment(gridPane, Pos.CENTER);
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

    @FXML
    private void ready(ActionEvent event) {

    }

    @FXML
    private void quit(ActionEvent event) throws InterruptedException, IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setHeaderText("You are about to exit the game");
        alert.setContentText("Go to the lobby?");
        alert.setTitle("Gravitrips");
        if (alert.showAndWait().get() == ButtonType.OK) {
            gameSpace.put(userName, "Left the game");
            Client.startLobby(stage);
        } else {
            System.out.println("Cancel");
        }
    }
}