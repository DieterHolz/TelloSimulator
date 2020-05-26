package tellosimulator.views;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import tellosimulator.TelloSimulator;
import tellosimulator.drone.TelloDrone;

public class Drone3d {

    private Group drone3d;

    public Drone3d(TelloDrone telloDrone, double width, double height, double depth) {

        drone3d = new Group();

        PhongMaterial lightskyblue = new PhongMaterial();
        lightskyblue.setDiffuseColor(Color.LIGHTSKYBLUE);
        Box drone = new Box(width, height, depth);
        drone.setMaterial(lightskyblue);

        drone3d = new Group();
        drone3d.getChildren().add(drone);

        drone3d.translateXProperty().set(TelloSimulator.ROOM_WIDTH /2);
        drone3d.translateYProperty().set(-height/2);
        drone3d.translateZProperty().set(Math.min(TelloSimulator.ROOM_DEPTH /5, 500));
    }

    public Group getDrone3d() {
        return drone3d;
    }

    public void add3dDroneToTelloDrone(TelloDrone telloDrone) {
        telloDrone.setDrone3d(this.drone3d);
    }
}
