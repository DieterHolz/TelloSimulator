package tellosimulator.views;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import tellosimulator.TelloSimulator;
import tellosimulator.drone.TelloDrone;

public class ApplicationUI extends StackPane {
    private final TelloDrone telloDrone;

    public ApplicationUI(TelloDrone telloDrone) {
        this.telloDrone = telloDrone;

        initialize3dDrone(18, 5, 16);
        initialize3dRoom(TelloSimulator.ROOMWIDTH,TelloSimulator.ROOMHEIGHT,TelloSimulator.ROOMDEPTH,TelloSimulator.WALLDEPTH);
    }

    private void initialize3dDrone(double width, double height, double depth) {

        PhongMaterial lightskyblue = new PhongMaterial();
        lightskyblue.setDiffuseColor(Color.LIGHTSKYBLUE);
        Box drone = new Box(width, height, depth);
        drone.setMaterial(lightskyblue);

        Group drone3d = new Group();
        drone3d.getChildren().add(drone);

        drone3d.translateXProperty().set(TelloSimulator.ROOMWIDTH /2);
        drone3d.translateYProperty().set(-height/2);
        drone3d.translateZProperty().set(Math.min(TelloSimulator.ROOMDEPTH/5, 500));

        this.getChildren().add(drone3d);
    }

    private void initialize3dRoom(double width, double height, double depth, double wallDepth) {

        PhongMaterial red = new PhongMaterial();
        red.setDiffuseColor(Color.RED);
        PhongMaterial blue = new PhongMaterial();
        blue.setDiffuseColor(Color.BLUE);
        PhongMaterial green = new PhongMaterial();
        green.setDiffuseColor(Color.GREEN);
        PhongMaterial yellow = new PhongMaterial();
        yellow.setDiffuseColor(Color.YELLOW);
        PhongMaterial black = new PhongMaterial();
        black.setDiffuseColor(Color.BLACK);

        Box wallBack = new Box(width, height, wallDepth);
        wallBack.setMaterial(red);
        wallBack.translateXProperty().set(width/2);
        wallBack.translateYProperty().set(-height/2);
        wallBack.translateZProperty().set(depth);

        Box wallRight = new Box(wallDepth, height, depth);
        wallRight.setMaterial(blue);
        wallRight.translateXProperty().set(width);
        wallRight.translateYProperty().set(-height/2);
        wallRight.translateZProperty().set(depth/2);

        Box wallLeft = new Box(wallDepth, height, depth);
        wallLeft.setMaterial(green);
        wallLeft.translateYProperty().set(-height/2);
        wallLeft.translateZProperty().set(depth/2);

        Box ceiling = new Box(width, wallDepth, depth);
        ceiling.setMaterial(yellow);
        ceiling.translateXProperty().set(width/2);
        ceiling.translateYProperty().set(-height);
        ceiling.translateZProperty().set(depth/2);

        Box floor = new Box(width, wallDepth, depth);
        floor.setMaterial(black);
        floor.translateXProperty().set(width/2);
        floor.translateZProperty().set(depth/2);

        Group room3D = new Group();
        room3D.getChildren().addAll(wallBack, wallRight, wallLeft, ceiling, floor);
        room3D.translateXProperty().set(width/2);
        room3D.translateYProperty().set(-height/2);

        this.getChildren().add(room3D);
    }

}
