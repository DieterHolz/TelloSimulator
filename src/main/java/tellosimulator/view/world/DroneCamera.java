package tellosimulator.view.world;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import tellosimulator.common.VectorHelper;
import tellosimulator.view.drone.DroneView;

public class DroneCamera extends PerspectiveCamera {

    private  DroneView droneView;

    private Translate pivotPosition = new Translate();
    private Translate cameraPosition = new Translate();

    private Rotate cameraRotateX = new Rotate();

    private DoubleProperty viewingAngle = new SimpleDoubleProperty();

    private double cameraZOffset = -10;
    private double cameraYOffset = -10;

    DroneCamera(DroneView droneView) {
        super(true);

        this.droneView = droneView;
        setNearClip(1.0);
        setFarClip(10000.0);

        cameraPosition.setZ(cameraZOffset);
        cameraPosition.setY(cameraYOffset);

        cameraRotateX.setAxis(Rotate.X_AXIS);
        cameraRotateX.setAngle(viewingAngle.doubleValue());

        setRotationAxis(VectorHelper.getUpwardsNormalVector());
        getTransforms().addAll(pivotPosition, cameraRotateX, cameraPosition);

        setupValueChangedListeners();
        setupBindings();
    }

    private void setupValueChangedListeners() {

        droneView.getDrone().rotateProperty().addListener((observable, oldValue, newValue) -> {
            setRotate(newValue.doubleValue());
        });

    }

    private void setupBindings() {
        pivotPosition.xProperty().bind(droneView.translateXProperty());
        pivotPosition.yProperty().bind(droneView.translateYProperty());
        pivotPosition.zProperty().bind(droneView.translateZProperty());
        //viewingAngle.bind(droneView.rotateProperty());
    }

}
