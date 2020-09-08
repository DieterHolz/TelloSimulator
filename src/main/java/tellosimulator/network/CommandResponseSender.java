package tellosimulator.network;

import tellosimulator.TelloSimulator;
import tellosimulator.command.CommandPackage;
import tellosimulator.log.Logger;
import tellosimulator.response.TelloResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public final class CommandResponseSender {
    static Logger logger = new Logger(TelloSimulator.MAIN_LOG, "CommandResponseSender");

    private static DatagramSocket socket;

    public static void sendOk(CommandPackage commandPackage) throws IOException {
        DatagramPacket responsePacket = new DatagramPacket(TelloResponse.OK.getBytes(), TelloResponse.OK.getBytes().length,	commandPackage.getOriginAddress(), commandPackage.getOriginPort());
        socket.send(responsePacket);
        logger.debug("Sent response: '" + TelloResponse.OK + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
    }

    public static void sendError(CommandPackage commandPackage) throws IOException {
        DatagramPacket responsePacket = new DatagramPacket(TelloResponse.ERROR.getBytes(), TelloResponse.OK.getBytes().length,	commandPackage.getOriginAddress(), commandPackage.getOriginPort());
        socket.send(responsePacket);
        logger.debug("Sent response: '" + TelloResponse.ERROR + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
    }

    public static void sendErrorNotJoyStick(CommandPackage commandPackage) throws IOException {
        DatagramPacket responsePacket = new DatagramPacket(TelloResponse.ERROR_NOT_JOYSTICK.getBytes(), TelloResponse.OK.getBytes().length,	commandPackage.getOriginAddress(), commandPackage.getOriginPort());
        socket.send(responsePacket);
        logger.debug("Sent response: '" + TelloResponse.ERROR_NOT_JOYSTICK + "' to " + commandPackage.getOriginAddress().getCanonicalHostName() + ":" + commandPackage.getOriginPort());
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public static void setSocket(DatagramSocket socket) {
        CommandResponseSender.socket = socket;
    }
}
