package tellosimulator.controller;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import tellosimulator.command.CommandHandler;
import tellosimulator.common.DefaultValueHelper;
import tellosimulator.command.CommandPackage;
import tellosimulator.common.VectorHelper;
import tellosimulator.network.CommandResponseSender;
import tellosimulator.model.DroneModel;
import tellosimulator.view.drone.DroneView;
import tellosimulator.view.drone.Rotor;
import java.io.IOException;

public class DroneController {
    private DroneModel droneModel;
    private DroneView droneView;

    private final int FRAMES_PER_SECOND = 60;

    public static double INITIAL_X_POSITION = 0;
    public static double INITIAL_Y_POSITION = 0;
    public static double INITIAL_Z_POSITION = 0;

    private AnimationTimer animationTimer;
    private Timeline timeline = new Timeline();

    private boolean animationRunning;
    private CommandHandler commandHandler;
    private CommandPackage commandPackage;
    boolean emergency = false;

    private boolean flyCurve = false;
    //TODO curve stuff, needs a refactoring
    Point3D curveApex;
    Point3D curveEnd;
    Point3D circleMidpoint;
    Point3D dronePosition;
    Point3D circleNormalVector;
    Point3D midDrone;

    enum Rotation {
        YAW,
        ROLL,
        PITCH
    }

    public DroneController(DroneModel droneModel, DroneView droneView) {
        this.droneModel = droneModel;
        this.droneView = droneView;
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

        droneModel.setxPosition(INITIAL_X_POSITION);
        droneModel.setyPosition(INITIAL_Y_POSITION);
        droneModel.setzPosition(INITIAL_Z_POSITION);
        droneModel.setYaw(0);
        droneModel.setPitch(0);
        droneModel.setRoll(0);

        spinDownRotors(false);
    }

