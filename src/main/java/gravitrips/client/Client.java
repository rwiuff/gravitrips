package gravitrips.client;

import java.io.IOException;
import java.util.Optional;

import org.jspace.RemoteSpace;

import gravitrips.server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Client extends Application {
    private static Settings settings;
    private FXMLLoader menuLoader;
    private static Parent mainMenuRoot;
    private Image icon16;
    private static Image icon32;
    private static FXMLLoader lobbyLoader;
    private static Parent lobbyRoot;
    private static LobbyController lobbyController;
    private Image icon64;
    private static Scene scene;
    private static FXMLLoader gameLoader;
    private static Parent gameRoot;
    private static GameController gameController;

    public static void main(String[] args) {
        settings = new Settings();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        populateResources();
        scene = new Scene(mainMenuRoot);
        primaryStage.setTitle("Gravitrips");
        primaryStage.getIcons().addAll(icon16, icon32, icon64);
        mainMenu(primaryStage);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            exit(primaryStage);
        });
    }

    static void exit(Stage primaryStage) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(primaryStage);
        alert.setContentText("Are you sure you want to exit Gravitrips?");
        alert.setHeaderText("You are about to exit Gravitrips");
        alert.setTitle("Exit Gravitrips");
        alert.setGraphic(new ImageView(icon32));
        if (alert.showAndWait().get() == ButtonType.OK)
            Platform.exit();
        System.exit(0);
    }

    static void mainMenu(Stage primaryStage) {
        scene.setRoot(mainMenuRoot);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    private void populateResources() throws IOException {
        menuLoader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        mainMenuRoot = menuLoader.load();
        icon16 = new Image(getClass().getResourceAsStream("/icons/icon16.png"));
        icon32 = new Image(getClass().getResourceAsStream("/icons/icon32.png"));
        icon64 = new Image(getClass().getResourceAsStream("/icons/icon64.png"));
    }

    public static Settings getSettings() {
        return settings;
    }

    public static void setSettings(Settings settings) {
        Client.settings = settings;
    }

    public static void connect(Stage stage) throws IOException, InterruptedException {
        startLobby(stage);
    }

    public static void host(Stage stage) throws IOException, InterruptedException {
        Thread serverThread = new Thread(new Server(settings));
        serverThread.start();
        startLobby(stage);
    }

    static void startLobby(Stage stage) throws IOException, InterruptedException {
        if (settings.getUserName() == null) {
            TextInputDialog dialog = new TextInputDialog("Username");
            dialog.initOwner(stage);
            dialog.setTitle("Gravitrips");
            dialog.setHeaderText("Chose your username");
            dialog.setContentText("Input name");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(userName -> Client.settings.setUserName(userName));
        }
        loadLobby();
        lobbyController.setup(settings);
        scene.setRoot(lobbyRoot);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    private static void loadLobby() throws IOException {
        lobbyLoader = new FXMLLoader(Client.class.getResource("/fxml/Lobby.fxml"));
        lobbyRoot = lobbyLoader.load();
        lobbyController = lobbyLoader.getController();
    }

    public static void game(Stage stage, RemoteSpace game_space) throws IOException, InterruptedException {
        loadGame();
        gameController.setup(settings, game_space);
        scene.setRoot(gameRoot);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    private static void loadGame() throws IOException {
        gameLoader = new FXMLLoader(Client.class.getResource("/fxml/Game.fxml"));
        gameRoot = gameLoader.load();
        gameController = gameLoader.getController();
    }

}
