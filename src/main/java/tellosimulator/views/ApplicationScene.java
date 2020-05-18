package tellosimulator.views;

import javafx.scene.*;
import javafx.scene.paint.Color;

public class ApplicationScene extends Scene {

    public static final int WIDTH = 1400;
    public static final int HEIGHT = 800;


    public ApplicationScene(Parent root) {
        super(root, WIDTH, HEIGHT);

        Camera camera = new PerspectiveCamera();
        this.setFill(Color.BLUE);
        this.setCamera(camera);
    }
}
