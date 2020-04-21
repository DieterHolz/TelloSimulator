package tellosimulator;

import tellosimulator.drone.TelloDrone;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;

public class TelloSimulator {

    public static void main(String[] args) throws Exception {

        TelloDrone telloDrone = new TelloDrone();

        UDPCommandConnection commandConnection = new UDPCommandConnection(telloDrone);
        commandConnection.start();

        UDPStateConnection stateConnection = new UDPStateConnection(telloDrone);
        stateConnection.start();

    }
}
