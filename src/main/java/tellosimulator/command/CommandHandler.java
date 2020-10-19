package tellosimulator.command;

import javafx.geometry.Point3D;
import tellosimulator.TelloSimulator;
import tellosimulator.exception.TelloIllegalArgumentException;
import tellosimulator.log.Logger;
import tellosimulator.math.VectorHelper;
import tellosimulator.network.CommandResponseSender;
import tellosimulator.network.CommandConnection;
import tellosimulator.network.VideoConnection;
import tellosimulator.video.VideoPublisher;
import tellosimulator.view.Drone;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CommandHandler {
	private Logger logger = new Logger(TelloSimulator.MAIN_LOG, "CommandHandler");

	Drone drone;
	VideoPublisher publisher;
	List<String> commandParams;

	public List<String> getCommandParams() {
		return commandParams;
	}

    public CommandHandler(Drone drone, CommandConnection commandConnection) {
        this.drone = drone;
    }

    public void handle(CommandPackage commandPackage) throws IOException {
        String received = commandPackage.getCommand();
		List<String> data = Arrays.asList(received.split(" "));
		String command = data.get(0);
		VideoConnection videoConnection = new VideoConnection();

		if (data.size() > 1) {
			commandParams = data.subList(1, data.size());
		}

		logger.info("handling command: " + command);

		if(!drone.isAnimationRunning() || command == TelloControlCommand.EMERGENCY || command == TelloSetCommand.RC) {
			switch (command) {
				case TelloControlCommand.COMMAND:
					break;

				case TelloControlCommand.TAKEOFF:
					if (checkParams(commandParams, 0)){
						drone.takeoff(commandPackage);
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
					}
					break;

				case TelloControlCommand.LAND:
					if (checkParams(commandParams, 0)){
						drone.land(commandPackage);
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
					}
					break;

				case TelloControlCommand.STREAMON:
					publisher = new VideoPublisher();
					if (!publisher.isRunning()) {
						publisher.setRunning(true);
						publisher.start();
					}
					break;

				case TelloControlCommand.STREAMOFF:
					if (publisher.isRunning()) {
						publisher.setRunning(false);
					}
					break;

				case TelloControlCommand.EMERGENCY:
					drone.emergency();
					break;

				case TelloControlCommand.UP:
					double xUp;
					if (checkParams(commandParams, 1)) {
						xUp = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(checkRange(command, xUp, 20, 500)) {
						drone.up(commandPackage, xUp);
					} else {
						CommandResponseSender.sendOutOfRange(commandPackage);
					}
					break;

				case TelloControlCommand.DOWN:
					double xDown;
					if (checkParams(commandParams, 1)) {
						xDown = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(checkRange(command, xDown, 20, 500)) {
						drone.down(commandPackage, xDown);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.LEFT:
					double xLeft;
					if (checkParams(commandParams, 1)) {
						xLeft = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(checkRange(command, xLeft, 20, 500)) {
						drone.left(commandPackage, xLeft);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.RIGHT:
					double xRight;
					if (checkParams(commandParams, 1)) {
						xRight = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}
					if(checkRange(command, xRight, 20, 500)) {
						drone.right(commandPackage, xRight);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.FORWARD:
					double xForward;
					if (checkParams(commandParams, 1)) {
						xForward = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(checkRange(command, xForward, 20, 500)) {
						drone.forward(commandPackage, xForward);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.BACK:
					double xBack;
					if (checkParams(commandParams, 1)) {
						xBack = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(checkRange(command, xBack, 20, 500)) {
						drone.back(commandPackage, xBack);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.CW:
					double xCw;
					if (checkParams(commandParams, 1)) {
						xCw = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(checkRange(command, xCw, 1, 360)) {
						drone.cw(commandPackage, xCw);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.CCW:
					double xCcw;
					if (checkParams(commandParams, 1)) {
						xCcw = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}
					if(checkRange(command, xCcw, 1, 360)) {
						drone.ccw(commandPackage, xCcw);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.FLIP:
					String flipDirection;
					if (commandParams != null && commandParams.size() == 1){
						flipDirection = commandParams.get(0);
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(validateFlip(flipDirection)) {
						drone.flip(commandPackage, flipDirection);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.GO:
					double xGo, yGo, zGo, speedGo;
					if (commandParams != null && commandParams.size() == 4){
						xGo = Double.parseDouble(commandParams.get(0));
						yGo = Double.parseDouble(commandParams.get(1));
						zGo = Double.parseDouble(commandParams.get(2));
						speedGo = Double.parseDouble(commandParams.get(3));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}
					//TODO: String midGo = params.get(4);
					if(validateGo(xGo, yGo, zGo, speedGo)) {
						drone.go(commandPackage, xGo, yGo, zGo, speedGo);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.STOP:
					if (commandParams == null) {
						drone.stop(commandPackage);
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
					}
					break;

				case TelloControlCommand.CURVE:
					double x1Curve = Double.parseDouble(commandParams.get(0));
					double y1Curve = Double.parseDouble(commandParams.get(1));
					double z1Curve = Double.parseDouble(commandParams.get(2));
					double x2Curve = Double.parseDouble(commandParams.get(3));
					double y2Curve = Double.parseDouble(commandParams.get(4));
					double z2Curve = Double.parseDouble(commandParams.get(5));
					double speedCurve = Double.parseDouble(commandParams.get(6));
                    //TODO: implement missionPadId

					if(validateCurve(x1Curve, y1Curve, z1Curve, x2Curve, y2Curve, z2Curve, speedCurve)) {
						drone.curve(commandPackage, x1Curve, y1Curve, z1Curve, x2Curve, y2Curve, z2Curve, speedCurve);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.JUMP:
					int xJump = Integer.parseInt(commandParams.get(0));
					int yJump = Integer.parseInt(commandParams.get(1));
					int zJump = Integer.parseInt(commandParams.get(2));
					int speedJump = Integer.parseInt(commandParams.get(3));
					int yawJump = Integer.parseInt(commandParams.get(4));
					String mid1Jump = commandParams.get(5);
					String mid2Jump = commandParams.get(6);

					if(validateJump(xJump, yJump, zJump, speedJump, yawJump, mid1Jump, mid2Jump)) {
						drone.jump(commandPackage, xJump, yJump, zJump, speedJump, yawJump, mid1Jump, mid2Jump);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloSetCommand.SPEED:
					double xSpeed = Double.parseDouble(commandParams.get(0));
					if (checkRange(TelloSetCommand.SPEED, xSpeed, 10, 100)){
						drone.setSpeed(xSpeed);
						CommandResponseSender.sendOk(commandPackage);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloSetCommand.RC:
					int a = Integer.parseInt(commandParams.get(0));
					int b = Integer.parseInt(commandParams.get(1));
					int c = Integer.parseInt(commandParams.get(2));
					int d = Integer.parseInt(commandParams.get(3));

					if(validateRc(a, b, c, d)) {
						drone.rc(commandPackage, a, b, c, d);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloSetCommand.WIFI:
					String wifiSsid = commandParams.get(0);
					String wifiPass = commandParams.get(1);

					if(validateWifi(wifiSsid, wifiPass)) {
						drone.setWifiSsid(wifiSsid);
						drone.setWifiPass(wifiPass);
						CommandResponseSender.sendOk(commandPackage);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloSetCommand.MON:
					drone.setMissionPadDetection(true);
					CommandResponseSender.sendOk(commandPackage);
					logger.warn("Mission pad detection is not supported by the TelloSimulator");
					break;

				case TelloSetCommand.MOFF:
					drone.setMissionPadDetection(false);
					CommandResponseSender.sendOk(commandPackage);
					logger.warn("Mission pad detection is not supported by the TelloSimulator");
					break;

				case TelloSetCommand.MDIRECTION:
					int xMdirection = Integer.parseInt(commandParams.get(0));

					if(validateMdirection(xMdirection)) {
						drone.mdirection(commandPackage, xMdirection);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloSetCommand.AP:
					String ssidAp = commandParams.get(0);
					String passAp = commandParams.get(1);
					if(validateAp(ssidAp, passAp)) {
						drone.ap(commandPackage, ssidAp, passAp);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloReadCommand.SPEED:
					//TODO: Read current speed
					break;

				case TelloReadCommand.BATTERY:
					//TODO: Send current battery percentage (0-100)
					break;

				case TelloReadCommand.TIME:
					//TODO: Send current flight time
					break;

				case TelloReadCommand.WIFI:
					//TODO: Send Wi-Fi SNR
					break;

				case TelloReadCommand.SDK:
					//TODO: Send the Tello SDK version
					break;

				case TelloReadCommand.SN:
					//TODO: Send the Tello serial number
					break;

				default:
					CommandResponseSender.sendError(commandPackage);
					logger.error("Invalid command:" + command + ". Command unknown - please check for typos.");  //TODO: we could check for similar commands with levenstein?
			}

        } else {
			CommandResponseSender.sendErrorNotJoyStick(commandPackage);
        }
	}

	private boolean checkParams(List<String> commandParams, int expectedNumberOfParams) {
		if (commandParams != null && commandParams.size() == expectedNumberOfParams) {
			return true;
		} else {
			return false;
		}
	}

	//validate methods
	private boolean checkRange(String command, double value, double min, double max) {
		if (value >= min && value <= max) {
			return true;
		}
		logger.error("Parameter value out of range *** command: "+ command +" *** input value: "+ value +"*** allowed values: " + min + " - " + max);
		return false;
	}

	private boolean validateFlip(String x) {
		if(!(x.equals("l") || x.equals("r") || x.equals("f") || x.equals("b"))) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.FLIP+", param name: x, input value: "+String.valueOf(x)+", valid values: l, r, f or b");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateGo(double x, double y, double z, double speed) {
        if(x<-500 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.GO+", param name: x, input value: "+String.valueOf(x)+", valid value: -500 - 500");
			return false;
		} else if(y<-500 || y>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.GO+", param name: y, input value: "+String.valueOf(x)+", valid value: -500 - 500");
			return false;
		} else if(z<-500 || z>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.GO+", param name: z, input value: "+String.valueOf(x)+", valid value: -500 - 500");
			return false;
		} else if(speed<10 || speed>100) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.GO+", param name: speed, input value: "+String.valueOf(x)+", valid value: 10 - 100");
			return false;
		} else if(x>=-20 && x<=20 && y>=-20 && y<=20 && z>=-20 && z<=20) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.GO+", param name: x, y and z, input value: x: "+String.valueOf(x)+", y: "+String.valueOf(y)+", z: "+String.valueOf(z)+"x, y and z values can't be set between -20 - 20 simultaneously");
			return false;
//		}
//	    TODO: implement mission pad id in validate method
//		if(!(mid.equals("m1") || mid.equals("m2") || mid.equals("m3") || mid.equals("m4") || mid.equals("m5") || mid.equals("m6") || mid.equals("m7") || mid.equals("m8") || mid==null || mid.equals(""))) {
//			throw new TelloIllegalArgumentException(TelloControlCommands.GO, "mid", mid, "m1-m8 or empty");
		} else {
			return true;
		}
	}

	private boolean validateCurve(double x1, double x2, double y1, double y2, double z1, double z2, double speed) {

		double radiusOfcircumscribedCircle = VectorHelper.radiusOfcircumscribedCircle(new Point3D(0,0,0), new Point3D(x1,y1,z1), new Point3D(x2,y2,z2));

        if(x1<-500 || x1>500) {
			logger.error("Illegal Argument. Command: " + TelloControlCommand.CURVE + ", param name: x1, input value: " + x1 + ", valid value: -500 - 500");
			return false;
		} else if(y1<-500 || y1>500) {
			logger.error("Illegal Argument. Command: " + TelloControlCommand.CURVE + ", param name: y1, input value: " + x1 + ", valid value: -500 - 500");
			return false;
		} else if(z1<-500 || z1>500) {
            logger.error("Illegal Argument. Command: " + TelloControlCommand.CURVE + ", param name: z1, input value: " + x1 + ", valid value: -500 - 500");
            return false;
        } else if(x2<-500 || x2>500) {
            logger.error("Illegal Argument. Command: " + TelloControlCommand.CURVE + ", param name: x2, input value: " + x1 + ", valid value: -500 - 500");
            return false;
        } else if(y2<-500 || y2>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE + ", param name: y2, input value: " + x1 + ", valid value: -500 - 500");
			return false;
		} else if(z2<-500 || z2>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE + ", param name: z2, input value: " + x1 + ", valid value: -500 - 500");
			return false;
		} else if(speed<10 || speed>60) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE + ", param name: speed, input value: " + speed + ", valid value: 10 - 60");
			return false;
		} else if(x1>=-20 && x1<=20 && y1>=-20 && y1<=20 && z1>=-20 && z1<=20) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE + ", param name: x, y and z, input value: x: " + x1 + ", y: " + y1 + ", z: " + z1 + "x, y and z values can't be set between -20 - 20 simultaneously");
			return false;
		} else if(x2>=-20 && x2<=20 && y2>=-20 && y2<=20 && z2>=-20 && z2<=20) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE + ", param name: x, y and z, input value: x: " + x2 + ", y: " + y2 + ", z: " + z2 + "x, y and z values can't be set between -20 - 20 simultaneously");
			return false;
//		} else if(!(mid.equals("m1") || mid.equals("m2") || mid.equals("m3") || mid.equals("m4") || mid.equals("m5") || mid.equals("m6") || mid.equals("m7") || mid.equals("m8") || mid==null || mid.equals(""))) {
//			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "mid", mid, "m1-m8");
//			return false;
            //todo: implement missionPadId
		} else if(radiusOfcircumscribedCircle < 50 || radiusOfcircumscribedCircle > 1000) {
			logger.error("Illegal Arguments. Command: " + TelloControlCommand.CURVE + ", Command: " + TelloControlCommand.CURVE + ", "); // todo: which error message does the drone return
			logger.error("Arc radius is not within a range of 0.5-10 meters.");
			return false;
		} else {
			return  true;
		}
	}

	private boolean validateJump(int x, int y, int z, int speed, int yaw, String mid1, String mid2) {
        if(x<-500 || x>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.JUMP, "x", String.valueOf(x), "-500-500");
		}
        if(y<-500 || y>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.JUMP, "y", String.valueOf(y), "-500-500");
		}
        if(z<-500 || z>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.JUMP, "z", String.valueOf(z), "-500-500");
		}
        if(speed<10 || speed>100) {
			throw new TelloIllegalArgumentException(TelloControlCommand.JUMP, "speed", String.valueOf(speed), "10-100");
		}
		// yaw value ist not documented in the sdk todo: find out the valid value for yaw
		if(!(mid1.equals("m1") || mid1.equals("m2") || mid1.equals("m3") || mid1.equals("m4") || mid1.equals("m5") || mid1.equals("m6") || mid1.equals("m7") || mid1.equals("m8"))) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "mid1", mid1, "m1-m8");
		}
		if(!(mid2.equals("m1") || mid2.equals("m2") || mid2.equals("m3") || mid2.equals("m4") || mid2.equals("m5") || mid2.equals("m6") || mid2.equals("m7") || mid2.equals("m8"))) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "mid2", mid2, "m1-m8");
		}
		//TODO: implement correct validateMethod
		return true;
    }

	private boolean validateRc(int a, int b, int c, int d) {
		if(a<-100 && a>100) {
			throw new TelloIllegalArgumentException(TelloSetCommand.RC, "a", String.valueOf(a), "-100-100");
		}
        if(b<-100 && b>100) {
			throw new TelloIllegalArgumentException(TelloSetCommand.RC, "b", String.valueOf(b), "-100-100");
		}
        if(c<-100 && c>100) {
			throw new TelloIllegalArgumentException(TelloSetCommand.RC, "c", String.valueOf(c), "-100-100");
		}
        if(d<-100 && d>100) {
			throw new TelloIllegalArgumentException(TelloSetCommand.RC, "d", String.valueOf(d), "-100-100");
		}
		//TODO: implement correct validateMethod
		return true;
	}

	private boolean validateWifi(String ssidWifi, String passWifi) {
		// todo: find out the valid values for ssid and pass (min length?)
		//TODO: implement correct validateMethod
		return true;
	}

	private boolean validateMdirection(int x) {
		if(!(x == 0 || x == 1 || x == 2)) {
			throw new TelloIllegalArgumentException(TelloSetCommand.MDIRECTION, "x", String.valueOf(x), "0, 1 or 2");
		}
		//TODO: implement correct validateMethod
		return true;
	}

	private boolean validateAp(String ssidAp, String passAp) {
		// todo: find out the valid values for ssid and pass (min length?)
		//TODO: implement correct validateMethod
		return true;
	}
}