    /**
     * Moves the drone to the given target coordinates.
     * @param target the position vector/coordinates of the target
     */
    private void moveToPoint(Point3D target, double speed){
        Point3D from = new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());
        Point3D to = target;
        Duration duration = Duration.seconds(from.distance(to) / speed);
        animate(createMoveAnimation(to, duration));
    }

    /**
     * Moves the drone in one direction for a certain distance.
     * @param directionVector the direction of the movement as a direction vector
     * @param distance the distance (in cm)
     */
    private void move (Point3D directionVector, double distance){
        Point3D from = new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());   // get p1
        Point3D to = from.add(directionVector.multiply(distance)); // vector addition to get p2 (times distance)
        Duration duration = Duration.seconds(distance / droneModel.getSpeed());
        animate(createMoveAnimation(to, duration));
    }

    /**
     * Lands the drone on ground level and spins down rotors.
     */
    private void land () {
        double distanceToGround = -droneModel.getyPosition()+INITIAL_Y_POSITION;
        Point3D from = new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());
        Point3D to = from.add(VectorHelper.getDownwardsNormalVector().multiply(distanceToGround));
        Duration duration = Duration.seconds(Math.abs(distanceToGround) / droneModel.getSpeed());
        animate(createMoveAnimation(to, duration), true);
    }

    /**
     * Rotates the drone around a certain axis for the given angle.
     * @param angle the angle it should rotate. Will be added to the current rotate value of this axis.
     * @param axis the rotation axis it should rotate around
     */
    private void rotate(double angle, Rotation axis) {
        animate(createRotateAnimation(axis, angle));
    }

    private Timeline createRotateAnimation(Rotation rotation, Double angle) {
        timeline.getKeyFrames().clear();

        KeyValue rotate = null;
        Duration duration = null;
        KeyFrame keyFrame = null;
        if (rotation == Rotation.YAW) {
            updateOrientation(angle);
            rotate = new KeyValue(droneModel.yawProperty(), angle);
            duration = Duration.millis(DefaultValueHelper.TURN_DURATION*Math.abs(angle)/360);
        } else if (rotation == Rotation.ROLL) {
            droneModel.setRoll(0);
            rotate = new KeyValue(droneModel.rollProperty(), angle);
            duration = Duration.millis(DefaultValueHelper.FLIP_DURATION);

        } else if (rotation == Rotation.PITCH) {
            droneModel.setPitch(0);
            rotate = new KeyValue(droneModel.pitchProperty(), angle);
            duration = Duration.millis(DefaultValueHelper.FLIP_DURATION);
        }

        if (rotate != null && duration != null){
            keyFrame = new KeyFrame(duration, rotate);
        }

        if (keyFrame != null){
            timeline.getKeyFrames().add(keyFrame);
        }
        return timeline;
    }

    private Timeline createMoveAnimation(Point3D target, Duration duration){
        timeline.getKeyFrames().clear();

        KeyValue keyX = new KeyValue(droneModel.xPositionProperty(), target.getX(), Interpolator.EASE_BOTH);
        KeyValue keyY = new KeyValue(droneModel.yPositionProperty(), target.getY(), Interpolator.EASE_BOTH);
        KeyValue keyZ = new KeyValue(droneModel.zPositionProperty(), target.getZ(), Interpolator.EASE_BOTH);

        KeyFrame keyFrame = new KeyFrame(duration, keyX, keyY, keyZ);
        timeline.getKeyFrames().add(keyFrame);
        return timeline;
    }

    public void emergency() {
        spinDownRotors(false);
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

    /**
     * Plays the timeline and sends ok to the operator once finished playing.
     * @param timeline  the timeline with its KeyFrames which should be animated.
     * @param landing   indicates if rotors should be stopped after the animation.
     */
    private void animate(Timeline timeline, boolean landing) {
        timeline.setOnFinished(event -> {
            animationRunning = false;
            try {
                CommandResponseSender.sendOk(commandPackage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (landing){
                spinDownRotors(true);
            }
        });
        if(!emergency) {
            animationRunning = true;
        }
        timeline.play();
    }

    /**
     * Plays the timeline and sends ok to the operator once finished playing. Rotors are not stopped.
     * @param timeline  the timeline with its KeyFrames which should be animated.
     */
    private void animate(Timeline timeline) {
        animate(timeline, false);
    }

    /**
     * Updates the drone position every frame depending on the currently set rc values forward/backward, left/right,
     * up/down depending on the current speed.
     */
    private void updateRcPosition() {
        Point3D oldPos = new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());
        Point3D forward = getCurrentOrientation().multiply(droneModel.getForwardBackwardDiff()/100);
        Point3D right = VectorHelper.getRightNormalVector(this).multiply(droneModel.getLeftRightDiff()/100);
        Point3D up = VectorHelper.getUpwardsNormalVector().multiply(droneModel.getUpDownDiff()/100);
        Point3D moveDirectionVector = forward.add(right).add(up);
        Point3D newPos = oldPos.add(moveDirectionVector.multiply(droneModel.getSpeed() / FRAMES_PER_SECOND));

        droneModel.setxPosition(newPos.getX());
        droneModel.setyPosition(newPos.getY());
        droneModel.setzPosition(newPos.getZ());
    }

    /**
     * Updates the drone rotation every frame depending on the currently set rc value of yaw.
     */
    private void updateRcYaw() {
        double oldYaw = droneModel.getYaw();
        double rotateAngle = (360F/(FRAMES_PER_SECOND * (DefaultValueHelper.TURN_DURATION/1000F)))*(droneModel.getYawDiff()/100);
        droneModel.setYaw((oldYaw + rotateAngle)%360);
        updateOrientation(rotateAngle);
    }

    /**
     * Updates the drone orientation vector. Called when yawing/turning.
     */
    private void updateOrientation(double rotateAngle) {
        double x1 = droneModel.getxOrientation();
        double z1 = droneModel.getzOrientation();
        droneModel.setxOrientation(Math.cos(rotateAngle * Math.PI / 180) * x1 - Math.sin(rotateAngle * Math.PI / 180) * z1);
        droneModel.setzOrientation(Math.sin(rotateAngle * Math.PI / 180) * x1 + Math.cos(rotateAngle * Math.PI / 180) * z1);
    }

    private void moveOnCurve() throws IOException {
        dronePosition = new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());
        System.out.println("dronePosition: " + dronePosition);

        if (dronePosition != null && curveEnd != null){

            double arcAngle = circleMidpoint.angle(dronePosition, curveEnd);
            System.out.println("arcAngle: " + arcAngle);
            if (arcAngle > 0.1) {

                //TODO: doesnt work if dronemid and curveapex are parallel (crossproduct = 0) -> add check for that
                double rotateDiffAngle = 1;

                Point3D newMidDrone = VectorHelper.rotateVector(midDrone, circleNormalVector, rotateDiffAngle);
                Point3D newPointOnCurve = newMidDrone.add(dronePosition);

                Point3D droneMid = (circleMidpoint.subtract(dronePosition));
                System.out.println("droneMid: " + droneMid);
                Point3D diffVector = droneMid.crossProduct(circleNormalVector);
                System.out.println("diffVector" + diffVector);
                droneModel.setUpDownDiff(diffVector.getY());
                droneModel.setLeftRightDiff(diffVector.getX());
                droneModel.setForwardBackwardDiff(diffVector.getZ());
            } else {
                droneModel.setUpDownDiff(0);
                droneModel.setLeftRightDiff(0);
                droneModel.setForwardBackwardDiff(0);
                flyCurve = false;
                CommandResponseSender.sendOk(commandPackage);
            }
        }
    }

    private void createAnimationLoop() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {  // called in every frame!
                updateRcPosition();
                updateRcYaw();
                if (flyCurve){
                    try {
                        moveOnCurve();
                    } catch (IOException e) {
                        //TODO naja
                        e.printStackTrace();
                    }
                }
            }
        };
        animationTimer.start();
    }

    private void spinUpRotors() {
        //TODO: smooth spin up
        for (Rotor rotor : droneView.getRotors()) {
            RotateTransition rotateTransition = new RotateTransition();
            rotateTransition.setAxis(Rotate.Y_AXIS);
            rotateTransition.setDuration( Duration.seconds(0.1) );
            rotateTransition.setByAngle( 360 );
            rotateTransition.setNode(rotor.getNode());
            rotateTransition.setCycleCount( Animation.INDEFINITE );
            rotor.setRotateTransition(rotateTransition);
            rotateTransition.play();
        }
    }

    private void spinDownRotors(boolean smoothSpinDown) {
        if (smoothSpinDown) {
            //TODO: smooth spin down
        }
        for (Rotor rotor : droneView.getRotors()) {
            if (rotor.getRotateTransition().statusProperty().get() == Animation.Status.RUNNING) {
                    rotor.getRotateTransition().stop();
            }
        }
    }

    /**
     * Helper method to get the vector of where the drone is currently facing.
     */
    public Point3D getCurrentOrientation(){
        return new Point3D(droneModel.getxOrientation(), droneModel.getyOrientation(), droneModel.getzOrientation());
    }

    // control commands
    public void takeoff(CommandPackage commandPackage) {
        spinUpRotors();
        droneModel.setTakeoffTime(System.currentTimeMillis());
        this.commandPackage = commandPackage;
        move(VectorHelper.getUpwardsNormalVector(), DefaultValueHelper.TAKEOFF_DISTANCE);
    }

    public void land(CommandPackage commandPackage) {
        this.commandPackage = commandPackage;
        land();
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
        double actualX = droneModel.getxPosition();
        double actualY = droneModel.getyPosition();
        double actualZ = droneModel.getzPosition();

        curveApex = new Point3D(actualX - y1, actualY - z1, actualZ + x1);
        curveEnd = new Point3D(actualX - y2, actualY - z2, actualZ + x2);
        dronePosition = new Point3D(actualX, actualY, actualZ);
        Point3D localCircleMidpoint = VectorHelper.midPointOfcircumscribedCircle(new Point3D(0,0,0), new Point3D(x1, y1, z1), new Point3D(x2, y2, z2));
        circleMidpoint = new Point3D(actualX - localCircleMidpoint.getY(), actualY - localCircleMidpoint.getZ(), actualZ + localCircleMidpoint.getX());
        System.out.println("Flying on curve");
        System.out.println("dronePosition: " + dronePosition);
        System.out.println("curveApex: " + curveApex);
        System.out.println("curveEnd: " + curveEnd);
        System.out.println("circleMidpoint: " + circleMidpoint);

        midDrone = dronePosition.subtract(circleMidpoint);
        Point3D midApex = curveApex.subtract(circleMidpoint);
        Point3D midEnd = curveEnd.subtract(circleMidpoint);
        System.out.println("MD length: " + midDrone.magnitude());
        System.out.println("ME length: " + midDrone.magnitude());
        System.out.println("MD: " + midDrone);
        System.out.println("ME: " + midDrone);
        circleNormalVector = (midDrone.crossProduct(midApex)).normalize();
        System.out.println("circleNormalVector "+circleNormalVector);


        flyCurve = true;


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
                ";x:" + droneModel.getxPosition() +
                ";y:" + droneModel.getyPosition() +
                ";z:" + droneModel.getzPosition() +
                ";pitch:" + droneModel.getPitch() +
                ";roll:" + droneModel.getRoll() +
                ";yaw:" + droneModel.getYaw() +
                ";vgx:" + droneModel.getLeftRightDiff() +
                ";vgy:" + droneModel.getUpDownDiff() +
                ";vgz:" + droneModel.getForwardBackwardDiff() +
                ";templ:" + droneModel.getTempLow() +
                ";temph:" + droneModel.getTempHigh() +
                ";tof:" + droneModel.getTofDistance() +
                ";h:" + droneModel.yPositionProperty().negate().doubleValue() +
                ";bat:" + getBatteryCharge() +
                ";baro:" + droneModel.getBarometer() +
                ";time:" + getFlightTime() +
                ";agx:" + droneModel.getAccelerationX() +
                ";agy:" + droneModel.getAccelerationY() +
                ";agz:" + droneModel.getAccelerationZ() +
                ";";
    }

    public long getFlightTime() {
        return System.currentTimeMillis() - droneModel.getTakeoffTime();
    }

    public int getBatteryCharge() {
        //todo: note down that we assume a linear battery decrease
        return 100 - (int)getFlightTime()/(DefaultValueHelper.BATTERY_LIFETIME/100);
    }

    //getter and setter
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
