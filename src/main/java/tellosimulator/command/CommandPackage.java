package tellosimulator.command;

import java.net.InetAddress;

public class CommandPackage {
    private String command;
    private InetAddress originAddress;
    private int originPort;
    private String response;

    public CommandPackage(String command, InetAddress originAddress, int originPort) {
        this.command = command;
        this.originAddress = originAddress;
        this.originPort = originPort;
    }

    public CommandPackage(String command, InetAddress originAddress, int originPort, String response) {
        this.command = command;
        this.originAddress = originAddress;
        this.originPort = originPort;
        this.response = response;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public InetAddress getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(InetAddress originAddress) {
        this.originAddress = originAddress;
    }

    public int getOriginPort() {
        return originPort;
    }

    public void setOriginPort(int originPort) {
        this.originPort = originPort;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
