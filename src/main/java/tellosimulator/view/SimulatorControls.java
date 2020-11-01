package tellosimulator.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import tellosimulator.controller.DroneController;

import java.util.Locale;

public class SimulatorControls extends GridPane {
    private final DroneController droneController;
    private final DroneView droneView;

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
    //TODO: add all other values

    public SimulatorControls(DroneController droneController, DroneView droneView){
        this.droneController = droneController;
        this.droneView = droneView;
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
        add(yawAngleLabel, 1, 5);
        add(yawAngleText, 2, 5);
        //TODO: add all other values to grid

        add(resetButton, 1, 7);

    }

    private void setupEventHandlers() {
        resetButton.setOnAction(event -> {
            droneController.resetValues();
        });
    }

    private void setupValueChangedListeners() {
        droneView.rotateProperty().addListener((observable, oldValue, newValue) -> {
            yawAngleText.textProperty().setValue(String.valueOf(Math.round(newValue.doubleValue() % 360)));
        });
    }

    private void setupBindings() {
        xPositionText.textProperty().bind(droneView.translateXProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        yPositionText.textProperty().bind(droneView.translateYProperty().add(DroneView.DRONE_HEIGHT/2).negate().asString(LOCALE_CH, NUMBER_FORMAT));
        zPositionText.textProperty().bind(droneView.translateZProperty().asString(LOCALE_CH, NUMBER_FORMAT));

        //TODO: bind other values
    }
}
