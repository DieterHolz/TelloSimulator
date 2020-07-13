package tellosimulator.views;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Room3d {

    private Group room3D;

    public Room3d(double width, double height, double depth, double wallDepth) {
        room3D = new Group();
        room3D.getChildren().addAll(
                prepareBox(width, height, wallDepth, Color.RED, width/2, -height/2, depth), //wallBack
                prepareBox(wallDepth, height, depth, Color.BLUE, width, -height/2, depth/2), //wallRight
                prepareBox(wallDepth, height, depth, Color.GREEN, 0, -height/2, depth/2), //wallLeft
                prepareBox(width, wallDepth, depth, Color.YELLOW, width/2, -height, depth/2), //ceiling
                prepareBox(width, wallDepth, depth, Color.BLACK, width/2, 0, depth/2)); //floor
    }

    public Group getRoom3d() {
        return room3D;
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
