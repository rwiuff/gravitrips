package gravitrips.client;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
    @FXML
    Button readyBtn;
    private int[][] board;
    private String userName;
    private RemoteSpace channel;
    private RemoteSpace chat;
    private Gson gson = new Gson();
    private int rows;
    private int columns;
    private int player;
    private Color fieldColour = Color.rgb(237, 28, 36);
    private Color playerOneColour = Color.rgb(255, 255, 255);
    private Color playerTwoColour = Color.rgb(147, 149, 152);
    private Color playerColour;
    private int size = 20;

    public void setup(Settings settings, String channelUri, String game_uri)
            throws UnknownHostException, IOException, InterruptedException {
        this.userName = settings.getUserName();
        this.chat = new RemoteSpace(game_uri);
        Thread chatThread = new Thread(new ClientChatHandler(chat, messages));
        label.setVisible(false);
        this.channel = new RemoteSpace(channelUri);
        this.channel.put(userName, "Joined the game");
        chatThread.start();
        Object[] serverSettings = channel.get(new ActualField("setup"), new FormalField(Integer.class),
                new FormalField(Integer.class), new FormalField(Integer.class));
        this.rows = (int) serverSettings[1];
        this.columns = (int) serverSettings[2];
        this.player = (int) serverSettings[3];
        playerColour = (player == 1) ? playerOneColour : playerTwoColour;
        getBoard();
        drawBoard();
    }

    private void getBoard() {
        try {
            String fetch = (String) channel.get(new ActualField("board"), new FormalField(String.class))[1];
            this.board = gson.fromJson(fetch, int[][].class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        try {
            while (true) {
                Object[] status = channel.get(new ActualField("status"), new FormalField(String.class));
                if (status[1].equals("turn")) {
                    channel.put(move());
                } else if (status[1].equals("invalid")) {
                    channel.put(move());
                } else if (status[1].equals("winner")) {
                    int winner = (int) channel.get(new ActualField("winner"), new FormalField(Integer.class))[1];
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drawBoard() {
        GridPane gridPane = new GridPane();
        for (int i = 0; i < columns; i++) {
            Rectangle rectangle = new Rectangle();
            rectangle.setWidth(size);
            rectangle.setHeight(size);
            rectangle.setFill(fieldColour);
            rectangle.setId(i + ";" + 0);
            gridPane.add(rectangle, i, 0);
            if (board[0][i] == 0) {
                Circle c = new Circle(10, playerColour);
                c.setOpacity(.7);
                c.setCenterX(rectangle.getWidth() / 2);
                c.setCenterY(rectangle.getHeight() / 2);
                gridPane.add(c, i, 0);
            }
        }
        for (int i = 1; i <= rows; i++) {
            for (int j = 0; j < columns; j++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(20);
                rectangle.setHeight(20);
                rectangle.setFill(fieldColour);
                rectangle.setId(j + ";" + i);
                gridPane.add(rectangle, j, i);
                Circle c;
                if (board[i - 1][j] == 1) {
                    c = new Circle(size / 2, playerOneColour);
                } else if (board[i - 1][j] == 2) {
                    c = new Circle(size / 2, playerTwoColour);
                } else {
                    c = new Circle(size / 2, fieldColour);
                }
                c.setCenterX(rectangle.getWidth() / 2);
                c.setCenterY(rectangle.getHeight() / 2);
                gridPane.add(c, j, i);
            }
        }
        gridPane.setAlignment(Pos.CENTER);
        gameField.getChildren().add(gridPane);
        StackPane.setAlignment(gridPane, Pos.CENTER);
    }

    @FXML
    private void onPaneClicked(MouseEvent event) {
        Pane pane = (Pane) event.getSource();
        System.out.println(GridPane.getColumnIndex(pane));
    }

    @FXML
    private void send(ActionEvent event) throws InterruptedException {
        String message = gamemessage.getText();
        gamemessage.clear();
        chat.put(userName, message);
    }

    @FXML
    private void onEnter(ActionEvent event) throws InterruptedException {
        send(event);
    }

    @FXML
    private void ready(ActionEvent event) {
        try {
            channel.put("status", userName, "ready");
            readyBtn.setDisable(true);
            readyBtn.setOpacity(.7);
            run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            chat.put(userName, "Left the game");
            Client.startLobby(stage);
        } else {
            System.out.println("Cancel");
        }
    }
}