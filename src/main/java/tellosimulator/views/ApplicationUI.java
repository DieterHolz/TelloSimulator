package tellosimulator.views;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import tellosimulator.TelloSimulator;
import tellosimulator.drone.TelloDrone;

public class ApplicationUI extends StackPane {
    private final TelloDrone telloDrone;
    private Group drone3d;

    public ApplicationUI(TelloDrone telloDrone) {
        this.telloDrone = telloDrone;

        initialize3dDrone(telloDrone, 18, 5, 16);
        initialize3dRoom(TelloSimulator.ROOM_WIDTH,TelloSimulator.ROOM_HEIGHT,TelloSimulator.ROOM_DEPTH,TelloSimulator.WALL_DEPTH);
    }

    private void initialize3dDrone(TelloDrone telloDrone, double width, double height, double depth) {

        PhongMaterial lightskyblue = new PhongMaterial();
        lightskyblue.setDiffuseColor(Color.LIGHTSKYBLUE);
        Box drone = new Box(width, height, depth);
        drone.setMaterial(lightskyblue);

        drone3d = new Group();
        drone3d.getChildren().add(drone);

        drone3d.translateXProperty().set(TelloSimulator.ROOM_WIDTH /2);
        drone3d.translateYProperty().set(-height/2);
        drone3d.translateZProperty().set(Math.min(TelloSimulator.ROOM_DEPTH /5, 500));

        telloDrone.setDrone3d(drone3d);
        this.getChildren().add(drone3d);
    }

    private void initialize3dRoom(double width, double height, double depth, double wallDepth) {

        Group room3D = new Group();
        room3D.getChildren().addAll(
                prepareBox(width, height, wallDepth, Color.RED, width/2, -height/2, depth), //wallBack
                prepareBox(wallDepth, height, depth, Color.BLUE, width, -height/2, depth/2), //wallRight
                prepareBox(wallDepth, height, depth, Color.GREEN, 0, -height/2, depth/2), //wallLeft
                prepareBox(width, wallDepth, depth, Color.YELLOW, width/2, -height, depth/2), //ceiling
                prepareBox(width, wallDepth, depth, Color.BLACK, width/2, 0, depth/2)); //floor
        room3D.translateXProperty().set(width/2);
        room3D.translateYProperty().set(-height/2);

        this.getChildren().add(room3D);
    }

    private Box prepareBox(double xDimension, double yDimension, double zDimension, Color color, double xTranslate, double yTranslate, double zTranslate) {

        Box box = new Box(xDimension, yDimension, zDimension);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);

        box.setMaterial(material);

        box.translateXProperty().set(xTranslate);
        box.translateYProperty().set(yTranslate);
        box.translateZProperty().set(zTranslate);

        return box;
    }


}
