package com.template.checkers;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Checkers {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 7;
    public static final int HEIGHT = 7;

    public static final int RIGHT_MENU_WIDTH = TILE_SIZE * 2;
    public static PieceTeam currentPlayerTeam = PieceTeam.RED;


    private static Stage stage = null;


    private static Tile[][] board = new Tile[WIDTH][HEIGHT];
    private static Group tileGroup = new Group();
    private static Group pieceGroup = new Group();


    public static void startNewGame() {
        pieceGroup.getChildren().clear();
        tileGroup.getChildren().clear();
        board = new Tile[WIDTH][HEIGHT];
    }

    private static void playSound(String soundName) {
        String musicFile = "src/com.mycompany.rpsGame/com/sounds/" + soundName + ".mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private static void reselectImagesForPieces(PieceTeam currentPlayerTeam) {
        pieceGroup.getChildren().forEach(node -> {
            Piece piece = (Piece) node;
            try {
                piece.reselectImage(currentPlayerTeam);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Checkers() {
    }

    private static List<PieceType> createShuffle() {
        List<PieceType> pieces = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            pieces.add(PieceType.STONE);
        }

        for (int i = 0; i < 4; i++) {
            pieces.add(PieceType.SCISSORS);
        }

        for (int i = 0; i < 4; i++) {
            pieces.add(PieceType.PAPER);
        }

        pieces.add(PieceType.KAMIKAZE);
        pieces.add(PieceType.FLAG);

        Collections.shuffle(pieces);
        return pieces;
    }

    public static void restartGame() throws FileNotFoundException {
        pieceGroup.getChildren().clear();
        createPieces();
        reselectImagesForPieces(currentPlayerTeam);
    }

    private static void createTiles() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);
            }
        }

    }

    private static void createPieces() throws FileNotFoundException {

        List<PieceType> reds = createShuffle();
        List<PieceType> whites = createShuffle();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Piece piece = null;

                if (y <= 2) {
                    if (!reds.isEmpty()) {
                        piece = makePiece(reds.get(reds.size() - 1), PieceTeam.RED, x, y);
                        reds.remove(reds.size() - 1);
                    }
                }

                if (y >= 5) {
                    if (!whites.isEmpty()) {
                        piece = makePiece(whites.get(whites.size() - 1), PieceTeam.WHITE, x, y);
                        whites.remove(whites.size() - 1);
                    }
                }

                if (piece != null) {
                    board[x][y].setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
    }

    public static Parent createContent(Stage primaryStage) throws FileNotFoundException {

        Checkers.stage = primaryStage;
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        root.getChildren().addAll(tileGroup, pieceGroup);

        if (!MainForms.gameStarted) {
            MainForms.gameStarted = true;

            createTiles();

            createPieces();
        }

        reselectImagesForPieces(currentPlayerTeam);


        return root;
    }


    private static MoveResult tryMove(Piece piece, int newX, int newY) {

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        if ((newX > WIDTH - 1) || (newY > WIDTH - 1) || (newX < 0) || (newY < 0)) {
            System.out.println("Move is impossible - too far away or beyond borders");
            return new MoveResult(MoveType.NONE);
        }

        if (piece.getTeam() != currentPlayerTeam) {
            return new MoveResult(MoveType.NONE);
        }

        if ((Math.abs(newX - x0) == 1 && newY == y0) || (Math.abs(newY - y0) == 1 && newX == x0)) {
            if (board[newX][newY].hasPiece()) {
                System.out.println("tile [" + newX + "; " + newY + "] has piece " + board[newX][newY].getPiece().getType());
                if (board[newX][newY].getPiece().getTeam() != piece.getTeam()) {
                    System.out.println("BATTLE");
                    if (board[newX][newY].getPiece().getType() != piece.getType()) {
                        return new MoveResult(MoveType.KILL, board[x0][y0].getPiece(), board[newX][newY].getPiece());
                    } else {
                        return new MoveResult(MoveType.EMPTY_BATTLE, board[x0][y0].getPiece(), board[newX][newY].getPiece());
                    }
                } else {
                    return new MoveResult(MoveType.NONE);
                }
            } else {

                return new MoveResult(MoveType.NORMAL);
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    private static int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }


    private static void killDefender(Tile fromTile, Tile toTile, Piece attacker, Piece defender) {
        fromTile.setPiece(null);
        toTile.setPiece(attacker);
        pieceGroup.getChildren().remove(defender);
    }

    private static void killAttacker(Tile fromTile, Piece attacker) {
        fromTile.setPiece(null);
        pieceGroup.getChildren().remove(attacker);
    }

    private static Piece makePiece(PieceType type, PieceTeam team, int x, int y) throws FileNotFoundException {

        Piece piece = new Piece(type, team, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result = tryMove(piece, newX, newY);

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());
            Piece attacker = result.getPiece();
            Piece defender = result.getDefenderPiece();

            switch (result.getType()) {
                case NONE:
                    playSound("error");
                    piece.abortMove();
                    break;
                case NORMAL:
                    changeCurrentPlayer();
                    playSound("step");
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    break;
                case EMPTY_BATTLE:


                    attacker.setExposed(true);
                    defender.setExposed(true);
                    playSound("draw");
                    piece.abortMove();
                    changeCurrentPlayer();
                    break;
                case KILL:

                    piece.move(newX, newY);


                    PieceType attackerType = attacker.getType();
                    PieceType defenderType = defender.getType();

                    attacker.setExposed(true);
                    defender.setExposed(true);
                    changeCurrentPlayer();

                    Tile fromTile = board[x0][y0];
                    Tile toTile = board[newX][newY];

                    if (attackerType != PieceType.FLAG && defenderType == PieceType.FLAG) {

                        killDefender(fromTile, toTile, attacker, defender);
                        MainForms.gameState = GameState.END;
                        System.out.println("Владелец флажка проигрывает!");
                        PieceTeam loser = attacker.getTeam();
                        finishGame(loser);
                        break;
                    }


                    if (attackerType == PieceType.KAMIKAZE || defenderType == PieceType.KAMIKAZE) {
                        playSound("kamikaze");
                        board[x0][y0].setPiece(null);
                        board[newX][newY].setPiece(null);
                        pieceGroup.getChildren().remove(attacker);
                        pieceGroup.getChildren().remove(defender);
                        break;
                    }

                    if ((attackerType == PieceType.STONE && defenderType == PieceType.SCISSORS) ||
                            (attackerType == PieceType.SCISSORS && defenderType == PieceType.PAPER) ||
                            (attackerType == PieceType.PAPER && defenderType == PieceType.STONE)) {
                        playSound(attacker.getType().toString());
                        killDefender(fromTile, toTile, attacker, defender);
                        break;
                    }

                    if ((defenderType == PieceType.STONE && attackerType == PieceType.SCISSORS) ||
                            (defenderType == PieceType.SCISSORS && attackerType == PieceType.PAPER) ||
                            (defenderType == PieceType.PAPER && attackerType == PieceType.STONE)) {
                        playSound(defender.getType().toString());
                        killAttacker(fromTile, attacker);
                        break;
                    }

                    if (attackerType == PieceType.FLAG && defenderType != PieceType.FLAG) {
                        MainForms.gameState = GameState.END;

                        System.out.println("Владелец флажка проигрывает!");
                        PieceTeam loser = attacker.getTeam();
                        killAttacker(fromTile, attacker);
                        finishGame(loser);
                        break;
                    }

                    piece.abortMove();
                    board[x0][y0].setPiece(piece);
                    break;
            }
        });

        return piece;
    }

    private static void finishGame(PieceTeam winner) {
        playSound("endgame");
        boolean redLost = (winner == PieceTeam.WHITE);
        try {
            Parent gameOverWindow = MainForms.createGameOverWindow(stage, redLost);
            Scene scene = new Scene(gameOverWindow, WIDTH * TILE_SIZE + RIGHT_MENU_WIDTH, HEIGHT * TILE_SIZE);
            stage.setScene(scene);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

    }

    private static void changeCurrentPlayer() {
        if (currentPlayerTeam == PieceTeam.RED) currentPlayerTeam = PieceTeam.WHITE;
        else currentPlayerTeam = PieceTeam.RED;
        reselectImagesForPieces(currentPlayerTeam);


    }


}
