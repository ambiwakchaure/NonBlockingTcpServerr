import java.net.Socket;
import java.nio.channels.SocketChannel;

public class SocketInfo {

    private SocketChannel clientSocket;
    private String csIpPort;
    private String dateTime;
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public SocketChannel getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(SocketChannel clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getCsIpPort() {
        return csIpPort;
    }

    public void setCsIpPort(String csIpPort) {
        this.csIpPort = csIpPort;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
