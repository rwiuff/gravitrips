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
    private void create(ActionEvent event){
        System.out.println("create");
    }

    @FXML
    private void join(ActionEvent event){
        System.out.println("join");
    }

    @FXML
    private void send(ActionEvent event){
        System.out.println("Send");
    }

}
