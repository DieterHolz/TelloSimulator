/*package tellosimulator.commands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tellosimulator.drone.TelloDrone;
import tellosimulator.exception.TelloIllegalArgumentException;

import static org.junit.jupiter.api.Assertions.*;

class CommandHandlerTest {

    TelloDrone telloDrone = new TelloDrone();
    CommandHandler commandHandler = new CommandHandler(telloDrone);

    @Test
    void testHandle() {
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
    void testValidateUp() {

        //given
        String receivedCommand1 = TelloControlCommands.UP + " -8568";
        String receivedCommand2 = TelloControlCommands.UP + " -501";
        String receivedCommand3 = TelloControlCommands.UP + " -500";
        String receivedCommand4 = TelloControlCommands.UP + " -499";
        String receivedCommand5 = TelloControlCommands.UP + " 10";
        String receivedCommand6 = TelloControlCommands.UP + " 499";
        String receivedCommand7 = TelloControlCommands.UP + " 500";
        String receivedCommand8 = TelloControlCommands.UP + " 501";
        String receivedCommand9 = TelloControlCommands.UP + " 2586";

        //when
        String response1 = commandHandler.handle(receivedCommand1);
        String response2 = commandHandler.handle(receivedCommand2);
        String response3 = commandHandler.handle(receivedCommand3);
        String response4 = commandHandler.handle(receivedCommand4);
        String response5 = commandHandler.handle(receivedCommand5);
        String response6 = commandHandler.handle(receivedCommand6);
        String response7 = commandHandler.handle(receivedCommand7);
        String response8 = commandHandler.handle(receivedCommand8);
        String response9 = commandHandler.handle(receivedCommand9);

        //then

        assertEquals("ok", response3);
        assertEquals("ok", response4);
        assertEquals("ok", response5);
        assertEquals("ok", response6);
        assertEquals("ok", response7);

//        assertThrows(TelloIllegalArgumentException, response1);
//
//        Exception exception = assertThrows(TelloIllegalArgumentException.class, () -> {
//            commandHandler.handle(receivedCommand9);
//        });
//
//        String expectedMessage = "Illegal Argument. Command: ";
//        String actualMessage = exception.getMessage();
//
//        assertTrue(actualMessage.contains(expectedMessage));
    }

}*/