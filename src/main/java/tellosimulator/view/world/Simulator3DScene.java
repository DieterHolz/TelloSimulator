package tellosimulator.view.world;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import tellosimulator.model.DroneModel;
import tellosimulator.view.drone.DroneView;


public class Simulator3DScene extends SubScene {
    private SimulatorCamera simulatorCamera;
    private DroneCamera droneCamera;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    private DroneModel droneModel;

    public Simulator3DScene(Parent sceneGraph, DroneView droneView, DroneModel droneMod) {
        super( sceneGraph, 1280, 720, true, SceneAntialiasing.BALANCED);
        droneModel = droneMod;
        simulatorCamera = new SimulatorCamera(droneView, 200, 25, -10);
        droneCamera = new DroneCamera(droneModel);
        setCamera(simulatorCamera);
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        setPickOnBounds(true); // needed to register MouseEvents on the whole SubScene
        addEventHandler(MouseEvent.ANY, mouseEventHandler);
        addEventHandler(ScrollEvent.ANY, scrollEventHandler);
    }

    private final EventHandler<MouseEvent> mouseEventHandler = event -> {

        if(!droneModel.isDroneCameraActive()) {

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

                if (event.isMiddleButtonDown() || event.isSecondaryButtonDown()) {
                    simulatorCamera.cameraPosition.setX(simulatorCamera.cameraPosition.getTx() - mouseDeltaX * modifierFactor * modifier * 0.4);
                    simulatorCamera.cameraPosition.setY(simulatorCamera.cameraPosition.getTy() - mouseDeltaY * modifierFactor * modifier * 0.4);
                }
                if (event.isPrimaryButtonDown()) {
                    simulatorCamera.cameraRotateY.setAngle(simulatorCamera.cameraRotateY.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0);
                    simulatorCamera.cameraRotateX.setAngle(simulatorCamera.cameraRotateX.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0);
                }
            }
        }
    };

    private final EventHandler<ScrollEvent> scrollEventHandler = event -> {
        if(!droneModel.isDroneCameraActive()) {
            double modifier = 1;
            if (event.isControlDown()) {
                modifier = 0.1;
            }
            double delta = event.getDeltaY();
            simulatorCamera.cameraPosition.setZ(simulatorCamera.cameraPosition.getTz() - delta * modifier * 0.5);
        }
    };

    public SimulatorCamera getSimulatorCamera() {
        return simulatorCamera;
    }

    public DroneCamera getDroneCamera() {
        return droneCamera;
    }
}
