package tellosimulator.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;

import java.io.IOException;

public class DroneView extends Group {

    public static final int DRONE_WIDTH = 18;
    public static final int DRONE_HEIGHT = 5;
    public static final int DRONE_DEPTH = 16;

    private Group drone;
    private Group drone3DModel;
    private Group pitchContainer;
    private Group rollContainer;


    public DroneView() throws IOException {
        buildDrone();
    }

    private void buildDrone() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/TelloDrone.fxml"));
        drone3DModel = loader.load();
        ObservableList<Node> children = drone3DModel.getChildren();
        // System.out.println(children.toString()); //TODO: identify rotor child nodes to make them rotate

        drone = new Group();
        pitchContainer = new Group();
        rollContainer = new Group();

        drone3DModel.setScaleX(1);
        drone3DModel.setScaleY(1);
        drone3DModel.setScaleZ(1);
        drone3DModel.setRotate(180);
        drone3DModel.setTranslateY(-DRONE_HEIGHT/2);
        pitchContainer.getChildren().add(drone3DModel);
        rollContainer.getChildren().add(pitchContainer);
        drone.getChildren().add(rollContainer);
        this.getChildren().add(drone);
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
}
