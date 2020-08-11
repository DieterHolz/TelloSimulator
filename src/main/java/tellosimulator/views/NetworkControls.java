package tellosimulator.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tellosimulator.TelloSimulator;
import tellosimulator.log.Logger;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;

import java.io.IOException;
import java.net.*;

public class NetworkControls extends VBox {
    private final Logger logger = new Logger(TelloSimulator.MAIN_LOG, "NetworkControls");
    private final Drone3d drone;

    private UDPCommandConnection commandConnection;
    private UDPStateConnection stateConnection;
    private String myIp;

    private ToggleButton startButton;
    private Label simulatorIpLabel;
    private TextField simulatorIpField;

    public NetworkControls(Drone3d drone) throws IOException {
        this.drone = drone;

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
        startButton = new ToggleButton("Start Drone");
        simulatorIpLabel = new Label("Simulator IP Adresse");
        simulatorIpField = new TextField(myIp);
        simulatorIpField.setEditable(false);
        simulatorIpField.setDisable(true);
    }

    private void layoutParts() {
        getChildren().addAll(startButton, simulatorIpLabel, simulatorIpField);
    }

    private void setupEventHandlers() {
        startButton.setOnAction(event -> {
            if (startButton.isSelected()){
                try {
                    commandConnection = new UDPCommandConnection(drone);
                    stateConnection = new UDPStateConnection(drone);
                    commandConnection.start();
                    stateConnection.start();
                    commandConnection.setRunning(true);
                    stateConnection.setRunning(true);
                } catch (SocketException e) {
                    logger.error(e.getMessage());
                }
                logger.debug("Drone turned on");

            } else {
                commandConnection.setRunning(false);
                stateConnection.setRunning(false);
                logger.debug("Drone turned off");
            }
        });
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
    }
}
