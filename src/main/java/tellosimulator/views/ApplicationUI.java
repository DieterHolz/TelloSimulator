package tellosimulator.views;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import tellosimulator.TelloSimulator;
import tellosimulator.drone.TelloDrone;

public class ApplicationUI extends StackPane {

    public ApplicationUI(TelloDrone telloDrone) {

        Drone3d drone3d = new Drone3d(18, 5, 16);
        this.getChildren().add(drone3d.getGroupDrone3d());
        telloDrone.setDrone3d(drone3d);

        Room3d room3d = new Room3d(TelloSimulator.ROOM_WIDTH,TelloSimulator.ROOM_HEIGHT,TelloSimulator.ROOM_DEPTH,TelloSimulator.WALL_DEPTH);
        Group room = room3d.getRoom3d();
        this.getChildren().add(room);
    }

}
