package tellosimulator.controller;

import javafx.animation.*;
import javafx.geometry.Point3D;
import javafx.util.Duration;
import tellosimulator.command.CommandHandler;
import tellosimulator.common.DefaultValueHelper;
import tellosimulator.command.CommandPackage;
import tellosimulator.common.VectorHelper;
import tellosimulator.network.CommandResponseSender;
import tellosimulator.model.DroneModel;
import tellosimulator.view.DroneView;
import tellosimulator.view.Simulator3DScene;

import java.io.IOException;

public class DroneController {
    private DroneView droneView; //TODO: let controller only change model and bind view to model
    private DroneModel droneModel;

    private final int FRAMES_PER_SECOND = 60;

    public static double INITIAL_X_POSITION = Simulator3DScene.ROOM_WIDTH /2;
    public static double INITIAL_Y_POSITION = -DroneView.DRONE_HEIGHT/2;
    public static double INITIAL_Z_POSITION = Math.min(Simulator3DScene.ROOM_DEPTH /5, 500);

    private AnimationTimer animationTimer;
    private Timeline timeline = new Timeline();
    private RotateTransition rotateTransition = new RotateTransition();

    private boolean animationRunning;
    private CommandHandler commandHandler;
    private CommandPackage commandPackage;
    boolean emergency = false;

    enum Rotation {
        YAW,
        ROLL,
        PITCH
    }


    public DroneController(DroneModel droneModel) throws IOException {
        this.droneModel = droneModel;
        this.droneView = new DroneView();
        resetValues();
        animationRunning = false;
        createAnimationLoop();
    }

    public void resetValues() {
        droneModel.setxOrientation(0);
        droneModel.setyOrientation(0);
        droneModel.setzOrientation(1);
        droneModel.setSpeed(DefaultValueHelper.DEFAULT_SPEED);
        droneModel.setMissionPadDetection(false);

        droneView.setTranslateX(INITIAL_X_POSITION);
        droneView.setTranslateY(INITIAL_Y_POSITION);
        droneView.setTranslateZ(INITIAL_Z_POSITION);
        droneView.getDrone().setRotate(0);
        droneView.getPitchContainer().setRotate(0);
        droneView.getRollContainer().setRotate(0);
    }


