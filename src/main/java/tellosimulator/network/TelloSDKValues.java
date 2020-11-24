package tellosimulator.network;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TelloSDKValues {

    public static final StringProperty operatorIpAddress = new SimpleStringProperty("127.0.0.1");

    public static final String SIM_LOCAL_ADDRESS = "127.0.0.1";
    public static final String OP_IP_ADDRESS = "127.0.0.1";
    public static final int OP_COMMAND_PORT = 8889;
    public static final int SIM_COMMAND_PORT = 8879;
    public static final int OP_STATE_PORT = 8890;
    public static final int SIM_STATE_PORT = 8880;
    public static final int OP_STREAM_PORT = 11111;
    public static final int SIM_STREAM_PORT = 11101;

    public static final int COMMAND_SOCKET_TIMEOUT = 20000;
    public static final int STATE_SOCKET_TIMEOUT = 1000;
    public static final int VIDEO_SOCKET_TIMEOUT = 1000;

    public static final int STREAM_DEFAULT_PACKET_SIZE = 1460;
    public static final int VIDEO_WIDTH = 960;
    public static final int VIDEO_HEIGHT = 720;

    public static String getOperatorIpAddress() {
        return operatorIpAddress.get();
    }

    public static StringProperty operatorIpAddressProperty() {
        return operatorIpAddress;
    }

    public static void setOperatorIpAddress(String operatorIpAddress) {
        TelloSDKValues.operatorIpAddress.set(operatorIpAddress);
    }


}
