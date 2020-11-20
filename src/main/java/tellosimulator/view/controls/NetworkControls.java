package tellosimulator.view.controls;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tellosimulator.TelloSimulator;
import tellosimulator.controller.DroneController;
import tellosimulator.log.Logger;
import tellosimulator.network.TelloSDKValues;
import tellosimulator.network.CommandConnection;
import tellosimulator.network.StateConnection;

import java.io.IOException;
import java.net.*;

public class NetworkControls extends VBox {
    private final Logger logger = new Logger(TelloSimulator.MAIN_LOG, "NetworkControls");
    private final DroneController droneController;

    private CommandConnection commandConnection;
    private String myIp;

    private ToggleButton startButton;
    private Label simulatorExternalIpLabel;
    private TextField simulatorExternalIpField;
    private Label simulatorLocalIpLabel;
    private TextField simulatorLocalIpField;
    private Label commandsLabel;
    private Label commandPortLabel;
    private TextField commandPortField;
    private Label stateLabel;
    private Label statePortLabel;
    private TextField statePortField;
    private Label statePortLabelNetwork;
    private TextField statePortFieldNetwork;
    private Label videoPortLabel;
    private TextField videoPortField;

    public NetworkControls(DroneController droneController) throws IOException {
        this.droneController = droneController;

        getExternalIPAddress();

        initializeSelf();
        initializeParts();
        layoutParts();
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
    }

    private void getExternalIPAddress() throws IOException {
        Socket s = new Socket("www.google.com", 80);
        myIp = s.getLocalAddress().getHostAddress();
        s.close();
    }

    private void initializeSelf() {
        setPadding(new Insets(4,4,4,4));
        setMinWidth(150);
    }

    private void initializeParts() {
        startButton = new ToggleButton("Start Virtual Drone");
        simulatorExternalIpLabel = new Label("IP Address in Network:");
        simulatorExternalIpField = new TextField(myIp);
        simulatorExternalIpField.setEditable(false);

        simulatorLocalIpLabel = new Label("Local Loopback IP Address:");
        simulatorLocalIpField = new TextField(TelloSDKValues.SIM_LOCAL_ADDRESS);
        simulatorLocalIpField.setEditable(false);

        commandsLabel = new Label("Send your commands to following ports:");
        commandPortLabel = new Label("Command Port:");
        commandPortField = new TextField(String.valueOf(TelloSDKValues.SIM_COMMAND_PORT));

        stateLabel = new Label("Listen for the drone state on following ports:");
        statePortLabel = new Label("State Port (Client Local):");
        statePortField = new TextField(String.valueOf(TelloSDKValues.SIM_STATE_PORT));

        statePortLabelNetwork = new Label("State Port (Client in Network):");
        statePortFieldNetwork  = new TextField(String.valueOf(TelloSDKValues.OP_STATE_PORT));

        //TODO: video label and field

    }

    private void layoutParts() {
        getChildren().addAll(
                startButton,
                simulatorExternalIpLabel,
                simulatorExternalIpField,
                simulatorLocalIpLabel,
                simulatorLocalIpField,
                commandsLabel,
                commandPortLabel,
                commandPortField,
                stateLabel,
                statePortLabel,
                statePortField,
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
                logger.debug("Drone turned ON");
            } else {
                commandConnection.setRunning(false);
                commandConnection = null;
                logger.debug("Drone turned OFF");
            }
        });
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
    }
}
