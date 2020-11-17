package tellosimulator.view.drone;

import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import tellosimulator.view.world.Camera;


public class DroneCameraScene extends SubScene {

    Camera droneCamera;

    public DroneCameraScene(Parent sceneGraph, Camera droneCamera) {
        super( sceneGraph, 320, 180, true, SceneAntialiasing.BALANCED);
        setCamera(droneCamera);
        setFill(new Color(0,1,0,0.5));
    }
}
