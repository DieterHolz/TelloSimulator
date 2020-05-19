package tellosimulator.views;

import javafx.scene.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tellosimulator.TelloSimulator;

public class ApplicationScene extends Scene {

    public ApplicationScene(Parent root, Stage stage) {
        super(root, Math.max(500, TelloSimulator.ROOM_WIDTH), Math.max(500, TelloSimulator.ROOM_HEIGHT), true);

        Camera camera = new PerspectiveCamera();
        camera.translateXProperty().set(TelloSimulator.ROOM_WIDTH /2);
        camera.translateYProperty().set(-TelloSimulator.ROOM_HEIGHT /2);
        this.setFill(Color.BLUE);
        this.setCamera(camera);

        initMouseControl(stage, root);
    }

    public void initMouseControl(Stage stage, Parent root) {
        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            root.translateZProperty().set(root.getTranslateZ() - delta);
        });
    }
}
