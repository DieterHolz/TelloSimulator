package tellosimulator.views;

import javafx.geometry.Point3D;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SimulatorControls extends VBox {
    private final Drone3d drone;

    // todo: add all your controls here

    private Button button;

    public SimulatorControls(Drone3d drone){
        this.drone = drone;
        initializeSelf();
        initializeParts();
        layoutParts();
        setupEventHandlers();
        setupBindings();
    }

    private void initializeSelf() {
    }

    private void initializeParts() {
        button = new Button("Reset drone");
    }

    private void layoutParts() {
        getChildren().addAll(button);
    }

    private void setupEventHandlers() {
        button.setOnAction(event -> {
            drone.setXPosition(0);
            drone.setYPosition(0);
            drone.setZPosition(0);
        });
    }

    private void setupBindings() {

    }
}
