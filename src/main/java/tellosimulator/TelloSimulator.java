package tellosimulator;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import tellosimulator.log.Log;
import tellosimulator.log.Logger;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;
import tellosimulator.views.Drone3d;
import tellosimulator.views.SimulatorPane;

public class TelloSimulator extends Application {

    public static final Log MAIN_LOG = new Log();
    private final Logger logger = new Logger(MAIN_LOG, "TelloSimulator");

    @Override
    public void start(Stage primaryStage) throws Exception{
        Drone3d telloDrone = new Drone3d();

        Region rootPanel = new SimulatorPane(primaryStage, telloDrone, MAIN_LOG);
        Scene scene = new Scene(rootPanel);

        scene.getStylesheets().add(getClass().getResource("/log-view.css").toExternalForm());

        primaryStage.setTitle("Tello Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.debug("Welcome. Start the virtual drone and connect to it with your Application.");
    }

    public static void main(String[] args) throws Exception { launch(args); }

}
