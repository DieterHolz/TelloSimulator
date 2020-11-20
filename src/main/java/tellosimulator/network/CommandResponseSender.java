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
    }

    private static DatagramSocket socket;

    public static void sendOk(CommandPackage commandPackage) {
        try {
            DatagramPacket responsePacket = new DatagramPacket(TelloResponse.OK.getBytes(), TelloResponse.OK.getBytes().length,	commandPackage.getOriginAddress(), commandPackage.getOriginPort());
            socket.send(responsePacket);
            logger.debug("Sent response: '" + TelloResponse.OK + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
        } catch (SocketException e1) {
            logger.warn("Could not send response: " + e1.getMessage());
        } catch (IOException e2) {
            logger.error("Error: " + e2);
        }
    }

    public static void sendError(CommandPackage commandPackage) {
        try {
            DatagramPacket responsePacket = new DatagramPacket(TelloResponse.ERROR.getBytes(), TelloResponse.OK.getBytes().length,	commandPackage.getOriginAddress(), commandPackage.getOriginPort());
            socket.send(responsePacket);
            logger.debug("Sent response: '" + TelloResponse.ERROR + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
        } catch (SocketException e1) {
            logger.warn("Could not send response: " + e1.getMessage());
        } catch (IOException e2) {
            logger.error("Error: " + e2);
        }
    }

    public static void sendErrorNotJoyStick(CommandPackage commandPackage) {
        try {
            DatagramPacket responsePacket = new DatagramPacket(TelloResponse.ERROR_NOT_JOYSTICK.getBytes(), TelloResponse.OK.getBytes().length,	commandPackage.getOriginAddress(), commandPackage.getOriginPort());
            socket.send(responsePacket);
            logger.debug("Sent response: '" + TelloResponse.ERROR_NOT_JOYSTICK + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
        } catch (SocketException e1) {
            logger.warn("Could not send response: " + e1.getMessage());
        } catch (IOException e2) {
            logger.error("Error: " + e2);
        }
    }

    public static void sendUnknownCommand(CommandPackage commandPackage) {
        try {
            String response = TelloResponse.UNKNOWN_COMMAND + commandPackage.getCommand();
            DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length,	commandPackage.getOriginAddress(), commandPackage.getOriginPort());
            socket.send(responsePacket);
            logger.debug("Sent response: '" + response + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
        } catch (SocketException e1) {
            logger.warn("Could not send response: " + e1.getMessage());
        } catch (IOException e2) {
            logger.error("Error: " + e2);
        }
    }

    public static void sendOutOfRange(CommandPackage commandPackage) {
        try {
            DatagramPacket responsePacket = new DatagramPacket(TelloResponse.OUT_OF_RANGE.getBytes(), TelloResponse.OUT_OF_RANGE.getBytes().length,	commandPackage.getOriginAddress(), commandPackage.getOriginPort());
            socket.send(responsePacket);
            logger.debug("Sent response: '" + TelloResponse.OUT_OF_RANGE + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
        } catch (SocketException e1) {
            logger.warn("Could not send response: " + e1.getMessage());
        } catch (IOException e2) {
            logger.error("Error: " + e2);
        }
    }

    public static void sendReadResponse(CommandPackage commandPackage, String response) {
        try {
            DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), TelloResponse.OUT_OF_RANGE.getBytes().length,	commandPackage.getOriginAddress(), commandPackage.getOriginPort());
            socket.send(responsePacket);
            logger.debug("Sent read response: '" + response + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
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
