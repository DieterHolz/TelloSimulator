package tellosimulator.network;

import tellosimulator.commands.CommandHandler;
import tellosimulator.commands.TelloCommands;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPCommandServer extends Thread {
	DatagramSocket commandSocket;

	private boolean running;
	private boolean sdkModeInitiated;
	private int securityTimeout = 15000;
	private byte[] buffer = new byte[512]; // TODO: how much buffer do we need?

	public UDPCommandServer() throws SocketException {

		try {
			commandSocket = new DatagramSocket(TelloSDKValues.SIM_COMMAND_PORT);
			InetAddress address = InetAddress.getByName(TelloSDKValues.OP_IP_ADDRESS);
			//commandSocket.setSoTimeout(securityTimeout);
			commandSocket.connect(address, TelloSDKValues.OP_COMMAND_PORT);

			System.out.println(address+ "HELLO");

			// sets timeout in milliseconds, limiting the waiting time when receiving data.
			// If the timeout expires, a SocketTimeoutException is raised.



		} catch (IOException ex) {
			System.out.println("Command server error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {
		running = true;
		sdkModeInitiated = false;

		while (running) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			try {
				System.out.println("Waiting for commands...");
				String received = readString();
				System.out.println("Received command: " + received);

				InetAddress address = InetAddress.getByName(TelloSDKValues.OP_IP_ADDRESS);
				int port = TelloSDKValues.OP_COMMAND_PORT;

				if (!sdkModeInitiated && received.equals(TelloCommands.COMMAND)) {
					sdkModeInitiated = true;
					String ok = TelloCommands.OK;
					DatagramPacket responsePacket = new DatagramPacket(ok.getBytes(), ok.getBytes().length, address, port);
					commandSocket.send(responsePacket);
					continue;
				}

				if (sdkModeInitiated) {
					String response = CommandHandler.handle(received);
					DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length,	address, port);
					commandSocket.send(responsePacket);
					continue;
				}

			} catch (SocketTimeoutException ex) {
				System.out.println("Timeout error: " + ex.getMessage());
				System.out.println("Safety feature triggerd: Tello will land automatically");
				// TODO: land the drone
				ex.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		commandSocket.close();
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