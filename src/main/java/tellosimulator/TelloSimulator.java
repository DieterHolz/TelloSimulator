package tellosimulator;

import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;

public class TelloSimulator {

    public static void main(String[] args) throws Exception {

        UDPCommandConnection commandConnection = new UDPCommandConnection();
        commandConnection.start();

        UDPStateConnection stateConnection = new UDPStateConnection();
        stateConnection.start();

    }
}
