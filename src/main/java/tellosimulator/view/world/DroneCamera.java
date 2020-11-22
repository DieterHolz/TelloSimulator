package tellosimulator.view.world;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import tellosimulator.view.drone.DroneView;

public class DroneCamera extends PerspectiveCamera {

    private  DroneView droneView;

    private Translate pivotPosition = new Translate();
    private Translate cameraPosition = new Translate();

    private DoubleProperty rotateXAngle = new SimpleDoubleProperty();
    private DoubleProperty rotateYAngle = new SimpleDoubleProperty();
    private DoubleProperty rotateZAngle = new SimpleDoubleProperty();

    public Rotate cameraRotateX = new Rotate();
    public Rotate cameraRotateY = new Rotate();
    public Rotate cameraRotateZ = new Rotate();

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
        cameraRotateX.setAngle(rotateXAngle.doubleValue());

        cameraRotateY.setAxis(Rotate.Y_AXIS);
        cameraRotateY.setAngle(rotateYAngle.doubleValue());

        cameraRotateZ.setAxis(Rotate.Z_AXIS);
        cameraRotateZ.setAngle(rotateZAngle.doubleValue());

        getTransforms().addAll(pivotPosition, cameraRotateX, cameraRotateY, cameraRotateZ, cameraPosition); //order matters!
        setupValueChangedListeners();
        setupBindings();
    }

    private void setupValueChangedListeners() {

        droneView.getRollContainer().rotateProperty().addListener((observable, oldValue, newValue) -> {
            double differenceValue = (double) oldValue - (double) newValue;
            cameraRotateX.setAngle(differenceValue);
            System.out.println(differenceValue);
            getTransforms().add(cameraRotateX);
        });

        droneView.getDrone().rotateProperty().addListener((observable, oldValue, newValue) -> {
            double differenceValue = (double) oldValue - (double) newValue;
            cameraRotateY.setAngle(differenceValue);
            System.out.println(oldValue + " - " + newValue + " - " + differenceValue);
            getTransforms().add(cameraRotateY);
        });

        droneView.getPitchContainer().rotateProperty().addListener((observable, oldValue, newValue) -> {
            double differenceValue = (double) oldValue - (double) newValue;
            cameraRotateZ.setAngle(differenceValue);
            System.out.println(differenceValue);
            getTransforms().add(cameraRotateZ);
        });
    }

    private void setupBindings() {
        pivotPosition.xProperty().bind(droneView.translateXProperty());
        pivotPosition.yProperty().bind(droneView.translateYProperty());
        pivotPosition.zProperty().bind(droneView.translateZProperty());

//        rotateXAngle.bind(droneView.getRollContainer().rotateProperty());
//        rotateYAngle.bind(droneView.getDrone().rotateProperty());
//        rotateZAngle.bind(droneView.getPitchContainer().rotateProperty());
    }
}
