package tellosimulator.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import tellosimulator.network.TelloSDKValues;
import tellosimulator.network.CommandConnection;
import tellosimulator.view.Drone;


import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(ApplicationExtension.class)
class CommandHandlerTest {

    Drone drone;
    CommandHandler commandHandler;
    CommandConnection commandConnection;
    String command;
    CommandPackage commandPackage;

    @BeforeEach
    void setUp() throws SocketException, UnknownHostException {
        drone = new Drone();
        commandConnection = new CommandConnection(drone);
        commandHandler = new CommandHandler(drone, commandConnection);
        commandPackage = new CommandPackage(null, InetAddress.getByName(TelloSDKValues.getOperatorIpAddress()), TelloSDKValues.SIM_COMMAND_PORT);

    }

    //TODO: is this in the corret class? Maybe move to DroneTest
    @Test
    void testRcHandling() throws IOException {
        assertEquals(0, drone.getForwardBackwardDiff());
        assertEquals(0, drone.getLeftRightDiff());
        assertEquals(0, drone.getUpDownDiff());
        assertEquals(0, drone.getYawDiff());

        computeResponse(TelloSetCommands.RC + " 0 10 20 30");

        assertEquals(0, drone.getForwardBackwardDiff());
        assertEquals(10, drone.getLeftRightDiff());
        assertEquals(20, drone.getUpDownDiff());
        assertEquals(30, drone.getYawDiff());
    }

    /**
     * Command validation
     */
    //TODO: update tests to new refactoring

    @Test
    void takeoffValid() throws IOException {
        String validCommand = TelloControlCommand.TAKEOFF;
        computeResponse(validCommand);
        assertNotEquals("error", commandPackage.getResponse());
    }

    @Test
    void takeoffTypo() throws IOException, InterruptedException {
        String invalidCommand = "takeofff";
        computeResponse(invalidCommand);
        assertEquals("error", commandPackage.getResponse());
    }

    //TODO: test with drone if it returns error
    @Test
    void takeoffParam() throws IOException {
        computeResponse("takeoff 30");
        assertEquals("error", commandPackage.getResponse());
    }

    private void computeResponse(String invalidCommand) throws IOException {
        commandPackage.setCommand(invalidCommand);
        commandHandler.handle(commandPackage);
    }