    private void rotate(double angle, Rotation axis) {

        rotateTransition.setByAngle(angle);

        if (axis == Rotation.YAW) {
            updateOrientation(angle);
            rotateTransition.setAxis(VectorHelper.getUpwardsNormalVector());
            rotateTransition.setDuration(Duration.millis(DefaultValueHelper.TURN_DURATION*Math.abs(angle)/360));
            rotateTransition.setNode(droneView.getDrone());

        } else if (axis == Rotation.ROLL) {
            rotateTransition.setAxis(new Point3D(0,0,1));
            rotateTransition.setDuration(Duration.millis(DefaultValueHelper.FLIP_DURATION));
            rotateTransition.setNode(droneView.getRollContainer());

        } else if (axis == Rotation.PITCH) {
            rotateTransition.setAxis(new Point3D(1,0,0));
            rotateTransition.setDuration(Duration.millis(DefaultValueHelper.FLIP_DURATION));
            rotateTransition.setNode(droneView.getPitchContainer());
        }

        rotateTransition.setOnFinished(event -> {
            animationRunning = false;
            try {
                CommandResponseSender.sendOk(commandPackage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        rotateTransition.play();
    }


    /**
     * Moves the drone to the given target coordinates.
     * @param target the position vector/coordinates of the target
     */
    private void moveToPoint(Point3D target, double speed){
        Point3D from = new Point3D(droneView.getTranslateX(), droneView.getTranslateY(), droneView.getTranslateZ());
        Point3D to = target;
        Duration duration = Duration.seconds(from.distance(to) / speed);
        animate(createMoveAnimation(to, duration));

    }

    private void animate(Timeline timeline) {
        timeline.setOnFinished(event -> {
            animationRunning = false;
            droneView.setRotationAxis(VectorHelper.getUpwardsNormalVector());
            try {
                CommandResponseSender.sendOk(commandPackage);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        if(!emergency) {
            animationRunning = true;
        }
        timeline.play();
    }

    /**
     * Moves the drone in one direction for a certain distance.
     * @param directionVector the direction of the movement as a direction vector
     * @param distance the distance (in cm)
     */
    private void move (Point3D directionVector, double distance){
        Point3D from = new Point3D(droneView.getTranslateX(), droneView.getTranslateY(), droneView.getTranslateZ());   // get p1
        Point3D to = from.add(directionVector.multiply(distance)); // vector addition to get p2 (times distance)
        Duration duration = Duration.seconds(distance / droneModel.getSpeed());
        animate(createMoveAnimation(to, duration));
    }

    private Timeline createMoveAnimation(Point3D target, Duration duration){
        timeline.getKeyFrames().clear();

        KeyValue keyX = new KeyValue(droneView.translateXProperty(), target.getX(), Interpolator.EASE_BOTH);
        KeyValue keyY = new KeyValue(droneView.translateYProperty(), target.getY(), Interpolator.EASE_BOTH);
        KeyValue keyZ = new KeyValue(droneView.translateZProperty(), target.getZ(), Interpolator.EASE_BOTH);

        KeyFrame keyFrame = new KeyFrame(duration, keyX, keyY, keyZ);
        timeline.getKeyFrames().add(keyFrame);
        return timeline;
    }

    public void emergency() {
        /*emergency = true;
        if (rotateTransition.getStatus() == Animation.Status.RUNNING) {
            rotateTransition.stop();
        }
        if(timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
        }
        animationRunning = false;
*//*
        Point3D to = new Point3D(0, INITIAL_Y_POSITION, 0);
        Duration duration = Duration.seconds(drone.getTranslateY()+INITIAL_Y_POSITION / TelloDefaultValues.DEFAULT_SPEED_OF_FALL);

        KeyValue keyX = new KeyValue(drone.translateXProperty(), to.getX(), Interpolator.EASE_IN);
        KeyValue keyY = new KeyValue(drone.translateYProperty(), to.getY(), Interpolator.EASE_IN);
        KeyValue keyZ = new KeyValue(drone.translateZProperty(), to.getZ(), Interpolator.EASE_IN);

        KeyFrame keyFrame = new KeyFrame(duration, keyX, keyY, keyZ);
        timeline.getKeyFrames().add(keyFrame);
        animate(timeline);*//*

        LOGGER.fatal("emergency!!!!");*/
    }


    // vector calculations



    public Point3D getCurrentOrientation(){
        return new Point3D(droneModel.getxOrientation(), droneModel.getyOrientation(), droneModel.getzOrientation());
    }

    /**
     * Updates the drone position every frame depending on the currently set rc values forward/backward, left/right,
     * up/down depending on the current speed.
     */
    private void updateRcPosition() {
        Point3D oldPos = new Point3D(droneView.getTranslateX(), droneView.getTranslateY(), droneView.getTranslateZ());
        Point3D forward = getCurrentOrientation().multiply(droneModel.getForwardBackwardDiff()/100);
        Point3D right = VectorHelper.getRightNormalVector(this).multiply(droneModel.getLeftRightDiff()/100);
        Point3D up = VectorHelper.getUpwardsNormalVector().multiply(droneModel.getUpDownDiff()/100);
        Point3D moveDirectionVector = forward.add(right).add(up);
        Point3D newPos = oldPos.add(moveDirectionVector.multiply(droneModel.getSpeed() / FRAMES_PER_SECOND));

        droneView.setTranslateX(newPos.getX());
        droneView.setTranslateY(newPos.getY());
        droneView.setTranslateZ(newPos.getZ());
    }

    /**
     * Updates the drone rotation every frame depending on the currently set rc value of yaw.
     */
    private void updateRcYaw() {
        droneView.setRotationAxis(VectorHelper.getUpwardsNormalVector());
        double oldRotate = droneView.getRotate();
        double rotateAngle = (360F/(FRAMES_PER_SECOND * (DefaultValueHelper.TURN_DURATION/1000F)))*(droneModel.getYawDiff()/100);
        droneView.setRotate((oldRotate + rotateAngle)%360);

        updateOrientation(rotateAngle);
    }

    private void updateOrientation(double rotateAngle) {
        double x1 = droneModel.getxOrientation();
        double z1 = droneModel.getzOrientation();
        droneModel.setxOrientation(Math.cos(rotateAngle * Math.PI / 180) * x1 - Math.sin(rotateAngle * Math.PI / 180) * z1);
        droneModel.setzOrientation(Math.sin(rotateAngle * Math.PI / 180) * x1 + Math.cos(rotateAngle * Math.PI / 180) * z1);
    }

    private void createAnimationLoop() {

        animationTimer = new AnimationTimer() {
            private long lastTimerCall;

            @Override
            public void handle(long now) {  // called in every frame!
                updateRcPosition();
                updateRcYaw();
            }
        };

        animationTimer.start();
    }

    public void takeoff(CommandPackage commandPackage) {
        this.commandPackage = commandPackage;
        move(VectorHelper.getUpwardsNormalVector(), DefaultValueHelper.TAKEOFF_DISTANCE);
    }

    public void land(CommandPackage commandPackage) {
        this.commandPackage = commandPackage;
        move(VectorHelper.getDownwardsNormalVector(), -droneView.getTranslateY()+INITIAL_Y_POSITION);
    }

    public void down(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        move(VectorHelper.getDownwardsNormalVector(), x);
    }

    public void up(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        move(VectorHelper.getUpwardsNormalVector(), x);
    }

    public void left(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        move(VectorHelper.getLeftNormalVector(this), x);
    }

    public void right(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        move(VectorHelper.getRightNormalVector(this), x);
    }

    public void forward(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        move(getCurrentOrientation(), x);
    }

    public void back(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        move(getCurrentOrientation().multiply(-1), x);
    }

    public void cw(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        rotate(x, Rotation.YAW);
    }

    public void ccw(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        rotate(-x, Rotation.YAW);
    }

    public void flip(CommandPackage commandPackage, String flipDirection) {
        this.commandPackage = commandPackage;
        switch(flipDirection) {
            case "r":
                rotate(360, Rotation.ROLL);
                break;

            case "l":
                rotate(-360, Rotation.ROLL);
                break;

            case "f":
                rotate(-360, Rotation.PITCH);
                break;

            case "b":
                rotate(360, Rotation.PITCH);
                break;
        }
    }

    public void go(CommandPackage commandPackage, double x, double y, double z, double speed) {
        this.commandPackage = commandPackage;
        moveToPoint(new Point3D(x,y,z), speed);
        //TODO: Fly to missionpad
    }

    public void stop(CommandPackage commandPackage) {
        //TODO: Hovers in the air works at any time
    }

    public void curve(CommandPackage commandPackage, double x1, double y1, double z1, double x2, double y2, double z2, double speed) {
        //TODO: Fly at a curve according to the two given coordinates at "speed" (cm/s)
        //Point3D point1 = new Point3D(drone.getTranslateX(), drone.getTranslateY(), drone.getTranslateZ());
        Point3D point1 = new Point3D(0,0,0);
        Point3D point2 = new Point3D(x1,y1,z1);
        Point3D point3 = new Point3D(x2,y2,z2);

    }

    public void jump(CommandPackage commandPackage, double x, double y, double z, double speed, double yaw, String mid1, String mid2) {
        //TODO: Fly at a curve according to the two given coordinates at "speed" (cm/s)
    }

    public void rc(CommandPackage commandPackage, double a, double b, double c, double d) {
        droneModel.setForwardBackwardDiff(a);
        droneModel.setLeftRightDiff(b);
        droneModel.setUpDownDiff(c);
        droneModel.setYawDiff(d);
    }

    public void ap(CommandPackage commandPackage, String ssid, String pass) {
        //TODO: Set the Tello to station mode, and connect to a new access point with the access points ssid and password.
    }

    public String getDroneState() {

        //TODO: check if mission pad detection feature is enabled/disbled
        return  "mid:" + droneModel.getMid() +
                ";x:" + droneModel.getX() +
                ";y:" + droneModel.getY() +
                ";z:" + droneModel.getZ() +
                ";pitch:" + droneModel.getPitch() +
                ";roll:" + droneModel.getRoll() +
                ";yaw:" + droneModel.getYaw() +
                ";vgx:" + droneModel.getSpeedX() +
                ";vgy:" + droneModel.getSpeedY() +
                ";vgz:" + droneModel.getSpeedZ() +
                ";templ:" + droneModel.getTempLow() +
                ";temph:" + droneModel.getTempHigh() +
                ";tof:" + droneModel.getTofDistance() +
                ";h:" + droneModel.getHeight() +
                ";bat:" + droneModel.getBattery() +
                ";baro:" + droneModel.getBarometer() +
                ";time:" + droneModel.getMotorTime() +
                ";agx:" + droneModel.getAccelerationX() +
                ";agy:" + droneModel.getAccelerationY() +
                ";agz:" + droneModel.getAccelerationZ() +
                ";";
    }

    //getter and setter

    public DroneView getDroneView() {
        return droneView;
    }

    public DroneModel getDroneModel() {
        return droneModel;
    }

    public boolean isAnimationRunning() {
        return animationRunning;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public void setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }
}
