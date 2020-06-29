package tellosimulator.views;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tellosimulator.TelloSimulator;
import tellosimulator.commands.TelloControlCommands;
import tellosimulator.network.UDPCommandConnection;

public class Drone3d {

    private int mid, x, y, z, pitch, roll, yaw, speedX, speedY, speedZ, tempLow, tempHigh, tofDistance, height, battery, motorTime;
    private double barometer, accelerationX, accelerationY, accelerationZ;

    private Group GroupDrone3d;
    private Drone3dCommandQueue drone3dCommandQueue;
    private AnimationTimer animationTimer;
    int currentAnimationLeftDistance = 0;
    Command3d currentAnimationCommand = null;

    private static final Logger LOGGER = LogManager.getLogger(UDPCommandConnection.class);


    public Drone3d(double width, double height, double depth) {

        GroupDrone3d = new Group();

        PhongMaterial lightskyblue = new PhongMaterial();
        lightskyblue.setDiffuseColor(Color.LIGHTSKYBLUE);
        Box box = new Box(width, height, depth);
        box.setMaterial(lightskyblue);

        GroupDrone3d = new Group();
        GroupDrone3d.getChildren().add(box);

        GroupDrone3d.translateXProperty().set(TelloSimulator.ROOM_WIDTH /2);
        GroupDrone3d.translateYProperty().set(-height/2);
        GroupDrone3d.translateZProperty().set(Math.min(TelloSimulator.ROOM_DEPTH /5, 500));

        drone3dCommandQueue = new Drone3dCommandQueue();

        createAnimationLoop();
    }

    private void createAnimationLoop() {

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if(drone3dCommandQueue.getCommandQueue().size() != 0) {

                    if(currentAnimationLeftDistance <= 0) {
                        currentAnimationCommand = drone3dCommandQueue.getCommandQueue().poll();
                        currentAnimationLeftDistance = currentAnimationCommand.getDistance();
                    }

                    if(currentAnimationCommand.getDirection() == TelloControlCommands.TAKEOFF) {
                        GroupDrone3d.translateYProperty().setValue(GroupDrone3d.getTranslateY()-1);
                    } else if(currentAnimationCommand.getDirection() == TelloControlCommands.DOWN) {
                        GroupDrone3d.translateYProperty().setValue(GroupDrone3d.getTranslateY()+1);
                    } else if(currentAnimationCommand.getDirection() == TelloControlCommands.UP) {
                        GroupDrone3d.translateYProperty().setValue(GroupDrone3d.getTranslateY()-1);
                    } else if(currentAnimationCommand.getDirection() == TelloControlCommands.LEFT) {
                        GroupDrone3d.translateXProperty().setValue(GroupDrone3d.getTranslateX()-1);
                    } else if(currentAnimationCommand.getDirection() == TelloControlCommands.RIGHT) {
                        GroupDrone3d.translateXProperty().setValue(GroupDrone3d.getTranslateX()+1);
                    } else if(currentAnimationCommand.getDirection() == TelloControlCommands.BACK) {
                        GroupDrone3d.translateZProperty().setValue(GroupDrone3d.getTranslateZ()+1);
                    } else if(currentAnimationCommand.getDirection() == TelloControlCommands.FORWARD) {
                        GroupDrone3d.translateZProperty().setValue(GroupDrone3d.getTranslateZ()-1);
                    }

                    currentAnimationLeftDistance--;
                }
            }
        };

        animationTimer.start();
    }
    public String getDroneState() {

        //TODO: check if mission pad detection feature is enabled/disbled
        return  "mid:" + mid +
                ";x:" + x +
                ";y:" + y +
                ";z:" + z +
                ";pitch:" + pitch +
                ";roll:" + roll +
                ";yaw:" + yaw +
                ";vgx:" + speedX +
                ";vgy:" + speedY +
                ";vgz:" + speedZ +
                ";templ:" + tempLow +
                ";temph:" + tempHigh +
                ";tof:" + tofDistance +
                ";h:" + height +
                ";bat:" + battery +
                ";baro:" + barometer +
                ";time:" + motorTime +
                ";agx:" + accelerationX +
                ";agy:" + accelerationY +
                ";agz:" + accelerationZ +
                ";";
    }

    //getter and setter

    public int getPitch() {
        return pitch;
    }

    public int getRoll() {
        return roll;
    }

    public int getYaw() {
        return yaw;
    }

    public int getSpeedX() {
        return speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public int getSpeedZ() {
        return speedZ;
    }

    public int getTempLow() {
        return tempLow;
    }

    public int getTempHigh() {
        return tempHigh;
    }

    public int getTofDistance() {
        return tofDistance;
    }

    public int getHeight() {
        return height;
    }

    public int getBattery() {
        return battery;
    }

    public int getMotorTime() {
        return motorTime;
    }

    public double getBarometer() {
        return barometer;
    }

    public double getAccelerationX() {
        return accelerationX;
    }

    public double getAccelerationY() {
        return accelerationY;
    }

    public double getAccelerationZ() {
        return accelerationZ;
    }

    public Group getGroupDrone3d() {
        return GroupDrone3d;
    }

    public Drone3dCommandQueue getDrone3dCommandQueue() {
        return drone3dCommandQueue;
    }

    public void setDrone3dCommandQueue(Drone3dCommandQueue drone3dCommandQueue) {
        this.drone3dCommandQueue = drone3dCommandQueue;
    }
}
