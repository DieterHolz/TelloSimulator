package tellosimulator.network;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import tellosimulator.TelloSimulator;
import tellosimulator.controller.DroneController;
import tellosimulator.log.Logger;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class StateConnection extends Thread {
    private final Logger logger = new Logger(TelloSimulator.MAIN_LOG, "StateConnection");

    DatagramSocket stateSocket;
    DroneController telloDroneController;
    InetAddress address;

    private BooleanProperty running = new SimpleBooleanProperty(false);

    public StateConnection(DroneController telloDroneController, InetAddress address) {
        this.setDaemon(true);
        this.address = address;
        this.telloDroneController = telloDroneController;

        setupValueChangedListener();
        try {
            stateSocket = new DatagramSocket(TelloSDKValues.SIM_STATE_PORT);
            stateSocket.connect(address, TelloSDKValues.OP_STATE_PORT);
        } catch (BindException bindException) {
            logger.error("could not establish connection. Address: " + address + "Port: " + TelloSDKValues.SIM_STATE_PORT + " " +
                    "is already in use. If your client program runs locally, please receive the state on port " +
                    TelloSDKValues.OP_STATE_PORT + " instead. Details: " + bindException);
        } catch (IOException ex) {
            logger.error("error: " + ex);
        }
    }

    private void setupValueChangedListener() {
        runningProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue == false) {
                stateSocket.close();
            }
        });
    }

    public void run() {
        setRunning(stateSocket != null);

        while (isRunning()) {
            try {
                String droneState = telloDroneController.getDroneState();
                DatagramPacket statePacket = new DatagramPacket(droneState.getBytes(), droneState.getBytes().length, address, TelloSDKValues.OP_STATE_PORT);
                stateSocket.send(statePacket);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e1) {
                logger.error("StateConnection error: " + e1);
            } catch (IOException e2) {
                logger.error("StateConnection error: " + e2);
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
        if (running) {
            logger.debug("running. Sending state every 10 ms.");
        } else {
            logger.debug("stopped.");
        }
    }
}
