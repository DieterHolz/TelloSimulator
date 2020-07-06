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
import tellosimulator.TelloSimulator;
import tellosimulator.commands.TelloControlCommands;
import tellosimulator.commands.TelloDefaultValues;
import tellosimulator.network.UDPCommandConnection;

import java.util.List;

public class Drone3d {
    
    private final double INITIAL_X_POSITION = TelloSimulator.ROOM_WIDTH /2;


    private int mid, x, y, z, pitch, roll, yaw, speedX, speedY, speedZ, tempLow, tempHigh, tofDistance, height, battery, motorTime;
    private double barometer, accelerationX, accelerationY, accelerationZ;

    private Group GroupDrone3d;
    private Drone3dCommandQueue drone3dCommandQueue;
    private AnimationTimer animationTimer;
    Command3d currentAnimationCommand = null;
    boolean animationRunning;
    
    private Point3D position;
    private Point3D orientation; // y is always 0..
    private int angle;
    private int speed;

    private static final Logger LOGGER = LogManager.getLogger(UDPCommandConnection.class);

    private final DoubleProperty xPosition = new SimpleDoubleProperty();
    private final DoubleProperty yPosition = new SimpleDoubleProperty();
    private final DoubleProperty zPosition = new SimpleDoubleProperty();


    public Drone3d(double width, double height, double depth) {

        drone3dCommandQueue = new Drone3dCommandQueue();
        GroupDrone3d = new Group();

        PhongMaterial lightskyblue = new PhongMaterial();
        lightskyblue.setDiffuseColor(Color.LIGHTSKYBLUE);
        Box box = new Box(width, height, depth);
        box.setMaterial(lightskyblue);

        GroupDrone3d = new Group();
        GroupDrone3d.getChildren().add(box);

        // move 3d Drone to starting position
        GroupDrone3d.translateXProperty().set(INITIAL_X_POSITION);
        GroupDrone3d.translateYProperty().set(-height/2);
        GroupDrone3d.translateZProperty().set(Math.min(TelloSimulator.ROOM_DEPTH /5, 500));
        
        // set initial position parameter
        Point3D initialPosition = new Point3D(INITIAL_X_POSITION, -height/2, Math.min(TelloSimulator.ROOM_DEPTH /5, 500));
        setPosition(initialPosition);

        // set initial orientation to z-axis (into the screen)
        setOrientation(new Point3D(0, 0 , 1));
        setAngle(0);
        setSpeed(TelloDefaultValues.DEFAULT_SPEED);

        animationRunning = false;
        setupValueChangedListeners();
        createAnimationLoop();


    }

    //TODO: add initialize methods like in oop2 / cue

    private void setupValueChangedListeners() {
    }


    private void rotate(int angle) {
        double x1 = getOrientation().getX();
        double z1 = getOrientation().getZ();
        double x2 = Math.cos(angle*Math.PI/180)  * x1 - Math.sin(angle*Math.PI/180) * z1;
        double z2 = Math.sin(angle*Math.PI/180) * x1 + Math.cos(angle*Math.PI/180) * z1;
        orientation = new Point3D(x2, 0, z2);
        setAngle(getAngle()+angle);
        Duration duration = Duration.millis(TelloDefaultValues.TURN_DURATION*Math.abs(angle)/360);
        Animation animation = createRotateAnimation(duration);
        animation.setOnFinished(event -> animationRunning = false);
        animation.play();
    }

    /**
     * Moves the drone to the given target coordinates.
     * @param target the position vector/coordinates of the target
     */
    private void move (Point3D target){
        double xPos = GroupDrone3d.getTranslateX();
        double yPos = GroupDrone3d.getTranslateY();
        double zPos = GroupDrone3d.getTranslateZ();
        Point3D from = new Point3D(xPos, yPos, zPos);   // get p1
        Point3D to = target;
        Duration duration = Duration.seconds(calculateDistance(from, to) / getSpeed());
        Animation animation = createTimeline(to, duration);
        animation.setOnFinished(event -> {
            animationRunning = false;
            // trigger next ?
        });
        animation.play();
    }

    /**
     * Moves the drone in one direction for a certain distance.
     * @param directionVector the direction of the movement as a direction vector
     * @param distance the distance (in cm)
     */
    private void move (Point3D directionVector, double distance){
        double xPos = GroupDrone3d.getTranslateX(); //???
        double yPos = GroupDrone3d.getTranslateY();
        double zPos = GroupDrone3d.getTranslateZ();
        Point3D from = new Point3D(xPos, yPos, zPos);   // get p1
        Point3D to = from.add(directionVector.multiply(distance)); // vector addition to get p2 (times distance)
        Duration duration = Duration.seconds(distance / getSpeed());
        Animation animation = createTimeline(to, duration);
        animation.setOnFinished(event -> {
            animationRunning = false;
            // trigger next ?
        });
        animationRunning = true;
        animation.play();

    }

