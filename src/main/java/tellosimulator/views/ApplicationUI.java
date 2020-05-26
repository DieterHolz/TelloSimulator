package tellosimulator.views;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import tellosimulator.TelloSimulator;
import tellosimulator.drone.TelloDrone;

public class ApplicationUI extends StackPane {
    private final TelloDrone telloDrone;
    private Group drone3d;

    public ApplicationUI(TelloDrone telloDrone) {
        this.telloDrone = telloDrone;

        Drone3d drone3d = new Drone3d(telloDrone, 18, 5, 16);
        Group drone = drone3d.getDrone3d();
        this.getChildren().add(drone);
        drone3d.add3dDroneToTelloDrone(telloDrone);

        Room3d room3d = new Room3d(TelloSimulator.ROOM_WIDTH,TelloSimulator.ROOM_HEIGHT,TelloSimulator.ROOM_DEPTH,TelloSimulator.WALL_DEPTH);
        Group room = room3d.getRoom3d();
        this.getChildren().add(room);
    }

}
