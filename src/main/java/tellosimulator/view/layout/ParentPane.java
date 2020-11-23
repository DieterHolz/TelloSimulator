package tellosimulator.view.layout;


import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tellosimulator.controller.DroneController;
import tellosimulator.log.Log;
import tellosimulator.model.DroneModel;
import tellosimulator.view.drone.DroneView;

import java.io.IOException;

public class ParentPane extends Pane {

    private final Stage stage;
    private final DroneController droneController;
    private final DroneModel droneModel;
    private final DroneView droneView;

    private Log log;

    private SettingsPane settingsPane;
    private SimulatorPane simulatorPane;

    public ParentPane(Stage stage, DroneController droneController, DroneModel droneModel, DroneView droneView, Log log) throws IOException {
        this.stage = stage;
        this.droneController = droneController;
        this.droneModel = droneModel;
        this.droneView = droneView;
        this.log = log;

        initializeSelf();
        initializeParts();
        layoutParts();
    }

    private void initializeSelf() {
        setPrefSize(1500, 900);
    }

    private  void initializeParts() {
        settingsPane = new SettingsPane(this);
    }

    private void layoutParts() {
        getChildren().add(settingsPane);
    }

    public void initializeSimulatorScreen(double roomWidth, double roomDepth, double roomHeight, double gridSize) throws IOException {
        simulatorPane = new SimulatorPane(stage, droneController, droneModel, droneView, log, roomWidth, roomDepth, roomHeight, gridSize);

        getChildren().remove(settingsPane);
        getChildren().add(simulatorPane);
    }


}
