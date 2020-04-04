package tellosimulator.network;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

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
//			videoSocket = new DatagramSocket(TelloSDKValues.SIM_STREAM_PORT);
//			videoSocket.setSoTimeout(TelloSDKValues.VIDEO_SOCKET_TIMEOUT);

//			videoSocket = new DatagramSocket((TelloSDKValues.SIM_STREAM_PORT));
//			InetAddress address = InetAddress.getByName(TelloSDKValues.OP_IP_ADDRESS);
//			//videoSocket.setSoTimeout(TelloSDKValues.VIDEO_SOCKET_TIMEOUT);
//			videoSocket.connect(address, TelloSDKValues.OP_STREAM_PORT);

			videoSocket = new DatagramSocket();

		} catch (IOException ex) {
			//TODO: throw custom exception eg. "TelloStreamException" instead
			System.out.println("Video server error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

//		while (running) {
//			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			try {
//				BufferedImage img = ImageIO.read(new File("src/main/resources/oop.jpg"));
//
//				ImageIO.write(img, "jpg", baos);
//
//				baos.flush();
//				byte[] buffer = baos.toByteArray();
//				videoSocket.send(packet);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}

		while(running) {
			try {
				BufferedImage img = ImageIO.read(new File("src/main/resources/testimg.jpg"));
				//System.out.println("bufferedimage: " + img);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(img, "jpg", baos);
				baos.flush();
				byte[] buffer = baos.toByteArray();

				InetAddress address = InetAddress.getByName(TelloSDKValues.OP_IP_ADDRESS);

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, TelloSDKValues.OP_STREAM_PORT);

				videoSocket.send(packet);

//				while(running) {
//					videoSocket.send(packet);
//					try {
//						TimeUnit.SECONDS.sleep(2);
//					} catch(InterruptedException e) {
//						e.printStackTrace();
//					}
//				}

			} catch(IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		videoSocket.close(); //todo: should maybe in a "finally" section
	}

}