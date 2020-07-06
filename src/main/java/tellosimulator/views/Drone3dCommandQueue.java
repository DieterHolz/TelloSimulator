package tellosimulator.views;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Drone3dCommandQueue {

    private Queue<Command3d> commandQueue;

    Drone3dCommandQueue() {
        commandQueue = new LinkedList<>();
    }






    //Getter and Setter

    public Queue<Command3d> getCommandQueue() {
        return commandQueue;
    }

    public void setCommandQueue(PriorityQueue<Command3d> commandQueue) {
        this.commandQueue = commandQueue;
    }
}
