package tellosimulator;

import tellosimulator.network.UDPCommandServer;

public class TelloSimulator {

    public static void main(String[] args) throws Exception {

        UDPCommandServer server = new UDPCommandServer();
        server.start();

    }

}
