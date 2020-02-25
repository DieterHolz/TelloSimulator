package tellosimulator.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPVideoServer extends Thread {

	DatagramSocket videoSocket;
	int videoPort = 11111;

	private boolean running;

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	private byte[] buffer = new byte[512]; // TODO: how much buffer do we need?

	public UDPVideoServer() throws SocketException {

		try {
			videoSocket = new DatagramSocket(videoPort);

		} catch (IOException ex) {
			System.out.println("Video server error: " + ex.getMessage());
			ex.printStackTrace();
		}

	}

	public void run() {

		while (running) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			// TODO: put video data in packet

			try {
				videoSocket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		videoSocket.close();
	}

}
