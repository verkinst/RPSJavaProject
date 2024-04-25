    package com.template.checkers;

    import javafx.scene.effect.Blend;
    import javafx.scene.effect.BlendMode;
    import javafx.scene.effect.ColorInput;
    import javafx.scene.effect.GaussianBlur;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.Rectangle;

    import java.io.FileInputStream;
    import java.io.FileNotFoundException;

    import static com.template.checkers.Checkers.TILE_SIZE;

    public class Piece extends StackPane {

        private PieceType type;
        private Integer STROKE_WIDTH = 5;

        private StackPane stackPane = new StackPane();

        private Image image = new Image(new FileInputStream("src/com.mycompany.rpsGame/com/images/undefined.png"));

        private PieceTeam team = PieceTeam.UNDEFINED;
        private double mouseX, mouseY;
        private double oldX, oldY;
        private boolean visibility = false;

        public boolean getVisibility() {
            return visibility;
        }

        public PieceType getType() {
            return type;
        }

        public PieceTeam getTeam() {
            return team;
        }

        public double getOldX() {
            return oldX;
        }

        public double getOldY() {
            return oldY;
        }

        private void selectImage(PieceType type, PieceTeam team, PieceTeam currentPlayerColor) throws FileNotFoundException {
            String commandPrefix = "";
            String typeSuffix = "";

            switch (team) {
                case RED:
                    commandPrefix = "red";
                    break;
                case WHITE:
                    commandPrefix = "blue";
                    break;
            }

            switch (type) {
                case STONE:
                    typeSuffix = "Stone";
                    break;
                case SCISSORS:
                    typeSuffix = "Scissors";
                    break;
                case PAPER:
                    typeSuffix = "Paper";
                    break;
                case KAMIKAZE:
                    typeSuffix = "Kamikaze";
                    break;
                case FLAG:
                    typeSuffix = "Flag";
                    break;
            }

            if (!this.getVisibility() && (currentPlayerColor != this.team)) {
                typeSuffix = "";
            }

            String imagePath = "src/com.mycompany.rpsGame/com/images/" + commandPrefix + typeSuffix + ".png";
            this.image = new Image(new FileInputStream(imagePath));
        }


        public void reselectImage(PieceTeam currentPlayerTeam) throws FileNotFoundException {
            this.selectImage(type, team, currentPlayerTeam);
            ImageView imageView = (ImageView) stackPane.getChildren().get(0);
            GaussianBlur gaussianBlur = new GaussianBlur(1.5);
            imageView.setEffect(gaussianBlur);
            imageView.setImage(this.image);
        }

        public Piece(PieceType type, PieceTeam team, int x, int y) throws FileNotFoundException {
            this.type = type;
            this.team = team;
            this.visibility = false;
            move(x, y);


            this.stackPane = new StackPane();
            this.selectImage(type, team, team);

            ImageView imageView = new ImageView(this.image);
            imageView.setSmooth(true);
            imageView.setFitWidth(TILE_SIZE);
            imageView.setFitHeight(TILE_SIZE);

            Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.TRANSPARENT);
            rectangle.setStrokeWidth(STROKE_WIDTH);

            stackPane.getChildren().addAll(imageView, rectangle);
            getChildren().addAll(stackPane);

            setOnMouseEntered(e -> rectangle.setStroke(Color.BLUE));

            setOnMouseExited(e -> rectangle.setStroke(Color.TRANSPARENT));

            setOnMousePressed(e -> {
                rectangle.setStroke(Color.RED);
                mouseX = e.getSceneX();
                mouseY = e.getSceneY();
            });

            setOnMouseDragged(e -> relocate((e.getSceneX() - mouseX) + oldX, e.getSceneY() - mouseY + oldY));
        }

        public void move(int x, int y) {
            oldX = x * TILE_SIZE;
            oldY = y * TILE_SIZE;
            relocate(oldX, oldY);
        }

        public void abortMove() {
            relocate(oldX, oldY);
        }

        public boolean isExposed() {
            return this.visibility;
        }

        public void setExposed(boolean visibility) {
            this.visibility = visibility;
        }
    }
