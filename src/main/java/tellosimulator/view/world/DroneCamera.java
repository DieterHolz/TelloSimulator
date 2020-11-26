package tellosimulator.view.world;

import javafx.scene.PerspectiveCamera;
import tellosimulator.common.VectorHelper;
import tellosimulator.model.DroneModel;

/**
 * The PerspectiveCamera bound to the position and yaw in the DroneModel. Represents the integrated forward facing
 * camera found in the Tello.
 *
 * @see DroneModel
 */
public class DroneCamera extends PerspectiveCamera {
    private  DroneModel droneModel;

    DroneCamera(DroneModel droneModel) {
        super(true);

        this.droneModel = droneModel;
        setNearClip(1.0);
        setFarClip(10000.0);

        setRotationAxis(VectorHelper.getUpwardsNormalVector());
        updateDroneCameraPosition(droneModel.xPositionProperty().doubleValue(), droneModel.yPositionProperty().doubleValue(), droneModel.zPositionProperty().doubleValue(), droneModel.yawProperty().doubleValue());

        setupValueChangedListeners();
    }

    private void setupValueChangedListeners() {

        droneModel.yawProperty().addListener((observable, oldValue, newValue) -> {
            updateDroneCameraPosition(droneModel.xPositionProperty().doubleValue(), droneModel.yPositionProperty().doubleValue(), droneModel.zPositionProperty().doubleValue(), newValue.doubleValue());
        });

        droneModel.xPositionProperty().addListener(((observable, oldValue, newValue) -> {
            updateDroneCameraPosition(newValue.doubleValue(), droneModel.yPositionProperty().doubleValue(), droneModel.zPositionProperty().doubleValue(), droneModel.yawProperty().doubleValue());
        }));

        droneModel.yPositionProperty().addListener(((observable, oldValue, newValue) -> {
            updateDroneCameraPosition(droneModel.xPositionProperty().doubleValue(), newValue.doubleValue(), droneModel.zPositionProperty().doubleValue(), droneModel.yawProperty().doubleValue());
        }));

        droneModel.zPositionProperty().addListener(((observable, oldValue, newValue) -> {
            updateDroneCameraPosition(droneModel.xPositionProperty().doubleValue(), droneModel.yPositionProperty().doubleValue(), newValue.doubleValue(), droneModel.yawProperty().doubleValue());
        }));

    }

    private void updateDroneCameraPosition(double xTranslate, double yTranslate, double zTranslate, double yaw) {
        setTranslateX(xTranslate);
        setTranslateY(yTranslate);
        setTranslateZ(zTranslate);
        setRotate(yaw);
    }

}
