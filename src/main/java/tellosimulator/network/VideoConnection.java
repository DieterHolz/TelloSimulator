package tellosimulator.network;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;

import javax.imageio.ImageIO;

public class VideoConnection extends Thread {
	DatagramSocket videoSocket;

	private boolean running = false;
	private int bufferSize = 2048;
	private int port = TelloSDKValues.OP_STREAM_PORT;
	private String host = TelloSDKValues.getOperatorIpAddress();

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public VideoConnection() throws SocketException {

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
			address = InetAddress.getByName(TelloSDKValues.getOperatorIpAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		while (running) {
			try {
				BufferedImage         img  = ImageIO.read(new File("src/main/resources/oop.jpg"));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(img, "jpg", baos);
				baos.flush();

				byte[] completeImg = baos.toByteArray();

				byte[] buff = new byte[bufferSize];
				int    c    = 0;

				DatagramSocket socket    = new DatagramSocket();
				InetAddress    IPAddress = InetAddress.getByName(host);

				int packetsSend = 0;
				for (int i = 0; i < completeImg.length; i++) {
					buff[c] = completeImg[i];
					c++;
					if ((i + 1) % bufferSize == 0) {
						DatagramPacket packet = new DatagramPacket(buff, buff.length, IPAddress, port);
						videoSocket.send(packet);
						Thread.sleep(1);  //kurz warten (keine Ahnung warum das noetig ist)
						c = 0;
						packetsSend++;
					}
				}

				DatagramPacket packet = new DatagramPacket(buff, buff.length, address, TelloSDKValues.OP_STREAM_PORT);
				videoSocket.send(packet);
				packetsSend++;
				System.out.println("sent last mini-packet :" + packet.getLength());
				System.out.println("packetsSend = " + packetsSend);

			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		videoSocket.close();
	}

}
