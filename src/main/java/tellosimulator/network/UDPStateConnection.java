package tellosimulator.network;

import tellosimulator.drone.TelloDrone;
import tellosimulator.state.TelloDroneState;
import tellosimulator.state.TelloStateSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class UDPStateConnection extends Thread {
    DatagramSocket stateSocket;
    TelloDrone telloDrone;

    private boolean running;
    private byte[] buffer = new byte[512];

    public boolean isRunning() {
        return running;
    }
    public void setRunning(boolean running) {
        this.running = running;
    }

    public UDPStateConnection(TelloDrone telloDrone) throws SocketException {

        this.telloDrone = telloDrone;

        try {
            stateSocket = new DatagramSocket(TelloSDKValues.SIM_STATE_PORT);
            InetAddress address = InetAddress.getByName(TelloSDKValues.OP_IP_ADDRESS);
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
            address = InetAddress.getByName(TelloSDKValues.OP_IP_ADDRESS);

            while (running) {
                try {
                    String droneState = TelloStateSerializer.serializeState(telloDrone.getDroneState());
                    DatagramPacket statePacket = new DatagramPacket(droneState.getBytes(), droneState.getBytes().length, address, TelloSDKValues.OP_STATE_PORT);
                    stateSocket.send(statePacket);
                    TimeUnit.SECONDS.sleep(1);
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