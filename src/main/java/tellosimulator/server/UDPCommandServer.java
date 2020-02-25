package tellosimulator.server;

import tellosimulator.commands.CommandHandler;
import tellosimulator.commands.TelloCommands;

import java.io.IOException;
import java.net.*;

public class UDPCommandServer extends Thread {
	DatagramSocket commandSocket = new DatagramSocket(null);

	int commandPort = 8889;

	private boolean running;
	private boolean sdkModeInitiated;
	private int securityTimeout = 15000;
	private byte[] buffer = new byte[512]; // TODO: how much buffer do we need?

	public UDPCommandServer() throws SocketException {

		try {
			InetAddress address = InetAddress.getLocalHost();
			commandSocket.connect(address, commandPort);
			System.out.println(commandSocket.getRemoteSocketAddress().toString());

			// sets timeout in milliseconds, limiting the waiting time when receiving data.
			// If the timeout expires, a SocketTimeoutException is raised.

			//commandSocket.setSoTimeout(securityTimeout);

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
				commandSocket.receive(packet);
				InetAddress address = packet.getAddress();
				int port = packet.getPort();

				packet = new DatagramPacket(buffer, buffer.length, address, port);
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println("Received command: " + received);

				/*if (!sdkModeInitiated && received.equals(TelloCommands.COMMAND)) {
					sdkModeInitiated = true;
					String ok = TelloCommands.OK;
					DatagramPacket responsePacket = new DatagramPacket(ok.getBytes(), ok.getBytes().length, address,
							port);
					commandSocket.send(responsePacket);
					continue;
				}*/

				/*if (sdkModeInitiated) {
					// String response = CommandHandler.handle(received);
					String response = "ok";
					DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length,
							address, port);
					commandSocket.send(responsePacket);
					continue;
				}*/

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
}