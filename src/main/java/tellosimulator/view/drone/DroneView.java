package tellosimulator.view.drone;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import tellosimulator.common.VectorHelper;
import tellosimulator.model.DroneModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DroneView extends Group {
    private DroneModel droneModel;

    private double droneScalingFactor = 0.45;

    private Group drone;
    private Group drone3DModel;
    private Group pitchContainer;
    private Group rollContainer;

    List<Rotor> rotors = new ArrayList<>();

    public DroneView(DroneModel droneModel) throws IOException {
        this.droneModel = droneModel;
        buildDrone();
        setupBindings();
        setupValueChangedListeners();
    }

    private void buildDrone() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/TelloDroneLowpoly.fxml"));
        drone3DModel = loader.load();

        ObservableList<Node> children = drone3DModel.getChildren();
        Rotor backRightRotor = new Rotor(children.get(42));
        Rotor frontRightRotor = new Rotor(children.get(33));
        Rotor backLeftRotor = new Rotor(children.get(37));
        Rotor frontLeftRotor = new Rotor(children.get(14));

        rotors.add(backRightRotor);
        rotors.add(frontRightRotor);
        rotors.add(backLeftRotor);
        rotors.add(frontLeftRotor);

        drone = new Group();
        pitchContainer = new Group();
        rollContainer = new Group();

        drone.setRotationAxis(VectorHelper.getUpwardsNormalVector());
        rollContainer.setRotationAxis(Rotate.Z_AXIS);
        pitchContainer.setRotationAxis(Rotate.X_AXIS);

        drone3DModel.setScaleX(droneScalingFactor);
        drone3DModel.setScaleY(droneScalingFactor);
        drone3DModel.setScaleZ(droneScalingFactor);
        drone3DModel.setRotate(180);
        pitchContainer.getChildren().add(drone3DModel);
        rollContainer.getChildren().add(pitchContainer);
        drone.getChildren().add(rollContainer);
        this.getChildren().add(drone);
    }


    private void setupBindings() {
        this.translateXProperty().bind(droneModel.xPositionProperty());
        this.translateYProperty().bind(droneModel.yPositionProperty());
        this.translateZProperty().bind(droneModel.zPositionProperty());
        this.getDrone().rotateProperty().bind(droneModel.yawProperty());
        this.getRollContainer().rotateProperty().bind(droneModel.rollProperty());
        this.getPitchContainer().rotateProperty().bind(droneModel.pitchProperty());
        droneModel.tofProperty().bind(this.translateYProperty().negate());
    }


    private void setupValueChangedListeners() {
        droneModel.motorsRunningProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                spinUpRotors();
            } else {
                spinDownRotors();
            }
        });
    }

    private void spinUpRotors() {
        droneModel.setMotorsRunning(true);
        for (Rotor rotor : getRotors()) {
            RotateTransition rotateTransition = new RotateTransition();
            rotateTransition.setAxis(Rotate.Y_AXIS);
            rotateTransition.setDuration( Duration.seconds(0.1) );
            rotateTransition.setByAngle( 360 );
            rotateTransition.setNode(rotor.getNode());
            rotateTransition.setCycleCount( Animation.INDEFINITE );
            rotor.setRotateTransition(rotateTransition);
            rotateTransition.play();
        }
    }

    private void spinDownRotors() {
        droneModel.setMotorsRunning(false);
        for (Rotor rotor : getRotors()) {
            if (rotor.getRotateTransition().statusProperty().get() == Animation.Status.RUNNING) {
                rotor.getRotateTransition().stop();
            }
        }
    }

    public Group getDrone() {
        return drone;
    }

    public void setDrone(Group drone) {
        this.drone = drone;
    }

    public Group getDrone3DModel() {
        return drone3DModel;
    }

    public void setDrone3DModel(Group drone3DModel) {
        this.drone3DModel = drone3DModel;
    }

    public Group getPitchContainer() {
        return pitchContainer;
    }

    public Group getRollContainer() {
        return rollContainer;
    }

    public List<Rotor> getRotors() {
        return rotors;
    }
}
