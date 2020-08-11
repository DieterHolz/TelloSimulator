package tellosimulator.views;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import tellosimulator.commands.TelloDefaultValues;

import java.util.Locale;

public class SimulatorControls extends GridPane {
    private final Drone3d drone;

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

    public SimulatorControls(Drone3d drone){
        this.drone = drone;
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
            drone.getDrone().setTranslateX(Drone3d.INITIAL_X_POSITION);
            drone.getDrone().setTranslateY(Drone3d.INITIAL_Y_POSITION);
            drone.getDrone().setTranslateZ(Drone3d.INITIAL_Z_POSITION);
            drone.getDrone().setRotate(0);
            drone.setxOrientation(0);
            drone.setyOrientation(0);
            drone.setzOrientation(1);
            drone.setYawAngle(0);
            drone.setRollAngle(0);
            drone.setPitchAngle(0);
            drone.setSpeed(TelloDefaultValues.DEFAULT_SPEED);
        });
    }

    private void setupValueChangedListeners() {
        drone.getDrone().rotateProperty().addListener((observable, oldValue, newValue) -> {
            yawAngleText.textProperty().setValue(String.valueOf(Math.round(newValue.doubleValue() % 360)));
        });
    }

    private void setupBindings() {
        xPositionText.textProperty().bind(drone.getDrone().translateXProperty().asString(LOCALE_CH, NUMBER_FORMAT));
        yPositionText.textProperty().bind(drone.getDrone().translateYProperty().add(Drone3d.DRONE_HEIGHT/2).negate().asString(LOCALE_CH, NUMBER_FORMAT));
        zPositionText.textProperty().bind(drone.getDrone().translateZProperty().asString(LOCALE_CH, NUMBER_FORMAT));

        //TODO: bind other values
    }
}
