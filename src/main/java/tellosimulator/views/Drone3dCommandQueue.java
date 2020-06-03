package tellosimulator.views;

import java.util.PriorityQueue;

public class Drone3dCommandQueue {

    private PriorityQueue<Command3d> commandQueue;

    Drone3dCommandQueue() {
        commandQueue = new PriorityQueue<>(new Command3dComparator());
    }






    //Getter and Setter

    public PriorityQueue<Command3d> getCommandQueue() {
        return commandQueue;
    }

    public void setCommandQueue(PriorityQueue<Command3d> commandQueue) {
        this.commandQueue = commandQueue;
    }
}
