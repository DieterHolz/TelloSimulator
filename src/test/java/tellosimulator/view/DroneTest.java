package tellosimulator.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tellosimulator.command.CommandHandler;
import tellosimulator.command.CommandPackage;
import tellosimulator.command.TelloSetCommand;
import tellosimulator.network.CommandConnection;
import tellosimulator.network.TelloSDKValues;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class DroneTest {

    Drone drone;
    CommandHandler commandHandler;
    CommandConnection commandConnection;
    CommandPackage commandPackage;


    @BeforeEach
    void setUp() throws IOException {
        drone = new Drone();
        commandConnection = new CommandConnection(drone);
        commandHandler = new CommandHandler(drone, commandConnection);
        commandPackage = new CommandPackage(null, InetAddress.getByName(TelloSDKValues.getOperatorIpAddress()), TelloSDKValues.SIM_COMMAND_PORT);
    }

    private void computeResponse(String invalidCommand) throws IOException {
        commandPackage.setCommand(invalidCommand);
        commandHandler.handle(commandPackage);
    }

    @Test
    void testRcHandling() throws IOException {
        assertEquals(0, drone.getForwardBackwardDiff());
        assertEquals(0, drone.getLeftRightDiff());
        assertEquals(0, drone.getUpDownDiff());
        assertEquals(0, drone.getYawDiff());

        computeResponse(TelloSetCommand.RC + " 0 10 20 30");

        assertEquals(0, drone.getForwardBackwardDiff());
        assertEquals(10, drone.getLeftRightDiff());
        assertEquals(20, drone.getUpDownDiff());
        assertEquals(30, drone.getYawDiff());
    }

}