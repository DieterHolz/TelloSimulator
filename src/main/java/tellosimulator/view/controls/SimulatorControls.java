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

/**
 * Simple control displaying drone information and providing buttons and slider to control the simulator.
 */
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
    private Label pitchAngleLabel;
    private Text pitchAngleText;
    private Label rollAngleLabel;
    private Text rollAngleText;

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
        setPrefWidth(200);
    }

    private void initializeParts() {
        xPositionLabel = new Label("X-Position:");
        xPositionText = new Text("0");
        yPositionLabel = new Label("Y-Position:");
        yPositionText = new Text("0");
        zPositionLabel = new Label("Z-Position:");
        zPositionText = new Text("0");

        yawAngleLabel = new Label("Yaw:");
        yawAngleText = new Text("0");
        pitchAngleLabel = new Label("Pitch:");
        pitchAngleText = new Text("0");
        rollAngleLabel = new Label("Roll:");
        rollAngleText = new Text("0");

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
    }

    private void layoutParts() {
        add(xPositionLabel, 1, 1);
        add(xPositionText, 2, 1);
        add(yPositionLabel, 1, 2);
        add(yPositionText, 2, 2);
        add(zPositionLabel, 1, 3);
        add(zPositionText, 2, 3);
        add(yawAngleLabel, 1, 4);
        add(yawAngleText, 2, 4);
        add(pitchAngleLabel, 1, 5);
        add(pitchAngleText, 2, 5);
        add(rollAngleLabel, 1, 6);
        add(rollAngleText, 2, 6);

        add(resetButton, 1, 7);
        setMargin(resetButton, new Insets(20,0,0,0));

        add(cameraButton, 1, 8, 2, 1);
        setMargin(cameraButton, new Insets(20,0,20,0));

        add(roomWidthLabel, 1, 11);
        add(roomWidthSlider, 1, 12);
        add(roomWidth, 2, 12);
        add(roomDepthLabel, 1, 13);
        add(roomDepthSlider, 1, 14);
        add(roomDepth, 2, 14);
        add(roomHeightLabel, 1, 15);
        add(roomHeightSlider, 1, 16);
        add(roomHeight, 2, 16);
        add(gridSizeLabel, 1, 17);
        add(gridSizeSlider, 1, 18);
        add(gridSize, 2, 18);
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

        droneModel.pitchProperty().addListener((observable, oldValue, newValue) -> {
            pitchAngleText.textProperty().setValue(String.valueOf(Math.round((360 - newValue.doubleValue()) % 360)));
        });

        droneModel.rollProperty().addListener((observable, oldValue, newValue) -> {
            rollAngleText.textProperty().setValue(String.valueOf(Math.round((360 - newValue.doubleValue()) % 360)));
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
            cubeWorld.setGridLineSpacing(Double.valueOf(newValue));
            updateCubeWorld();
        });
    }

    private void updateCubeWorld() {
        cubeWorld.getChildren().clear();
        cubeWorld.init();
    }

    private void setupBindings() {
        yPositionText.textProperty().bind(droneModel.xPositionProperty().negate().asString(LOCALE_CH, NUMBER_FORMAT));
        zPositionText.textProperty().bind(droneModel.yPositionProperty().negate().asString(LOCALE_CH, NUMBER_FORMAT));
        xPositionText.textProperty().bind(droneModel.zPositionProperty().asString(LOCALE_CH, NUMBER_FORMAT));

        roomWidth.textProperty().bind(roomWidthSlider.valueProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        roomDepth.textProperty().bind(roomDepthSlider.valueProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        roomHeight.textProperty().bind(roomHeightSlider.valueProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        gridSize.textProperty().bind(gridSizeSlider.valueProperty().asString(LOCALE_CH, NUMBER_FORMAT));
    }
}
