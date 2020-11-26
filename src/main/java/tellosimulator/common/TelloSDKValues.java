package tellosimulator.common;

/**
 * This class holds constants defined in the Tello SDK 2.0 (marked TELLO_).
 * Completed with some constants used only by the simulator (marked SIM_).
 */
public class TelloSDKValues {
    public static final int TELLO_COMMAND_PORT = 8889;
    public static final int SIM_COMMAND_PORT = 8879;
    public static final int TELLO_STATE_PORT = 8890;
    public static final int SIM_STATE_PORT = 8880;
    public static final int TELLO_STREAM_PORT = 11111;
    public static final int SIM_STREAM_PORT = 11101;

    public static final int COMMAND_SOCKET_TIMEOUT = 20000;
    public static final int STATE_SOCKET_TIMEOUT = 1000;
    public static final int VIDEO_SOCKET_TIMEOUT = 1000;

    public static final int STREAM_DEFAULT_PACKET_SIZE = 1460;
    public static final int VIDEO_WIDTH = 960;
    public static final int VIDEO_HEIGHT = 720;
}
