package tellosimulator.view.layout;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tellosimulator.controller.DroneController;
import tellosimulator.log.Log;
import tellosimulator.model.DroneModel;
import tellosimulator.view.world.Room3d;
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
    private SimulatorControls simulatorControls;
    private NetworkControls networkControls;

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
        setupValueChangeListeners();
        setupEventHandlers();
        setupBindings();
    }


    private void initializeParts() throws IOException {
        simulator3DScene = new Simulator3DScene(stage, buildSceneGraph());
        simulatorControls = new SimulatorControls(droneModel, droneController);
        networkControls = new NetworkControls(droneController);
        logBox = new LogBox(log);
    }

    private void layoutParts() {
        setPadding(new Insets(10));
        setCenter(simulator3DScene);
        setLeft(simulatorControls);
        setRight(networkControls);
        setBottom(logBox);
    }


    private void setupValueChangeListeners() {
    }


    private void setupEventHandlers() {
    }

    private void setupBindings() {
    }

    private Parent buildSceneGraph() {
        Group droneGroup = droneView;

        Room3d room3d = new Room3d(Simulator3DScene.ROOM_WIDTH,Simulator3DScene.ROOM_HEIGHT,Simulator3DScene.ROOM_DEPTH,Simulator3DScene.WALL_DEPTH);
        Group roomGroup = room3d.getRoom3d();
        //CubeWorld cubeWorld = new CubeWorld(false);

        // we have to add all 3D elements as a Group to the Scene Graph
        Group root = new Group();
        root.getChildren().addAll(droneGroup, roomGroup); //add cubeWorld
        return root;
    }




}
