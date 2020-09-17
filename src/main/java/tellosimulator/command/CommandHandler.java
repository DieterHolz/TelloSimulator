package tellosimulator.command;

import tellosimulator.TelloSimulator;
import tellosimulator.exception.TelloIllegalArgumentException;
import tellosimulator.log.Logger;
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

					if(validateUp(xUp)) {
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

					if(validateDown(xDown)) {
						drone.down(commandPackage, xDown);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.LEFT:
					int xLeft = Integer.parseInt(commandParams.get(0));

					if(validateLeft(xLeft)) {
						drone.left(commandPackage, xLeft);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.RIGHT:
					int xRight = Integer.parseInt(commandParams.get(0));
					if(validateRight(xRight)) {
						drone.right(commandPackage, xRight);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.FORWARD:
					int xForward = Integer.parseInt(commandParams.get(0));

					if(validateForward(xForward)) {
						drone.forward(commandPackage, xForward);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.BACK:
					int xBack = Integer.parseInt(commandParams.get(0));

					if(validateBack(xBack)) {
						drone.back(commandPackage, xBack);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.CW:
					int xCw = Integer.parseInt(commandParams.get(0));

					if(validateCw(xCw)) {
						drone.cw(commandPackage, xCw);
					} else {
						CommandResponseSender.sendError(commandPackage);
					}
					break;

				case TelloControlCommand.CCW:
					int xCcw = Integer.parseInt(commandParams.get(0));

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
					int x2Curve = Integer.parseInt(commandParams.get(1));
					int y1Curve = Integer.parseInt(commandParams.get(2));
					int y2Curve = Integer.parseInt(commandParams.get(3));
					int z1Curve = Integer.parseInt(commandParams.get(4));
					int z2Curve = Integer.parseInt(commandParams.get(5));
					int speedCurve = Integer.parseInt(commandParams.get(6));
					String midCurve = commandParams.get(7);

					if(validateCurve(x1Curve, x2Curve, y1Curve, y2Curve, z1Curve, z2Curve, speedCurve, midCurve)) {
						drone.curve(commandPackage, x1Curve, x2Curve, y1Curve, y2Curve, z1Curve, z2Curve, speedCurve, midCurve);
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

	//validate methods
	// TODO: return String instead of throwing exceptions and log
	private boolean validateUp(double x) {
		if(x<20 || x>500) {
			logger.error("Parameter out of allowed range. Command: "+ TelloControlCommand.UP+", param name: "+"x"+", input value: "+ x +", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateDown(double x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+ TelloControlCommand.DOWN+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateLeft(int x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+ TelloControlCommand.LEFT+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateRight(int x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+ TelloControlCommand.RIGHT+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateForward(int x) {
        if(x<20 || x>500) {
        	logger.error("Illegal Argument. Command: "+ TelloControlCommand.FORWARD+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
        	return true;
		}
	}

	private boolean validateBack(int x) {
        if(x<20 || x>500) {
			logger.error("Illegal Argument. Command: "+ TelloControlCommand.BACK+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"20 - 500");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateCw(int x) {
        if(x<1 || x>360) {
			logger.error("Illegal Argument. Command: "+ TelloControlCommand.CW+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"1 - 360");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateCcw(int x) {
        if(x<1 || x>360) {
			logger.error("Illegal Argument. Command: "+ TelloControlCommand.CCW+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid value: "+"1 - 360");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateFlip(String x) {
		if(!(x.equals("l") || x.equals("r") || x.equals("f") || x.equals("b"))) {
			logger.error("Illegal Argument. Command: "+ TelloControlCommand.FLIP+", param name: "+"x"+", input value: "+String.valueOf(x)+", valid values: "+"l, r, f or b");
			return false;
		} else {
			return true;
		}
	}

	private boolean validateGo(int x, int y, int z, int speed) {
        if(x<-500 || x>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.GO, "x", String.valueOf(x), "-500-500");
		}
        if(y<-500 || y>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.GO, "y", String.valueOf(y), "-500-500");
		}
        if(z<-500 || z>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.GO, "z", String.valueOf(z), "-500-500");
		}
		if(speed<10 || speed>100) {
			throw new TelloIllegalArgumentException(TelloControlCommand.GO, "speed", String.valueOf(speed), "-500-500");
		}
		if(x>=-20 && x<=20 && y>=-20 && y<=20 && z>=-20 && z<=20) {
            throw new TelloIllegalArgumentException(TelloControlCommand.GO, "x, y and z", "x: "+String.valueOf(x)+", y: "+String.valueOf(y)+", z: "+String.valueOf(z), "x, y and z values can't be set between -20-20 simultaneously");
		}
//	    TODO: implement mission pad id in validate method
//		if(!(mid.equals("m1") || mid.equals("m2") || mid.equals("m3") || mid.equals("m4") || mid.equals("m5") || mid.equals("m6") || mid.equals("m7") || mid.equals("m8") || mid==null || mid.equals(""))) {
//			throw new TelloIllegalArgumentException(TelloControlCommands.GO, "mid", mid, "m1-m8 or empty");
//		}
		//TODO: implement correct validateMethod
		return true;
	}

	private boolean validateCurve(int x1, int x2, int y1, int y2, int z1, int z2, int speed, String mid) {
        if(x1<-500 || x1>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "x1", String.valueOf(x1), "-500-500");
		}
        if(x2<-500 || x2>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "x2", String.valueOf(x2), "-500-500");
		}
        if(y1<-500 || y1>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "y1", String.valueOf(y1), "-500-500");
		}
        if(y2<-500 || y2>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "y2", String.valueOf(y2), "-500-500");
		}
        if(z1<-500 || z1>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "z1", String.valueOf(z1), "-500-500");
		}
        if(z2<-500 || z2>500) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "z2", String.valueOf(z2), "-500-500");
		}
		if(speed<10 || speed>100) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "speed", String.valueOf(speed), "-500-500");
		}
		if(x1>=-20 && x1<=20 && y1>=-20 && y1<=20 && z1>=-20 && z1<=20) {
            throw new TelloIllegalArgumentException(TelloControlCommand.GO, "x1, y1 and z1", "x1: "+String.valueOf(x1)+", y1: "+String.valueOf(y1)+", z1: "+String.valueOf(z1), "x1, y1 and z1 values can't be set between -20-20 simultaneously");
		}
		if(x2>=-20 && x2<=20 && y2>=-20 && y2<=20 && z2>=-20 && z2<=20) {
            throw new TelloIllegalArgumentException(TelloControlCommand.GO, "x2, y2 and z2", "x2: "+String.valueOf(x2)+", y2: "+String.valueOf(y2)+", z2: "+String.valueOf(z2), "x2, y2 and z2 values can't be set between -20-20 simultaneously");
		}
		if(!(mid.equals("m1") || mid.equals("m2") || mid.equals("m3") || mid.equals("m4") || mid.equals("m5") || mid.equals("m6") || mid.equals("m7") || mid.equals("m8") || mid==null || mid.equals(""))) {
			throw new TelloIllegalArgumentException(TelloControlCommand.CURVE, "mid", mid, "m1-m8");
		}
		//todo: test that the arc radius is not within a range of 0.5-10 meters
		//TODO: implement correct validateMethod
		return true;
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
