package gravitrips.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LobbyController {

    private String host;
    private String port;
    private Settings settings;
    private String userName;

    public void setup(Settings settings) {
        this.settings = settings;
        this.host = settings.getHost();
        this.port = settings.getPort();
        this.userName = settings.getUserName();
    }

    @FXML
    private void refresh(ActionEvent event){
        System.out.println("Refresh");
    }

    @FXML
    private void connect(ActionEvent event){
        System.out.println("Connect");
    }

    @FXML
    private void send(ActionEvent event){
        System.out.println("Send");
    }

}
