package tellosimulator.view.world;

import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import tellosimulator.view.drone.DroneView;

import java.awt.*;


public class Simulator3DScene extends SubScene {
    Camera simulatorCamera;
    Camera droneCamera;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    public Simulator3DScene(Parent sceneGraph, DroneView droneView) {
        super( sceneGraph, 1280, 720, true, SceneAntialiasing.BALANCED);
        simulatorCamera = new Camera(droneView, 200, 25, -10);
        droneCamera = new Camera(droneView, 10, 10, 0);
        setCamera(droneCamera);
        setFill(new Color(1,0,0,0.5));
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        setPickOnBounds(true); // needed to register MouseEvents on the whole SubScene
        addEventHandler(MouseEvent.ANY, mouseEventHandler);
        addEventHandler(ScrollEvent.ANY, scrollEventHandler);
        addEventHandler(KeyEvent.ANY, keyboardEventHandler);
    }

    private final EventHandler<KeyEvent> keyboardEventHandler = event -> {
        if(event.getCode() == KeyCode.K) {
            System.out.println("bla");
            simulatorCamera.cameraPosition.setZ(1000);
        }
    };

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

            if (event.isMiddleButtonDown() || event.isSecondaryButtonDown()) {
                simulatorCamera.cameraPosition.setX(simulatorCamera.cameraPosition.getTx() - mouseDeltaX*modifierFactor*modifier*0.4);
                simulatorCamera.cameraPosition.setY(simulatorCamera.cameraPosition.getTy() - mouseDeltaY*modifierFactor*modifier*0.4);
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

    public Camera getDroneCamera() {
        return droneCamera;
    }

    public Camera getSimulatorCamera() {
        return simulatorCamera;
    }
}
