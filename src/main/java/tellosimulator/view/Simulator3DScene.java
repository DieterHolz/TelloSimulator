package tellosimulator.view;

import javafx.scene.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;


public class Simulator3DScene extends SubScene {

    public static final double ROOM_WIDTH = 300;
    public static final double ROOM_HEIGHT = 240;
    public static final double ROOM_DEPTH = 800;
    public static final double WALL_DEPTH = 0.01;

    public Simulator3DScene(Stage stage, Parent sceneGraph) {
        super(sceneGraph, ROOM_WIDTH*2, ROOM_HEIGHT*2, true, SceneAntialiasing.BALANCED);
        setupCamera();

        initMouseControl(stage, sceneGraph);
    }


    private void setupValueChangeListeners() {
    }


    private void setupBindings() {
    }

    private void setupCamera() {
        Camera camera = new PerspectiveCamera();
        camera.translateXProperty().set(-ROOM_WIDTH/2);
        camera.translateYProperty().set(-ROOM_HEIGHT*1.5);
        camera.translateZProperty().set(0);
        setCamera(camera);
    }

    public void initMouseControl(Stage stage, Parent sceneGraph) {
        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            sceneGraph.translateZProperty().set(sceneGraph.getTranslateZ() - delta);
        });
    }
}
