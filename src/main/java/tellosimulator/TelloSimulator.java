package tellosimulator;

import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;
import tellosimulator.views.ApplicationScene;
import tellosimulator.views.ApplicationUI;
import tellosimulator.views.Drone3d;

public class TelloSimulator extends Application {

    public static final double ROOM_WIDTH = 300;
    public static final double ROOM_HEIGHT = 240;
    public static final double ROOM_DEPTH = 800;
    public static final double WALL_DEPTH = 0.01;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Drone3d telloDrone = new Drone3d(18, 5, 16);

        Parent rootPanel = new ApplicationUI(telloDrone);
        Scene scene = new ApplicationScene(rootPanel, primaryStage);

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
