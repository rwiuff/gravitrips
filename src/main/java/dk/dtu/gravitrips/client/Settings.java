package dk.dtu.gravitrips.client;

public class Settings {
    private String host;
    private String port;
    private int columns;
    private int rows;

    public Settings() {
        this.host = "localhost";
        this.port = "31415";
        this.columns = 6;
        this.rows = 6;
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
}
