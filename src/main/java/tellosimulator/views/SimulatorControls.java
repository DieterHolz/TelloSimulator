package tellosimulator.views;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class SimulatorControls extends GridPane {
    private final Drone3d drone;

    private static final String NUMBER_FORMAT = "%.1f";

    private Button button;
    private Label xPositionLabel;
    private Text xPositionText;
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
        setMinWidth(150);
    }

    private void initializeParts() {
        button = new Button("Reset drone");
        xPositionLabel = new Label("X Positon:");
        xPositionText = new Text();
        //TODO: init all other values
    }

    private void layoutParts() {
        add(button, 1, 0);
        add(xPositionLabel, 1,1);
        add(xPositionText, 2, 1);
        //TODO: add all other values to grid
    }

    private void setupEventHandlers() {
        button.setOnAction(event -> {
            drone.getDrone().setTranslateX(Drone3d.INITIAL_X_POSITION);
            drone.getDrone().setTranslateY(Drone3d.INITIAL_Y_POSITION);
            drone.getDrone().setTranslateZ(Drone3d.INITIAL_Z_POSITION);
            //TODO: reset rotation/orientation too
        });
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
        xPositionText.textProperty().bind(drone.getDrone().translateXProperty().asString(NUMBER_FORMAT));
        //TODO: bind other values
    }
}
