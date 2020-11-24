package tellosimulator.controller;

import javafx.animation.*;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import tellosimulator.TelloSimulator;
import tellosimulator.command.CommandHandler;
import tellosimulator.common.DefaultValueHelper;
import tellosimulator.command.CommandPackage;
import tellosimulator.common.VectorHelper;
import tellosimulator.log.Logger;
import tellosimulator.network.CommandResponseSender;
import tellosimulator.model.DroneModel;
import tellosimulator.view.drone.DroneView;
import tellosimulator.view.drone.Rotor;

public class DroneController {
    private final Logger logger = new Logger(TelloSimulator.MAIN_LOG, "DroneController");

    private DroneModel droneModel;
    private DroneView droneView;

    private final int FRAMES_PER_SECOND = 60;

    public static double INITIAL_X_POSITION = 0;
    public static double INITIAL_Y_POSITION = 0;
    public static double INITIAL_Z_POSITION = 0;

    private AnimationTimer animationTimer;
    private Timeline timeline = new Timeline();

    private boolean animationRunning;
    private boolean motorsRunning = false;
    private CommandHandler commandHandler;
    private CommandPackage commandPackage;

    boolean emergency = false;

    private boolean flyCurve = false;
    //TODO curve stuff, needs a refactoring
    double curveRadius;
    double arcLength;
    double arcAngle;
    double rotateDiffAnglePerFrame;

    Point3D curveP1;
    Point3D curveEnd;
    Point3D curveCenter;
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
        resetDiffs();
        resetOrientation();
        resetPosition();
        resetRotates();
        resetSpeed();
        droneModel.setMissionPadDetection(false);

