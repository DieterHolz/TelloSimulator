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
	private byte[] maxBuffer = new byte[1460];

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public UDPVideoConnection() {

		try {
			videoSocket = new DatagramSocket();
		} catch (IOException ex) {
			//TODO: throw custom exception eg. "TelloStreamException" instead
			System.out.println("Video server error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

		while(running) {
			try {
				BufferedImage img = ImageIO.read(new File("src/main/resources/testimg.jpg"));
				//System.out.println("bufferedimage: " + img);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(img, "jpg", baos);
				baos.flush();
				byte[] buffer = baos.toByteArray();

				InetAddress address = InetAddress.getByName(TelloSDKValues.OP_IP_ADDRESS);

				int count = 0;
				for(int i = 1;i < buffer.length + 1; i++) {
					maxBuffer[count] = buffer[i-1];
					count++;
					if(i%1460==0){
						DatagramPacket packet = new DatagramPacket(maxBuffer, maxBuffer.length, address, TelloSDKValues.OP_STREAM_PORT);
						maxBuffer = new byte[1460];
						count=0;
						videoSocket.send(packet);

						System.out.println("sent a mini-packet");
					}
				}

				DatagramPacket packet = new DatagramPacket(maxBuffer, count, address, TelloSDKValues.OP_STREAM_PORT);
				videoSocket.send(packet);
				System.out.println("sent LAST mini-packet");

				// DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, TelloSDKValues.OP_STREAM_PORT);
				// videoSocket.send(packet);
				// System.out.println("sending packet: " + packet.getData());

			} catch(IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		videoSocket.close(); //todo: should maybe in a "finally" section
	}

}
