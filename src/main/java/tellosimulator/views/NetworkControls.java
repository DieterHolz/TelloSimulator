package tellosimulator.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tellosimulator.TelloSimulator;
import tellosimulator.network.TelloSDKValues;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;

import java.io.IOException;
import java.net.*;

public class NetworkControls extends VBox {
    private final Drone3d drone;
    private final TelloSDKValues sdkValues;

    private UDPCommandConnection commandConnection;
    private UDPStateConnection stateConnection;
    private String myIp;

    private ToggleButton connectButton;
    private Label simulatorIpLabel;
    private TextField simulatorIpField;
    private Label operatorIpLabel;
    private TextField operatorIpField;

    public NetworkControls(Drone3d drone, TelloSDKValues sdkValues, UDPCommandConnection commandConnection, UDPStateConnection stateConnection) throws IOException {
        this.drone = drone;
        this.sdkValues = sdkValues;
        this.commandConnection = commandConnection;
        this.stateConnection = stateConnection;
        Socket s = new Socket("www.google.com", 80);
        myIp = s.getLocalAddress().getHostAddress();
        s.close();
        initializeSelf();
        initializeParts();
        layoutParts();
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
    }

    private void initializeSelf() {
        setPadding(new Insets(4,4,4,4));
        setMinWidth(150);
    }

    private void initializeParts() throws UnknownHostException {
        connectButton = new ToggleButton("Verbinden (TODO)");
        simulatorIpLabel = new Label("Simulator IP Adresse");
        simulatorIpField = new TextField(myIp);
        simulatorIpField.setEditable(false);
        operatorIpLabel = new Label("Operator IP Adresse");
        operatorIpField = new TextField();
    }

    private void layoutParts() {
        getChildren().addAll(connectButton, simulatorIpLabel, simulatorIpField, operatorIpLabel, operatorIpField);
    }

    private void setupEventHandlers() {
        connectButton.setOnAction(event -> {
            if (connectButton.isSelected()){
                commandConnection.start();
                stateConnection.start();

            } else {
                commandConnection.setRunning(false);
                stateConnection.setRunning(false);
            }
        });
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
        operatorIpField.textProperty().bindBidirectional(sdkValues.operatorIpAddressProperty());
    }
}
