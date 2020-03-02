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

			if (command.equals(TelloCommands.STREAMON)) {
				if (!videoServer.isRunning()) {
					videoServer.setRunning(true);
					videoServer.run();
				}
			}

			if (command.equals(TelloCommands.STREAMOFF)) {
				if (videoServer.isRunning()) {
					videoServer.setRunning(false);
				}
			}

			return "ok";
		} catch (Exception e) {
			return "error";
		}
	}
}
