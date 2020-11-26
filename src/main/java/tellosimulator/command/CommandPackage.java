package tellosimulator.command;

import java.net.InetAddress;

/**
 * Wrapper class for a command, containing its command String, as well as the originating IP-address and Port.
 */
public class CommandPackage {
    private String command;
    private InetAddress originAddress;
    private int originPort;

    public CommandPackage(String command, InetAddress originAddress, int originPort) {
        this.command = command;
        this.originAddress = originAddress;
        this.originPort = originPort;
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
}
