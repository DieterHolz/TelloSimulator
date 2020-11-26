package tellosimulator.view.world;

import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import tellosimulator.view.drone.DroneView;

/**
 * The PerspectiveCamera in the 3D-Scene. Position is bound to the {@code DroneView}. Pivot and zoom is controllable
 * by the user (MouseEvents for rotating and moving the camera are handled on the {@code Simulator3DScene}).
 *
 * @see DroneView
 * @see Simulator3DScene
 */
public class SimulatorCamera extends PerspectiveCamera {
    private DroneView droneView;

    private Translate pivotPosition = new Translate();
    public Translate cameraPosition = new Translate();

    public Rotate cameraRotateX = new Rotate();
    public Rotate cameraRotateY = new Rotate();

    public SimulatorCamera(DroneView droneView, double camZOffset, double camViewingAngle) {
        super(true);
        this.droneView = droneView;
        setNearClip(1.0);
        setFarClip(10000.0);

        cameraRotateX.setAxis(Rotate.X_AXIS);
        cameraRotateX.setAngle(camViewingAngle);

        cameraRotateY.setAxis(Rotate.Y_AXIS);

        cameraPosition.setZ(-camZOffset);

        getTransforms().addAll(pivotPosition, cameraRotateY, cameraRotateX, cameraPosition); //order matters!

        setupBindings();
    }

    private void setupBindings() {
        pivotPosition.xProperty().bind(droneView.translateXProperty());
        pivotPosition.yProperty().bind(droneView.translateYProperty());
        pivotPosition.zProperty().bind(droneView.translateZProperty());
    }
}
