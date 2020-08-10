package tellosimulator.views;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

import java.awt.*;

public class Room3d {

    private Group room3D;
    private double width;
    private double height;
    private double depth;
    private double wallDepth;

    public Room3d(double width, double height, double depth, double wallDepth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.wallDepth = wallDepth;

        room3D = new Group();
        room3D.getChildren().addAll(
                prepareBox(width, height, wallDepth, Color.grayRgb(220), width/2, -height/2, depth+wallDepth/2), //wallBack
                prepareBox(wallDepth, height, depth, Color.grayRgb(220), width+wallDepth/2, -height/2, depth/2), //wallRight
                prepareBox(wallDepth, height, depth, Color.grayRgb(220), 0-wallDepth/2, -height/2, depth/2), //wallLeft
                prepareBox(width, wallDepth, depth, Color.grayRgb(220), width/2, -height-wallDepth/2, depth/2), //ceiling
                prepareBox(width, wallDepth, depth, Color.grayRgb(220), width/2, 0+wallDepth/2, depth/2) //floor
        );
        room3D.getChildren().addAll(prepareLightSources());
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

    private Node[] prepareLightSources() {
        PointLight pointLight = new PointLight();
        pointLight.setColor(Color.rgb(252,252,255));
        pointLight.getTransforms().add(new Translate(width/2,-height+wallDepth,depth/2));

        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(Color.rgb(100,100,100));

        return new Node[]{pointLight, ambientLight};
    }
}
