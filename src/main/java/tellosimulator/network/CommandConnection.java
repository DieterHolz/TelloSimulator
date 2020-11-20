package tellosimulator.network;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import tellosimulator.TelloSimulator;
import tellosimulator.command.CommandHandler;
import tellosimulator.command.CommandPackage;
import tellosimulator.command.TelloControlCommand;
import tellosimulator.log.Logger;
import tellosimulator.controller.DroneController;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CommandConnection extends Thread {
	private final Logger logger = new Logger(TelloSimulator.MAIN_LOG, "UDPCommandConnection");

	private DatagramSocket commandSocket;
	private DroneController telloDroneController;
	private StateConnection stateConnection;

	private BooleanProperty running = new SimpleBooleanProperty(false);
	private boolean sdkModeInitiated;
	private byte[] buffer = new byte[512];

	public CommandConnection(DroneController telloDroneController) {
		this.setDaemon(true);
		this.telloDroneController = telloDroneController;
		setupValueChangedListener();

		try {
			commandSocket = new DatagramSocket(TelloSDKValues.SIM_COMMAND_PORT);
			CommandResponseSender.setSocket(commandSocket);
			InetAddress address = InetAddress.getByName(TelloSDKValues.getOperatorIpAddress());
			//TODO: uncomment to set timeout in final version
			//commandSocket.setSoTimeout(TelloSDKValues.COMMAND_SOCKET_TIMEOUT);
		} catch (IOException e2) {
			logger.error("Command server error: " + e2);
		}
	}

	private void setupValueChangedListener() {
		runningProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == false) {
				if (stateConnection != null) {
					stateConnection.setRunning(false);
				}
				commandSocket.close();
			}
		});
	}

	public void run() {
		sdkModeInitiated = false;
		CommandHandler commandHandler = new CommandHandler(telloDroneController, this);
		telloDroneController.setCommandHandler(commandHandler);

		while (isRunning()) {

			try {
				DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
				logger.debug("Waiting for commands on port " + TelloSDKValues.SIM_COMMAND_PORT);
				commandSocket.receive(receivedPacket);
				if (isRunning()){ // check needed if drone is turned off during receiving
					String received = new String(receivedPacket.getData(), receivedPacket.getOffset(), receivedPacket.getLength());				// old: String received = readString();
					InetAddress address = receivedPacket.getAddress();
					int port = receivedPacket.getPort();
					logger.info("Received command: '" + received + "' from " + address.getCanonicalHostName() + ":" + port);

					CommandPackage commandPackage = new CommandPackage(received, address, port);
					if (!sdkModeInitiated && received.equals(TelloControlCommand.COMMAND)) {
						initiateStateConnection(address);
						sdkModeInitiated = true;
						CommandResponseSender.sendOk(commandPackage);
						continue;
					}
					if (sdkModeInitiated) {
						commandHandler.handle(commandPackage);
						continue;
					}
				}

			} catch (SocketTimeoutException e1) {
				logger.error("Socket timeout: " + e1.getMessage());
				logger.warn("Safety feature triggered: Tello will land automatically");
				// TODO: land the drone
				telloDroneController.land(null);
				setRunning(false);
			} catch (SocketException e2) {
				if (e2.getMessage().equals("socket closed")) {
					logger.info("Socket closed");
				} else {
					logger.warn(e2.getMessage());
				}
				//TODO: land the drone
				setRunning(false);
			} catch (IOException e3) {
				logger.error(e3.getMessage());
				e3.printStackTrace();
			}
		}
	}

	private void initiateStateConnection(InetAddress address) throws SocketException {
		stateConnection = new StateConnection(telloDroneController, address);
		stateConnection.start();
	}

	private byte[] readBytes() throws IOException {
		byte[] data = new byte[256];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		commandSocket.receive(packet);
		return Arrays.copyOf(data, packet.getLength());
	}

	String readString() throws IOException {
		byte[] data = readBytes();
		String str = new String(data, StandardCharsets.UTF_8);
		if (TelloSDKValues.DEBUG) System.out.println("[IN ] " + str.trim());
		return str;
	}

	public boolean isRunning() {
		return running.get();
	}

	public BooleanProperty runningProperty() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running.set(running);
	}
}