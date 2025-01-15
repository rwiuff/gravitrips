package dk.dtu.gravitrips.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Client extends Application {
    private static Settings settings;
    private FXMLLoader menuLoader;
    private Parent mainMenuRoot;
    private Image icon16;
    private static Image icon32;
    private Image icon64;
    private Scene scene;

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
        alert.setContentText("Are you sure you want to exit Gravitrips?");
        alert.setHeaderText("You are about to exit Gravitrips");
        alert.setTitle("Exit Gravitrips");
        alert.setGraphic(new ImageView(icon32));
        if (alert.showAndWait().get() == ButtonType.OK)
            Platform.exit();
    }

    private void mainMenu(Stage primaryStage) {
        scene.setRoot(mainMenuRoot);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    private void populateResources() throws IOException {
        menuLoader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        mainMenuRoot = menuLoader.load();
        icon16 = new Image(getClass().getResourceAsStream("icons/icon16.png"));
        icon32 = new Image(getClass().getResourceAsStream("icons/icon32.png"));
        icon64 = new Image(getClass().getResourceAsStream("icons/icon64.png"));
    }

    public static Settings getSettings() {
        return settings;
    }

    public static void setSettings(Settings settings) {
        Client.settings = settings;
    }
}
