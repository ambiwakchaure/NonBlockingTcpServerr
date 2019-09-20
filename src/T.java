import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class T {
    public static ConcurrentHashMap<String, SocketInfo> CLIENT_SOCKETS = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, SocketChannel> CLIENT_SOCKETS_CMD = new ConcurrentHashMap<>();

    public static void storeCommandSockets(String imei, SocketChannel clientSocket)
    {
        try
        {
            //check web imei exists
            if(CLIENT_SOCKETS_CMD.containsKey(imei))
            {
                CLIENT_SOCKETS_CMD.replace(imei, clientSocket);
            }
            else
            {
                CLIENT_SOCKETS_CMD.put(imei, clientSocket);
            }
        }
        catch(Exception e)
        {
            LogMaster.saveExceptionLogDetails("VtsRunnable 560", "" + e);
        }
    }
    public static void writeCommand(SocketChannel clienSocket, String device_id, String command)
    {
        try
        {

            if(CLIENT_SOCKETS_CMD.containsKey(device_id))
            {
                //write data to device
                T.writeMessage(device_id,CLIENT_SOCKETS_CMD.get(device_id), command);
            }
            else
            {
                T.writeMessage(device_id,clienSocket, "2");
                //when imei not found
                LogMaster.writeCommandLog(clienSocket,device_id, command, "2");
                clienSocket.close();
            }

        } catch (Exception e) {
            T.writeMessage(device_id,clienSocket, "Error to write");
            System.out.println("Error to write");
        }
    }
    public static void updateDeviceStatus(String imei, String status) {
        //pass string for decoding
        String[] dataArrayDecode = {Constants.IMEI + "#" + imei, Constants.DEVICE_STATUS + "#" + status};
        String decodeResponse = RequestClass.sendingPostRequest(Constants.UPDATE_DEVICE_STATUS, dataArrayDecode);
    }

    public static String returnImei(SocketChannel clientSocket) {
        String returnImei = "2";
        try {
            String key = clientSocket.getRemoteAddress().toString().replace("/", "");
            if (CLIENT_SOCKETS.containsKey(key)) {
                SocketInfo info = CLIENT_SOCKETS.get(key);
                returnImei = "1#" + info.getDeviceId();
            } else {
                returnImei = "0";
            }
        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("T.returnImei 34", "" + e);
            e.printStackTrace();
        }

        return returnImei;

    }

    public static void storeSockets(SocketChannel clientSocket, String imei) {

        try
        {
            String key = clientSocket.getRemoteAddress().toString().replace("/", "");
            if (CLIENT_SOCKETS.containsKey(key)) {

                SocketInfo info = CLIENT_SOCKETS.get(key);
                String[] data = T.getSystemDateTime().split(" ");
                info.setClientSocket(clientSocket);
                info.setCsIpPort(key);
                info.setDateTime(data[1]);
                info.setDeviceId(imei);

                CLIENT_SOCKETS.replace(key, info);
            } else {
                SocketInfo info = new SocketInfo();
                info.setClientSocket(clientSocket);
                info.setCsIpPort(clientSocket.getRemoteAddress().toString().replace("/", ""));
                info.setDeviceId(imei);
                String[] data = T.getSystemDateTime().split(" ");
                info.setDateTime(data[1]);
                CLIENT_SOCKETS.put(clientSocket.getRemoteAddress().toString().replace("/", ""), info);
            }
            LogMaster.saveDeviceDetails(
                    "Conected",
                    imei,
                    String.valueOf(clientSocket.getRemoteAddress().toString().replace("/", "")),
                    "Location Packet");
        }
        catch (IOException ex) {
            LogMaster.saveExceptionLogDetails("T.returnImei 73", "" + ex);
        }
    }

    public static boolean validateJsonResponse(String stringData, String where) {
        boolean status = false;
        try {
            if (stringData == null || stringData.equals(null)) {

                System.out.println(where + ": Warning : null json found");
                status = false;
            } else if (stringData == "") {

                System.out.println(where + ": Warning : empty json found");
                status = false;
            } else if (stringData != "" && stringData != null && stringData.length() > 0) {
                status = true;
            }

        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("T.validateJsonResponse 95", "" + e);

        }
        return status;

    }

    public static Long returnsTimeToSec(String timeFormat) {
        long secondsC = 0;
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date reference = dateFormat.parse("00:00:00");
            Date date = dateFormat.parse(timeFormat);

            secondsC = (date.getTime() - reference.getTime()) / 1000L;

        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("T.returnsTimeToSec 115", "" + e);
            e.printStackTrace();
        }
        return secondsC;
    }

    public static void saveProcessIds(
            String serviceName) {
        //File log = new File(Constants.PROCESS_IDS);

        String[] date = T.getSystemDateTime().split(" ");
        File log = new File(Constants.PROCESS_IDS + date[0].replace("-", "_") + ".txt");
        log.getParentFile().mkdirs();

        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try {
            String content = "Service Name: " + serviceName + ".jar\r\nProcess ID: " + getPid() + "\r\nDate Time: " + T.getSystemDateTime();
            fileWriter = new FileWriter(log, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content + "\r\n-----------------------------------------------------------------------------------------------------\r\n");
            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("T.saveProcessIds 139", "" + e);
            //LogMaster.saveExceptionLogDetails("saveProcessIds: ",String.valueOf(e));
        }
    }

    public static String getPid() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeBean.getName();
        long pid = Long.valueOf(jvmName.split("@")[0]);

        return String.valueOf(pid);
    }

    public static String stringToHex(String string) {
        StringBuilder buf = new StringBuilder(200);
        for (char ch : string.toCharArray()) {
            if (buf.length() > 0) {
                buf.append("");
            }
            buf.append(String.format("%04x", (int) ch));
        }
        return buf.toString();
    }

    public static String getCRC16(int[] p) {
        int[] a = {
                0X0000, 0X1189, 0X2312, 0X329B, 0X4624, 0X57AD, 0X6536, 0X74BF,
                0X8C48, 0X9DC1, 0XAF5A, 0XBED3, 0XCA6C, 0XDBE5, 0XE97E, 0XF8F7,
                0X1081, 0X0108, 0X3393, 0X221A, 0X56A5, 0X472C, 0X75B7, 0X643E,
                0X9CC9, 0X8D40, 0XBFDB, 0XAE52, 0XDAED, 0XCB64, 0XF9FF, 0XE876,
                0X2102, 0X308B, 0X0210, 0X1399, 0X6726, 0X76AF, 0X4434, 0X55BD,
                0XAD4A, 0XBCC3, 0X8E58, 0X9FD1, 0XEB6E, 0XFAE7, 0XC87C, 0XD9F5,
                0X3183, 0X200A, 0X1291, 0X0318, 0X77A7, 0X662E, 0X54B5, 0X453C,
                0XBDCB, 0XAC42, 0X9ED9, 0X8F50, 0XFBEF, 0XEA66, 0XD8FD, 0XC974,
                0X4204, 0X538D, 0X6116, 0X709F, 0X0420, 0X15A9, 0X2732, 0X36BB,
                0XCE4C, 0XDFC5, 0XED5E, 0XFCD7, 0X8868, 0X99E1, 0XAB7A, 0XBAF3,
                0X5285, 0X430C, 0X7197, 0X601E, 0X14A1, 0X0528, 0X37B3, 0X263A,
                0XDECD, 0XCF44, 0XFDDF, 0XEC56, 0X98E9, 0X8960, 0XBBFB, 0XAA72,
                0X6306, 0X728F, 0X4014, 0X519D, 0X2522, 0X34AB, 0X0630, 0X17B9,
                0XEF4E, 0XFEC7, 0XCC5C, 0XDDD5, 0XA96A, 0XB8E3, 0X8A78, 0X9BF1,
                0X7387, 0X620E, 0X5095, 0X411C, 0X35A3, 0X242A, 0X16B1, 0X0738,
                0XFFCF, 0XEE46, 0XDCDD, 0XCD54, 0XB9EB, 0XA862, 0X9AF9, 0X8B70,
                0X8408, 0X9581, 0XA71A, 0XB693, 0XC22C, 0XD3A5, 0XE13E, 0XF0B7,
                0X0840, 0X19C9, 0X2B52, 0X3ADB, 0X4E64, 0X5FED, 0X6D76, 0X7CFF,
                0X9489, 0X8500, 0XB79B, 0XA612, 0XD2AD, 0XC324, 0XF1BF, 0XE036,
                0X18C1, 0X0948, 0X3BD3, 0X2A5A, 0X5EE5, 0X4F6C, 0X7DF7, 0X6C7E,
                0XA50A, 0XB483, 0X8618, 0X9791, 0XE32E, 0XF2A7, 0XC03C, 0XD1B5,
                0X2942, 0X38CB, 0X0A50, 0X1BD9, 0X6F66, 0X7EEF, 0X4C74, 0X5DFD,
                0XB58B, 0XA402, 0X9699, 0X8710, 0XF3AF, 0XE226, 0XD0BD, 0XC134,
                0X39C3, 0X284A, 0X1AD1, 0X0B58, 0X7FE7, 0X6E6E, 0X5CF5, 0X4D7C,
                0XC60C, 0XD785, 0XE51E, 0XF497, 0X8028, 0X91A1, 0XA33A, 0XB2B3,
                0X4A44, 0X5BCD, 0X6956, 0X78DF, 0X0C60, 0X1DE9, 0X2F72, 0X3EFB,
                0XD68D, 0XC704, 0XF59F, 0XE416, 0X90A9, 0X8120, 0XB3BB, 0XA232,
                0X5AC5, 0X4B4C, 0X79D7, 0X685E, 0X1CE1, 0X0D68, 0X3FF3, 0X2E7A,
                0XE70E, 0XF687, 0XC41C, 0XD595, 0XA12A, 0XB0A3, 0X8238, 0X93B1,
                0X6B46, 0X7ACF, 0X4854, 0X59DD, 0X2D62, 0X3CEB, 0X0E70, 0X1FF9,
                0XF78F, 0XE606, 0XD49D, 0XC514, 0XB1AB, 0XA022, 0X92B9, 0X8330,
                0X7BC7, 0X6A4E, 0X58D5, 0X495C, 0X3DE3, 0X2C6A, 0X1EF1, 0X0F78

        };
        int d = 0xffff;
        int i, k, j;
        for (i = 0; i < p.length; i++) {
            j = (d ^ p[i]) & (0xff);
            d = (d >> 8) ^ a[j];
        }

        return Integer.toHexString(d ^ 0xffff);
    }

    public int getCheckSum(byte[] data) {
        int tmp;
        int res = 0;
        for (int i = 0; i < data.length; i++) {
            tmp = res << 1;
            tmp += 0xff & data[i];
            res = ((tmp & 0xff) + (tmp >> 8)) & 0xff;
        }
        return res;
    }

    public static String decToHex(int num) {
        // For storing remainder
        int rem;

        // For storing result
        String str2 = "";

        // Digits in hexadecimal number system
        char hex[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        while (num > 0) {
            rem = num % 16;
            str2 = hex[rem] + str2;
            num = num / 16;
        }

        return str2;
    }

    public static String hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return String.valueOf(val);
    }

    public static String getSystemDateTime() {

        String systemTime = null;

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            systemTime = df.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            LogMaster.saveExceptionLogDetails("T.getSystemDateTime 263", "" + e);
            e.printStackTrace();
        }
        return systemTime;

    }

    public static String getSystemDate() {

        String systemTime = null;

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            systemTime = df.format(Calendar.getInstance().getTime());
        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("T.getSystemDate 280", "" + e);
            e.printStackTrace();
        }
        return systemTime;

    }

    public static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(" " + Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public static String convertAssciiToHex(String ascciString) {

        // Step-1 - Convert ASCII string to char array
        char[] ch = ascciString.toCharArray();

        // Step-2 Iterate over char array and cast each element to Integer.
        StringBuilder builder = new StringBuilder();

        for (char c : ch) {
            int i = (int) c;
//          // Step-3 Convert integer value to hex using toHexString() method.
            builder.append(" " + Integer.toHexString(i).toUpperCase());

        }

        return builder.toString();
    }

    public static String hexToAscci(String hexString) {
        StringBuilder output = null;
        try {
            output = new StringBuilder();
            for (int i = 0; i < hexString.length(); i += 2) {
                String str = hexString.substring(i, i + 2);

                output.append((char) Integer.parseInt(str, 16));
            }
        } catch (Exception e) {
            LogMaster.saveExceptionLogDetails("T.hexToAscci 323 Input Hex String : " + hexString, "" + e);
        }

        return output.toString();
    }

    public static void writeMessage(String imei,SocketChannel clientSocket, String serverAck) {
        try
        {
            /*OutputStream os = clientSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(serverAck);
            bw.flush();*/
            ByteBuffer buf = ByteBuffer.allocate(48);
            buf.clear();
            buf.put(serverAck.getBytes());

            buf.flip();

            while(buf.hasRemaining()) {
                clientSocket.write(buf);
            }
        } catch (Exception e) {

        }

    }
}
