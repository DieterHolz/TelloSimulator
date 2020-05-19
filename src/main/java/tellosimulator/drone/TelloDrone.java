package tellosimulator.drone;

import javafx.scene.Group;
import tellosimulator.views.ApplicationUI;

public class TelloDrone {

	private Group drone3d;
    private int mid, x, y, z, pitch, roll, yaw, speedX, speedY, speedZ, tempLow, tempHigh, tofDistance, height, battery, motorTime;
    private double barometer, accelerationX, accelerationY, accelerationZ;

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

	//TODO: save coordinates as coordinate/triple or something like this?

	public void takeOff() {
		//TODO: Auto takeoff.
		drone3d.translateYProperty().set(-50);
	}

	public void land() {
		//TODO: Auto landing.
	}

	public void emergency() {
		//TODO: Stop motors immediately.
	}

	public void up(int x) {
		//TODO: Ascend to “x” cm.
		drone3d.translateYProperty().set(drone3d.getTranslateY()-x);
	}

	public void down(int x) {
		//TODO: Descend to “x” cm.
		drone3d.translateYProperty().set(drone3d.getTranslateY()+x);
	}

	public void left(int x) {
		//TODO: Fly left for “x” cm.
		drone3d.translateXProperty().set(drone3d.getTranslateX()-x);
	}

	public void right(int x) {
		//TODO: Fly right for “x” cm.
		drone3d.translateXProperty().set(drone3d.getTranslateX()+x);
	}

	public void forward(int x) {
		//TODO: Fly forward for “x” cm.
		drone3d.translateZProperty().set(drone3d.getTranslateZ()-x);
	}

	public void back(int x) {
		//TODO: Fly backward for “x” cm.
		drone3d.translateZProperty().set(drone3d.getTranslateZ()+x);
	}

	public void cw(int x) {
		//TODO: Rotate "x" degrees clockwise.
	}

	public void ccw(int x) {
		//TODO: Rotate "x" degrees counterclockwise. x = 1-360
	}

	public void flip(String x) {
		//TODO: Flip in "x" direction. "l"=left, "r"=right, "f"=forward, "b"=back
	}

	public void go(int x, int y, int z, int speed) {
		//TODO: Fly to "x" "y" "z" at "speed" (cm/s).
	}

	public void go(int x, int y, int z, int speed, String mid) {
		//TODO: Fly to "x" "y" "z" at "speed" (cm/s).
	}

	public void stop() {
		//TODO: Hovers in the air
	}

	public void curve(int x1, int y1, int z1, int x2, int y2, int z2, int speed, String mid) {
		//TODO: Fly at a curve according to the two given coordinates at "speed" (cm/s)
	}

	public void jump(int x, int y, int z, int speed, int yaw, String mid1, String mid2) {
		//TODO: Fly at a curve according to the two given coordinates at "speed" (cm/s)
	}

	public void speed(int x) {
		//TODO: Set speed to "x" cm/s
	}

	public void rc(int a, int b, int c, int d) {
		//TODO: Set remot controller control via four channels
	}

	public void wifi(String ssid, String pass) {
		//TODO: Set Wi-Fi password
	}

	public void mon() {
		//TODO: Enable mission pad detection (both forward and downward detection).
	}

	public void moff() {
		//TODO: Disable mission pad detection.
	}

	public void mdirection(int x) {
		if(x==0) {
			//TODO: Enable downward detection only
		} else if(x==1) {
			//TODO: Enable forward detection only
		} else if(x==2) {
			//TODO: Enable both forward and downward detection
		}
	}

	public void ap(String ssid, String pass) {
		//TODO: Set the Tello to station mode, and connect to a new access point with the access points ssid and password.
	}

	public void readSpeed() {
		//TODO: Send current speed (10-100 cm/s)
	}

	public void readBattery() {
		//TODO: Send current battery percentage (0-100)
	}

	public void readTime() {
		//TODO: Send current flight time
	}

	public void readWifi() {
		//TODO: Send Wi-Fi SNR
	}

	public void readSdk() {
		//TODO: Send the Tello SDK version
	}

	public void readSn() {
		//TODO: Send the Tello serial number
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



	// Getter and Setter

	public Group getDrone3d() {
		return drone3d;
	}

	public void setDrone3d(Group drone3d) {
		this.drone3d = drone3d;
	}

}