    private Timeline createTimeline(Point3D target, Duration duration){
        Timeline timeline = new Timeline();

        KeyValue keyX = new KeyValue(GroupDrone3d.translateXProperty(), target.getX(), Interpolator.EASE_BOTH);
        KeyValue keyY = new KeyValue(GroupDrone3d.translateYProperty(), target.getY(), Interpolator.EASE_BOTH);
        KeyValue keyZ = new KeyValue(GroupDrone3d.translateZProperty(), target.getZ(), Interpolator.EASE_BOTH);

        KeyFrame keyFrame = new KeyFrame(duration, keyX, keyY, keyZ);
        timeline.getKeyFrames().add(keyFrame);
        return timeline;
    }

    private Timeline createRotateAnimation(Duration duration){
        Timeline timeline = new Timeline();
        GroupDrone3d.setRotationAxis(getUpwardsNormalVector());
        KeyValue key = new KeyValue(GroupDrone3d.rotateProperty(), getAngle());
        KeyFrame keyFrame = new KeyFrame(duration, key);
        timeline.getKeyFrames().add(keyFrame);
        return timeline;
    }


    // vector calculations

    private double calculateDistance(Point3D from, Point3D to) {
        //TODO : calculate distance between two given points/ortsvektoren
        return 0;
    }

    private Point3D getLeftNormalVector(){
        //TODO: calculate the vector pointing -90°(left) from the current orientation on the xz-plane
        return new Point3D(-getOrientation().getZ(), getOrientation().getY(), getOrientation().getX());
    }

    private Point3D getRightNormalVector(){
        //TODO: calculate the vector pointing +90°(right) from the current orientation on the xz-plane
        return new Point3D(getOrientation().getZ(), getOrientation().getY(), -getOrientation().getX());
    }

    private Point3D getUpwardsNormalVector(){
        return new Point3D(0,-1,0);
    }

    private Point3D getDownwardsNormalVector(){
        return new Point3D(0,1,0);
    }


    private void createAnimationLoop() {

        animationTimer = new AnimationTimer() {
            private long lastTimerCall;

            @Override
            public void handle(long now) {  // called in every frame!

                if(drone3dCommandQueue.getCommandQueue().size() > 0 && !animationRunning) {
                    animationRunning = true;
                    currentAnimationCommand = drone3dCommandQueue.getCommandQueue().poll();
                    List<String> params = currentAnimationCommand.getParameters();


                    if(currentAnimationCommand.getInstruction() == TelloControlCommands.TAKEOFF) {
                        move(getUpwardsNormalVector(), TelloDefaultValues.TAKEOFF_DISTANCE);  // 0,-1,0 should point upwards
                    }
                    else if(currentAnimationCommand.getInstruction() == TelloControlCommands.DOWN) {
                        move(getDownwardsNormalVector(), Integer.parseInt(params.get(0)));
                    }
                    else if(currentAnimationCommand.getInstruction() == TelloControlCommands.UP) {
                        move(getUpwardsNormalVector(), Integer.parseInt(params.get(0)));
                    }
                    else if(currentAnimationCommand.getInstruction() == TelloControlCommands.LEFT) {
                        move(getLeftNormalVector(), Integer.parseInt(params.get(0)));
                    }
                    else if(currentAnimationCommand.getInstruction() == TelloControlCommands.RIGHT) {
                        move(getRightNormalVector(), Integer.parseInt(params.get(0)));
                    }
                    else if(currentAnimationCommand.getInstruction() == TelloControlCommands.BACK) {
                        move(getOrientation().multiply(-1), Integer.parseInt(params.get(0)));
                    }
                    else if(currentAnimationCommand.getInstruction() == TelloControlCommands.FORWARD) {
                        move(getOrientation(), Integer.parseInt(params.get(0)));
                    }
                    else if(currentAnimationCommand.getInstruction() == TelloControlCommands.CW) {
                        rotate(Integer.parseInt(params.get(0)));
                    }
                    else if(currentAnimationCommand.getInstruction() == TelloControlCommands.CCW) {
                        rotate(-Integer.parseInt(params.get(0)));
                    }
                    // TODO
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

    public Group getGroupDrone3d() {
        return GroupDrone3d;
    }

    public Drone3dCommandQueue getDrone3dCommandQueue() {
        return drone3dCommandQueue;
    }

    public void setDrone3dCommandQueue(Drone3dCommandQueue drone3dCommandQueue) {
        this.drone3dCommandQueue = drone3dCommandQueue;
    }

    public Point3D getPosition() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public Point3D getOrientation() {
        return orientation;
    }

    public void setOrientation(Point3D orientation) {
        this.orientation = orientation;
    }


    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void startAnimationLoop() {
    }


    public double getxPosition() {
        return xPosition.get();
    }

    public DoubleProperty xPositionProperty() {
        return xPosition;
    }

    public void setxPosition(double xPosition) {
        this.xPosition.set(xPosition);
    }

    public double getyPosition() {
        return yPosition.get();
    }

    public DoubleProperty yPositionProperty() {
        return yPosition;
    }

    public void setyPosition(double yPosition) {
        this.yPosition.set(yPosition);
    }

    public double getzPosition() {
        return zPosition.get();
    }

    public DoubleProperty zPositionProperty() {
        return zPosition;
    }

    public void setzPosition(double zPosition) {
        this.zPosition.set(zPosition);
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
