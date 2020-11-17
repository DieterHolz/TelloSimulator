package tellosimulator.view.controls;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import tellosimulator.controller.DroneController;
import tellosimulator.model.DroneModel;

import java.util.Locale;

public class SimulatorControls extends GridPane {
    private final DroneModel droneModel;
    private final DroneController droneController;

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

    //TODO: add all other values

    public SimulatorControls(DroneModel droneModel, DroneController droneController){
        this.droneModel = droneModel;
        this.droneController = droneController;
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

        //TODO: init all other values

        resetButton = new Button("Reset Drone Position");

    }

    private void layoutParts() {

        add(xPositionLabel, 1,1);
        add(xPositionText, 2, 1);
        add(zPositionLabel, 1, 2);
        add(zPositionText, 2, 2);
        add(yPositionLabel, 1, 3);
        add(yPositionText, 2, 3);
        add(yawAngleLabel, 1, 4);
        add(yawAngleText, 2, 4);
        //TODO: add all other values to grid
        add(cameraButton, 1,5);
        add(resetButton, 1, 6);

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
            yawAngleText.textProperty().setValue(String.valueOf(Math.round(newValue.doubleValue() % 360)));
        });

        droneModel.droneCameraActiveProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                cameraButton.setText("Change to Simulator Camera");
            } else {
                cameraButton.setText("Change to Drone Camera");
            }
        });
    }

    private void setupBindings() {
        xPositionText.textProperty().bind(droneModel.xPositionProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        yPositionText.textProperty().bind(droneModel.yPositionProperty().negate().asString(LOCALE_CH, NUMBER_FORMAT));
        zPositionText.textProperty().bind(droneModel.zPositionProperty().asString(LOCALE_CH, NUMBER_FORMAT));

        //TODO: bind other values
    }
}
