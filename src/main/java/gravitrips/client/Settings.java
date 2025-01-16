package gravitrips.client;

public class Settings {
    private String host;
    private String port;
    private int columns;
    private int rows;
    private String userName;

    public Settings() {
        this.host = "localhost";
        this.port = "31415";
        this.columns = 16;
        this.rows = 16;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
