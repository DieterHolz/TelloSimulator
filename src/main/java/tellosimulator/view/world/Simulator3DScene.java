package tellosimulator.view.world;

import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import tellosimulator.view.drone.DroneView;


public class Simulator3DScene extends SubScene {
    SimulatorCamera simulatorCamera;

    public static final double ROOM_WIDTH = 600;
    public static final double ROOM_HEIGHT = 400;
    public static final double ROOM_DEPTH = 800;
    public static final double WALL_DEPTH = 0.01;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    public Simulator3DScene(Stage stage, Parent sceneGraph, DroneView droneView) {
        super( sceneGraph, 1200, 800, true, SceneAntialiasing.BALANCED); //TODO: resize/bind width and height to parent (maybe put SubScene in StackPane, StackPane in Borderpane center)
        simulatorCamera = new SimulatorCamera(droneView);
        setCamera(simulatorCamera);

        stage.addEventHandler(MouseEvent.ANY, mouseEventHandler);
        stage.addEventHandler(ScrollEvent.ANY, scrollEventHandler);
    }

    private final EventHandler<MouseEvent> mouseEventHandler = event -> {

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();

        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            double modifier = 1.0;
            double modifierFactor = 0.3;

            if (event.isControlDown()) {
                modifier = 0.1;
            }
            if (event.isShiftDown()) {
                modifier = 10.0;
            }

            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            if (event.isMiddleButtonDown() || (event.isPrimaryButtonDown() && event.isSecondaryButtonDown())) {
                simulatorCamera.cameraPosition.setX(simulatorCamera.cameraPosition.getTx() - mouseDeltaX*modifierFactor*modifier*0.3);
                simulatorCamera.cameraPosition.setY(simulatorCamera.cameraPosition.getTy() - mouseDeltaY*modifierFactor*modifier*0.3);
            }
            if (event.isPrimaryButtonDown()) {
                simulatorCamera.cameraRotateY.setAngle(simulatorCamera.cameraRotateY.getAngle() + mouseDeltaX*modifierFactor*modifier*2.0);
                simulatorCamera.cameraRotateX.setAngle(simulatorCamera.cameraRotateX.getAngle() - mouseDeltaY*modifierFactor*modifier*2.0);
            }
        }
    };

    private final EventHandler<ScrollEvent> scrollEventHandler = event -> {
        double modifier = 1;
        if (event.isControlDown()) {
            modifier = 0.1;
        }
        double delta = event.getDeltaY();
        simulatorCamera.cameraPosition.setZ(simulatorCamera.cameraPosition.getTz() - delta*modifier*0.5);
    };
}
