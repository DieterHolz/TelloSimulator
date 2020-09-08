package tellosimulator.network;

import tellosimulator.TelloSimulator;
import tellosimulator.command.CommandHandler;
import tellosimulator.command.CommandPackage;
import tellosimulator.command.TelloControlCommand;
import tellosimulator.log.Logger;
import tellosimulator.view.Drone;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class CommandConnection extends Thread {
	// private static final Logger LOGGER = LogManager.getLogger(UDPCommandConnection.class);

	Logger logger = new Logger(TelloSimulator.MAIN_LOG, "UDPCommandConnection");

	DatagramSocket commandSocket;
	Drone telloDrone;

	private boolean running = false;
	private boolean sdkModeInitiated;
	private byte[] buffer = new byte[512];

	public CommandConnection(Drone telloDrone) throws SocketException {

		this.telloDrone = telloDrone;

		try {
			commandSocket = new DatagramSocket(TelloSDKValues.SIM_COMMAND_PORT);
			CommandResponseSender.setSocket(commandSocket);
			InetAddress address = InetAddress.getByName(TelloSDKValues.getOperatorIpAddress());
			//TODO: uncomment to set timeout in final version
			//commandSocket.setSoTimeout(TelloSDKValues.COMMAND_SOCKET_TIMEOUT);
		} catch (IOException ex) {
			//TODO: throw custom exception instead
			logger.error("Command server error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {
		sdkModeInitiated = false;
		CommandHandler commandHandler = new CommandHandler(telloDrone, this);
		telloDrone.setCommandHandler(commandHandler);

		while (running) {

			try {
				DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
				logger.debug("Waiting for commands on port " + TelloSDKValues.SIM_COMMAND_PORT);
				commandSocket.receive(receivedPacket);
				if (running){ // check needed if drone is turned off during receiving
					String received = new String(receivedPacket.getData(), receivedPacket.getOffset(), receivedPacket.getLength());				// old: String received = readString();
					InetAddress address = receivedPacket.getAddress();
					int port = receivedPacket.getPort();
					logger.info("Received command: '" + received + "' from " + address.getCanonicalHostName() + ":" + port);

					CommandPackage commandPackage = new CommandPackage(received, address, port);
					if (!sdkModeInitiated && received.equals(TelloControlCommand.COMMAND)) {
						initiateStateConnection(address);
						sdkModeInitiated = true;
						CommandResponseSender.sendOk(commandPackage);
						continue;
					}

					if (sdkModeInitiated) {
						commandHandler.handle(commandPackage);
						continue;
					}
				}


			} catch (SocketTimeoutException ex) {
				logger.error("Timeout error: " + ex.getMessage());
				logger.warn("Safety feature triggerd: Tello will land automatically");
				// TODO: land the drone
				running = false;
				ex.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		commandSocket.close(); //todo: should maybe in a "finally" section
	}

	private void initiateStateConnection(InetAddress address) {
	}

	private byte[] readBytes() throws IOException {
		byte[] data = new byte[256];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		commandSocket.receive(packet);
		return Arrays.copyOf(data, packet.getLength());
	}

	String readString() throws IOException {
		byte[] data = readBytes();
		String str = new String(data, "UTF-8");
		if (TelloSDKValues.DEBUG) System.out.println("[IN ] " + str.trim());
		return str;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}