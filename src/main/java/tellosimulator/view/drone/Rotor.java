package tellosimulator.view.drone;

import javafx.animation.RotateTransition;
import javafx.scene.Node;

public class Rotor {
    Node node;
    RotateTransition rotateTransition;

    Rotor(Node node) {
        this.node = node;
        this.rotateTransition = new RotateTransition();
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public RotateTransition getRotateTransition() {
        return rotateTransition;
    }

    public void setRotateTransition(RotateTransition rotateTransition) {
        this.rotateTransition = rotateTransition;
    }
}
