package tellosimulator.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tellosimulator.command.CommandHandler;
import tellosimulator.command.CommandPackage;
import tellosimulator.command.TelloSetCommand;
import tellosimulator.controller.DroneController;
import tellosimulator.model.DroneModel;
import tellosimulator.network.CommandConnection;
import tellosimulator.common.TelloSDKValues;
import tellosimulator.view.drone.DroneView;

import java.io.IOException;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DroneControllerTest {
    DroneModel droneModel;
    DroneView droneView;
    DroneController droneController;
    CommandHandler commandHandler;
    CommandConnection commandConnection;
    CommandPackage commandPackage;


    @BeforeEach
    void setUp() throws IOException {
        droneModel = new DroneModel();
        droneView = new DroneView(droneModel);
        droneController = new DroneController(droneModel);
        commandConnection = new CommandConnection(droneController);
        commandHandler = new CommandHandler(droneController);
        commandPackage = new CommandPackage(null, InetAddress.getByName("127.0.0.1"), TelloSDKValues.SIM_COMMAND_PORT);
    }

    private void computeResponse(String command) {
        commandPackage.setCommand(command);
        commandHandler.handle(commandPackage);
    }

    @Test
    void testRcHandling() {
        assertEquals(0, droneController.getDroneModel().getLeftRightDiff());
        assertEquals(0, droneController.getDroneModel().getForwardBackwardDiff());
        assertEquals(0, droneController.getDroneModel().getUpDownDiff());
        assertEquals(0, droneController.getDroneModel().getYawDiff());

        computeResponse(TelloSetCommand.RC + " 0 10 20 30");

        assertEquals(0, droneController.getDroneModel().getLeftRightDiff());
        assertEquals(10, droneController.getDroneModel().getForwardBackwardDiff());
        assertEquals(20, droneController.getDroneModel().getUpDownDiff());
        assertEquals(-30, droneController.getDroneModel().getYawDiff());
    }

}