package tellosimulator;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;
import tellosimulator.views.Drone3d;
import tellosimulator.views.SimulatorPane;

public class TelloSimulator extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Drone3d telloDrone = new Drone3d(18, 5, 16);
        Region rootPanel = new SimulatorPane(primaryStage, telloDrone);

        Scene scene = new Scene(rootPanel);

        primaryStage.setTitle("Simulator Tello Drohne");
        primaryStage.setScene(scene);
        primaryStage.show();

        UDPCommandConnection commandConnection = new UDPCommandConnection(telloDrone);
        commandConnection.start();

        UDPStateConnection stateConnection = new UDPStateConnection(telloDrone);
        stateConnection.start();
    }

    public static void main(String[] args) throws Exception { launch(args); }

}
