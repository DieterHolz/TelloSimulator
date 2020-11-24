package tellosimulator.network;

import tellosimulator.TelloSimulator;
import tellosimulator.command.CommandPackage;
import tellosimulator.log.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public final class CommandResponseSender {
    private final static Logger logger = new Logger(TelloSimulator.MAIN_LOG, "CommandResponseSender");

    private class TelloResponse {
        public static final String OK = "ok";
        public static final String ERROR = "error";
        public static final String ERROR_NOT_JOYSTICK = "error Not joystick";
        public static final String UNKNOWN_COMMAND = "unknown command: ";
        public static final String OUT_OF_RANGE = "out of range";
        public static final String ERROR_MOTOR_STOP = "error Motor stop";
        public static final String ERROR_RADIUS_TOO_LAGRE = "error Radius is too large!";
    }

    private static DatagramSocket socket;

    public static void sendOk(CommandPackage commandPackage) {
        sendResponse(commandPackage, TelloResponse.OK);
    }

    public static void sendError(CommandPackage commandPackage) {
        sendResponse(commandPackage, TelloResponse.ERROR);
    }

    public static void sendErrorNotJoyStick(CommandPackage commandPackage) {
        sendResponse(commandPackage, TelloResponse.ERROR_NOT_JOYSTICK);
    }

    public static void sendUnknownCommand(CommandPackage commandPackage) {
        sendResponse(commandPackage, TelloResponse.UNKNOWN_COMMAND + commandPackage.getCommand());
    }

    public static void sendOutOfRange(CommandPackage commandPackage) {
        sendResponse(commandPackage, TelloResponse.OUT_OF_RANGE);
    }

    public static void sendMotorStop(CommandPackage commandPackage) {
        sendResponse(commandPackage, TelloResponse.ERROR_MOTOR_STOP);
    }

    public static void sendReadResponse(CommandPackage commandPackage, String response) {
        sendResponse(commandPackage, response);
    }

    public static void sendRadiusError(CommandPackage commandPackage) {
        sendResponse(commandPackage, TelloResponse.ERROR_RADIUS_TOO_LAGRE);
    }

    private static void sendResponse(CommandPackage commandPackage, String response) {
        if (commandPackage == null) {
            return;
        }
        try {
            DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, commandPackage.getOriginAddress(), commandPackage.getOriginPort());
            socket.send(responsePacket);
            logger.debug("Sent response: '" + response + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
        } catch (SocketException e1) {
            logger.warn("Could not send response: " + e1.getMessage());
        } catch (IOException e2) {
            logger.error("Error: " + e2);
        }
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public static void setSocket(DatagramSocket socket) {
        CommandResponseSender.socket = socket;
    }
}
