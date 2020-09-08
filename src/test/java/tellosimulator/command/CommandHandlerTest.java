package tellosimulator.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import tellosimulator.network.CommandResponseSender;
import tellosimulator.network.TelloSDKValues;
import tellosimulator.network.CommandConnection;
import tellosimulator.view.Drone;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandHandlerTest {

    Drone drone;
    CommandHandler commandHandler;
    CommandConnection commandConnection;
    CommandPackage commandPackage;
    CommandResponseSender commandResponseSender;

    @BeforeAll
    void setUpMocks() throws SocketException {
        drone = mock(Drone.class);
        commandConnection = mock(CommandConnection.class);
        commandResponseSender = mock(CommandResponseSender.class);
        commandResponseSender.setSocket(new DatagramSocket(TelloSDKValues.SIM_COMMAND_PORT));
    }

    @BeforeEach
    void setUp() throws UnknownHostException {
        commandHandler = new CommandHandler(drone, commandConnection);
        commandPackage = new CommandPackage(null, InetAddress.getByName(TelloSDKValues.getOperatorIpAddress()), TelloSDKValues.SIM_COMMAND_PORT);
    }

    /**
     * Command validation
     */

    @Test
    void command() throws IOException {
        commandPackage.setCommand(TelloControlCommand.COMMAND);
        commandHandler.handle(commandPackage);
        verify(drone, atLeastOnce()).isAnimationRunning();
        verifyNoMoreInteractions(drone);
    }

    @Test
    void takeoff() throws IOException {
        commandPackage.setCommand(TelloControlCommand.TAKEOFF);
        commandHandler.handle(commandPackage);
        verify(drone, times(1)).takeoff(commandPackage);
    }

    @Test
    void takeoffTypo() throws IOException {
        commandPackage.setCommand("takeofff");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).takeoff(commandPackage);
    }

}