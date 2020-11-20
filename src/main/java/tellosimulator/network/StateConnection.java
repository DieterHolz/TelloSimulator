package tellosimulator.network;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import tellosimulator.controller.DroneController;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class StateConnection extends Thread {
    DatagramSocket stateSocket;
    DroneController telloDroneController;
    InetAddress address;

    private BooleanProperty running = new SimpleBooleanProperty(false);

    public StateConnection(DroneController telloDroneController, InetAddress address) {
        this.address = address;
        this.telloDroneController = telloDroneController;

        setupValueChangedListener();
        try {
            stateSocket = new DatagramSocket(TelloSDKValues.SIM_STATE_PORT);
            stateSocket.connect(address, TelloSDKValues.OP_STATE_PORT);
        } catch (IOException ex) {
            System.out.println("State server error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void setupValueChangedListener() {
        runningProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false) {
                stateSocket.close();
            }
        });
    }

    public void run() {
        setRunning(true);

        while (isRunning()) {
            try {
                String droneState = telloDroneController.getDroneState();
                DatagramPacket statePacket = new DatagramPacket(droneState.getBytes(), droneState.getBytes().length, address, TelloSDKValues.OP_STATE_PORT);
                stateSocket.send(statePacket);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public BooleanProperty runningProperty() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }
}
