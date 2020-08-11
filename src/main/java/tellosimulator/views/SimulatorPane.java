package tellosimulator.views;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tellosimulator.log.Level;
import tellosimulator.log.Log;
import tellosimulator.log.Logger;
import tellosimulator.network.TelloSDKValues;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;

import java.io.IOException;

public class SimulatorPane extends BorderPane {
    private final Stage stage;
    private final Drone3d drone;


    private Simulator3DScene simulator3DScene;
    private SimulatorControls simulatorControls;
    private NetworkControls networkControls;

    private Log log;
    private LogViewer logViewer;


    public SimulatorPane(Stage stage, Drone3d drone, Log log) throws IOException {
        this.stage = stage;
        this.drone = drone;
        this.log = log;
        initializeParts();
        layoutParts();
        setupValueChangeListeners();
        setupEventHandlers();
        setupBindings();
    }


    private void initializeParts() throws IOException {
        simulator3DScene = new Simulator3DScene(stage, buildSceneGraph());
        simulatorControls = new SimulatorControls(drone);
        networkControls = new NetworkControls(drone);
        logViewer = new LogViewer(log);
    }

    private void layoutParts() {
        setPadding(new Insets(10));
        setCenter(simulator3DScene);
        setLeft(simulatorControls);
        setRight(networkControls);
        setBottom(logViewer);
    }


    private void setupValueChangeListeners() {
    }


    private void setupEventHandlers() {
    }

    private void setupBindings() {
    }

    private Parent buildSceneGraph() {
        Group droneGroup = drone.getDrone();

        Room3d room3d = new Room3d(Simulator3DScene.ROOM_WIDTH,Simulator3DScene.ROOM_HEIGHT,Simulator3DScene.ROOM_DEPTH,Simulator3DScene.WALL_DEPTH);
        Group roomGroup = room3d.getRoom3d();

        // we have to add all 3D elements as a Group to the Scene Graph
        Group root = new Group();
        root.getChildren().addAll(droneGroup, roomGroup);
        return root;
    }




}
