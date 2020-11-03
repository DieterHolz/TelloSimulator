package tellosimulator.view.world;

import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;


public class Simulator3DScene extends SubScene {
    Camera camera;

    public static final double ROOM_WIDTH = 300;
    public static final double ROOM_HEIGHT = 240;
    public static final double ROOM_DEPTH = 800;
    public static final double WALL_DEPTH = 0.01;

    public Simulator3DScene(Stage stage, Parent sceneGraph) {
        super(sceneGraph, ROOM_WIDTH*2, ROOM_HEIGHT*2, true, SceneAntialiasing.BALANCED);
        setupCamera();

        setOnMouseDragged( new MouseLook());
        initMouseControl(stage, sceneGraph);
    }


    private void setupValueChangeListeners() {
    }


    private void setupBindings() {
    }

    private void setupCamera() {
        camera = new PerspectiveCamera();
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

    private class MouseLook implements EventHandler<MouseEvent> {
        private Rotate rotation;
        private int oldX, newX;
        private boolean alreadyMoved = false;

        @Override
        public void handle(MouseEvent event) {
            if ( alreadyMoved ) {
                newX = (int) event.getScreenX();

                if ( oldX < newX ) { // if mouse moved to right
                    rotation = new Rotate( 10.0,
                            // camera rotates around its location
                            0, 0, 0,
                            Rotate.Y_AXIS );


                } else if ( oldX > newX ) { // if mouse moved to left
                    rotation = new Rotate( -10.0,
                            // camera rotates around its location
                            0, 0, 0,
                            Rotate.Y_AXIS );

                }
                camera.getTransforms().addAll( rotation );

                oldX = newX;
            } else {
                oldX = (int) event.getScreenX();
                alreadyMoved = true;
            }
        }
    }
}
