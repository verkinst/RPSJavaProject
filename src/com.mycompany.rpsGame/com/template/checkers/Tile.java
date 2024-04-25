package com.template.checkers;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    private Piece piece;

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    public Tile(boolean light, int x, int y) {
        setWidth(Checkers.TILE_SIZE);
        setHeight(Checkers.TILE_SIZE);

        relocate(x * Checkers.TILE_SIZE, y * Checkers.TILE_SIZE);

        setFill(light ? Color.valueOf("#bde0ff") : Color.valueOf("#afdafc"));
    }
}