    //TODO: implement with commandPackage
//    @Test
//    void landValidation() {
//        String validCommand = TelloControlCommands.LAND;
//        String invalidCommand = "landdd";
//
//        assertEquals("ok", commandHandler.handle(validCommand));
//        assertEquals("error", commandHandler.handle(invalidCommand));
//    }
//
//    @Test
//    void forwardValidation() {
//        String validCommand = TelloControlCommands.FORWARD + " 30";
//        String invalidDoubleCommand = TelloControlCommands.FORWARD + " 30.33";
//        String invalidCommand = "drawrof 30";
//
//        String lowestValidParameter = TelloControlCommands.FORWARD + " 20";
//        String lowestInvalidParameter = TelloControlCommands.FORWARD + " 19";
//
//        String highestValidParameter = TelloControlCommands.FORWARD + " 500";
//        String highestInvalidParameter = TelloControlCommands.FORWARD + " 501";
//
//        assertEquals("ok", commandHandler.handle(validCommand));
//        assertEquals("error", commandHandler.handle(invalidDoubleCommand));
//        assertEquals("error", commandHandler.handle(invalidCommand));
//
//        assertEquals("ok", commandHandler.handle(lowestValidParameter));
//        assertEquals("error", commandHandler.handle(lowestInvalidParameter));
//
//        assertEquals("ok", commandHandler.handle(highestValidParameter));
//        assertEquals("error", commandHandler.handle(highestInvalidParameter));
//    }
//
//    @Test
//    void backwardValidation() {
//        String validCommand = TelloControlCommands.BACK + " 30";
//        String invalidDoubleCommand = TelloControlCommands.BACK + " 30.33";
//        String invalidCommand = "drawrof 30";
//
//        String lowestValidParameter = TelloControlCommands.BACK + " 20";
//        String lowestInvalidParameter = TelloControlCommands.BACK + " 19";
//
//        String highestValidParameter = TelloControlCommands.BACK + " 500";
//        String highestInvalidParameter = TelloControlCommands.BACK + " 501";
//
//        assertEquals("ok", commandHandler.handle(validCommand));
//        assertEquals("error", commandHandler.handle(invalidDoubleCommand));
//        assertEquals("error", commandHandler.handle(invalidCommand));
//
//        assertEquals("ok", commandHandler.handle(lowestValidParameter));
//        assertEquals("error", commandHandler.handle(lowestInvalidParameter));
//
//        assertEquals("ok", commandHandler.handle(highestValidParameter));
//        assertEquals("error", commandHandler.handle(highestInvalidParameter));
//    }
//
//    @Test
//    void leftValidation() {
//        String validCommand = TelloControlCommands.LEFT + " 30";
//        String invalidDoubleCommand = TelloControlCommands.LEFT + " 30.33";
//        String invalidCommand = "drawrof 30";
//
//        String lowestValidParameter = TelloControlCommands.LEFT + " 20";
//        String lowestInvalidParameter = TelloControlCommands.LEFT + " 19";
//
//        String highestValidParameter = TelloControlCommands.LEFT + " 500";
//        String highestInvalidParameter = TelloControlCommands.LEFT + " 501";
//
//        assertEquals("ok", commandHandler.handle(validCommand));
//        assertEquals("error", commandHandler.handle(invalidDoubleCommand));
//        assertEquals("error", commandHandler.handle(invalidCommand));
//
//        assertEquals("ok", commandHandler.handle(lowestValidParameter));
//        assertEquals("error", commandHandler.handle(lowestInvalidParameter));
//
//        assertEquals("ok", commandHandler.handle(highestValidParameter));
//        assertEquals("error", commandHandler.handle(highestInvalidParameter));
//    }
//
//    @Test
//    void rightValidation() {
//        String validCommand = TelloControlCommands.RIGHT + " 30";
//        String invalidDoubleCommand = TelloControlCommands.RIGHT + " 30.33";
//        String invalidCommand = "drawrof 30";
//
//        String lowestValidParameter = TelloControlCommands.RIGHT + " 20";
//        String lowestInvalidParameter = TelloControlCommands.RIGHT + " 19";
//
//        String highestValidParameter = TelloControlCommands.RIGHT + " 500";
//        String highestInvalidParameter = TelloControlCommands.RIGHT + " 501";
//
//        assertEquals("ok", commandHandler.handle(validCommand));
//        assertEquals("error", commandHandler.handle(invalidDoubleCommand));
//        assertEquals("error", commandHandler.handle(invalidCommand));
//
//        assertEquals("ok", commandHandler.handle(lowestValidParameter));
//        assertEquals("error", commandHandler.handle(lowestInvalidParameter));
//
//        assertEquals("ok", commandHandler.handle(highestValidParameter));
//        assertEquals("error", commandHandler.handle(highestInvalidParameter));
//    }
//
//    @Test
//    void upValidation() {
//        String validCommand = TelloControlCommands.UP + " 30";
//        String invalidDoubleCommand = TelloControlCommands.UP + " 30.33";
//        String invalidCommand = "drawrof 30";
//
//        String lowestValidParameter = TelloControlCommands.UP + " 20";
//        String lowestInvalidParameter = TelloControlCommands.UP + " 19";
//
//        String highestValidParameter = TelloControlCommands.UP + " 500";
//        String highestInvalidParameter = TelloControlCommands.UP + " 501";
//
//        assertEquals("ok", commandHandler.handle(validCommand));
//        assertEquals("error", commandHandler.handle(invalidDoubleCommand));
//        assertEquals("error", commandHandler.handle(invalidCommand));
//
//        assertEquals("ok", commandHandler.handle(lowestValidParameter));
//        assertEquals("error", commandHandler.handle(lowestInvalidParameter));
//
//        assertEquals("ok", commandHandler.handle(highestValidParameter));
//        assertEquals("error", commandHandler.handle(highestInvalidParameter));
//    }
//
//    @Test
//    void downValidation() {
//        String validCommand = TelloControlCommands.DOWN + " 30";
//        String invalidDoubleCommand = TelloControlCommands.DOWN + " 30.33";
//        String invalidCommand = "drawrof 30";
//
//        String lowestValidParameter = TelloControlCommands.DOWN + " 20";
//        String lowestInvalidParameter = TelloControlCommands.DOWN + " 19";
//
//        String highestValidParameter = TelloControlCommands.DOWN + " 500";
//        String highestInvalidParameter = TelloControlCommands.DOWN + " 501";
//
//        assertEquals("ok", commandHandler.handle(validCommand));
//        assertEquals("error", commandHandler.handle(invalidDoubleCommand));
//        assertEquals("error", commandHandler.handle(invalidCommand));
//
//        assertEquals("ok", commandHandler.handle(lowestValidParameter));
//        assertEquals("error", commandHandler.handle(lowestInvalidParameter));
//
//        assertEquals("ok", commandHandler.handle(highestValidParameter));
//        assertEquals("error", commandHandler.handle(highestInvalidParameter));
//    }
//
//    @Test
//    void rotateCwValidation() {
//        String validCommand = TelloControlCommands.CW + " 30";
//        String invalidDoubleCommand = TelloControlCommands.CW + " 30.33";
//        String invalidCommand = "drawrof 30";
//
//        String lowestValidParameter = TelloControlCommands.CW + " 1";
//        String lowestInvalidParameter = TelloControlCommands.CW + " 0";
//
//        String highestValidParameter = TelloControlCommands.CW + " 360";
//        String highestInvalidParameter = TelloControlCommands.CW + " 361";
//
//        assertEquals("ok", commandHandler.handle(validCommand));
//        assertEquals("error", commandHandler.handle(invalidDoubleCommand));
//        assertEquals("error", commandHandler.handle(invalidCommand));
//
//        assertEquals("ok", commandHandler.handle(lowestValidParameter));
//        assertEquals("error", commandHandler.handle(lowestInvalidParameter));
//
//        assertEquals("ok", commandHandler.handle(highestValidParameter));
//        assertEquals("error", commandHandler.handle(highestInvalidParameter));
//    }
//
//    @Test
//    void rotateCCwValidation() {
//        String validCommand = TelloControlCommands.CCW + " 30";
//        String invalidDoubleCommand = TelloControlCommands.CCW + " 30.33";
//        String invalidCommand = "drawrof 30";
//
//        String lowestValidParameter = TelloControlCommands.CCW + " 1";
//        String lowestInvalidParameter = TelloControlCommands.CCW + " 0";
//
//        String highestValidParameter = TelloControlCommands.CCW + " 360";
//        String highestInvalidParameter = TelloControlCommands.CCW + " 361";
//
//        assertEquals("ok", commandHandler.handle(validCommand));
//        assertEquals("error", commandHandler.handle(invalidDoubleCommand));
//        assertEquals("error", commandHandler.handle(invalidCommand));
//
//        assertEquals("ok", commandHandler.handle(lowestValidParameter));
//        assertEquals("error", commandHandler.handle(lowestInvalidParameter));
//
//        assertEquals("ok", commandHandler.handle(highestValidParameter));
//        assertEquals("error", commandHandler.handle(highestInvalidParameter));
//    }
//
//    @Test
//    void flipValidation() {
//        String flipLeft = TelloControlCommands.FLIP + " l";
//        String invalidFlip = "flip l l";
//    }

}