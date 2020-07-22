package tellosimulator.views;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tellosimulator.network.TelloSDKValues;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;

import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;

public class SimulatorPane extends BorderPane {
    private final Stage stage;
    private final Drone3d drone;

    private Simulator3DScene simulator3DScene;
    private SimulatorControls simulatorControls;
    private NetworkControls networkControls;


    public SimulatorPane(Stage stage, Drone3d drone) throws IOException {
        this.stage = stage;
        this.drone = drone;
        initializeParts();
        layoutParts();
        setupValueChangeListeners();
        setupEventHandlers();
        setupBindings();
    }


    private void initializeParts() throws IOException {
        simulator3DScene = new Simulator3DScene(stage, buildSceneGraph());
        simulatorControls = new SimulatorControls(drone);
        networkControls = new NetworkControls(drone, new TelloSDKValues(), new UDPCommandConnection(drone), new UDPStateConnection(drone));
    }

    private void layoutParts() {
        setPadding(new Insets(10));
        setCenter(simulator3DScene);
        setLeft(simulatorControls);
        setRight(networkControls);
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
