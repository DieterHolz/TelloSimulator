package tellosimulator.views;

public class Command3d {
    String direction;
    int distance;
    int priority;

    public Command3d(String direction, int distance) {
        this.direction = direction;
        this.distance = distance;
        priority = 3;
    }

    public Command3d(String direction, int distance, int priority) {
        this(direction, distance);
        this.priority = 3;
    }

    public String toString() {
        return "direction: " + direction + ", distance: " + distance + ", priority: " + priority;
    }


    //Getter and setter

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
