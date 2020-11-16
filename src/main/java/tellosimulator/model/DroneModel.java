package tellosimulator.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class DroneModel {

    private int mid = -1, tempLow, tempHigh, tofDistance; //todo: tempLow? tempHigh? barometer?
    private double barometer, accelerationX, accelerationY, accelerationZ;
    private String wifiSsid;
    private String wifiPass;
    private boolean missionPadDetection;
    private int missionPadDetectionMode;
    private long flightTime;

    private long takeoffTime = 0L;

    private final DoubleProperty xPosition = new SimpleDoubleProperty();
    private final DoubleProperty yPosition = new SimpleDoubleProperty();
    private final DoubleProperty zPosition = new SimpleDoubleProperty();

    private final DoubleProperty pitch = new SimpleDoubleProperty();
    private final DoubleProperty roll = new SimpleDoubleProperty();
    private final DoubleProperty yaw = new SimpleDoubleProperty();

    private final DoubleProperty xOrientation = new SimpleDoubleProperty();
    private final DoubleProperty yOrientation = new SimpleDoubleProperty();
    private final DoubleProperty zOrientation = new SimpleDoubleProperty();
    private final DoubleProperty speed = new SimpleDoubleProperty();
    private final DoubleProperty forwardBackwardDiff = new SimpleDoubleProperty(0);
    private final DoubleProperty leftRightDiff = new SimpleDoubleProperty(0);
    private final DoubleProperty upDownDiff = new SimpleDoubleProperty(0);
    private final DoubleProperty yawDiff = new SimpleDoubleProperty(0);

    public DroneModel() {
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

    public double getPitch() {
        return pitch.get();
    }

    public DoubleProperty pitchProperty() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch.set(pitch);
    }

    public double getRoll() {
        return roll.get();
    }

    public DoubleProperty rollProperty() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll.set(roll);
    }

    public double getYaw() {
        return yaw.get();
    }

    public DoubleProperty yawProperty() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw.set(yaw);
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getTempLow() {
        return tempLow;
    }

    public void setTempLow(int tempLow) {
        this.tempLow = tempLow;
    }

    public int getTempHigh() {
        return tempHigh;
    }

    public void setTempHigh(int tempHigh) {
        this.tempHigh = tempHigh;
    }

    public int getTofDistance() {
        return tofDistance;
    }

    public void setTofDistance(int tofDistance) {
        this.tofDistance = tofDistance;
    }

    public double getBarometer() {
        return barometer;
    }

    public void setBarometer(double barometer) {
        this.barometer = barometer;
    }

    public double getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(double accelerationX) {
        this.accelerationX = accelerationX;
    }

    public double getAccelerationY() {
        return accelerationY;
    }

    public void setAccelerationY(double accelerationY) {
        this.accelerationY = accelerationY;
    }

    public double getAccelerationZ() {
        return accelerationZ;
    }

    public void setAccelerationZ(double accelerationZ) {
        this.accelerationZ = accelerationZ;
    }

    public String getWifiSsid() {
        return wifiSsid;
    }

    public void setWifiSsid(String wifiSsid) {
        this.wifiSsid = wifiSsid;
    }

    public String getWifiPass() {
        return wifiPass;
    }

    public void setWifiPass(String wifiPass) {
        this.wifiPass = wifiPass;
    }

    public boolean isMissionPadDetection() {
        return missionPadDetection;
    }

    public void setMissionPadDetection(boolean missionPadDetection) {
        this.missionPadDetection = missionPadDetection;
    }

    public int getMissionPadDetectionMode() {
        return missionPadDetectionMode;
    }

    public void setMissionPadDetectionMode(int missionPadDetectionMode) {
        this.missionPadDetectionMode = missionPadDetectionMode;
    }

    public long getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(long flightTime) {
        this.flightTime = flightTime;
    }

    public long getTakeoffTime() {
        return takeoffTime;
    }

    public void setTakeoffTime(long takeoffTime) {
        this.takeoffTime = takeoffTime;
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
}
