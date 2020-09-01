package tellosimulator.views;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Duration;
import tellosimulator.commands.CommandHandler;
import tellosimulator.commands.TelloDefaultValues;
import tellosimulator.network.CommandPackage;

import java.io.IOException;

public class Drone3d {
    private final int FRAMES_PER_SECOND = 60;

    static final int DRONE_WIDTH = 18;
    static final int DRONE_HEIGHT = 5;
    static final int DRONE_DEPTH = 16;

    static double INITIAL_X_POSITION = Simulator3DScene.ROOM_WIDTH /2;
    public static double INITIAL_Y_POSITION = -DRONE_HEIGHT/2;
    static double INITIAL_Z_POSITION = Math.min(Simulator3DScene.ROOM_DEPTH /5, 500);

    private int mid, x, y, z, pitch, roll, yaw, speedX, speedY, speedZ, tempLow, tempHigh, tofDistance, height, battery, motorTime;
    private double barometer, accelerationX, accelerationY, accelerationZ;

    private Group drone;
    private Group pitchContainer;
    private Group rollContainer;
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

    private final DoubleProperty xOrientation = new SimpleDoubleProperty();
    private final DoubleProperty yOrientation = new SimpleDoubleProperty();
    private final DoubleProperty zOrientation = new SimpleDoubleProperty();
    private final DoubleProperty speed = new SimpleDoubleProperty();

    private final DoubleProperty forwardBackwardDiff = new SimpleDoubleProperty(0);
    private final DoubleProperty leftRightDiff = new SimpleDoubleProperty(0);
    private final DoubleProperty upDownDiff = new SimpleDoubleProperty(0);
    private final DoubleProperty yawDiff = new SimpleDoubleProperty(0);


    public Drone3d() {
        buildDrone();
        setInititalValues();
        animationRunning = false;
        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();
        createAnimationLoop();
    }

    private void buildDrone() {
        drone = new Group();
        pitchContainer = new Group();
        rollContainer = new Group();
        PhongMaterial lightskyblue = new PhongMaterial();
        lightskyblue.setDiffuseColor(Color.LIGHTSKYBLUE);
        Box box = new Box(DRONE_WIDTH, DRONE_HEIGHT, DRONE_DEPTH);
        box.setMaterial(lightskyblue);
        pitchContainer.getChildren().add(box);
        rollContainer.getChildren().add(pitchContainer);
        drone.getChildren().add(rollContainer);
    }

    private void setInititalValues() {

        drone.setTranslateX(INITIAL_X_POSITION);
        drone.setTranslateY(INITIAL_Y_POSITION);
        drone.setTranslateZ(INITIAL_Z_POSITION);

        setxOrientation(0);
        setyOrientation(0);
        setzOrientation(1);

        setSpeed(TelloDefaultValues.DEFAULT_SPEED);
    }

    private void setupEventHandlers() {
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
    }

