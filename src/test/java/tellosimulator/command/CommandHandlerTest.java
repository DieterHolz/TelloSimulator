package tellosimulator.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        commandConnection = mock(CommandConnection.class);
        commandResponseSender = mock(CommandResponseSender.class);
        commandResponseSender.setSocket(new DatagramSocket(TelloSDKValues.SIM_COMMAND_PORT));
    }

    @BeforeEach
    void setUp() throws UnknownHostException {
        drone = mock(Drone.class);
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

    @Test
    void takeoffWithParam() throws IOException {
        commandPackage.setCommand(TelloControlCommand.TAKEOFF + " 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).takeoff(commandPackage);
    }

    @Test
    void land() throws IOException {
        commandPackage.setCommand(TelloControlCommand.LAND);
        commandHandler.handle(commandPackage);
        verify(drone, times(1)).land(commandPackage);
    }

    @Test
    void landTypo() throws IOException {
        commandPackage.setCommand("landdo");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).land(commandPackage);
    }

    @Test
    void landWithParam() throws IOException {
        commandPackage.setCommand(TelloControlCommand.LAND +" 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).land(commandPackage);
    }

    @Test
    void emergency() throws IOException {
        commandPackage.setCommand(TelloControlCommand.EMERGENCY);
        commandHandler.handle(commandPackage);
        verify(drone, times(1)).emergency();
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void upValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.UP + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(1)).up(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, 19, 19.99999, 500.00001, 501, Integer.MAX_VALUE})
    void upInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.UP + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(0)).up(commandPackage, dist);
    }

    @Test
    void upTypo() throws IOException {
        commandPackage.setCommand("up30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(drone, times(0)).up(commandPackage, 30);
    }

    @Test
    void upNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.UP);
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).up(commandPackage, 0);
    }

    @Test
    void upTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.UP + " 30 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).up(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void downValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.DOWN + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(1)).down(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, 19, 19.99999, 500.00001, 501, Integer.MAX_VALUE})
    void downInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.DOWN + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(0)).down(commandPackage, dist);
    }

    @Test
    void downTypo() throws IOException {
        commandPackage.setCommand("down30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(drone, times(0)).down(commandPackage, 30);
    }

    @Test
    void downNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.DOWN);
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).down(commandPackage, 0);
    }

    @Test
    void downTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.DOWN + " 30 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).down(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void leftValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.LEFT + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(1)).left(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, 19, 19.99999, 500.00001, 501, Integer.MAX_VALUE})
    void leftInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.LEFT + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(0)).left(commandPackage, dist);
    }

    @Test
    void leftTypo() throws IOException {
        commandPackage.setCommand("left30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(drone, times(0)).left(commandPackage, 30);
    }

    @Test
    void leftNoParams() throws IOException {
        commandPackage.setCommand("left");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).left(commandPackage, 0);
    }

    @Test
    void leftTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.LEFT + " 30 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).left(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void rightValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.RIGHT + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(1)).right(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, 19, 19.99999, 500.00001, 501, Integer.MAX_VALUE})
    void rightInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.RIGHT + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(0)).right(commandPackage, dist);
    }

    @Test
    void rightTypo() throws IOException {
        commandPackage.setCommand("right30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(drone, times(0)).right(commandPackage, 30);
    }

    @Test
    void rightNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.RIGHT);
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).right(commandPackage, 0);
    }

    @Test
    void rightTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.RIGHT + " 30 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).right(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void forwardValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.FORWARD + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(1)).forward(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, 19, 19.99999, 500.00001, 501, Integer.MAX_VALUE})
    void forwardInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.FORWARD + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(0)).forward(commandPackage, dist);
    }

    @Test
    void forwardTypo() throws IOException {
        commandPackage.setCommand("forward30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(drone, times(0)).forward(commandPackage, 30);
    }

    @Test
    void forwardNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.FORWARD);
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).forward(commandPackage, 0);
    }

    @Test
    void forwardTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.FORWARD + " 30 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).forward(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void backValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.BACK + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(1)).back(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, 19, 19.99999, 500.00001, 501, Integer.MAX_VALUE})
    void backInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.BACK + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(0)). back(commandPackage, dist);
    }

    @Test
    void backTypo() throws IOException {
        commandPackage.setCommand("back30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(drone, times(0)).back(commandPackage, 30);
    }

    @Test
    void backNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.BACK);
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).back(commandPackage, 0);
    }

    @Test
    void backTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.BACK + " 30 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).back(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {1, 45, 180, 210, 360})
    void cwValidParam(double angle) throws IOException {
        commandPackage.setCommand(TelloControlCommand.CW + " " + angle);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(angle));
        verify(drone, times(1)).cw(commandPackage, angle);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, 0, 0.9999, 360.00001, 361, Integer.MAX_VALUE})
    void cwInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.CW + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(0)).cw(commandPackage, dist);
    }

    @Test
    void cwTypo() throws IOException {
        commandPackage.setCommand("cw30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(drone, times(0)).cw(commandPackage, 30);
    }

    @Test
    void cwNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CW);
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).cw(commandPackage, 0);
    }

    @Test
    void cwTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CW + " 30 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).cw(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {1, 45, 180, 210, 360})
    void ccwValidParam(double angle) throws IOException {
        commandPackage.setCommand(TelloControlCommand.CCW + " " + angle);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(angle));
        verify(drone, times(1)).ccw(commandPackage, angle);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, 0, 0.9999, 360.00001, 361, Integer.MAX_VALUE})
    void ccwInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.CCW + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(drone, times(0)).ccw(commandPackage, dist);
    }

    @Test
    void ccwTypo() throws IOException {
        commandPackage.setCommand("ccw30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(drone, times(0)).ccw(commandPackage, 30);
    }

    @Test
    void ccwNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CCW);
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).ccw(commandPackage, 0);
    }

    @Test
    void ccwTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CCW + " 30 30");
        commandHandler.handle(commandPackage);
        verify(drone, times(0)).ccw(commandPackage, 30);
    }
}