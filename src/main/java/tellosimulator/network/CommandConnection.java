package tellosimulator.network;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import tellosimulator.TelloSimulator;
import tellosimulator.command.CommandHandler;
import tellosimulator.command.CommandPackage;
import tellosimulator.command.TelloControlCommand;
import tellosimulator.common.TelloSDKValues;
import tellosimulator.log.Logger;
import tellosimulator.controller.DroneController;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

/**
 * This Thread receives UDP-Packets, wraps them into a {@code CommandPackage}, and passes them on to the
 * {@code CommandHandler} for further processing. It also initiates the {@code StateConnection} Thread.
 *
 * @see CommandHandler
 * @see StateConnection
 */
public class CommandConnection extends Thread {
	private final Logger logger = new Logger(TelloSimulator.MAIN_LOG, "CommandConnection");

	private DatagramSocket commandSocket;
	private DroneController telloDroneController;
	private StateConnection stateConnection;
	private boolean sdkModeInitiated;
	private byte[] buffer = new byte[512];

	private final BooleanProperty running = new SimpleBooleanProperty(false);

	public CommandConnection(DroneController telloDroneController) {
		this.setDaemon(true);
		this.telloDroneController = telloDroneController;
		setupValueChangedListener();

		try {
			commandSocket = new DatagramSocket(TelloSDKValues.SIM_COMMAND_PORT);
			CommandResponseSender.setSocket(commandSocket);
			commandSocket.setSoTimeout(TelloSDKValues.COMMAND_SOCKET_TIMEOUT);
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
		CommandHandler commandHandler = new CommandHandler(telloDroneController);

		while (isRunning()) {

			try {
				DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
				logger.debug("Waiting for commands on port " + TelloSDKValues.SIM_COMMAND_PORT);
				commandSocket.receive(receivedPacket);
				if (isRunning()){ // check needed if drone is turned off during receiving
					String received = new String(receivedPacket.getData(), receivedPacket.getOffset(), receivedPacket.getLength());
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
				logger.warn("Did not receive a command for " +
						TimeUnit.MILLISECONDS.toSeconds(TelloSDKValues.COMMAND_SOCKET_TIMEOUT) +
						" seconds. Safety feature triggered: Tello will land automatically");
				telloDroneController.land(null);
				setRunning(false);
			} catch (SocketException e2) {
				if (e2.getMessage().equals("socket closed")) {
					logger.warn("socket closed");
				} else {
					logger.error(e2.getMessage());
				}
				telloDroneController.land(null);
				setRunning(false);
			} catch (IOException e3) {
				logger.error(e3.getMessage());
			}
		}
	}

	private void initiateStateConnection(InetAddress address) {
		stateConnection = new StateConnection(telloDroneController, address);
		stateConnection.start();
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