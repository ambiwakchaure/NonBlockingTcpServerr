import java.io.*;
import java.net.Socket;

public class LogMaster {

    public static void writeCommandLog(
            Socket clienSocket,
            String imei,
            String command,
            String response) {
        String[] dateTime = T.getSystemDateTime().split(" ");
        File log = new File(Constants.WRITE_LOG + dateTime[0].replace("-", "_") + ".txt");
        log.getParentFile().mkdirs();
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;
        try {
            String content = "IMEI :" + imei + "\r\nClient IP :" + clienSocket.getRemoteSocketAddress().toString().replace("/", "") + "\r\nCommand :" + command + "\r\nResponse :" + response + "\r\nTimeStamp :" + T.getSystemDateTime();
            fileWriter = new FileWriter(log, true);

            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content + "\r\n----------------------------------------------------------------------------------------------------------------------------------------\r\n");
            bufferedWriter.close();

            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("LogMaster.writeCommandLog 34", "" + e);

        }

    }

    public static void storeValidInvalidString(String storeContent, String status) {
        // File log = new File(System.getProperty("dir")+Constants.DEVICE_DETAILS_LOG_MASTER_PATH);

        String date = T.getSystemDate();
        File log = null;

        if (status.equals("valid")) {
            log = new File(Constants.VALID_STRINGS + date.replace("-", "_") + ".txt");
        } else if (status.equals("invalid")) {
            log = new File(Constants.INVALID_STRINGS + date.replace("-", "_") + ".txt");
        } else if (status.equals("TS_101")) {
            log = new File(Constants.TS_101_STRINGS + date.replace("-", "_") + ".txt");
        } else if (status.equals("AIS_140")) {
            log = new File(Constants.AIS_140_STRINGS + date.replace("-", "_") + ".txt");
        }
        log.getParentFile().mkdirs();
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try {

            fileWriter = new FileWriter(log, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(storeContent + "\r\n-----------------------------------------------------------------------------------------------------\r\n");

            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            LogMaster.saveExceptionLogDetails("LogMaster.storeValidInvalidString 41", "" + e);
        }
    }

    public static void deviceStat(String deviceCount) throws IOException {
        // File log = new File(System.getProperty("dir")+Constants.DEVICE_DETAILS_LOG_MASTER_PATH);
        File log = new File(Constants.DEVICE_STAT);
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try {
            String content = "Device count : " + deviceCount;
            fileWriter = new FileWriter(log, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
            fileWriter.close();

            //System.out.println("Done");
        } catch (Exception e) {
            LogMaster.saveExceptionLogDetails("LogMaster.deviceStat 64", "" + e);
        }
    }

    public static void clearFile() {
        // File log = new File(System.getProperty("dir")+Constants.DEVICE_DETAILS_LOG_MASTER_PATH);

        File log = new File(Constants.DEVICE_STAT);
        try {
            PrintWriter writer = new PrintWriter(log);
            writer.print("");
            writer.close();

        } catch (Exception e) {
            LogMaster.saveExceptionLogDetails("LogMaster.clearFile 64", "" + e);
        }
    }

    public static void saveOpenCloseDevie(String ipPort, String status) {
        // File log = new File(System.getProperty("dir")+Constants.DEVICE_DETAILS_LOG_MASTER_PATH);

        String[] date = T.getSystemDateTime().split(" ");
        File log;

        if (status.equals("open")) {
            log = new File(Constants.OPEN_DEVICES_LOG + date[0].replace("-", "_") + ".txt");
        } else {
            log = new File(Constants.CLOSE_DEVICES_LOG + date[0].replace("-", "_") + ".txt");
        }
        log.getParentFile().mkdirs();
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try {
            String content = "\r\nIP :" + ipPort + "\tTimeStamp :" + T.getSystemDateTime();

            fileWriter = new FileWriter(log, true);

            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();

            //System.out.println("Done");
        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("LogMaster.clearFile 113", "" + e);

        }
    }

    public static void saveExceptionLogDetails(
            String exceptionLocation,
            String exceptionDetails) {
        String[] dateTime = T.getSystemDateTime().split(" ");
        File log = new File(Constants.EXCEPTION_LOG_MASTER + dateTime[0].replace("-", "_") + ".txt");
        log.getParentFile().mkdirs();
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;
        try {
            String content = "Location :" + exceptionLocation + "\r\nDate :" + dateTime[0] + "\r\nTime :" + dateTime[1] + "\r\nException :" + exceptionDetails;
            fileWriter = new FileWriter(log, true);

            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content + "\r\n----------------------------------------------------------------------------------------------------------------------------------------\r\n");
            bufferedWriter.close();

            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("LogMaster.saveExceptionLogDetails 138", "" + e);

        }

    }

    public static void saveDeviceDetails(
            String status,
            String deviceID,
            String clientPort,
            String clientLocalPort,
            String clientLocalAddress,
            String packetName) throws IOException {

        String[] date = T.getSystemDateTime().split(" ");
        File log = new File(Constants.All_DEVICE_LOG + date[0].replace("-", "_") + ".txt");
        log.getParentFile().mkdirs();

        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try {
            String content
                    = "Status :" + status
                    + "\r\nPacket Name :" + packetName
                    + "\r\nDevice ID :" + deviceID
                    + "\r\nDevice Port :" + clientPort
                    + "\r\nDevice Local Port :" + clientLocalPort
                    + "\r\nDevice Local Address :" + clientLocalAddress
                    + "\r\nDate Time :" + T.getSystemDateTime();

            fileWriter = new FileWriter(log, true);

            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content + "\r\n----------------------------------------------------------------------\r\n");
            bufferedWriter.close();
            fileWriter.close();

            //System.out.println("Done");
        } catch (Exception e) {
            System.out.print("three : " + e);
            LogMaster.saveExceptionLogDetails("LogMaster.saveExceptionLogDetails 178", "" + e);
        }
    }
}
