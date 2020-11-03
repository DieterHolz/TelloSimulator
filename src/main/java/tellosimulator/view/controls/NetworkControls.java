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
    private StateConnection stateConnection;
    private String myIp;

    private ToggleButton startButton;
    private Label simulatorExternalIpLabel;
    private TextField simulatorExternalIpField;
    private Label simulatorLocalIpLabel;
    private TextField simulatorLocalIpField;
    private Label portsLabel;
    private Label commandPortLabel;
    private TextField commandPortField;
    private Label statePortLabel;
    private TextField statePortField;
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

    private void initializeParts() throws UnknownHostException {
        startButton = new ToggleButton("Start Virtual Drone");
        simulatorExternalIpLabel = new Label("External IP Address:");
        simulatorExternalIpField = new TextField(myIp);
        simulatorExternalIpField.setEditable(false);

        simulatorLocalIpLabel = new Label("Local IP Address:");
        simulatorLocalIpField = new TextField(TelloSDKValues.SIM_LOCAL_ADDRESS);
        simulatorLocalIpField.setEditable(false);

        portsLabel = new Label("Connect to the following ports:");
        commandPortLabel = new Label("Command Port:");
        commandPortField = new TextField(String.valueOf(TelloSDKValues.SIM_COMMAND_PORT));

        statePortLabel = new Label("State Port:");
        statePortField = new TextField(String.valueOf(TelloSDKValues.SIM_STATE_PORT));

        //TODO: video label and field

    }

    private void layoutParts() {
        getChildren().addAll(
                startButton,
                simulatorExternalIpLabel,
                simulatorExternalIpField,
                simulatorLocalIpLabel,
                simulatorLocalIpField,
                portsLabel,
                commandPortLabel,
                commandPortField,
                statePortLabel,
                statePortField
        );
    }

    private void setupEventHandlers() {
        startButton.setOnAction(event -> {
            if (startButton.isSelected()){
                try {
                    if (commandConnection == null || !commandConnection.isAlive()){
                        commandConnection = new CommandConnection(droneController);
                        stateConnection = new StateConnection(droneController);
                        commandConnection.start();
                        stateConnection.start();
                    }

                    commandConnection.setRunning(true);
                    stateConnection.setRunning(true);
                } catch (SocketException e) {
                    logger.error(e.getMessage());
                }
                logger.debug("Drone turned ON");

            } else {
                commandConnection.setRunning(false);
                stateConnection.setRunning(false);
                logger.debug("Drone turned OFF");
            }
        });
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
    }
}
