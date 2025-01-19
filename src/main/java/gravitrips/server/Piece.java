package gravitrips.server;

import javafx.scene.paint.Color;

public class Piece {

    private String player;
    private Color colour;

    public Piece(String player, Color colour) {
        this.player = player;
        this.colour = colour;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }
}