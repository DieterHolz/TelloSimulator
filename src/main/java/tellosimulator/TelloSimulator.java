package tellosimulator;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import tellosimulator.log.Log;
import tellosimulator.log.Logger;
import tellosimulator.views.Drone3d;
import tellosimulator.views.SimulatorPane;

public class TelloSimulator extends Application {

    public static final Log SIM_LOG = new Log();
    Logger mainLogger = new Logger(SIM_LOG, "TelloSimulator");

    @Override
    public void start(Stage primaryStage) throws Exception{
        Drone3d telloDrone = new Drone3d();
        Region rootPanel = new SimulatorPane(primaryStage, telloDrone, SIM_LOG);

        Scene scene = new Scene(rootPanel);
        scene.getStylesheets().add(getClass().getResource("/log-view.css").toExternalForm());

        primaryStage.setTitle("Simulator Tello Drohne");
        primaryStage.setScene(scene);
        primaryStage.show();
        mainLogger.debug("Application started.");

    }

    public static void main(String[] args) throws Exception { launch(args); }

}
