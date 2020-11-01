package tellosimulator.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class DroneModel {

    private int mid, x, y, z, pitch, roll, yaw, speedX, speedY, speedZ, tempLow, tempHigh, tofDistance, height, battery, motorTime;
    private double barometer, accelerationX, accelerationY, accelerationZ;
    private String wifiSsid;
    private String wifiPass;
    private boolean missionPadDetection;
    private int missionPadDetectionMode;
    private long flightTime;

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

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public int getYaw() {
        return yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public int getSpeedZ() {
        return speedZ;
    }

    public void setSpeedZ(int speedZ) {
        this.speedZ = speedZ;
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getMotorTime() {
        return motorTime;
    }

    public void setMotorTime(int motorTime) {
        this.motorTime = motorTime;
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
