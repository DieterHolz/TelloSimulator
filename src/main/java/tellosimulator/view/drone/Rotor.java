package tellosimulator.view.drone;

import javafx.animation.RotateTransition;
import javafx.scene.Node;

/**
 * Wrapper for the DroneView rotor nodes, containing the rotor node itself and a corresponding RotateTransition.
 *
 * @see DroneView
 */
public class Rotor {
    private Node node;
    private RotateTransition rotateTransition;

    public Rotor(Node node) {
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
