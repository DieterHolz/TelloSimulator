package tellosimulator.view.world;

import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import tellosimulator.view.drone.DroneView;

public class SimulatorCamera extends PerspectiveCamera {
    private DroneView droneView;

    private Translate pivotPosition = new Translate();
    public Translate cameraPosition = new Translate();

    public Rotate cameraRotateX = new Rotate();
    public Rotate cameraRotateY = new Rotate();

    private double cameraZOffset = 200;
    private double cameraYOffset = 25;
    private double viewingAngle = -10;

    public SimulatorCamera(DroneView droneView) {
        super(true);
        this.droneView = droneView;
        setNearClip(1.0);
        setFarClip(10000.0);

        cameraRotateX.setAxis(Rotate.X_AXIS);
        cameraRotateX.setAngle(viewingAngle);

        cameraRotateY.setAxis(Rotate.Y_AXIS);

        cameraPosition.setZ(-cameraZOffset);
        cameraPosition.setY(-cameraYOffset);

        getTransforms().addAll(pivotPosition, cameraRotateY, cameraRotateX, cameraPosition); //order matters!

        setupBindings();
    }

    private void setupBindings() {
        pivotPosition.xProperty().bind(droneView.translateXProperty());
        pivotPosition.yProperty().bind(droneView.translateYProperty());
        pivotPosition.zProperty().bind(droneView.translateZProperty());
    }
}
