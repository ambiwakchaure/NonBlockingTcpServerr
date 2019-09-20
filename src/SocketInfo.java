import java.net.Socket;

public class SocketInfo {

    private Socket clientSocket;
    private String csIpPort;
    private String dateTime;
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
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
