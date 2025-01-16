package gravitrips.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainMenuController {
    @FXML
    Button startBtn;

    @FXML
    Button settingsBtn;

    @FXML
    Button exitBtn;

    @FXML
    private void begin(ActionEvent event) throws IOException, InterruptedException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("Gravitrips");
        alert.setHeaderText("Connect or host?");
        alert.setContentText("Chose an option");

        ButtonType connect = new ButtonType("Connect");
        ButtonType host = new ButtonType("Host");
        ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(connect, host, cancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == connect) {
            Client.connect(stage);
        } else if (result.get() == host) {
            Client.host(stage);
        } else {
            System.out.println("Cancel");
        }
    }

    @FXML
    private void settingsGoto(ActionEvent event) { // Press on settings
        Settings settings = Client.getSettings(); // Get settings from Client class
        Dialog<HashMap<String, String>> dialog = new Dialog<>(); // Create dialog
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        dialog.initOwner(stage);
        dialog.setTitle("Settings");
        dialog.setHeaderText("Settings for Gravitrips");
        dialog.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/icon32.png"))));

        ButtonType applyBtn = new ButtonType("Apply", ButtonData.APPLY);

        dialog.getDialogPane().getButtonTypes().addAll(applyBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField rows = new TextField();
        rows.setPromptText("16");
        TextField columns = new TextField();
        columns.setPromptText("16");
        TextField host = new TextField();
        host.setPromptText("localhost");
        TextField port = new TextField();
        port.setPromptText("31415");

        grid.add(new Label("Rows [6 - 20]"), 0, 0);
        grid.add(rows, 1, 0);
        grid.add(new Label("Columns [6 - 20]"), 0, 1);
        grid.add(columns, 1, 1);
        grid.add(new Label("Host"), 0, 2);
        grid.add(host, 1, 2);
        grid.add(new Label("Port"), 0, 3);
        grid.add(port, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyBtn) {
                HashMap<String, String> newSettings = new HashMap<String, String>();
                newSettings.put("rows", rows.getText());
                newSettings.put("columns", columns.getText());
                newSettings.put("host", host.getText());
                newSettings.put("port", port.getText());
                return newSettings; // Create new settings from dialog box
            }
            return null;
        });
        Optional<HashMap<String, String>> result = dialog.showAndWait();

        result.ifPresent(newSettings -> {
            if (!newSettings.get("rows").isBlank()) {
                int newRows = Integer.parseInt(newSettings.get("rows"));
                if (newRows >= 6 && newRows <= 20) {
                    settings.setRows(newRows);
                } else {
                    settings.setRows(6);
                }
            }
            if (!newSettings.get("columns").isBlank()) {
                int newColumns = Integer.parseInt(newSettings.get("columns"));
                if (newColumns >= 6 && newColumns <= 20) {
                    settings.setColumns(newColumns);
                } else {
                    settings.setColumns(6);
                }
            }
            if (!newSettings.get("host").isBlank()) {
                settings.setHost(newSettings.get("host"));
            } else {
                settings.setHost("localhost");
            }
            if (!newSettings.get("port").isBlank()) {
                settings.setPort(newSettings.get("port"));
            } else {
                settings.setPort("31415");
            }
            Client.setSettings(settings);
        });
    }

    @FXML
    private void exitProgram(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Client.exit(stage); // Exit method
    }
}
