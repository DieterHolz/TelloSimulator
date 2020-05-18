package tellosimulator.views;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Sphere;
import tellosimulator.drone.TelloDrone;

public class ApplicationUI extends StackPane {
    private final TelloDrone telloDrone;

    public ApplicationUI(TelloDrone telloDrone) {
        this.telloDrone = telloDrone;

        initialize3dDrone();
    }

    private void initialize3dDrone() {
        Sphere drone = new Sphere(50);

        Group group = new Group();
        group.getChildren().add(drone);

        drone.translateXProperty().set(ApplicationScene.WIDTH / 2);
        drone.translateYProperty().set(ApplicationScene.HEIGHT / 2);

        this.getChildren().add(group);
    }

}
