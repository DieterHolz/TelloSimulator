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
import tellosimulator.controller.DroneController;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandHandlerTest {

    DroneController droneController;
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
    void setUp() throws IOException {
        droneController = mock(DroneController.class);
        commandHandler = new CommandHandler(droneController, commandConnection);
        commandPackage = new CommandPackage(null, InetAddress.getByName(TelloSDKValues.getOperatorIpAddress()), TelloSDKValues.SIM_COMMAND_PORT);
    }

    /**
     * Command validation
     */

    @Test
    void command() throws IOException {
        commandPackage.setCommand(TelloControlCommand.COMMAND);
        commandHandler.handle(commandPackage);
        verify(droneController, atLeastOnce()).isAnimationRunning();
        verifyNoMoreInteractions(droneController);
    }

    @Test
    void takeoff() throws IOException {
        commandPackage.setCommand(TelloControlCommand.TAKEOFF);
        commandHandler.handle(commandPackage);
        verify(droneController, times(1)).takeoff(commandPackage);
    }

    @Test
    void takeoffTypo() throws IOException {
        commandPackage.setCommand("takeofff");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).takeoff(commandPackage);
    }

    @Test
    void takeoffWithParam() throws IOException {
        commandPackage.setCommand(TelloControlCommand.TAKEOFF + " 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).takeoff(commandPackage);
    }

    @Test
    void land() throws IOException {
        commandPackage.setCommand(TelloControlCommand.LAND);
        commandHandler.handle(commandPackage);
        verify(droneController, times(1)).land(commandPackage);
    }

    @Test
    void landTypo() throws IOException {
        commandPackage.setCommand("landdo");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).land(commandPackage);
    }

    @Test
    void landWithParam() throws IOException {
        commandPackage.setCommand(TelloControlCommand.LAND +" 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).land(commandPackage);
    }

    @Test
    void emergency() throws IOException {
        commandPackage.setCommand(TelloControlCommand.EMERGENCY);
        commandHandler.handle(commandPackage);
        verify(droneController, times(1)).emergency();
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void upValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.UP + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(1)).up(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, 19, 19.99999, 500.00001, 501, Double.POSITIVE_INFINITY})
    void upInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.UP + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(0)).up(commandPackage, dist);
    }

    @Test
    void upTypo() throws IOException {
        commandPackage.setCommand("up30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(droneController, times(0)).up(commandPackage, 30);
    }

    @Test
    void upNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.UP);
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).up(commandPackage, 0);
    }

    @Test
    void upTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.UP + " 30 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).up(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void downValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.DOWN + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(1)).down(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, 19, 19.99999, 500.00001, 501, Double.POSITIVE_INFINITY})
    void downInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.DOWN + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(0)).down(commandPackage, dist);
    }

    @Test
    void downTypo() throws IOException {
        commandPackage.setCommand("down30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(droneController, times(0)).down(commandPackage, 30);
    }

    @Test
    void downNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.DOWN);
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).down(commandPackage, 0);
    }

    @Test
    void downTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.DOWN + " 30 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).down(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void leftValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.LEFT + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(1)).left(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, 19, 19.99999, 500.00001, 501, Double.POSITIVE_INFINITY})
    void leftInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.LEFT + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(0)).left(commandPackage, dist);
    }

    @Test
    void leftTypo() throws IOException {
        commandPackage.setCommand("left30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(droneController, times(0)).left(commandPackage, 30);
    }

    @Test
    void leftNoParams() throws IOException {
        commandPackage.setCommand("left");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).left(commandPackage, 0);
    }

    @Test
    void leftTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.LEFT + " 30 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).left(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void rightValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.RIGHT + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(1)).right(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, 19, 19.99999, 500.00001, 501, Double.POSITIVE_INFINITY})
    void rightInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.RIGHT + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(0)).right(commandPackage, dist);
    }

    @Test
    void rightTypo() throws IOException {
        commandPackage.setCommand("right30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(droneController, times(0)).right(commandPackage, 30);
    }

    @Test
    void rightNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.RIGHT);
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).right(commandPackage, 0);
    }

    @Test
    void rightTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.RIGHT + " 30 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).right(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void forwardValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.FORWARD + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(1)).forward(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, 19, 19.99999, 500.00001, 501, Double.POSITIVE_INFINITY})
    void forwardInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.FORWARD + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(0)).forward(commandPackage, dist);
    }

    @Test
    void forwardTypo() throws IOException {
        commandPackage.setCommand("forward30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(droneController, times(0)).forward(commandPackage, 30);
    }

    @Test
    void forwardNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.FORWARD);
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).forward(commandPackage, 0);
    }

    @Test
    void forwardTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.FORWARD + " 30 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).forward(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {20, 20.1, 21, 30, 499, 499.99999, 500})
    void backValidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.BACK + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(1)).back(commandPackage, dist);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, 19, 19.99999, 500.00001, 501, Double.POSITIVE_INFINITY})
    void backInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.BACK + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(0)). back(commandPackage, dist);
    }

    @Test
    void backTypo() throws IOException {
        commandPackage.setCommand("back30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(droneController, times(0)).back(commandPackage, 30);
    }

    @Test
    void backNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.BACK);
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).back(commandPackage, 0);
    }

    @Test
    void backTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.BACK + " 30 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).back(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {1, 45, 180, 210, 360})
    void cwValidParam(double angle) throws IOException {
        commandPackage.setCommand(TelloControlCommand.CW + " " + angle);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(angle));
        verify(droneController, times(1)).cw(commandPackage, angle);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, 0, 0.9999, 360.00001, 361, Double.POSITIVE_INFINITY})
    void cwInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.CW + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(0)).cw(commandPackage, dist);
    }

    @Test
    void cwTypo() throws IOException {
        commandPackage.setCommand("cw30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(droneController, times(0)).cw(commandPackage, 30);
    }

    @Test
    void cwNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CW);
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).cw(commandPackage, 0);
    }

    @Test
    void cwTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CW + " 30 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).cw(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(doubles = {1, 45, 180, 210, 360})
    void ccwValidParam(double angle) throws IOException {
        commandPackage.setCommand(TelloControlCommand.CCW + " " + angle);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(angle));
        verify(droneController, times(1)).ccw(commandPackage, angle);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, 0, 0.9999, 360.00001, 361, Integer.MAX_VALUE})
    void ccwInvalidParam(double dist) throws IOException {
        commandPackage.setCommand(TelloControlCommand.CCW + " " + dist);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(dist));
        verify(droneController, times(0)).ccw(commandPackage, dist);
    }

    @Test
    void ccwTypo() throws IOException {
        commandPackage.setCommand("ccw30");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(droneController, times(0)).ccw(commandPackage, 30);
    }

    @Test
    void ccwNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CCW);
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).ccw(commandPackage, 0);
    }

    @Test
    void ccwTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CCW + " 30 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).ccw(commandPackage, 30);
    }

    @ParameterizedTest
    @ValueSource(strings = {"l", "r", "f", "b"})
    void flipValidParam(String flipDirection) throws IOException {
        commandPackage.setCommand(TelloControlCommand.FLIP + " " + flipDirection);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), flipDirection);
        verify(droneController, times(1)).flip(commandPackage, flipDirection);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ll", "rr", "fff", "bbb"})
    void flipInvalidParam(String flipDirection) throws IOException {
        commandPackage.setCommand(TelloControlCommand.FLIP + " " + flipDirection);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 1);
        assertEquals(commandHandler.getCommandParams().get(0), flipDirection);
        verify(droneController, times(0)).flip(commandPackage, flipDirection);
    }

    @Test
    void flipTypo() throws IOException {
        commandPackage.setCommand("flipb");
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams(), null);
        verify(droneController, times(0)).flip(commandPackage, "b");
    }

    @Test
    void flipNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.FLIP);
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).flip(commandPackage, "");
    }

    @Test
    void flipTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.FLIP + " f b");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).flip(commandPackage, "f");
    }


    @ParameterizedTest
    @ValueSource(doubles = {-500, -499.999, -499, 0, 20, 499, 499.99999, 500})
    void goValidXParam(double x) throws IOException {
        double y = 30;
        double z = 40;
        double speed = 50;
        commandPackage.setCommand(TelloControlCommand.GO + " " + x + " " + y + " " + z + " " + speed);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 4);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(x));
        assertEquals(commandHandler.getCommandParams().get(1), String.valueOf(y));
        assertEquals(commandHandler.getCommandParams().get(2), String.valueOf(z));
        assertEquals(commandHandler.getCommandParams().get(3), String.valueOf(speed));
        verify(droneController, times(1)).go(commandPackage, x, y, z, speed);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-500, -499.999, -499, 0, 20, 499, 499.99999, 500})
    void goValidYParam(double y) throws IOException {
        double x = 30;
        double z = 40;
        double speed = 50;
        commandPackage.setCommand(TelloControlCommand.GO + " " + x + " " + y + " " + z + " " + speed);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 4);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(x));
        assertEquals(commandHandler.getCommandParams().get(1), String.valueOf(y));
        assertEquals(commandHandler.getCommandParams().get(2), String.valueOf(z));
        assertEquals(commandHandler.getCommandParams().get(3), String.valueOf(speed));
        verify(droneController, times(1)).go(commandPackage, x, y, z, speed);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-500, -499.999, -499, 0, 20, 499, 499.99999, 500})
    void goValidZParam(double z) throws IOException {
        double x = 30;
        double y = 40;
        double speed = 50;
        commandPackage.setCommand(TelloControlCommand.GO + " " + x + " " + y + " " + z + " " + speed);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 4);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(x));
        assertEquals(commandHandler.getCommandParams().get(1), String.valueOf(y));
        assertEquals(commandHandler.getCommandParams().get(2), String.valueOf(z));
        assertEquals(commandHandler.getCommandParams().get(3), String.valueOf(speed));
        verify(droneController, times(1)).go(commandPackage, x, y, z, speed);
    }

    @ParameterizedTest
    @ValueSource(doubles = {10, 50, 100})
    void goValidSpeedParam(double speed) throws IOException {
        double x = 30;
        double y = 40;
        double z = 50;
        commandPackage.setCommand(TelloControlCommand.GO + " " + x + " " + y + " " + z + " " + speed);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 4);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(x));
        assertEquals(commandHandler.getCommandParams().get(1), String.valueOf(y));
        assertEquals(commandHandler.getCommandParams().get(2), String.valueOf(z));
        assertEquals(commandHandler.getCommandParams().get(3), String.valueOf(speed));
        verify(droneController, times(1)).go(commandPackage, x, y, z, speed);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, -501, -500.1, 500.1, 501, Double.POSITIVE_INFINITY})
    void goInvalidXParam(double x) throws IOException {
        double y = 40;
        double z = 50;
        double speed = 50;
        commandPackage.setCommand(TelloControlCommand.GO + " " + x + " " + y + " " + z + " " + speed);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 4);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(x));
        assertEquals(commandHandler.getCommandParams().get(1), String.valueOf(y));
        assertEquals(commandHandler.getCommandParams().get(2), String.valueOf(z));
        assertEquals(commandHandler.getCommandParams().get(3), String.valueOf(speed));
        verify(droneController, times(0)).go(commandPackage, x, y, z, speed);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, -501, -500.1, 500.1, 501, Double.POSITIVE_INFINITY})
    void goInvalidYParam(double y) throws IOException {
        double x = 40;
        double z = 50;
        double speed = 50;
        commandPackage.setCommand(TelloControlCommand.GO + " " + x + " " + y + " " + z + " " + speed);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 4);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(x));
        assertEquals(commandHandler.getCommandParams().get(1), String.valueOf(y));
        assertEquals(commandHandler.getCommandParams().get(2), String.valueOf(z));
        assertEquals(commandHandler.getCommandParams().get(3), String.valueOf(speed));
        verify(droneController, times(0)).go(commandPackage, x, y, z, speed);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NEGATIVE_INFINITY, -501, -500.1, 500.1, 501, Double.POSITIVE_INFINITY})
    void goInvalidZParam(double z) throws IOException {
        double x = 40;
        double y = 50;
        double speed = 50;
        commandPackage.setCommand(TelloControlCommand.GO + " " + x + " " + y + " " + z + " " + speed);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 4);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(x));
        assertEquals(commandHandler.getCommandParams().get(1), String.valueOf(y));
        assertEquals(commandHandler.getCommandParams().get(2), String.valueOf(z));
        assertEquals(commandHandler.getCommandParams().get(3), String.valueOf(speed));
        verify(droneController, times(0)).go(commandPackage, x, y, z, speed);
    }

    @Test
    void goInvalidParamCombination() throws IOException {
        double x = 0;
        double y = -10;
        double z = 10;
        double speed = 50;
        commandPackage.setCommand(TelloControlCommand.GO + " " + x + " " + y + " " + z + " " + speed);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 4);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(x));
        assertEquals(commandHandler.getCommandParams().get(1), String.valueOf(y));
        assertEquals(commandHandler.getCommandParams().get(2), String.valueOf(z));
        assertEquals(commandHandler.getCommandParams().get(3), String.valueOf(speed));
        verify(droneController, times(0)).go(commandPackage, x, y, z, speed);
    }

    @Test
    void goTypo() throws IOException {
        double x = 30;
        double y = 30;
        double z = 30;
        double speed = 50;
        commandPackage.setCommand("goo" + " " + x + " " + y + " " + z + " " + speed);
        commandHandler.handle(commandPackage);
        assertEquals(commandHandler.getCommandParams().size(), 4);
        assertEquals(commandHandler.getCommandParams().get(0), String.valueOf(x));
        assertEquals(commandHandler.getCommandParams().get(1), String.valueOf(y));
        assertEquals(commandHandler.getCommandParams().get(2), String.valueOf(z));
        assertEquals(commandHandler.getCommandParams().get(3), String.valueOf(speed));
        verify(droneController, times(0)).go(commandPackage, x, y, z, speed);
    }

    @Test
    void goNoParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.GO);
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).flip(commandPackage, "");
    }

    @Test
    void goTooMuchParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.GO + " 30 30 30 50 30");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).go(commandPackage, 30, 30, 30, 50);
    }

    @Test
    void stopValid() throws IOException {
        commandPackage.setCommand(TelloControlCommand.STOP);
        commandHandler.handle(commandPackage);
        verify(droneController, times(1)).stop(commandPackage);
    }

    @Test
    void stopTypo() throws IOException {
        commandPackage.setCommand("stopp");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).stop(commandPackage);
    }

    @Test
    void stopWithParams() throws IOException {
        commandPackage.setCommand(TelloControlCommand.STOP + "10");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).stop(commandPackage);
    }

    @Test
    void curveValid() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CURVE + " 100 100 100 40 40 40 50");
        commandHandler.handle(commandPackage);
        verify(droneController, times(1)).curve(commandPackage, 100, 100, 100, 40, 40, 40, 50);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-501, -500.1, -500.11, -500.5, 500.1, 501})
    void curveInvalidRange(double x) throws IOException {
        commandPackage.setCommand(TelloControlCommand.CURVE + " " + x + " 101 100 40 40 40 50");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).curve(commandPackage, x, 100, 100, 40, 40, 40, 50);
    }

    @Test
    void curveInvalidArcRadius() throws IOException {
        commandPackage.setCommand(TelloControlCommand.CURVE + " 30 30 30 40 40 40 50");
        commandHandler.handle(commandPackage);
        verify(droneController, times(0)).curve(commandPackage, 30, 30, 30, 40, 40, 40, 50);
    }
}