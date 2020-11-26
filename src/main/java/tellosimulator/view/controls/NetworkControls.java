package tellosimulator.view.controls;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tellosimulator.TelloSimulator;
import tellosimulator.controller.DroneController;
import tellosimulator.log.Logger;
import tellosimulator.common.TelloSDKValues;
import tellosimulator.network.CommandConnection;

import java.io.IOException;
import java.net.*;

/**
 * Simple control displaying network information and providing a "Start"/"Stop"-Button for the drone.
 */
public class NetworkControls extends VBox {
    private final Logger logger = new Logger(TelloSimulator.MAIN_LOG, "NetworkControls");
    private final DroneController droneController;

    private CommandConnection commandConnection;
    private String myIp;

    private ToggleButton startButton;
    private Label simulatorExternalIpLabel;
    private TextField simulatorExternalIpField;
    private Label commandsLabel;
    private Label commandPortLabel;
    private TextField commandPortField;
    private Label stateLabel;
    private Label statePortLabelNetwork;
    private TextField statePortFieldNetwork;

    private final String BUTTON_TEXT_START_DRONE = "Start Drone";
    private final String BUTTON_TEXT_STOP_DRONE = "Stop Drone";


    public NetworkControls(DroneController droneController) {
        this.droneController = droneController;

        getExternalIPAddress();

        initializeSelf();
        initializeParts();
        layoutParts();
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
    }

    private void getExternalIPAddress() {
        try {
            Socket s = new Socket("www.google.com", 80);
            myIp = s.getLocalAddress().getHostAddress();
            s.close();
        } catch (IOException e) {
            myIp = "127.0.0.1";
            logger.debug("No internet connection?.");
        }
    }

    private void initializeSelf() {
        setPadding(new Insets(4,4,4,4));
        setMinWidth(150);
        setPrefWidth(200);
    }

    private void initializeParts() {
        startButton = new ToggleButton(BUTTON_TEXT_START_DRONE);
        setMargin(startButton, new Insets(0,0,20,0));

        simulatorExternalIpLabel = new Label("IP Address:");
        simulatorExternalIpField = new TextField(myIp);
        simulatorExternalIpField.setEditable(false);

        commandsLabel = new Label("Send your commands to:");
        commandPortLabel = new Label("Command Port:");
        commandPortField = new TextField(String.valueOf(TelloSDKValues.SIM_COMMAND_PORT));

        stateLabel = new Label("Listen for the drone state on:");
        statePortLabelNetwork = new Label("State Port:");
        statePortFieldNetwork  = new TextField(String.valueOf(TelloSDKValues.TELLO_STATE_PORT));
    }

    private void layoutParts() {
        getChildren().addAll(
                startButton,
                simulatorExternalIpLabel,
                simulatorExternalIpField,
                commandsLabel,
                commandPortLabel,
                commandPortField,
                stateLabel,
                statePortLabelNetwork,
                statePortFieldNetwork
        );
    }

    private void setupEventHandlers() {
        startButton.setOnAction(event -> {
            if (startButton.isSelected()){
                if (commandConnection == null || !commandConnection.isAlive()){
                    commandConnection = new CommandConnection(droneController);
                    commandConnection.start();
                }
                commandConnection.setRunning(true);
                startButton.setText(BUTTON_TEXT_STOP_DRONE);
                logger.debug("Drone turned ON");
            } else {
                commandConnection.setRunning(false);
                commandConnection = null;
                droneController.emergency();
                droneController.setEmergency(false);
                startButton.setText(BUTTON_TEXT_START_DRONE);
                logger.debug("Drone turned OFF");
            }
        });
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
    }
}
