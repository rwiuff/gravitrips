package gravitrips.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class GameController {
    @FXML
    TextArea gamemessage;
    @FXML
    TextFlow messages;
    @FXML
    Label label = new Label();
    @FXML
    Rectangle rectangle;
    @FXML
    Button readyBtn;
    @FXML
    GridPane gridPane = new GridPane();
    private int[][] board;
    private String userName;
    private RemoteSpace channel;
    private RemoteSpace chat;
    private Gson gson = new Gson();
    private int player;
    private Color fieldColour = Color.rgb(237, 28, 36);
    private Color playerOneColour = Color.rgb(255, 255, 255);
    private Color playerTwoColour = Color.rgb(147, 149, 152);
    private Color playerColour;

    public void setup(Settings settings, String channelUri, String game_uri, Scene scene)
            throws UnknownHostException, IOException, InterruptedException {
        this.userName = settings.getUserName();
        this.chat = new RemoteSpace(game_uri);
        Thread chatThread = new Thread(new ClientChatHandler(chat, messages));
        rectangle.setVisible(false);
        label.setVisible(false);
        this.channel = new RemoteSpace(channelUri);
        this.channel.put(userName, "Joined the game");
        chatThread.start();
        Object[] serverSettings = channel.get(new ActualField("setup"), new FormalField(Integer.class));
        this.player = (int) serverSettings[1];
        playerColour = (player == 1) ? playerOneColour : playerTwoColour;
        getBoard();
    }

    private void getBoard() {
        try {
            String fetch = (String) channel.get(new ActualField("board"), new FormalField(String.class))[1];
            this.board = gson.fromJson(fetch, int[][].class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Service<Void> service = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        while (true) {
                            String branch = (String) channel.get(new FormalField(String.class))[0];
                            if (branch.equals("continue")) {
                                System.out.println("Client: Continuing");
                                int turn = (int) channel.get(new ActualField("turn"),
                                        new FormalField(Integer.class))[1];
                                if (turn == player) {
                                    System.out.println("Client: My turn");
                                    final CountDownLatch latch = new CountDownLatch(1);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                drawBoard(true);
                                                getInput();
                                            } finally {
                                                latch.countDown();
                                            }
                                        }
                                    });
                                    latch.await();
                                    System.out.print("Afterclick");
                                    while (true) {
                                        branch = (String) channel.get(new FormalField(String.class))[0];
                                        if (branch.equals("continue")) {
                                            final CountDownLatch latch1 = new CountDownLatch(1);
                                            Platform.runLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        getInput();
                                                    } finally {
                                                        latch1.countDown();
                                                    }
                                                }
                                            });
                                            latch1.await();
                                        } else
                                            break;
                                    }
                                }
                                final CountDownLatch latch = new CountDownLatch(1);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            getBoard();
                                            drawBoard(false);
                                        } finally {
                                            latch.countDown();
                                        }
                                    }
                                });
                                latch.await();
                            } else
                                break;
                        }
                        winner();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    };

    private void run() {
        try {
            while (true) {
                System.out.println("Client: Waiting to start");
                String begin = (String) channel.get(new ActualField("begin"))[0];
                if (begin.equals("begin")) {
                    System.out.println("Client: Start");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.start();
    }

    private void getInput() {
        gridPane.getChildren().forEach(item -> {
            item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Circle source = (Circle) event.getSource();
                    int input = GridPane.getColumnIndex(source);
                    System.out.println("CLICK " + input);
                    try {
                        channel.put(input);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    private void drawBoard(boolean b) {
        if (b) {
            for (int i = 0; i < 7; i++) {
                if (board[0][i] == 0) {
                    Circle c = new Circle(57 / 2, playerColour);
                    c.setStroke(Color.rgb(50, 50, 50));
                    c.setOpacity(.3);
                    gridPane.add(c, i, 0);
                }
            }
        }
        for (int i = 1; i <= 6; i++) {
            for (int j = 0; j < 7; j++) {
                Circle c;
                if (board[i - 1][j] == 1) {
                    c = new Circle(57 / 2, playerOneColour);
                    c.setStroke(Color.rgb(50, 50, 50));
                } else if (board[i - 1][j] == 2) {
                    c = new Circle(57 / 2, playerTwoColour);
                    c.setStroke(Color.rgb(50, 50, 50));
                } else {
                    c = new Circle(57 / 2, fieldColour);
                }
                gridPane.add(c, j, i);
            }
        }
    }

    private void winner() {
        try {
            Object[] conclusion = channel.get(new FormalField(Integer.class), new FormalField(String.class), new FormalField(String.class));
            rectangle.setVisible(true);
            label.setVisible(true);
            label.setText("Winner is " + conclusion[1]);
            label.setFont(Font.font(18));
            label.setAlignment(Pos.CENTER);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void send(ActionEvent event) throws InterruptedException {
        String message = gamemessage.getText();
        gamemessage.clear();
        chat.put(userName, message);
    }

    @FXML
    private void onEnter(KeyEvent event) throws InterruptedException {
        if (event.getCode().equals(KeyCode.ENTER))
            send(new ActionEvent());
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
