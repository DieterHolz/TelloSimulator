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
import java.util.Vector;

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

		if(!drone.isAnimationRunning() || command == TelloControlCommand.EMERGENCY || command == TelloSetCommands.RC) {
			switch (command) {
				case TelloControlCommand.COMMAND:
					break;

				case TelloControlCommand.TAKEOFF:
					if (commandParams == null){
						drone.takeoff(commandPackage);
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
					}
					break;

				case TelloControlCommand.LAND:
					if (commandParams == null){
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
					if (commandParams != null && commandParams.size() == 1) {
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
					if (commandParams != null && commandParams.size() == 1) {
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
					if (commandParams != null && commandParams.size() == 1) {
						xLeft = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(validateLeft(xLeft)) {
						drone.left(commandPackage, xLeft);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.RIGHT:
					double xRight;
					if (commandParams != null && commandParams.size() == 1) {
						xRight = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}
					if(validateRight(xRight)) {
						drone.right(commandPackage, xRight);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.FORWARD:
					double xForward;
					if (commandParams != null && commandParams.size() == 1) {
						xForward = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(validateForward(xForward)) {
						drone.forward(commandPackage, xForward);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.BACK:
					double xBack;
					if (commandParams != null && commandParams.size() == 1) {
						xBack = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(validateBack(xBack)) {
						drone.back(commandPackage, xBack);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.CW:
					double xCw;
					if (commandParams != null && commandParams.size() == 1) {
						xCw = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}

					if(validateCw(xCw)) {
						drone.cw(commandPackage, xCw);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.CCW:
					double xCcw;
					if (commandParams != null && commandParams.size() == 1) {
						xCcw = Double.parseDouble(commandParams.get(0));
					} else {
						CommandResponseSender.sendUnknownCommand(commandPackage);
						break;
					}
					if(validateCcw(xCcw)) {
						drone.ccw(commandPackage, xCcw);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.FLIP:
					String xFlip = commandParams.get(0);

					if(validateFlip(xFlip)) {
						drone.flip(commandPackage, xFlip);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.GO:
					int xGo = Integer.parseInt(commandParams.get(0));
					int yGo = Integer.parseInt(commandParams.get(1));
					int zGo = Integer.parseInt(commandParams.get(2));
					int speedGo = Integer.parseInt(commandParams.get(3));
					//TODO: String midGo = params.get(4);

					if(validateGo(xGo, yGo, zGo, speedGo)) {
						drone.go(commandPackage, xGo, yGo, zGo, speedGo);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.STOP:
					drone.stop(commandPackage);
					break;

				case TelloControlCommand.CURVE:
					int x1Curve = Integer.parseInt(commandParams.get(0));
					int y1Curve = Integer.parseInt(commandParams.get(1));
					int z1Curve = Integer.parseInt(commandParams.get(2));
					int x2Curve = Integer.parseInt(commandParams.get(3));
					int y2Curve = Integer.parseInt(commandParams.get(4));
					int z2Curve = Integer.parseInt(commandParams.get(5));
					int speedCurve = Integer.parseInt(commandParams.get(6));
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

				case TelloSetCommands.SPEED:
					drone.speed(commandPackage);
					break;

				case TelloSetCommands.RC:
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

				case TelloSetCommands.WIFI:
					String ssidWifi = commandParams.get(0);
					String passWifi = commandParams.get(1);

					if(validateWifi(ssidWifi, passWifi)) {
						drone.wifi(commandPackage, ssidWifi, passWifi);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloSetCommands.MON:
					drone.mon(commandPackage);
					break;

				case TelloSetCommands.MOFF:
					drone.moff(commandPackage);
					break;

				case TelloSetCommands.MDIRECTION:
					int xMdirection = Integer.parseInt(commandParams.get(0));

					if(validateMdirection(xMdirection)) {
						drone.mdirection(commandPackage, xMdirection);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloSetCommands.AP:
					String ssidAp = commandParams.get(0);
					String passAp = commandParams.get(1);
					if(validateAp(ssidAp, passAp)) {
						drone.ap(commandPackage, ssidAp, passAp);
					} else {
						CommandResponseSender.sendError(commandPackage);
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
					CommandResponseSender.sendError(commandPackage);
					logger.error("Invalid command:" + command + ". Command unknown - please check for typos.");  //TODO: we could check for similar commands with levenstein?
			}

        } else {
			CommandResponseSender.sendErrorNotJoyStick(commandPackage);
        }
	}

	private boolean checkParams(List<String> commandParams, int expectedNumberOfParams) {
		if (commandParams != null && commandParams.size() == 1) {
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

	private boolean validateUp(double x) {
		if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.UP+", param name: x, input value: "+String.valueOf(x)+", valid value: 20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateDown(double x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.DOWN+", param name: x, input value: "+String.valueOf(x)+", valid value: 20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateLeft(double x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.LEFT+", param name: x, input value: "+String.valueOf(x)+", valid value: 20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateRight(double x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.RIGHT+", param name: x, input value: "+String.valueOf(x)+", valid value: 20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateForward(double x) {
        if(x<20 || x>500) {
        	logger.error("Illegal Argument. Command: "+TelloControlCommand.FORWARD+", param name: x, input value: "+String.valueOf(x)+", valid value: 20 - 500");
			return false;
		} else {
        	return true;
		}
	}

	private boolean validateBack(double x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.BACK+", param name: x, input value: "+String.valueOf(x)+", valid value: 20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateCw(double x) {
        if(x<1 || x>360) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CW+", param name: x, input value: "+String.valueOf(x)+", valid value: 1 - 360");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateCcw(double x) {
        if(x<1 || x>360) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CCW+", param name: x, input value: "+String.valueOf(x)+", valid value: 1 - 360");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateFlip(String x) {
		if(!(x.equals("l") || x.equals("r") || x.equals("f") || x.equals("b"))) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.FLIP+", param name: x, input value: "+String.valueOf(x)+", valid values: l, r, f or b");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateGo(int x, int y, int z, int speed) {
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

	private boolean validateCurve(int x1, int x2, int y1, int y2, int z1, int z2, int speed) {

		double radiusOfcircumscribedCircle = VectorHelper.radiusOfcircumscribedCircle(new Point3D(0,0,0), new Point3D(x1,y1,z1), new Point3D(x2,y2,z2));

        if(x1<-500 || x1>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE+", param name: x1, input value: "+String.valueOf(x1)+", valid value: -500 - 500");
			return false;
		} else if(y1<-500 || y1>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE+", param name: y1, input value: "+String.valueOf(x1)+", valid value: -500 - 500");
			return false;
		} else if(z1<-500 || z1>500) {
            logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE+", param name: z1, input value: "+String.valueOf(x1)+", valid value: -500 - 500");
            return false;
        } else if(x2<-500 || x2>500) {
            logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE+", param name: x2, input value: "+String.valueOf(x1)+", valid value: -500 - 500");
            return false;
        } else if(y2<-500 || y2>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE+", param name: y2, input value: "+String.valueOf(x1)+", valid value: -500 - 500");
			return false;
		} else if(z2<-500 || z2>500) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE+", param name: z2, input value: "+String.valueOf(x1)+", valid value: -500 - 500");
			return false;
		} else if(speed<10 || speed>60) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE+", param name: speed, input value: "+String.valueOf(speed)+", valid value: 10 - 60");
			return false;
		} else if(x1>=-20 && x1<=20 && y1>=-20 && y1<=20 && z1>=-20 && z1<=20) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE+", param name: x, y and z, input value: x: "+String.valueOf(x1)+", y: "+String.valueOf(y1)+", z: "+String.valueOf(z1)+"x, y and z values can't be set between -20 - 20 simultaneously");
			return false;
		} else if(x2>=-20 && x2<=20 && y2>=-20 && y2<=20 && z2>=-20 && z2<=20) {
			logger.error("Illegal Argument. Command: "+TelloControlCommand.CURVE+", param name: x, y and z, input value: x: "+String.valueOf(x2)+", y: "+String.valueOf(y2)+", z: "+String.valueOf(z2)+"x, y and z values can't be set between -20 - 20 simultaneously");
			return false;
//		} else if(!(mid.equals("m1") || mid.equals("m2") || mid.equals("m3") || mid.equals("m4") || mid.equals("m5") || mid.equals("m6") || mid.equals("m7") || mid.equals("m8") || mid==null || mid.equals(""))) {
//			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "mid", mid, "m1-m8");
//			return false;
            //todo: implement missionPadId
		} else if(radiusOfcircumscribedCircle < 50 || radiusOfcircumscribedCircle > 1000) {
			logger.error("Illegal Arguments. Command: "+TelloControlCommand.CURVE+", Command: \"+TelloControlCommand.CURVE+\", "); // todo: which error message does the drone return
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

	private boolean validateSpeed(int x) {
        if(x<10 || x>100) {
			throw new TelloIllegalArgumentException(TelloSetCommands.SPEED, "speed", String.valueOf(x), "10-100");
		}
		//TODO: implement correct validateMethod
		return true;
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
		return true;
	}

	private boolean validateWifi(String ssidWifi, String passWifi) {
		// todo: find out the valid values for ssid and pass (min length?)
		//TODO: implement correct validateMethod
		return true;
	}

	private boolean validateMdirection(int x) {
		if(!(x == 0 || x == 1 || x == 2)) {
			throw new TelloIllegalArgumentException(TelloSetCommands.MDIRECTION, "x", String.valueOf(x), "0, 1 or 2");
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
