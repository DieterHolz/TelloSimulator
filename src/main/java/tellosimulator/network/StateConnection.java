package tellosimulator.network;

import tellosimulator.controller.DroneController;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class StateConnection extends Thread {
    DatagramSocket stateSocket;
    DroneController telloDroneController;

    private boolean running;
    private byte[] buffer = new byte[512];

    public boolean isRunning() {
        return running;
    }
    public void setRunning(boolean running) {
        this.running = running;
    }

    public StateConnection(DroneController telloDroneController) throws SocketException {

        this.telloDroneController = telloDroneController;

        try {
            stateSocket = new DatagramSocket(TelloSDKValues.SIM_STATE_PORT);
            InetAddress address = InetAddress.getByName(TelloSDKValues.getOperatorIpAddress());
            //commandSocket.setSoTimeout(TelloSDKValues.STATE_SOCKET_TIMEOUT);
            stateSocket.connect(address, TelloSDKValues.OP_STATE_PORT);

        } catch (IOException ex) {
            System.out.println("State server error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        running = true;
        InetAddress address = null;
        try {
            address = InetAddress.getByName(TelloSDKValues.getOperatorIpAddress());

            while (running) {
                try {
                    String droneState = telloDroneController.getDroneState();
                    DatagramPacket statePacket = new DatagramPacket(droneState.getBytes(), droneState.getBytes().length, address, TelloSDKValues.OP_STATE_PORT);
                    stateSocket.send(statePacket);
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }

        stateSocket.close();
    }


}
