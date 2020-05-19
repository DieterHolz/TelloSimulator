package tellosimulator;

import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
import tellosimulator.drone.TelloDrone;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPStateConnection;
import tellosimulator.views.ApplicationScene;
import tellosimulator.views.ApplicationUI;

public class TelloSimulator extends Application {

    public static final double ROOMWIDTH = 1000;
    public static final double ROOMHEIGHT = 240;
    public static final double ROOMDEPTH = 1000;
    public static final double WALLDEPTH = 0.01;

    @Override
    public void start(Stage primaryStage) throws Exception{

        TelloDrone telloDrone = new TelloDrone();

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
