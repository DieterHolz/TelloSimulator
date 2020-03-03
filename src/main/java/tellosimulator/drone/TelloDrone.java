package tellosimulator.drone;

public class TelloDrone {

	//TODO: save coordinates as coordinate/triple or something like this?

	public void takeOff() {
		System.out.println("TelloDrone: takeOff");
		//TODO: Auto takeoff.
	}

	public void land() {
		System.out.println("TelloDrone: land");
		//TODO: Auto landing.
	}

	public void emergency() {
		//TODO: Stop motors immediately.
	}

	public void up(int x) {
		if(x>=20 && x<=500) {
			//TODO: Ascend to “x” cm. x = 20-500
		} else {
			//TODO: throw exception
		}
	}

	public void down(int x) {
		if(x>=20 && x<=500) {
			//TODO: Descend to “x” cm. x = 20-500
		} else {
			//TODO: throw exception
		}
	}

	public void left(int x) {
		if(x>=20 && x<=500) {
			//TODO: Fly left for “x” cm. x = 20-500
		} else {
			//TODO: throw exception
		}
	}

	public void right(int x) {
		if(x>=20 && x<=500) {
			//TODO: Fly right for “x” cm. x = 20-500
		} else {
			//TODO: throw exception
		}
	}

	public void forward(int x) {
		if(x>=20 && x<=500) {
			//TODO: Fly forward for “x” cm. x = 20-500
		} else {
			//TODO: throw exception
		}
	}

	public void back(int x) {
		if(x>=20 && x<=500) {
			//TODO: Fly backward for “x” cm. x = 20-500
		} else {
			//TODO: throw exception
		}
	}

	public void cw(int x) {
		if(x>=1 && x<=360) {
			//TODO: Rotate "x" degrees clockwise. x = 1-360
		} else {
			//TODO: throw exception
		}
	}

	public void ccw(int x) {
		if (x >= 1 && x <= 360) {
			//TODO: Rotate "x" degrees counterclockwise. x = 1-360
		} else {
			//TODO: throw exception
		}
	}

	public void flip(String x) {
		if(x.equals("l") || x.equals("r") || x.equals("f") || x.equals("b")) {
			//TODO: Flip in "x" direction. "l"=left, "r"=right, "f"=forward, "b"=back
		} else {
			//TODO: throw exception
		}
	}

	public void go(int x, int y, int z, int speed) {
		if(x>=-500 && x<=500 && y>=-500 && y<=500 && z>=-500 && z<=500 && speed>=10 && speed<=100 && !(x>=-20 && x<=20 && y>=-20 && y<=20 && z>=-20 && z<=20)) {
			//TODO: Fly to "x" "y" "z" at "speed" (cm/s).
		} else {
			//TODO: throw exception
		}
	}

	public void go(int x, int y, int z, int speed, String mid) {
		if(x>=-500 && x<=500 && y>=-500 && y<=500 && z>=-500 && z<=500 && speed>=10 && speed<=100 && !(x>=-20 && x<=20 && y>=-20 && y<=20 && z>=-20 && z<=20)) {
			//TODO: Fly to "x" "y" "z" at "speed" (cm/s).
		} else {
			//TODO: throw exception
		}
	}

	public void stop() {
		//TODO: Hovers in the air
	}

	public void curve(int x1, int y1, int z1, int x2, int y2, int z2, int speed) {
		//TODO: test that the arc radius is not within a range of 0.5-10 meters
		if(x1>=-500 && x1<=500 && y1>=-500 && y1<=500 && z1>=-500 && z1<=500 &&
				x2>=-500 && x2<=500 && y2>=-500 && y2<=500 && z2>=-500 && z2<=500 &&
				speed>=10 && speed<=60 &&
				!(x1>=-20 && x1<=20 && y1>=-20 && y1<=20 && z1>=-20 && z1<=20) &&
				!(x2>=-20 && x2<=20 && y2>=-20 && y2<=20 && z2>=-20 && z2<=20)) {
			//TODO: Fly at a curve according to the two given coordinates at "speed" (cm/s)
		} else {
			//TODO: throw exception
		}
	}

	public void curve(int x1, int y1, int z1, int x2, int y2, int z2, int speed, String mid) {
		//TODO: test that the arc radius is not within a range of 0.5-10 meters
		if((mid.equals("m1") || mid.equals("m2") || mid.equals("m3") || mid.equals("m4") ||
				mid.equals("m5") || mid.equals("m6") || mid.equals("m7") || mid.equals("m8")) &&
				x1>=-500 && x1<=500 && y1>=-500 && y1<=500 && z1>=-500 && z1<=500 &&
				x2>=-500 && x2<=500 && y2>=-500 && y2<=500 && z2>=-500 && z2<=500 &&
				speed>=10 && speed<=60 &&
				!(x1>=-20 && x1<=20 && y1>=-20 && y1<=20 && z1>=-20 && z1<=20) &&
				!(x2>=-20 && x2<=20 && y2>=-20 && y2<=20 && z2>=-20 && z2<=20)) {
			//TODO: Fly at a curve according to the two given coordinates at "speed" (cm/s)
		} else {
			//TODO: throw exception
		}
	}

	public void jump(int x, int y, int z, int speed, int yaw, String mid1, String mid2) {
		//TODO: throw exception if the arc radius is not within a range of 0.5-10 meters
		//TODO: testing of yaw?
		if((mid1.equals("m1") || mid1.equals("m2") || mid1.equals("m3") || mid1.equals("m4") ||
				mid1.equals("m5") || mid1.equals("m6") || mid1.equals("m7") || mid1.equals("m8")) &&
				(mid2.equals("m1") || mid2.equals("m2") || mid2.equals("m3") || mid2.equals("m4") ||
				mid2.equals("m5") || mid2.equals("m6") || mid2.equals("m7") || mid2.equals("m8")) &&
				x>=-500 && x<=500 && y>=-500 && y<=500 && z>=-500 && z<=500 &&
				speed>=10 && speed<=100 &&
				!(x>=-20 && x<=20 && y>=-20 && y<=20 && z>=-20 && z<=20)) {
			//TODO: Fly at a curve according to the two given coordinates at "speed" (cm/s)
		} else {
			//TODO: throw exception
		}
	}

	public void speed(int x) {
		if(x>=10 && x<=100) {
			//TODO: Set speed to "x" cm/s
		} else {
			//TODO: throw exception
		}
	}

	public void rc(String a, int a1, String b, int b1, String c, int c1, String d, int d1) {
		if((a.equals("left") || a.equals("right")) && a1>=-100 && a1<=100 &&
				(b.equals("left") || b.equals("right")) && b1>=-100 && b1<=100 &&
				(c.equals("left") || c.equals("right")) && c1>=-100 && c1<=100 &&
				(d.equals("left") || d.equals("right")) && d1>=-100 && d1<=100) {
			//TODO: Set remot controller control via four channels
		} else {
			//TODO: throw exception
		}
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
		} else {
			//TODO: throw exception
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

}
