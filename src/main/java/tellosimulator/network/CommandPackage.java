package tellosimulator.network;

import java.net.InetAddress;

public class CommandPackage {
    private String command;
    private InetAddress address;
    private int port;
    private String response;

    public CommandPackage(String command, InetAddress address, int port) {
        this.command = command;
        this.address = address;
        this.port = port;
        this.response = response;
    }

    public CommandPackage(String command, InetAddress address, int port, String response) {
        this.command = command;
        this.address = address;
        this.port = port;
        this.response = response;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
