package tellosimulator.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tellosimulator.commands.CommandHandler;
import tellosimulator.commands.TelloControlCommands;
import tellosimulator.views.Drone3d;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPCommandConnection extends Thread {
	private static final Logger LOGGER = LogManager.getLogger(UDPCommandConnection.class);

	DatagramSocket commandSocket;
	Drone3d telloDrone;

	private boolean running = false;
	private boolean sdkModeInitiated;
	private byte[] buffer = new byte[512]; // TODO: how much buffer do we need?

	public UDPCommandConnection(Drone3d telloDrone) throws SocketException {

		this.telloDrone = telloDrone;

		try {
			commandSocket = new DatagramSocket(TelloSDKValues.SIM_COMMAND_PORT);
			InetAddress address = InetAddress.getByName(TelloSDKValues.getOperatorIpAddress());
			//TODO: uncomment to set timeout in final version
			//commandSocket.setSoTimeout(TelloSDKValues.COMMAND_SOCKET_TIMEOUT);
			commandSocket.connect(address, TelloSDKValues.OP_COMMAND_PORT);
		} catch (IOException ex) {
			//TODO: throw custom exception instead
			LOGGER.error("Command server error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {
		running = true;
		sdkModeInitiated = false;
		CommandHandler commandHandler = new CommandHandler(telloDrone);

		while (running) {

			try {
				LOGGER.info("Waiting for commands...");
				String received = readString();
				LOGGER.info("Received command: " + received);

				InetAddress address = InetAddress.getByName(TelloSDKValues.getOperatorIpAddress());
				int port = TelloSDKValues.OP_COMMAND_PORT;

				if (!sdkModeInitiated && received.equals(TelloControlCommands.COMMAND)) {
					sdkModeInitiated = true;
					String ok = TelloControlCommands.OK;
					DatagramPacket responsePacket = new DatagramPacket(ok.getBytes(), ok.getBytes().length, address, port);
					commandSocket.send(responsePacket);
					continue;
				}

				if (sdkModeInitiated) {
					String response = commandHandler.handle(received);
					LOGGER.debug("vom CommandHandler erhaltene Antwort: "+response);
					DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length,	address, port);
					commandSocket.send(responsePacket);
					continue;
				}

			} catch (SocketTimeoutException ex) {
				LOGGER.error("Timeout error: " + ex.getMessage());
				LOGGER.warn("Safety feature triggerd: Tello will land automatically");
				// TODO: land the drone
				running = false;
				ex.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		commandSocket.close(); //todo: should maybe in a "finally" section
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
}