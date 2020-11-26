package tellosimulator;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import tellosimulator.log.Log;
import tellosimulator.log.Logger;
import tellosimulator.controller.DroneController;
import tellosimulator.model.DroneModel;
import tellosimulator.view.drone.DroneView;
import tellosimulator.view.layout.SimulatorPane;


/**
 * The main class of the TelloSimulator.
 *
 * @author Daniel Obrist
 * @author Severin Peyer
 *
 */
public class TelloSimulator extends Application {

    public static final Log MAIN_LOG = new Log();
    private final Logger logger = new Logger(MAIN_LOG, "TelloSimulator");

    @Override
    public void start(Stage primaryStage) throws Exception{
        DroneModel droneModel = new DroneModel();
        DroneView droneView = new DroneView(droneModel);
        DroneController droneController = new DroneController(droneModel);

        Region rootPanel = new SimulatorPane(droneController, droneModel, droneView, MAIN_LOG);
        Scene scene = new Scene(rootPanel);

        scene.getStylesheets().add(getClass().getResource("/log-view.css").toExternalForm());

        primaryStage.setTitle("Tello Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.debug("Welcome. Start the virtual drone and connect to it with your Application.");
    }

    public static void main(String[] args) throws Exception { launch(args); }

}