        stopAnimation();
        emergency = false;
        flyCurve = false;
        spinDownRotors();
    }

    private void resetPosition() {
        droneModel.setxPosition(INITIAL_X_POSITION);
        droneModel.setyPosition(INITIAL_Y_POSITION);
        droneModel.setzPosition(INITIAL_Z_POSITION);
    }

    private void resetOrientation() {
        droneModel.setxOrientation(0);
        droneModel.setyOrientation(0);
        droneModel.setzOrientation(1);
    }

    private void resetDiffs() {
        droneModel.setLeftRightDiff(0);
        droneModel.setForwardBackwardDiff(0);
        droneModel.setUpDownDiff(0);
        droneModel.setYawDiff(0);
    }

    private void resetRotates() {
        droneModel.setYaw(0);
        droneModel.setPitch(0);
        droneModel.setRoll(0);
    }

    private void resetSpeed() {
        droneModel.setSpeed(DefaultValueHelper.DEFAULT_SPEED);
    }

    private void stopAnimation() {
        timeline.stop();
        timeline.getKeyFrames().clear();
        animationRunning = false;
    }

    /**
     * Moves the drone to the given target coordinates (view coordinate system, not drone coordinate system!).
     * @param target the position vector/coordinates of the target
     */
    private void moveToPoint(Point3D target, double speed){
        Point3D from = new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());
        Duration duration = Duration.seconds(from.distance(target) / speed);
        animate(createMoveAnimation(target, duration));
    }

    /**
     * Moves the drone in one direction for a certain distance.
     * @param directionVector the direction of the movement as a direction vector
     * @param distance the distance (in cm)
     */
    private void move (Point3D directionVector, double distance){
        Point3D from = new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());
        Point3D to = from.add(directionVector.normalize().multiply(distance));
        Duration duration = Duration.seconds(distance / droneModel.getSpeed());
        animate(createMoveAnimation(to, duration));
    }

    /**
     * Moves the drone in one direction for a certain distance at a certain speed.
     * @param directionVector the direction of the movement as a direction vector
     * @param distance the distance (in cm)
     * @param speed the speed (in cm/s)
     */
    private void move (Point3D directionVector, double distance, double speed){
        Point3D from = new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());
        Point3D to = from.add(directionVector.normalize().multiply(distance));
        Duration duration = Duration.seconds(distance / speed);
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

    private Timeline createMoveAnimation(Point3D target, Duration duration){
        timeline.getKeyFrames().clear();

        KeyValue keyX = new KeyValue(droneModel.xPositionProperty(), target.getX(), Interpolator.EASE_BOTH);
        KeyValue keyY = new KeyValue(droneModel.yPositionProperty(), target.getY(), Interpolator.EASE_BOTH);
        KeyValue keyZ = new KeyValue(droneModel.zPositionProperty(), target.getZ(), Interpolator.EASE_BOTH);

        KeyFrame keyFrame = new KeyFrame(duration, keyX, keyY, keyZ);
        timeline.getKeyFrames().add(keyFrame);
        return timeline;
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
            rotate = new KeyValue(droneModel.yawProperty(), droneModel.getYaw() + angle);
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

    /**
     * Updates the drone orientation vector. Called when yawing/turning.
     */
    private void updateOrientation(double rotateAngle) {
        double x1 = droneModel.getxOrientation();
        double z1 = droneModel.getzOrientation();
        droneModel.setxOrientation(Math.cos(rotateAngle * Math.PI / 180) * x1 - Math.sin(rotateAngle * Math.PI / 180) * z1);
        droneModel.setzOrientation(Math.sin(rotateAngle * Math.PI / 180) * x1 + Math.cos(rotateAngle * Math.PI / 180) * z1);
    }

    /**
     * Plays the timeline and sends ok to the operator once finished playing. Rotors are not stopped.
     * @param timeline  the timeline with its KeyFrames which should be animated.
     */
    private void animate(Timeline timeline) {
        animate(timeline, false);
    }

    /**
     * Plays the timeline and sends ok to the operator once finished playing.
     * @param timeline  the timeline with its KeyFrames which should be animated.
     * @param landing   indicates if rotors should be stopped after the animation.
     */
    private void animate(Timeline timeline, boolean landing) {
        timeline.setOnFinished(event -> {
            if (landing){
                spinDownRotors();
            }
            animationRunning = false;
            CommandResponseSender.sendOk(commandPackage);
        });
        if(!emergency) {
            animationRunning = true;
        }
        timeline.play();
    }

    /**
     * Updates the drone position every frame depending on the currently set rc values forward/backward, left/right,
     * up/down depending on the current speed.
     */
    private void updateRcPosition() {
        Point3D oldPos = new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());
        Point3D forward = getDroneOrientation().multiply(droneModel.getForwardBackwardDiff()/100);
        Point3D right = VectorHelper.getRightNormalVector(getDroneOrientation()).multiply(droneModel.getLeftRightDiff()/100);
        Point3D up = VectorHelper.getUpwardsNormalVector().multiply(droneModel.getUpDownDiff()/100);
        Point3D moveDirectionVector = forward.add(right).add(up);
        Point3D newPos = oldPos.add(moveDirectionVector);

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
     * Updates the drone position every frame while moving on a curve.
     */
    private void moveOnCurve() {
        dronePosition = getDronePosition();

        if (curveEnd != null){
            double newArcAngle = curveCenter.angle(dronePosition, curveEnd);

            //TODO: this should really be checked differently, but works for now
            if (newArcAngle > 1) {
                Point3D newMidDrone = VectorHelper.rotateVector(midDrone, circleNormalVector, rotateDiffAnglePerFrame);
                Point3D newPointOnCurve = curveCenter.add(newMidDrone);
                midDrone = newMidDrone;
                droneModel.setxPosition(newPointOnCurve.getX());
                droneModel.setyPosition(newPointOnCurve.getY());
                droneModel.setzPosition(newPointOnCurve.getZ());

            } else {
                droneModel.setxPosition(curveEnd.getX());
                droneModel.setyPosition(curveEnd.getY());
                droneModel.setzPosition(curveEnd.getZ());
                flyCurve = false;
                CommandResponseSender.sendOk(commandPackage);
            }
        }
    }

    private void createAnimationLoop() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {  // called in every frame!
                updateRcYaw();
                updateRcPosition();
                if (flyCurve){
                    moveOnCurve();
                }
            }
        };
        animationTimer.start();
    }

    private void spinUpRotors() {
        if (!motorsRunning) {
            motorsRunning = true;
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
    }

    private void spinDownRotors() {
        if (motorsRunning){
            motorsRunning = false;
            for (Rotor rotor : droneView.getRotors()) {
                if (rotor.getRotateTransition().statusProperty().get() == Animation.Status.RUNNING) {
                    rotor.getRotateTransition().stop();
                }
            }
        }
    }

    public Point3D getDroneOrientation(){
        return new Point3D(droneModel.getxOrientation(), droneModel.getyOrientation(), droneModel.getzOrientation());
    }

    private Point3D getDronePosition() {
        return new Point3D(droneModel.getxPosition(), droneModel.getyPosition(), droneModel.getzPosition());
    }

    // control commands
    public void takeoff(CommandPackage commandPackage) {
        spinUpRotors();
        droneModel.setTakeoffTime(System.currentTimeMillis());
        this.commandPackage = commandPackage;
        move(VectorHelper.getUpwardsNormalVector(), DefaultValueHelper.TAKEOFF_DISTANCE);
    }

    public void land(CommandPackage commandPackage) {
        if (commandPackage != null) {
            this.commandPackage = commandPackage;
        }

        if (motorsRunning) {
            land();
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void down(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            move(VectorHelper.getDownwardsNormalVector(), x);
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void up(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            move(VectorHelper.getUpwardsNormalVector(), x);
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void left(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            move(VectorHelper.getLeftNormalVector(getDroneOrientation()), x);
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void right(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            move(VectorHelper.getRightNormalVector(getDroneOrientation()), x);
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void forward(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            move(getDroneOrientation(), x);
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void back(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            move(getDroneOrientation().multiply(-1), x);
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void cw(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            rotate(-x, Rotation.YAW);
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void ccw(CommandPackage commandPackage, double x) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            rotate(x, Rotation.YAW);
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void flip(CommandPackage commandPackage, String flipDirection) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
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
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void stop(CommandPackage commandPackage) {
        resetDiffs();
        stopAnimation();
        CommandResponseSender.sendOk(commandPackage);
    }

    public void emergency() {
        if (motorsRunning) {
            emergency = true;
            resetDiffs();
            resetSpeed();
            stopAnimation();
            spinDownRotors();
            playFallAnimation();
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    private void playFallAnimation() {
        Point3D to = new Point3D(droneModel.getxPosition(), 0, droneModel.getzPosition());
        Duration duration = Duration.seconds(Math.sqrt(2 * Math.abs(droneModel.getyPosition() / DefaultValueHelper.DEFAULT_SPEED_OF_FALL)));
        KeyValue keyY = new KeyValue(droneModel.yPositionProperty(), to.getY(), Interpolator.EASE_IN); //TODO use a more realistic interpolator

        KeyFrame keyFrame = new KeyFrame(duration, keyY);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setOnFinished(null);
        timeline.play();
    }

    public void go(CommandPackage commandPackage, double x, double y, double z, double speed) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            dronePosition = getDronePosition();

            //adapt values to javafx coordinate system
            Point3D relativeCoords = new Point3D(-y, -z, x);

            //correct rotation relative to drone orientation
            double offsetAngle = droneModel.getYaw();
            Point3D rotatedCoords = VectorHelper.rotateAroundYAxis(relativeCoords, offsetAngle);

            //transform relative to drone position
            Point3D transformedCoords = new Point3D(rotatedCoords.getX() + dronePosition.getX(), rotatedCoords.getY() + dronePosition.getY(), rotatedCoords.getZ() + dronePosition.getZ());

            move(transformedCoords, dronePosition.distance(transformedCoords), speed);
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void go(CommandPackage commandPackage, double x, double y, double z, double speed, String mid) {
        sendNotImplemented(commandPackage);
    }

    public void curve(CommandPackage commandPackage, double x1, double y1, double z1, double x2, double y2, double z2, double speed) {
        this.commandPackage = commandPackage;
        if (motorsRunning) {
            dronePosition = getDronePosition();

            curveRadius = VectorHelper.radiusOfcircumscribedCircle(Point3D.ZERO, new Point3D(x1, y1, z1), new Point3D(x2, y2, z2));
            Point3D unadaptedCurveCenter = VectorHelper.midPointOfcircumscribedCircle(Point3D.ZERO, new Point3D(x1, y1, z1), new Point3D(x2, y2, z2));

            //adapt values to javafx coordinate system
            Point3D inputCurveP1 = new Point3D(-y1, -z1, x1);
            Point3D inputCurveP2 = new Point3D(-y2, -z2, x2);
            Point3D inputCircleMidPoint = new Point3D(-unadaptedCurveCenter.getY(), -unadaptedCurveCenter.getZ(), unadaptedCurveCenter.getX());

            //correct rotation relative to drone orientation
            double offsetAngle = droneModel.getYaw();

            Point3D rotatedP1 = VectorHelper.rotateAroundYAxis(inputCurveP1, offsetAngle);
            Point3D rotatedP2 = VectorHelper.rotateAroundYAxis(inputCurveP2, offsetAngle);
            Point3D rotatedCenter = VectorHelper.rotateAroundYAxis(inputCircleMidPoint, offsetAngle);

            //transform all points relative to drone position
            curveP1 = new Point3D(rotatedP1.getX() + dronePosition.getX(), rotatedP1.getY() + dronePosition.getY(), rotatedP1.getZ() + dronePosition.getZ());
            curveEnd = new Point3D(rotatedP2.getX() + dronePosition.getX(), rotatedP2.getY() + dronePosition.getY(), rotatedP2.getZ() + dronePosition.getZ());
            curveCenter = new Point3D(rotatedCenter.getX() + dronePosition.getX(), rotatedCenter.getY() + dronePosition.getY(), rotatedCenter.getZ() + dronePosition.getZ());

            arcAngle = curveCenter.angle(dronePosition, curveEnd);
            arcLength = (arcAngle/360) * 2 * curveRadius * Math.PI;
            midDrone = dronePosition.subtract(curveCenter);
            Point3D midP1 = curveP1.subtract(curveCenter);
            Point3D midP2 = curveEnd.subtract(curveCenter);
            circleNormalVector = (midDrone.crossProduct(midP1)).normalize();
            rotateDiffAnglePerFrame = 0.001 * speed/5;
            flyCurve = true;
        } else {
            logger.error("Failed to execute command. Motor not running.");
            CommandResponseSender.sendMotorStop(commandPackage);
        }
    }

    public void curve(CommandPackage commandPackage, double x1, double y1, double z1, double x2, double y2, double z2, double speed, String mid) {
        sendNotImplemented(commandPackage);
    }

    public void jump(CommandPackage commandPackage, double x, double y, double z, double speed, double yaw, String mid1, String mid2) {
        sendNotImplemented(commandPackage);
    }

    public void rc(double a, double b, double c, double d) {
        droneModel.setLeftRightDiff(a);
        droneModel.setForwardBackwardDiff(b);
        droneModel.setUpDownDiff(c);
        droneModel.setYawDiff(-d);
    }

    public void ap(String ssid, String pass) {
        //TODO: Set the Tello to station mode, and connect to a new access point with the access points ssid and password.
        // Sets the Wi-Fi SSID and password. The Tello will reboot afterwords
        droneModel.setWifiSsid(ssid);
        droneModel.setWifiPass(pass);
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

    private void sendNotImplemented(CommandPackage commandPackage) {
        this.commandPackage = commandPackage;
        logger.warn("Command not implemented.");
        CommandResponseSender.sendOk(commandPackage);
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

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public boolean isMotorsRunning() {
        return motorsRunning;
    }

    public void setMotorsRunning(boolean motorsRunning) {
        this.motorsRunning = motorsRunning;
    }
}
