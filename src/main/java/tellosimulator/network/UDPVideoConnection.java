package tellosimulator.network;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;

public class UDPVideoConnection extends Thread {
	DatagramSocket videoSocket;

	private boolean running = false;
	private byte[] buffer = new byte[2048];

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public UDPVideoConnection() throws SocketException {

		try {
			videoSocket = new DatagramSocket();
			videoSocket.setSoTimeout(TelloSDKValues.VIDEO_SOCKET_TIMEOUT);

		} catch (IOException ex) {
			//TODO: throw custom exception eg. "TelloStreamException" instead
			System.out.println("Video server error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

		InetAddress address = null;
		try {
			address = InetAddress.getByName(TelloSDKValues.OP_IP_ADDRESS);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		while (running) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				BufferedImage img = ImageIO.read(new File("src/main/resources/oop.jpg"));
				ImageIO.write(img, "jpg", baos);
				baos.flush();
				byte[] buffer = baos.toByteArray();

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, TelloSDKValues.OP_STREAM_PORT);
				videoSocket.send(packet);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		videoSocket.close();
	}

}
