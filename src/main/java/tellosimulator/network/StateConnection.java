package tellosimulator.network;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import tellosimulator.TelloSimulator;
import tellosimulator.common.TelloSDKValues;
import tellosimulator.controller.DroneController;
import tellosimulator.log.Logger;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

/**
 * This asynchronous thread sends the drone state every 100 ms as UDP-Packets to the configured address.
 */
public class StateConnection extends Thread {
    private final Logger logger = new Logger(TelloSimulator.MAIN_LOG, "StateConnection");

    private DatagramSocket stateSocket;
    private DroneController telloDroneController;
    private InetAddress address;

    private BooleanProperty running = new SimpleBooleanProperty(false);

    public StateConnection(DroneController telloDroneController, InetAddress address) {
        this.setDaemon(true);
        this.address = address;
        this.telloDroneController = telloDroneController;

        setupValueChangedListener();
        try {
            stateSocket = new DatagramSocket(TelloSDKValues.SIM_STATE_PORT);
            stateSocket.connect(address, TelloSDKValues.TELLO_STATE_PORT);
        } catch (BindException bindException) {
            logger.error("could not establish connection. Address: " + address + "Port: " + TelloSDKValues.SIM_STATE_PORT + " " +
                    "is already bound. If your client program runs locally, " +
                    "please bind your state socket to port " + TelloSDKValues.TELLO_STATE_PORT + ". Details: " + bindException);
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
                DatagramPacket statePacket = new DatagramPacket(droneState.getBytes(), droneState.getBytes().length, address, TelloSDKValues.TELLO_STATE_PORT);
                stateSocket.send(statePacket);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException | IOException e1) {
                logger.error("error: " + e1);
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
            logger.debug("running. Sending state every 100 ms.");
        } else {
            logger.debug("stopped.");
        }
    }
}
