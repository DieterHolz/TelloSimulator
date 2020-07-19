package tellosimulator.views;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import tellosimulator.network.TelloSDKValues;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkControls extends VBox {
    private final TelloSDKValues sdkValues;
    private String myIp;

    private Label simulatorIpLabel;
    private TextField simulatorIpField;
    private Label operatorIpLabel;
    private TextField operatorIpField;

    public NetworkControls(TelloSDKValues sdkValues) throws UnknownHostException {
        this.sdkValues = sdkValues;
        myIp = "TODO"; //TODO: display external IP of this machine here
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
        simulatorIpLabel = new Label("Simulator IP Adresse");
        simulatorIpField = new TextField(myIp);
        simulatorIpField.setEditable(false);
        operatorIpLabel = new Label("Operator IP Adresse");
        operatorIpField = new TextField();
    }

    private void layoutParts() {
        getChildren().addAll(simulatorIpLabel, simulatorIpField, operatorIpLabel, operatorIpField);
    }

    private void setupEventHandlers() {
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
        operatorIpField.textProperty().bindBidirectional(sdkValues.operatorIpAddressProperty());
    }
}
