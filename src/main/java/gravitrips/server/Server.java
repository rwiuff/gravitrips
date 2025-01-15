package gravitrips.server;

import gravitrips.client.Settings;

public class Server {
    private Settings settings;
    private String host;
    private String port;
    private int rows;
    private int columns;

    public Server(Settings settings) {
        this.settings = settings;
        this.host = settings.getHost();
        this.port = settings.getPort();
        this.rows = settings.getRows();
        this.columns = settings.getColumns();
    }

}
