package tellosimulator.commands;

import tellosimulator.drone.TelloDrone;
import tellosimulator.network.UDPVideoConnection;

import java.util.Arrays;
import java.util.List;

public class CommandHandler {

    TelloDrone telloDrone;

    public CommandHandler(TelloDrone telloDrone) {
        this.telloDrone = telloDrone;
    }

    public String handle(String received) {
		try {
			List<String> data = Arrays.asList(received.split(" "));
			String command = data.get(0);
            List<String> params = null;
			UDPVideoConnection videoConnection = new UDPVideoConnection();

            if (data.size() > 1) {
				params = data.subList(1, data.size());
			}

			switch(command) {

				case TelloControlCommands.COMMAND:
					System.out.println("command handling for command: command");
					break;

				case TelloControlCommands.TAKEOFF:
                    telloDrone.takeOff();
					break;

				case TelloControlCommands.LAND:
					telloDrone.land();
					break;

				case TelloControlCommands.STREAMON:
                    System.out.println("command handling for command: streamon");
					videoConnection.connect();
					if (!videoConnection.isRunning()) {
						videoConnection.setRunning(true);
						videoConnection.run();
					}
					break;

				case TelloControlCommands.STREAMOFF:
					System.out.println("command handling for command: streamoff");

					if (videoConnection.isRunning()) {
						videoConnection.setRunning(false);
					}
					break;

				case TelloControlCommands.EMERGENCY:
					telloDrone.emergency();
					break;

				case TelloControlCommands.UP:
				    telloDrone.up(Integer.parseInt(params.get(0)));
					break;

				case TelloControlCommands.DOWN:
					telloDrone.down(Integer.parseInt(params.get(0)));
					break;

				case TelloControlCommands.LEFT:
					telloDrone.left(Integer.parseInt(params.get(0)));
					break;

				case TelloControlCommands.RIGHT:
					telloDrone.right(Integer.parseInt(params.get(0)));
					break;

				case TelloControlCommands.FORWARD:
					telloDrone.forward(Integer.parseInt(params.get(0)));
					break;

				case TelloControlCommands.BACK:
                    telloDrone.back(Integer.parseInt(params.get(0)));
					break;

				case TelloControlCommands.CW:
					telloDrone.cw(Integer.parseInt(params.get(0)));
					break;

				case TelloControlCommands.CCW:
					telloDrone.ccw(Integer.parseInt(params.get(0)));
					break;

				case TelloControlCommands.FLIP:
					telloDrone.flip(params.get(0));
					break;

				case TelloControlCommands.GO:
				    telloDrone.go(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)), Integer.parseInt(params.get(3)), params.get(4));
					break;

				case TelloControlCommands.STOP:
                    telloDrone.stop();
					break;

				case TelloControlCommands.CURVE:
                    telloDrone.curve(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)), Integer.parseInt(params.get(3)), Integer.parseInt(params.get(4)),
                            Integer.parseInt(params.get(5)), Integer.parseInt(params.get(6)), params.get(7));
					break;

				case TelloControlCommands.JUMP:
                    telloDrone.jump(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)), Integer.parseInt(params.get(3)), Integer.parseInt(params.get(4)),
                            params.get(5), params.get(6));
					break;

				case TelloSetCommands.SPEED:
					telloDrone.speed(Integer.parseInt(params.get(0)));
					break;

				case TelloSetCommands.RC:
					telloDrone.rc(params.get(0), Integer.parseInt(params.get(1)), params.get(2), Integer.parseInt(params.get(3)), params.get(4), Integer.parseInt(params.get(5)), params.get(6), Integer.parseInt(params.get(7)));
					break;

				case TelloSetCommands.WIFI:
                    telloDrone.wifi(params.get(0), params.get(1));
					break;

				case TelloSetCommands.MON:
					telloDrone.mon();
					break;

				case TelloSetCommands.MOFF:
					telloDrone.moff();
					break;

				case TelloSetCommands.MDIRECTION:
					telloDrone.mdirection(Integer.parseInt(params.get(0)));
					break;

				case TelloSetCommands.AP:
                    telloDrone.ap(params.get(0), params.get(1));
					break;

				case TelloReadCommands.SPEED:
					telloDrone.readSpeed();
					break;

				case TelloReadCommands.BATTERY:
					telloDrone.readBattery();
					break;

				case TelloReadCommands.TIME:
					telloDrone.readTime();
					break;

				case TelloReadCommands.WIFI:
					telloDrone.readWifi();
					break;

				case TelloReadCommands.SDK:
					telloDrone.readSdk();
					break;

				case TelloReadCommands.SN:
					telloDrone.readSn();

				default:
					System.out.println("command handling for command: invalid command");
					// error
			}

			return "ok";
		} catch (Exception e) {
			return "error";
		}
	}
}
