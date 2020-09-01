package tellosimulator.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import tellosimulator.views.Drone3d;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
class CommandHandlerTest {

    Drone3d drone;
    CommandHandler commandHandler;


    @BeforeEach
    void setUp() {
        drone = new Drone3d();
        commandHandler = new CommandHandler(drone);
    }

    @Test
    void testTakeoff() {
        //given
        String receivedValidCommand = TelloControlCommands.TAKEOFF;
        String receivedInvalidCommand = "takeofff";

        //when
        String responseOk = commandHandler.handle(receivedValidCommand);
        String responseError = commandHandler.handle(receivedInvalidCommand);

        //then
        assertEquals("ok", responseOk);
        assertEquals("error", responseError);
    }

    @Test
    void testForward() {
        //given
        String receivedValidCommand = TelloControlCommands.FORWARD + " 30";
        String receivedInvalidCommand = "drawrof";

        //when
        String responseOk = commandHandler.handle(receivedValidCommand);
        String responseError = commandHandler.handle(receivedInvalidCommand);

        //then
        assertEquals("ok", responseOk);
        assertEquals("error", responseError);
    }
}