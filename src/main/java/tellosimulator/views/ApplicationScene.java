package tellosimulator.views;

import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tellosimulator.TelloSimulator;

public class ApplicationScene extends Scene {

    public ApplicationScene(Parent root, Stage primaryStage) {
        super(root, Math.max(500, TelloSimulator.ROOMWIDTH), Math.max(500, TelloSimulator.ROOMHEIGHT), true);

        Camera camera = new PerspectiveCamera();
        camera.translateXProperty().set(TelloSimulator.ROOMWIDTH /2);
        camera.translateYProperty().set(-TelloSimulator.ROOMHEIGHT /2);
        this.setFill(Color.BLUE);
        this.setCamera(camera);
    }
}
