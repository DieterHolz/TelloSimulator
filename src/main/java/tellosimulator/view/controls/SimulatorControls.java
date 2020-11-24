package tellosimulator.view.controls;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import tellosimulator.controller.DroneController;
import tellosimulator.model.DroneModel;
import tellosimulator.view.world.CubeWorld;
import java.util.Locale;

public class SimulatorControls extends GridPane {
    private final DroneModel droneModel;
    private final DroneController droneController;
    private CubeWorld cubeWorld;
    private static final String NUMBER_FORMAT = "%.1f";
    private static final Locale LOCALE_CH = new Locale("de", "CH");

    private Button resetButton;
    private Label xPositionLabel;
    private Text xPositionText;
    private Label yPositionLabel;
    private Text yPositionText;
    private Label zPositionLabel;
    private Text zPositionText;
    private Label yawAngleLabel;
    private Text yawAngleText;
    private Button cameraButton;

    private Label roomWidthLabel;
    private Slider roomWidthSlider;
    private Label roomWidth;
    private Label roomDepthLabel;
    private Slider roomDepthSlider;
    private Label roomDepth;
    private Label roomHeightLabel;
    private Slider roomHeightSlider;
    private Label roomHeight;
    private Label gridSizeLabel;
    private Slider gridSizeSlider;
    private Label gridSize;

    //TODO: add all other values

    public SimulatorControls(DroneModel droneModel, DroneController droneController, CubeWorld cubeWorld){
        this.droneModel = droneModel;
        this.droneController = droneController;
        this.cubeWorld = cubeWorld;
        initializeSelf();
        initializeParts();
        layoutParts();
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
    }

    private void initializeSelf() {
        setHgap(10);
        setVgap(5);
        setPadding(new Insets(4,4,4,4));
        setMinWidth(200);
    }

    private void initializeParts() {
        xPositionLabel = new Label("X-Position:");
        xPositionText = new Text();

        zPositionLabel = new Label("Z-Position:");
        zPositionText = new Text();

        yPositionLabel = new Label("Height:");
        yPositionText = new Text();

        yawAngleLabel = new Label("Rotation/Yaw:");
        yawAngleText = new Text("0");

        cameraButton = new Button("Change to Drone Camera");

        resetButton = new Button("Reset Drone Position");

        roomWidthLabel = new Label("Room width (m):");
        roomWidthSlider = new Slider(1, 25, 5);
        roomWidthSlider.setBlockIncrement(0.1);
        roomWidth = new Label("5");

        roomDepthLabel = new Label("Room depth (m):");
        roomDepthSlider = new Slider(1, 25, 5);
        roomDepthSlider.setBlockIncrement(0.1);
        roomDepth = new Label("5");

        roomHeightLabel = new Label("Room height (m):");
        roomHeightSlider = new Slider(1, 25, 3);
        roomHeightSlider.setBlockIncrement(0.1);
        roomHeight = new Label("3");

        gridSizeLabel = new Label("Grid size (cm):");
        gridSizeSlider = new Slider(5, 100, 40);
        gridSizeSlider.setBlockIncrement(0.1);
        gridSize = new Label("40");
        //TODO: init all other values

    }

    private void layoutParts() {
        add(xPositionLabel, 1, 1);
        add(xPositionText, 2, 1);
        add(zPositionLabel, 1, 2);
        add(zPositionText, 2, 2);
        add(yPositionLabel, 1, 3);
        add(yPositionText, 2, 3);
        add(yawAngleLabel, 1, 4);
        add(yawAngleText, 2, 4);

        add(cameraButton, 1, 5);
        setMargin(cameraButton, new Insets(20,0,0,0));
        add(resetButton, 1, 6);
        setMargin(resetButton, new Insets(20,0,0,0));

        add(roomWidthLabel, 1, 8);
        add(roomWidthSlider, 2, 8);
        add(roomWidth, 3, 8);
        add(roomDepthLabel, 1, 9);
        add(roomDepthSlider, 2, 9);
        add(roomDepth, 3, 9);
        add(roomHeightLabel, 1, 10);
        add(roomHeightSlider, 2, 10);
        add(roomHeight, 3, 10);
        add(gridSizeLabel, 1, 11);
        add(gridSizeSlider, 2, 11);
        add(gridSize, 3, 11);
    }

    private void setupEventHandlers() {
        resetButton.setOnAction(event -> {
            droneController.resetValues();
        });

        cameraButton.setOnAction(event -> {
            droneModel.setDroneCameraActive(!droneModel.isDroneCameraActive());
        });
    }

    private void setupValueChangedListeners() {
        droneModel.yawProperty().addListener((observable, oldValue, newValue) -> {
            yawAngleText.textProperty().setValue(String.valueOf(Math.round((360 - newValue.doubleValue()) % 360)));
        });

        droneModel.droneCameraActiveProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                cameraButton.setText("Change to Simulator Camera");
            } else {
                cameraButton.setText("Change to Drone Camera");
            }
        });

        roomWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            cubeWorld.setRoomSizeX(Double.valueOf(newValue) * 100);
            updateCubeWorld();
        });

        roomHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            cubeWorld.setRoomSizeY(Double.valueOf(newValue) * 100);
            updateCubeWorld();
        });

        roomDepth.textProperty().addListener((observable, oldValue, newValue) -> {
            cubeWorld.setRoomSizeZ(Double.valueOf(newValue) * 100);
            updateCubeWorld();
        });

        gridSize.textProperty().addListener((observable, oldValue, newValue) -> {
            cubeWorld.setGrildLineSpacing(Double.valueOf(newValue));
            updateCubeWorld();
        });
    }

    private void updateCubeWorld() {
        cubeWorld.getChildren().clear();
        cubeWorld.init();
    }

    private void setupBindings() {
        xPositionText.textProperty().bind(droneModel.xPositionProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        yPositionText.textProperty().bind(droneModel.yPositionProperty().negate().asString(LOCALE_CH, NUMBER_FORMAT));
        zPositionText.textProperty().bind(droneModel.zPositionProperty().asString(LOCALE_CH, NUMBER_FORMAT));

        roomWidth.textProperty().bind(roomWidthSlider.valueProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        roomDepth.textProperty().bind(roomDepthSlider.valueProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        roomHeight.textProperty().bind(roomHeightSlider.valueProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        gridSize.textProperty().bind(gridSizeSlider.valueProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        //TODO: bind other values
    }
}
