package tellosimulator.network;

import tellosimulator.TelloSimulator;
import tellosimulator.commands.CommandHandler;
import tellosimulator.commands.TelloControlCommands;
import tellosimulator.log.Logger;
import tellosimulator.views.Drone3d;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPCommandConnection extends Thread {
	// private static final Logger LOGGER = LogManager.getLogger(UDPCommandConnection.class);

	Logger logger = new Logger(TelloSimulator.MAIN_LOG, "UDPCommandConnection");

	DatagramSocket commandSocket;
	Drone3d telloDrone;

	private boolean running = false;
	private boolean sdkModeInitiated;
	private byte[] buffer = new byte[512];

	public UDPCommandConnection(Drone3d telloDrone) throws SocketException {

		this.telloDrone = telloDrone;

		try {
			commandSocket = new DatagramSocket(TelloSDKValues.SIM_COMMAND_PORT);
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
		//running = true;
		sdkModeInitiated = false;
		CommandHandler commandHandler = new CommandHandler(telloDrone);

		while (running) {

			try {
				logger.info("Waiting for commands...");
				DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
				commandSocket.receive(receivedPacket);
				String received = new String(receivedPacket.getData(), receivedPacket.getOffset(), receivedPacket.getLength());				// String received = readString();
				logger.info("Received command: " + received);

				InetAddress address = receivedPacket.getAddress();
				int port = receivedPacket.getPort();

				if (!sdkModeInitiated && received.equals(TelloControlCommands.COMMAND)) {
					initiateStateConnection(address);
					sdkModeInitiated = true;
					String ok = TelloControlCommands.OK;
					DatagramPacket responsePacket = new DatagramPacket(ok.getBytes(), ok.getBytes().length, address, port);
					commandSocket.send(responsePacket);
					continue;
				}

				if (sdkModeInitiated) {
					String response = commandHandler.handle(received);
					logger.debug("Vom CommandHandler erhaltene Antwort: "+response);
					DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length,	address, port);
					commandSocket.send(responsePacket);
					continue;
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