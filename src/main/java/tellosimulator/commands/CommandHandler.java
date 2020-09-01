package tellosimulator.commands;

import tellosimulator.TelloSimulator;
import tellosimulator.exception.TelloIllegalArgumentException;
import tellosimulator.log.Logger;
import tellosimulator.network.CommandPackage;
import tellosimulator.network.UDPCommandConnection;
import tellosimulator.network.UDPVideoConnection;
import tellosimulator.video.VideoPublisher;
import tellosimulator.views.Drone3d;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CommandHandler {
	private Logger logger = new Logger(TelloSimulator.MAIN_LOG, "CommandHandler");
	private String errorString = "error";
	private UDPCommandConnection udpCommandConnection;

	Drone3d drone3d;
	VideoPublisher publisher;

    public CommandHandler(Drone3d drone3d, UDPCommandConnection udpCommandConnection) {
        this.drone3d = drone3d;
        this.udpCommandConnection = udpCommandConnection;
    }

    public void handle(CommandPackage commandPackage) throws IOException {
        String received = commandPackage.getCommand();
		List<String> data = Arrays.asList(received.split(" "));
		String command = data.get(0);
		List<String> params = null;
		UDPVideoConnection videoConnection = new UDPVideoConnection();

		if (data.size() > 1) {
			params = data.subList(1, data.size());
		}

		logger.info("handling command: " + command);

		if(!drone3d.isAnimationRunning() || command == TelloControlCommands.EMERGENCY || command == TelloSetCommands.RC) {
			switch (command) {
				case TelloControlCommands.COMMAND:
					break;

				case TelloControlCommands.TAKEOFF:
					drone3d.takeoff(commandPackage);
					break;

				case TelloControlCommands.LAND:
					drone3d.land(commandPackage);
					break;

				case TelloControlCommands.STREAMON:
					publisher = new VideoPublisher();
					if (!publisher.isRunning()) {
						publisher.setRunning(true);
						publisher.start();
					}
					break;

				case TelloControlCommands.STREAMOFF:
					if (publisher.isRunning()) {
						publisher.setRunning(false);
					}
					break;

				case TelloControlCommands.EMERGENCY:
					drone3d.emergency();
					break;

				case TelloControlCommands.UP:
					int xUp = Integer.parseInt(params.get(0));

					if(validateUp(xUp)) {
						drone3d.up(commandPackage, xUp);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.DOWN:
					int xDown = Integer.parseInt(params.get(0));

					if(validateDown(xDown)) {
						drone3d.down(commandPackage, xDown);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.LEFT:
					int xLeft = Integer.parseInt(params.get(0));

					if(validateLeft(xLeft)) {
						drone3d.left(commandPackage, xLeft);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.RIGHT:
					int xRight = Integer.parseInt(params.get(0));
					if(validateRight(xRight)) {
						drone3d.right(commandPackage, xRight);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.FORWARD:
					int xForward = Integer.parseInt(params.get(0));

					if(validateForward(xForward)) {
						drone3d.forward(commandPackage, xForward);
					} else {
					    commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.BACK:
					int xBack = Integer.parseInt(params.get(0));

					if(validateBack(xBack)) {
						drone3d.back(commandPackage, xBack);
					} else {
                        commandPackage.setResponse(errorString);
                        returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.CW:
					int xCw = Integer.parseInt(params.get(0));

					if(validateCw(xCw)) {
						drone3d.cw(commandPackage, xCw);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.CCW:
					int xCcw = Integer.parseInt(params.get(0));

					if(validateCcw(xCcw)) {
						drone3d.ccw(commandPackage, xCcw);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.FLIP:
					String xFlip = params.get(0);

					if(validateFlip(xFlip)) {
						drone3d.flip(commandPackage, xFlip);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.GO:
					int xGo = Integer.parseInt(params.get(0));
					int yGo = Integer.parseInt(params.get(1));
					int zGo = Integer.parseInt(params.get(2));
					int speedGo = Integer.parseInt(params.get(3));
					String midGo = params.get(4);

					if(validateGo(xGo, yGo, zGo, speedGo, midGo)) {
						drone3d.go(commandPackage, xGo, yGo, zGo, speedGo, midGo);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.STOP:
					drone3d.stop(commandPackage);
					break;

				case TelloControlCommands.CURVE:
					int x1Curve = Integer.parseInt(params.get(0));
					int x2Curve = Integer.parseInt(params.get(1));
					int y1Curve = Integer.parseInt(params.get(2));
					int y2Curve = Integer.parseInt(params.get(3));
					int z1Curve = Integer.parseInt(params.get(4));
					int z2Curve = Integer.parseInt(params.get(5));
					int speedCurve = Integer.parseInt(params.get(6));
					String midCurve = params.get(7);

					if(validateCurve(x1Curve, x2Curve, y1Curve, y2Curve, z1Curve, z2Curve, speedCurve, midCurve)) {
						drone3d.curve(commandPackage, x1Curve, x2Curve, y1Curve, y2Curve, z1Curve, z2Curve, speedCurve, midCurve);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloControlCommands.JUMP:
					int xJump = Integer.parseInt(params.get(0));
					int yJump = Integer.parseInt(params.get(1));
					int zJump = Integer.parseInt(params.get(2));
					int speedJump = Integer.parseInt(params.get(3));
					int yawJump = Integer.parseInt(params.get(4));
					String mid1Jump = params.get(5);
					String mid2Jump = params.get(6);

					if(validateJump(xJump, yJump, zJump, speedJump, yawJump, mid1Jump, mid2Jump)) {
						drone3d.jump(commandPackage, xJump, yJump, zJump, speedJump, yawJump, mid1Jump, mid2Jump);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloSetCommands.SPEED:
					drone3d.speed(commandPackage);
					break;

				case TelloSetCommands.RC:
					int a = Integer.parseInt(params.get(0));
					int b = Integer.parseInt(params.get(0));
					int c = Integer.parseInt(params.get(0));
					int d = Integer.parseInt(params.get(0));

					if(validateRc(a, b, c, d)) {
						drone3d.rc(commandPackage, a, b, c, d);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloSetCommands.WIFI:
					String ssidWifi = params.get(0);
					String passWifi = params.get(1);

					if(validateWifi(ssidWifi, passWifi)) {
						drone3d.wifi(commandPackage, ssidWifi, passWifi);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloSetCommands.MON:
					drone3d.mon(commandPackage);
					break;

				case TelloSetCommands.MOFF:
					drone3d.moff(commandPackage);
					break;

				case TelloSetCommands.MDIRECTION:
					int xMdirection = Integer.parseInt(params.get(0));

					if(validateMdirection(xMdirection)) {
						drone3d.mdirection(commandPackage, xMdirection);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloSetCommands.AP:
					String ssidAp = params.get(0);
					String passAp = params.get(1);
					if(validateAp(ssidAp, passAp)) {
						drone3d.ap(commandPackage, ssidAp, passAp);
					} else {
						commandPackage.setResponse(errorString);
						returnResponseStringToUDPConncection(commandPackage);
					}
					break;

				case TelloReadCommands.SPEED:
					//TODO: Send current speed (10-100 cm/s)
					break;

				case TelloReadCommands.BATTERY:
					//TODO: Send current battery percentage (0-100)
					break;

				case TelloReadCommands.TIME:
					//TODO: Send current flight time
					break;

				case TelloReadCommands.WIFI:
					//TODO: Send Wi-Fi SNR
					break;

				case TelloReadCommands.SDK:
					//TODO: Send the Tello SDK version
					break;

				case TelloReadCommands.SN:
					//TODO: Send the Tello serial number
					break;

				default:
					logger.error("Invalid command:" + command + ". Command unknown - please check for typos.");  //TODO: we could check for similar commands with levenstein?
					throw new IllegalArgumentException("invalid command");
			}

        } else {
            commandPackage.setResponse("Error no joystick");
            returnResponseStringToUDPConncection(commandPackage);
        }
	}

	//validate methods
	// TODO: return String instead of throwing exceptions and log
	private boolean validateUp(int x) {
		if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommands.UP+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateDown(int x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommands.DOWN+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateLeft(int x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommands.LEFT+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateRight(int x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommands.RIGHT+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateForward(int x) {
        if(x<20 || x>500) {
        	logger.error("Illegal Argument. Command: "+TelloControlCommands.FORWARD+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
        	return true;
		}
	}

	private boolean validateBack(int x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommands.BACK+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateCw(int x) {
        if(x<1 || x>360) {
			logger.error("Illegal Argument. Command: "+TelloControlCommands.CW+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"1 - 360");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateCcw(int x) {
        if(x<1 || x>360) {
			logger.error("Illegal Argument. Command: "+TelloControlCommands.CCW+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"1 - 360");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateFlip(String x) {
		if(!(x.equals("l") || x.equals("r") || x.equals("f") || x.equals("b"))) {
			logger.error("Illegal Argument. Command: "+TelloControlCommands.FLIP+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid values: "+"l, r, f or b");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateGo(int x, int y, int z, int speed, String mid) {
        if(x<-500 || x>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.GO, "x", String.valueOf(x), "-500-500");
		}
        if(y<-500 || y>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.GO, "y", String.valueOf(y), "-500-500");
		}
        if(z<-500 || z>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.GO, "z", String.valueOf(z), "-500-500");
		}
		if(speed<10 || speed>100) {
			throw new TelloIllegalArgumentException(TelloControlCommands.GO, "speed", String.valueOf(speed), "-500-500");
		}
		if(x>=-20 && x<=20 && y>=-20 && y<=20 && z>=-20 && z<=20) {
            throw new TelloIllegalArgumentException(TelloControlCommands.GO, "x, y and z", "x: "+String.valueOf(x)+", y: "+String.valueOf(y)+", z: "+String.valueOf(z), "x, y and z values can't be set between -20-20 simultaneously");
		}
		if(!(mid.equals("m1") || mid.equals("m2") || mid.equals("m3") || mid.equals("m4") || mid.equals("m5") || mid.equals("m6") || mid.equals("m7") || mid.equals("m8") || mid==null || mid.equals(""))) {
			throw new TelloIllegalArgumentException(TelloControlCommands.GO, "mid", mid, "m1-m8 or empty");
		}
		//TODO: implement correct validateMethod
		return false;
	}

	private boolean validateCurve(int x1, int x2, int y1, int y2, int z1, int z2, int speed, String mid) {
        if(x1<-500 || x1>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "x1", String.valueOf(x1), "-500-500");
		}
        if(x2<-500 || x2>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "x2", String.valueOf(x2), "-500-500");
		}
        if(y1<-500 || y1>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "y1", String.valueOf(y1), "-500-500");
		}
        if(y2<-500 || y2>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "y2", String.valueOf(y2), "-500-500");
		}
        if(z1<-500 || z1>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "z1", String.valueOf(z1), "-500-500");
		}
        if(z2<-500 || z2>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "z2", String.valueOf(z2), "-500-500");
		}
		if(speed<10 || speed>100) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "speed", String.valueOf(speed), "-500-500");
		}
		if(x1>=-20 && x1<=20 && y1>=-20 && y1<=20 && z1>=-20 && z1<=20) {
            throw new TelloIllegalArgumentException(TelloControlCommands.GO, "x1, y1 and z1", "x1: "+String.valueOf(x1)+", y1: "+String.valueOf(y1)+", z1: "+String.valueOf(z1), "x1, y1 and z1 values can't be set between -20-20 simultaneously");
		}
		if(x2>=-20 && x2<=20 && y2>=-20 && y2<=20 && z2>=-20 && z2<=20) {
            throw new TelloIllegalArgumentException(TelloControlCommands.GO, "x2, y2 and z2", "x2: "+String.valueOf(x2)+", y2: "+String.valueOf(y2)+", z2: "+String.valueOf(z2), "x2, y2 and z2 values can't be set between -20-20 simultaneously");
		}
		if(!(mid.equals("m1") || mid.equals("m2") || mid.equals("m3") || mid.equals("m4") || mid.equals("m5") || mid.equals("m6") || mid.equals("m7") || mid.equals("m8") || mid==null || mid.equals(""))) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "mid", mid, "m1-m8");
		}
		//todo: test that the arc radius is not within a range of 0.5-10 meters
		//TODO: implement correct validateMethod
		return false;
	}

	private boolean validateJump(int x, int y, int z, int speed, int yaw, String mid1, String mid2) {
        if(x<-500 || x>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.JUMP, "x", String.valueOf(x), "-500-500");
		}
        if(y<-500 || y>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.JUMP, "y", String.valueOf(y), "-500-500");
		}
        if(z<-500 || z>500) {
			throw new TelloIllegalArgumentException(TelloControlCommands.JUMP, "z", String.valueOf(z), "-500-500");
		}
        if(speed<10 || speed>100) {
			throw new TelloIllegalArgumentException(TelloControlCommands.JUMP, "speed", String.valueOf(speed), "10-100");
		}
		// yaw value ist not documented in the sdk todo: find out the valid value for yaw
		if(!(mid1.equals("m1") || mid1.equals("m2") || mid1.equals("m3") || mid1.equals("m4") || mid1.equals("m5") || mid1.equals("m6") || mid1.equals("m7") || mid1.equals("m8"))) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "mid1", mid1, "m1-m8");
		}
		if(!(mid2.equals("m1") || mid2.equals("m2") || mid2.equals("m3") || mid2.equals("m4") || mid2.equals("m5") || mid2.equals("m6") || mid2.equals("m7") || mid2.equals("m8"))) {
			throw new TelloIllegalArgumentException(TelloControlCommands.CURVE, "mid2", mid2, "m1-m8");
		}
		//TODO: implement correct validateMethod
		return false;
    }

	private boolean validateSpeed(int x) {
        if(x<10 || x>100) {
			throw new TelloIllegalArgumentException(TelloSetCommands.SPEED, "speed", String.valueOf(x), "10-100");
		}
		//TODO: implement correct validateMethod
		return false;
	}

	private boolean validateRc(int a, int b, int c, int d) {
		if(a<-100 && a>100) {
			throw new TelloIllegalArgumentException(TelloSetCommands.RC, "a", String.valueOf(a), "-100-100");
		}
        if(b<-100 && b>100) {
			throw new TelloIllegalArgumentException(TelloSetCommands.RC, "b", String.valueOf(b), "-100-100");
		}
        if(c<-100 && c>100) {
			throw new TelloIllegalArgumentException(TelloSetCommands.RC, "c", String.valueOf(c), "-100-100");
		}
        if(d<-100 && d>100) {
			throw new TelloIllegalArgumentException(TelloSetCommands.RC, "d", String.valueOf(d), "-100-100");
		}
		//TODO: implement correct validateMethod
		return false;
	}

	private boolean validateWifi(String ssidWifi, String passWifi) {
		// todo: find out the valid values for ssid and pass (min length?)
		//TODO: implement correct validateMethod
		return false;
	}

	private boolean validateMdirection(int x) {
		if(!(x == 0 || x == 1 || x == 2)) {
			throw new TelloIllegalArgumentException(TelloSetCommands.MDIRECTION, "x", String.valueOf(x), "0, 1 or 2");
		}
		//TODO: implement correct validateMethod
		return false;
	}

	private boolean validateAp(String ssidAp, String passAp) {
		// todo: find out the valid values for ssid and pass (min length?)
		//TODO: implement correct validateMethod
		return false;
	}

    public void returnResponseStringToUDPConncection(CommandPackage commandPackage) throws IOException {
        udpCommandConnection.returnResponseStringToOperator(commandPackage);
    }
}
