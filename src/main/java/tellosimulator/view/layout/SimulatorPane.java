package tellosimulator.view.layout;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tellosimulator.controller.DroneController;
import tellosimulator.log.Log;
import tellosimulator.model.DroneModel;
import tellosimulator.view.world.CubeWorld;
import tellosimulator.view.controls.NetworkControls;
import tellosimulator.view.controls.SimulatorControls;
import tellosimulator.view.drone.DroneView;
import tellosimulator.view.log.LogBox;
import tellosimulator.view.world.Simulator3DScene;

import java.io.IOException;

public class SimulatorPane extends BorderPane {
    private final Stage stage;
    private final DroneController droneController;
    private final DroneModel droneModel;
    private final DroneView droneView;
    private Simulator3DScene simulator3DScene;
    private StackPane subSceneHolder;
    private SimulatorControls simulatorControls;
    private NetworkControls networkControls;
    private CubeWorld cubeWorld;

    private Log log;
    private LogBox logBox;

    public SimulatorPane(Stage stage, DroneController droneController, DroneModel droneModel, DroneView droneView, Log log) throws IOException {
        this.stage = stage;
        this.droneController = droneController;
        this.droneModel = droneModel;
        this.droneView = droneView;
        this.log = log;
        initializeParts();
        layoutParts();
        setupValueChangedListeners();
        setupBindings();
    }

    private void initializeParts() throws IOException {

        simulator3DScene = new Simulator3DScene(buildSceneGraph(), droneView, droneModel);
        subSceneHolder = new StackPane(simulator3DScene);
        subSceneHolder.setMinWidth(160);
        subSceneHolder.setMinHeight(90);
        subSceneHolder.setPrefSize(1280, 720);

        simulatorControls = new SimulatorControls(droneModel, droneController, cubeWorld);
        networkControls = new NetworkControls(droneController);
        logBox = new LogBox(log);

    }

    private void layoutParts() {
        setPadding(new Insets(10));
        setCenter(subSceneHolder);
        setLeft(simulatorControls);
        setRight(networkControls);
        setBottom(logBox);
    }

    private void setupValueChangedListeners() {
        droneModel.droneCameraActiveProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                simulator3DScene.setCamera(simulator3DScene.getDroneCamera());
            } else {
                simulator3DScene.setCamera(simulator3DScene.getSimulatorCamera());
            }
        });
    }

    private void setupBindings() {
        simulator3DScene.widthProperty().bind(subSceneHolder.widthProperty());
        simulator3DScene.heightProperty().bind(subSceneHolder.heightProperty());
    }

    private Parent buildSceneGraph() {
        cubeWorld = new CubeWorld(1000, 500, 800, 40);

        // we have to add all 3D elements as a Group to the Scene Graph
        Group root = new Group();
        root.getChildren().addAll(droneView, cubeWorld);
        return root;
    }
}
