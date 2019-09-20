import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class F {

    public static void addInvalidString(String device_string, Socket clientSocket) {
        try {
            //store socket details
            T.storeSockets(clientSocket, "NA");
            try {
                LogMaster.saveDeviceDetails(
                        "Conected",
                        "NA",
                        String.valueOf(clientSocket.getPort()),
                        String.valueOf(clientSocket.getLocalPort()),
                        String.valueOf(clientSocket.getRemoteSocketAddress().toString().replace("/", "")),
                        "Packet not found");
                //store invalid string in file
                String storeContent = "\r\n Date : " + T.getSystemDateTime() + "\r\n Imei : NA\r\n String : " + device_string + "\r\n Client Ip :" + clientSocket.getRemoteSocketAddress().toString().replace("/", "");
                LogMaster.storeValidInvalidString(storeContent, "invalid");
                System.out.println("Invalid String");
            } catch (IOException ex) {
                LogMaster.saveExceptionLogDetails("VtsRunnable 338", "" + ex);
            }

        } catch (Exception ex) {
            LogMaster.saveExceptionLogDetails("VtsRunnable 345", "" + ex);
        }
    }

    public static void saveIncomingData(String imei,Socket clientSocket, String imei_packet, String string_type, String packet_length, String hexString, String server_created_date, String client_address) {
        String server_ack;
        server_ack = "$" + imei_packet + "*";
        String[] dataArrayDecode = {Constants.IMEI + "#" + imei_packet, Constants.DEVICE_STRING + "#" + hexString};
        String decodeResponse = RequestClass.sendingPostRequest(Constants.DECODE_AIS140_STRING, dataArrayDecode);
        //System.out.println("AIS 140 decodeResponse : "+decodeResponse);

        String parseStatus = Parsing.parseCommonApi(decodeResponse);
        if (parseStatus.equals("1")) {
            T.writeMessage(imei,clientSocket, server_ack);
            //store valid string in file
            String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + hexString + "\r\n Imei : " + imei_packet + "\r\n String Type :" + string_type + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack + "\r\n Decoding Response : " + decodeResponse;
            LogMaster.storeValidInvalidString(storeContent, "AIS_140");
            System.out.println(imei_packet + " " + server_created_date + " AIS 140 " + string_type + " decoded");
        } else if (parseStatus.equals("0")) {
            //store valid string in file
            String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + hexString + "\r\n Imei : " + imei_packet + "\r\n String Type :" + string_type + "\r\n Packet Length : " + packet_length + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack + "\r\n Decoding Response : " + decodeResponse;
            LogMaster.storeValidInvalidString(storeContent, "AIS_140");
            System.out.println("Oops ! problem to decode string");
        } else {
            //store valid string in file
            String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + hexString + "\r\n Imei : " + imei_packet + "\r\n String Type :" + string_type + "\r\n Packet Length : " + packet_length + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack + "\r\n Decoding Response : " + decodeResponse;
            LogMaster.storeValidInvalidString(storeContent, "AIS_140");
            System.out.println("Oops ! problem to decode string");
        }
    }

    public static String checkImeiExists(String imei) {
        String imeiStatus = "2";

        try {

            File f = new File(Constants.VALIDATE_IMEI + imei + ".txt");

            if (f.exists() && !f.isDirectory()) {
                imeiStatus = "1";
            } else {
                imeiStatus = "0";
            }
        } catch (Exception e) {
            LogMaster.saveExceptionLogDetails("F.checkImeiExists 16", "" + e);
            System.err.println("Source : checkImeiExists : " + e);
        }
        return imeiStatus;
    }
}