    private void rotate(int angle, Rotation axis) {

        rotateTransition.setByAngle(angle);

        if (axis == Rotation.YAW) {
            double x1 = getxOrientation();
            double z1 = getzOrientation();

            setxOrientation(Math.cos(angle * Math.PI / 180) * x1 - Math.sin(angle * Math.PI / 180) * z1);
            setzOrientation(Math.sin(angle * Math.PI / 180) * x1 + Math.cos(angle * Math.PI / 180) * z1);
            rotateTransition.setAxis(getUpwardsNormalVector());
            rotateTransition.setDuration(Duration.millis(TelloDefaultValues.TURN_DURATION*Math.abs(angle)/360));
            rotateTransition.setNode(drone);

        } else if (axis == Rotation.ROLL) {
            rotateTransition.setAxis(new Point3D(0,0,1));
            rotateTransition.setDuration(Duration.millis(TelloDefaultValues.FLIP_DURATION));
            rotateTransition.setNode(rollContainer);

        } else if (axis == Rotation.PITCH) {
            rotateTransition.setAxis(new Point3D(1,0,0));
            rotateTransition.setDuration(Duration.millis(TelloDefaultValues.FLIP_DURATION));
            rotateTransition.setNode(pitchContainer);
        }

        rotateTransition.setOnFinished(event -> {
            animationRunning = false;
            commandPackage.setResponse("ok");
            try {
                returnResponseStringToCommandHandler();
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
    private void move (Point3D target){
        double xPos = drone.getTranslateX();
        double yPos = drone.getTranslateY();
        double zPos = drone.getTranslateZ();
        Point3D from = new Point3D(xPos, yPos, zPos);   // get p1
        Point3D to = target;
        //TODO: generate correct duration with speed (cm/s)
        Duration duration = Duration.seconds(calculateDistance(from, to) / getSpeed());
        animate(createMoveAnimation(to, duration));

    }

    private void animate(Timeline timeline) {
        timeline.setOnFinished(event -> {
            animationRunning = false;
            drone.setRotationAxis(getUpwardsNormalVector());
            commandPackage.setResponse("ok");
            try {
                returnResponseStringToCommandHandler();
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
        Point3D from = new Point3D(drone.getTranslateX(), drone.getTranslateY(), drone.getTranslateZ());   // get p1
        Point3D to = from.add(directionVector.multiply(distance)); // vector addition to get p2 (times distance)
        Duration duration = Duration.seconds(distance / getSpeed());
        animate(createMoveAnimation(to, duration));
    }

    private Timeline createMoveAnimation(Point3D target, Duration duration){
        timeline.getKeyFrames().clear();

        KeyValue keyX = new KeyValue(drone.translateXProperty(), target.getX(), Interpolator.EASE_BOTH);
        KeyValue keyY = new KeyValue(drone.translateYProperty(), target.getY(), Interpolator.EASE_BOTH);
        KeyValue keyZ = new KeyValue(drone.translateZProperty(), target.getZ(), Interpolator.EASE_BOTH);

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

    private double calculateDistance(Point3D from, Point3D to) {
        return Math.sqrt(Math.pow(from.getX()-to.getX(), 2) + Math.pow(from.getY()-to.getY(), 2) + Math.pow(from.getZ()-to.getZ(), 2));
    }

    private Point3D getLeftNormalVector(){
        //TODO: calculate the vector pointing -90°(left) from the current orientation on the xz-plane
        return getUpwardsNormalVector().crossProduct(getCurrentOrientation());
    }

    private Point3D getRightNormalVector(){
        //TODO: calculate the vector pointing +90°(right) from the current orientation on the xz-plane
        return getCurrentOrientation().crossProduct(getUpwardsNormalVector());
    }

    private Point3D getUpwardsNormalVector(){
        return new Point3D(0,-1,0);
    }

    private Point3D getDownwardsNormalVector(){
        return new Point3D(0,1,0);
    }

    private Point3D getCurrentOrientation(){
        return new Point3D(getxOrientation(), getyOrientation(), getzOrientation());
    }

    /**
     * Updates the drone position every frame depending on the currently set rc values forward/backward, left/right,
     * up/down depending on the current speed.
     */
    private void updateRcPosition() {
        Point3D oldPos = new Point3D(drone.getTranslateX(), drone.getTranslateY(), drone.getTranslateZ());
        Point3D forward = getCurrentOrientation().multiply(getForwardBackwardDiff()/100);
        Point3D right = getRightNormalVector().multiply(getLeftRightDiff()/100);
        Point3D up = getUpwardsNormalVector().multiply(getUpDownDiff()/100);
        Point3D moveDirectionVector = forward.add(right).add(up);
        Point3D newPos = oldPos.add(moveDirectionVector.multiply(getSpeed() / FRAMES_PER_SECOND));

        drone.setTranslateX(newPos.getX());
        drone.setTranslateY(newPos.getY());
        drone.setTranslateZ(newPos.getZ());
    }

    /**
     * Updates the drone rotation every frame depending on the currently set rc value of yaw.
     */
    private void updateRcYaw() {
        drone.setRotationAxis(getUpwardsNormalVector());
        double oldRotate = drone.getRotate();
        double rotateAngle = (360/(FRAMES_PER_SECOND * (TelloDefaultValues.TURN_DURATION/1000F)))*(getYawDiff()/100);
        drone.setRotate(oldRotate + rotateAngle);
        setxOrientation(Math.cos(rotateAngle * Math.PI / 180) * getxOrientation() - Math.sin(rotateAngle * Math.PI / 180) * getzOrientation());
        setzOrientation(Math.sin(rotateAngle * Math.PI / 180) * getxOrientation() + Math.cos(rotateAngle * Math.PI / 180) * getzOrientation());
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
        move(getUpwardsNormalVector(), TelloDefaultValues.TAKEOFF_DISTANCE);
    }

    public void land(CommandPackage commandPackage) {
        this.commandPackage = commandPackage;
        move(getDownwardsNormalVector(), -getDrone().getTranslateY()+INITIAL_Y_POSITION);
    }

    public void down(CommandPackage commandPackage, int x) {
        this.commandPackage = commandPackage;
        move(getDownwardsNormalVector(), x);
    }

    public void up(CommandPackage commandPackage, int x) {
        this.commandPackage = commandPackage;
        move(getUpwardsNormalVector(), x);
    }

    public void left(CommandPackage commandPackage, int x) {
        this.commandPackage = commandPackage;
        move(getLeftNormalVector(), x);
    }

    public void right(CommandPackage commandPackage, int x) {
        this.commandPackage = commandPackage;
        move(getRightNormalVector(), x);
    }

    public void forward(CommandPackage commandPackage, int x) {
        this.commandPackage = commandPackage;
        move(getCurrentOrientation(), x);
    }

    public void back(CommandPackage commandPackage, int x) {
        this.commandPackage = commandPackage;
        move(getCurrentOrientation().multiply(-1), x);
    }

    public void cw(CommandPackage commandPackage, int x) {
        this.commandPackage = commandPackage;
        rotate(x, Rotation.YAW);
    }

    public void ccw(CommandPackage commandPackage, int x) {
        this.commandPackage = commandPackage;
        rotate(-x, Rotation.YAW);
    }

    public void flip(CommandPackage commandPackage, String x) {
        this.commandPackage = commandPackage;
        switch(x) {
            case "r":
                rotate(360, Rotation.ROLL);
                break;

            case "l":
                rotate(-360, Rotation.ROLL);
                break;

            case "f":
                rotate(360, Rotation.PITCH);
                break;

            case "b":
                rotate(-360, Rotation.PITCH);
                break;
        }
    }

    public void go(CommandPackage commandPackage, int x, int y, int z, int speed) {
        this.commandPackage = commandPackage;
        move(new Point3D(x,y,z)); //TODO: implement speed
        //TODO: Fly to missionpad
    }

    public void stop(CommandPackage commandPackage) {
        //TODO: Hovers in the air
    }

    public void curve(CommandPackage commandPackage, int x1, int x2, int y1, int y2, int z1, int z2, int speed, String mid) {
        //TODO: Fly at a curve according to the two given coordinates at "speed" (cm/s)
    }

    public void jump(CommandPackage commandPackage, int x, int y, int z, int speed, int yaw, String mid1, String mid2) {
        //TODO: Fly at a curve according to the two given coordinates at "speed" (cm/s)
    }

    public void speed(CommandPackage commandPackage) {
        //TODO: Set speed to "x" cm/s
    }

    public void rc(CommandPackage commandPackage, int a, int b, int c, int d) {
        //TODO: Set remot controller control via four channels
    }

    public void wifi(CommandPackage commandPackage, String ssidWifi, String passWifi) {
        //TODO: Set Wi-Fi password
    }

    public void mon(CommandPackage commandPackage) {
        //TODO: Enable mission pad detection (both forward and downward detection).
    }

    public void moff(CommandPackage commandPackage) {
        //TODO: Disable mission pad detection.
    }

    public void mdirection(CommandPackage commandPackage, int x) {
        if (x == 0) {
            //TODO: Enable downward detection only
        } else if (x == 1) {
            //TODO: Enable forward detection only
        } else if (x == 2) {
            //TODO: Enable both forward and downward detection
        }
    }

    public void ap(CommandPackage commandPackage, String ssid, String pass) {
        //TODO: Set the Tello to station mode, and connect to a new access point with the access points ssid and password.
    }

    private void returnResponseStringToCommandHandler() throws IOException {
        commandHandler.returnResponseStringToUDPConncection(commandPackage);
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

    public Group getDrone() {
        return drone;
    }

    public double getxOrientation() {
        return xOrientation.get();
    }

    public DoubleProperty xOrientationProperty() {
        return xOrientation;
    }

    public void setxOrientation(double xOrientation) {
        this.xOrientation.set(xOrientation);
    }

    public double getyOrientation() {
        return yOrientation.get();
    }

    public DoubleProperty yOrientationProperty() {
        return yOrientation;
    }

    public void setyOrientation(double yOrientation) {
        this.yOrientation.set(yOrientation);
    }

    public double getzOrientation() {
        return zOrientation.get();
    }

    public DoubleProperty zOrientationProperty() {
        return zOrientation;
    }

    public void setzOrientation(double zOrientation) {
        this.zOrientation.set(zOrientation);
    }

    public double getSpeed() {
        return speed.get();
    }

    public DoubleProperty speedProperty() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed.set(speed);
    }

    public double getForwardBackwardDiff() {
        return forwardBackwardDiff.get();
    }

    public DoubleProperty forwardBackwardDiffProperty() {
        return forwardBackwardDiff;
    }

    public void setForwardBackwardDiff(double forwardBackwardDiff) {
        this.forwardBackwardDiff.set(forwardBackwardDiff);
    }

    public double getLeftRightDiff() {
        return leftRightDiff.get();
    }

    public DoubleProperty leftRightDiffProperty() {
        return leftRightDiff;
    }

    public void setLeftRightDiff(double leftRightDiff) {
        this.leftRightDiff.set(leftRightDiff);
    }

    public double getUpDownDiff() {
        return upDownDiff.get();
    }

    public DoubleProperty upDownDiffProperty() {
        return upDownDiff;
    }

    public void setUpDownDiff(double upDownDiff) {
        this.upDownDiff.set(upDownDiff);
    }

    public double getYawDiff() {
        return yawDiff.get();
    }

    public DoubleProperty yawDiffProperty() {
        return yawDiff;
    }

    public void setYawDiff(double yawDiff) {
        this.yawDiff.set(yawDiff);
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
