package tellosimulator.network;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPVideoConnection extends Thread {

	DatagramSocket videoSocket;
	int videoPort = TelloSDKValues.SIM_STREAM_PORT;

	private boolean running = false;

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	private byte[] buffer = new byte[2048];

	public UDPVideoConnection() throws SocketException {
	}

	public void connect() throws SocketException{
		try {
			videoSocket = new DatagramSocket(videoPort);
			videoSocket.setSoTimeout(TelloSDKValues.VIDEO_SOCKET_TIMEOUT);

		} catch (IOException ex) {
			//TODO: throw custom exception eg. "TelloStreamException" instead
			System.out.println("Video server error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

		while (running) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				BufferedImage img = ImageIO.read(new File("src/main/resources/oop.jpg"));

				ImageIO.write(img, "jpg", baos);

				baos.flush();
				byte[] buffer = baos.toByteArray();
				videoSocket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		videoSocket.close();
	}

}
