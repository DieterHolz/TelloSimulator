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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tellosimulator.commands.TelloControlCommands;
import tellosimulator.commands.TelloDefaultValues;
import tellosimulator.network.UDPCommandConnection;

import java.util.List;

public class Drone3d {

    static final int DRONE_WIDTH = 18;
    static final int DRONE_HEIGHT = 5;
    static final int DRONE_DEPTH = 16;

    static double INITIAL_X_POSITION = Simulator3DScene.ROOM_WIDTH /2;
    static double INITIAL_Y_POSITION = -DRONE_HEIGHT/2;
    static double INITIAL_Z_POSITION = Math.min(Simulator3DScene.ROOM_DEPTH /5, 500);

    private int mid, x, y, z, pitch, roll, yaw, speedX, speedY, speedZ, tempLow, tempHigh, tofDistance, height, battery, motorTime;
    private double barometer, accelerationX, accelerationY, accelerationZ;

    private Group drone;
    private Group pitchContainer;
    private Group rollContainer;
    private Drone3dCommandQueue commandQueue;
    private AnimationTimer animationTimer;
    private Timeline timeline = new Timeline();
    private RotateTransition rotateTransition = new RotateTransition();

    Command3d currentAnimationCommand = null;
    boolean animationRunning;
    boolean emergency = false;

    enum Rotation {
        YAW,
        ROLL,
        PITCH
    }

    private static final Logger LOGGER = LogManager.getLogger(UDPCommandConnection.class);

    private final DoubleProperty xPosition = new SimpleDoubleProperty(); // needed? we have translateXProperty
    private final DoubleProperty yPosition = new SimpleDoubleProperty();
    private final DoubleProperty zPosition = new SimpleDoubleProperty();
    private final DoubleProperty xOrientation = new SimpleDoubleProperty();
    private final DoubleProperty yOrientation = new SimpleDoubleProperty();
    private final DoubleProperty zOrientation = new SimpleDoubleProperty();

    private final DoubleProperty yawAngle = new SimpleDoubleProperty();
    private final DoubleProperty rollAngle = new SimpleDoubleProperty(); //TODO: do we need these?
    private final DoubleProperty pitchAngle = new SimpleDoubleProperty(); //TODO: do we need these?
    private final DoubleProperty speed = new SimpleDoubleProperty();


    public Drone3d() {
        initCommandQueue();
        buildDrone();
        setInititalValues();

        animationRunning = false;

        setupEventHandlers();
        setupValueChangedListeners();
        setupBindings();

        createAnimationLoop();
    }
    
    private void initCommandQueue() {
        commandQueue = new Drone3dCommandQueue();
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
        // move 3d Drone to starting position
        drone.setTranslateX(INITIAL_X_POSITION);
        drone.setTranslateY(INITIAL_Y_POSITION);
        drone.setTranslateZ(INITIAL_Z_POSITION);

        // set initial orientation to z-axis (into the screen)
        setxOrientation(0);
        setyOrientation(0);
        setzOrientation(1);
        setYawAngle(0);
        setRollAngle(0);
        setPitchAngle(0);
        setSpeed(TelloDefaultValues.DEFAULT_SPEED);
    }

    private void setupEventHandlers() {
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
    }

    private void animateUI() {

    }

    private void updateUI() {

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
        Duration duration = Duration.seconds(calculateDistance(from, to) / getSpeed());
        animate(createMoveAnimation(to, duration));

    }

    private void animate(Timeline timeline) {
        timeline.setOnFinished(event -> {
            animationRunning = false;
            drone.setRotationAxis(getUpwardsNormalVector());
            //drone.setRotate(getYawAngle());
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


    // vector calculations-

    private double calculateDistance(Point3D from, Point3D to) {
        //TODO : calculate distance between two given points/ortsvektoren
        return 0;
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


    private void createAnimationLoop() {

        animationTimer = new AnimationTimer() {
            private long lastTimerCall;

            @Override
            public void handle(long now) {  // called in every frame!

                if(commandQueue.getCommandQueue().size() > 0 && !animationRunning && !emergency) {
                    animationRunning = true;
                    currentAnimationCommand = commandQueue.getCommandQueue().poll();
                    List<String> params = currentAnimationCommand.getParameters();

                    switch(currentAnimationCommand.getInstruction()) {

                        case TelloControlCommands.TAKEOFF:
                            move(getUpwardsNormalVector(), TelloDefaultValues.TAKEOFF_DISTANCE);
                            break;

                        case TelloControlCommands.LAND:
                            move(getDownwardsNormalVector(), -drone.getTranslateY()+INITIAL_Y_POSITION);
                            break;

                        case TelloControlCommands.DOWN:
                            move(getDownwardsNormalVector(), Integer.parseInt(params.get(0)));
                            break;

                        case TelloControlCommands.UP:
                            move(getUpwardsNormalVector(), Integer.parseInt(params.get(0)));
                            break;

                        case TelloControlCommands.LEFT:
                            move(getLeftNormalVector(), Integer.parseInt(params.get(0)));
                            break;

                        case TelloControlCommands.RIGHT:
                            move(getRightNormalVector(), Integer.parseInt(params.get(0)));
                            break;

                        case TelloControlCommands.BACK:
                            move(getCurrentOrientation().multiply(-1), Integer.parseInt(params.get(0)));
                            break;

                        case TelloControlCommands.FORWARD:
                            move(getCurrentOrientation(), Integer.parseInt(params.get(0)));
                            break;

                        case TelloControlCommands.CW:
                            rotate(Integer.parseInt(params.get(0)), Rotation.YAW);
                            break;

                        case TelloControlCommands.CCW:
                            rotate(-Integer.parseInt(params.get(0)), Rotation.YAW);
                            break;

                        case TelloControlCommands.FLIP:
                            switch(params.get(0)) {
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
                            break;

                        // TODO: handle other commands
                    }
                    lastTimerCall = now;

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

    public Group getDrone() {
        return drone;
    }

    public Drone3dCommandQueue getCommandQueue() {
        return commandQueue;
    }

    public void setCommandQueue(Drone3dCommandQueue commandQueue) {
        this.commandQueue = commandQueue;
    }

    public void startAnimationLoop() {
    }


    public double getXPosition() {
        return xPosition.get();
    }

    public DoubleProperty xPositionProperty() {
        return xPosition;
    }

    public void setXPosition(double xPosition) {
        this.xPosition.set(xPosition);
    }

    public double getYPosition() {
        return yPosition.get();
    }

    public DoubleProperty YPositionProperty() {
        return yPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition.set(yPosition);
    }

    public double getZPosition() {
        return zPosition.get();
    }

    public DoubleProperty zPositionProperty() {
        return zPosition;
    }

    public void setZPosition(double zPosition) {
        this.zPosition.set(zPosition);
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

    public double getYawAngle() {
        return yawAngle.get();
    }

    public DoubleProperty yawAngleProperty() {
        return yawAngle;
    }

    public void setYawAngle(double yawAngle) {
        this.yawAngle.set(yawAngle);
    }

    public double getRollAngle() {
        return rollAngle.get();
    }

    public DoubleProperty rollAngleProperty() {
        return rollAngle;
    }

    public void setRollAngle(double rollAngle) {
        this.rollAngle.set(rollAngle);
    }

    public double getPitchAngle() {
        return pitchAngle.get();
    }

    public DoubleProperty pitchAngleProperty() {
        return pitchAngle;
    }

    public void setPitchAngle(double pitchAngle) {
        this.pitchAngle.set(pitchAngle);
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


}
