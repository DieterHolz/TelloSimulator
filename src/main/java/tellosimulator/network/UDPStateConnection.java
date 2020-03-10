package tellosimulator.network;

import tellosimulator.state.TelloDroneState;
import tellosimulator.state.TelloStateSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPStateConnection extends Thread {

    DatagramSocket stateSocket;

    private boolean running;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private byte[] buffer = new byte[512];

    public UDPStateConnection() throws SocketException {

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

        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            //TelloDroneState state = new TelloDroneState();
            //TelloStateSerializer.serializeState(state);

        }
        stateSocket.close();
    }

}
