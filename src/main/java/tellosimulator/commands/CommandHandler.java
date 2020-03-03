package tellosimulator.commands;

import tellosimulator.network.UDPVideoServer;

import java.util.Arrays;
import java.util.List;

public class CommandHandler {

	public static String handle(String received) {
		try {
			List<String> data = Arrays.asList(received.split(" "));
			String command = data.get(0);

			UDPVideoServer videoServer = new UDPVideoServer();

			if (data.size() > 1) {
				List<String> params = data.subList(1, data.size());
			}

			// TODO: implement each command (switch statement?)

			switch(command) {

				case TelloControlCommands.COMMAND:
					System.out.println("command");
					break;

				case TelloControlCommands.TAKEOFF:
					System.out.println("takeoff");
					break;

				case TelloControlCommands.LAND:
					System.out.println("land");
					break;

				case TelloControlCommands.STREAMON:
					System.out.println("streamon");
					if (!videoServer.isRunning()) {
						videoServer.setRunning(true);
						videoServer.run();
					}
					break;

				case TelloControlCommands.STREAMOFF:
					System.out.println("streamoff");
					if (videoServer.isRunning()) {
						videoServer.setRunning(false);
					}
					break;

				case TelloControlCommands.EMERGENCY:
					System.out.println("emergency");
					break;

				case TelloControlCommands.UP:
					System.out.println("up");
					break;

				case TelloControlCommands.DOWN:
					System.out.println("down");
					break;

				case TelloControlCommands.LEFT:
					System.out.println("left");
					break;

				case TelloControlCommands.RIGHT:
					System.out.println("right");
					break;

				case TelloControlCommands.FORWARD:
					System.out.println("forward");
					break;

				case TelloControlCommands.BACK:
					System.out.println("back");
					break;

				case TelloControlCommands.CW:
					System.out.println("cw");
					break;

				case TelloControlCommands.CCW:
					System.out.println("ccw");
					break;

				case TelloControlCommands.FLIP:
					System.out.println("flip");
					break;

				case TelloControlCommands.GO:
					System.out.println("go");
					break;

				case TelloControlCommands.STOP:
					System.out.println("stop");
					break;

				case TelloControlCommands.CURVE:
					System.out.println("curve");
					break;

				case TelloControlCommands.JUMP:
					System.out.println("jump");
					break;

				case TelloSetCommands.SPEED:
					System.out.println("speed");
					break;

				case TelloSetCommands.RC:
					System.out.println("rc");
					break;

				case TelloSetCommands.WIFI:
					System.out.println("wifi");
					break;

				case TelloSetCommands.MON:
					System.out.println("mon");
					break;

				case TelloSetCommands.MOFF:
					System.out.println("moff");
					break;

				case TelloSetCommands.MDIRECTION:
					System.out.println("mdirection");
					break;

				case TelloSetCommands.AP:
					System.out.println("ap");
					break;

				case TelloReadCommands.SPEED:
					System.out.println("speed?");
					break;

				case TelloReadCommands.BATTERY:
					System.out.println("battery?");
					break;

				case TelloReadCommands.TIME:
					System.out.println("time?");
					break;

				case TelloReadCommands.WIFI:
					System.out.println("wifi?");
					break;

				case TelloReadCommands.SDK:
					System.out.println("sdk?");
					break;

				case TelloReadCommands.SN:
					System.out.println("sn?");

				default:
					System.out.println("invalid command");
					// error
			}

			return "ok";
		} catch (Exception e) {
			return "error";
		}
	}
}
