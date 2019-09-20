import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceuteProtocol
{
    //TS101
    public static void executeProtocolTS101(String hexString, Socket clientSocket)
    {
        try
        {
            String server_created_date = "";
            String client_address = "";
            String hexStringReplace = hexString.replace("$", "#");
            String[] BENOSYS_BTSData_String = hexStringReplace.split("##");

            for (int k = 0; k < BENOSYS_BTSData_String.length; k++) {
                String tempString = BENOSYS_BTSData_String[k].replace("\n", "");

                if (tempString.length() > 0 && tempString != null) {
                    String bString = "$$" + tempString.replace("\n", "");
                    //System.out.println("TS_101 Received String : ["+k+"] : "+bString);
                    String[] BENOSYS_BTSData = bString.split(",");
                    if (tempString.contains("TS_101") && BENOSYS_BTSData.length >= 51) {
                        String serialNumber = BENOSYS_BTSData[1];
                        //Get Device ID from device_verfication_master where device id already exist and check if client address verified
                        String serialStatus = F.checkImeiExists(serialNumber);
                        if (serialStatus.equals("1")) {
                            T.storeCommandSockets(serialNumber,clientSocket);
                            //store sockets
                            T.storeSockets(clientSocket, serialNumber);
                            T.updateDeviceStatus(serialNumber, "1");
                            server_created_date = T.getSystemDateTime();
                            client_address = clientSocket.getRemoteSocketAddress().toString().replace("/", "");

                            //pass string for decoding
                            String[] dataArrayDecode = {Constants.IMEI + "#" + serialNumber, Constants.DEVICE_STRING + "#" + bString};
                            String decodeResponse = RequestClass.sendingPostRequest(Constants.DECODE_TS101_STRING, dataArrayDecode);

                            String parseStatus = Parsing.parseCommonApi(decodeResponse);
                            if (parseStatus.equals("1")) {
                                //store valid string in file
                                String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + bString + "\r\n Imei : " + serialNumber + "\r\n Serisl Number :" + serialNumber + "\r\n Client Ip : " + client_address + "\r\n TS_101 Response : " + decodeResponse;
                                LogMaster.storeValidInvalidString(storeContent, "TS_101");
                                System.out.println(serialNumber + " " + server_created_date + " TS 101 String decoded");
                                T.writeMessage(serialNumber,clientSocket, "ACK");
                            } else if (parseStatus.equals("0")) {
                                //store valid string in file
                                String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + bString + "\r\n Imei : " + serialNumber + "\r\n Serisl Number :" + serialNumber + "\r\n Client Ip : " + client_address + "\r\n TS_101 Response : " + decodeResponse;
                                LogMaster.storeValidInvalidString(storeContent, "TS_101");
                                System.out.println("Oops ! problem to decode string");
                                T.writeMessage(serialNumber,clientSocket, "ACK");
                            } else {
                                //store valid string in file
                                String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + bString + "\r\n Imei : " + serialNumber + "\r\n Serisl Number :" + serialNumber + "\r\n Client Ip : " + client_address + "\r\n TS_101 Response : " + decodeResponse;
                                LogMaster.storeValidInvalidString(storeContent, "TS_101");
                                System.out.println("Oops ! problem to decode string");
                                T.writeMessage(serialNumber,clientSocket, "ACK");
                            }
                        } else {
                            //store sockets
                            T.storeSockets(clientSocket, "NA");
                            //store invalid string in file
                            String storeContent = "\r\n Date : " + server_created_date + "\r\n Imei : NA\r\n String : " + bString + "\r\n Client Ip :" + client_address + "\r\n Protocol : TS 101";
                            LogMaster.storeValidInvalidString(storeContent, "invalid");
                            System.out.println("Invalid TS 101 IMEI number");
                            T.writeMessage(serialNumber,clientSocket, "ACK");
                        }
                    } else {
                        T.writeMessage("NA",clientSocket, "ACK");
                        F.addInvalidString(bString, clientSocket);
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogMaster.saveExceptionLogDetails("ExceuteProtocol.executeProtocolTS101() 78", "" + e);
        }
    }
    public static void executeProtocolAIS140(String hexString, Socket clientSocket)
    {
        try
        {
            String[] dataArrayComma = hexString.split("\\$");
            String server_created_date = "";
            String client_address = "";
            for (int i = 1; i < dataArrayComma.length; i++) {
                String stringData = "$" + dataArrayComma[i];

                //System.out.println("AIS 140 Received String : ["+i+"] : "+stringData);
                String[] dataArrayAis140 = stringData.split(",");
                String startBit = dataArrayAis140[0];
                String endBit = dataArrayAis140[dataArrayAis140.length - 1];

                if (startBit.equals("$") && endBit.equals("*")) {
                    String server_ack, string_type;
                    server_created_date = T.getSystemDateTime();
                    client_address = clientSocket.getRemoteSocketAddress().toString().replace("/", "");
                    String imei = null;
                    int packet_length = dataArrayAis140.length;
                    //login packet
                    if (dataArrayAis140[1].equals("01") && dataArrayAis140.length == 7) {
                        //Login packet

                        imei = dataArrayAis140[4];
                        String serialStatus = F.checkImeiExists(dataArrayAis140[4]);
                        //System.out.println("serialStatus : "+serialStatus);
                        //System.out.println("IMEI : "+dataArrayAis140[4]);
                        if (serialStatus.equals("1")) {
                            //write message to device
                            T.storeCommandSockets(imei,clientSocket);
                            T.writeMessage(imei,clientSocket, "$,1,*");
                            //store sockets
                            server_ack = "$" + dataArrayAis140[4] + "*";
                            T.storeSockets(clientSocket, dataArrayAis140[4]);
                            string_type = "Login Packet";
                            T.writeMessage(imei,clientSocket, server_ack);
                            //Send Device Status
                            T.updateDeviceStatus(imei, "1");
                            //store valid string in file
                            String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + stringData + "\r\n Imei : " + dataArrayAis140[4] + "\r\n String Type :" + string_type + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack;
                            LogMaster.storeValidInvalidString(storeContent, "AIS_140");
                            System.out.println(dataArrayAis140[4] + " " + server_created_date + " AIS140 " + string_type + " decoded");

                        } else {
                            //store sockets
                            T.storeSockets(clientSocket, "NA");
                            //Send Device Status
                            T.updateDeviceStatus(imei, "0");
                            //store invalid string in file
                            String storeContent = "\r\n Date : " + server_created_date + "\r\n Imei : NA\r\n String : " + stringData + "\r\n Client Ip :" + client_address + "\r\n Protocol : AIS 140";
                            LogMaster.storeValidInvalidString(storeContent, "invalid");
                            System.out.println("Invalid AIS_140 IMEI number");
                        }
                    } //health packet
                    else if (dataArrayAis140[1].equals("02") || dataArrayAis140[1].equals("03") || dataArrayAis140[1].equals("04")) {

                        String imei_packet;
                        //get imei from collection arraylist
                        String imeiStatus = T.returnImei(clientSocket);
                        if (imeiStatus.contains("#")) {
                            String[] imeiData = imeiStatus.split("#");
                            if (imeiData.length > 0) {
                                imei = imeiData[1];
                            }
                        }
                        if (!imei.equals("NA")) {
                            //Health/Heartbeat Packet
                            if (dataArrayAis140[1].equals("02") && packet_length == 15) {
                                T.storeSockets(clientSocket, imei);
                                string_type = "Health/Heartbeat Packet";
                                imei_packet = dataArrayAis140[4];
                                F.saveIncomingData(imei,clientSocket, imei_packet, string_type, "" + dataArrayAis140.length, stringData, server_created_date, client_address);

                            } //Normal Packet
                            else if (dataArrayAis140[1].equals("03") && packet_length == 55) {
                                T.storeSockets(clientSocket, imei);
                                string_type = "Normal Packet";
                                imei_packet = dataArrayAis140[7];
                                F.saveIncomingData(imei,clientSocket, imei_packet, string_type, "" + packet_length, stringData, server_created_date, client_address);
                            } //Alarm Packet
                            else if (dataArrayAis140[1].equals("04") && packet_length == 55) {
                                T.storeSockets(clientSocket, imei);
                                string_type = "Alarm Packet";
                                imei_packet = dataArrayAis140[7];
                                F.saveIncomingData(imei,clientSocket, imei_packet, string_type, "" + packet_length, stringData, server_created_date, client_address);
                            } else {
                                F.addInvalidString(stringData, clientSocket);
                            }

                        } else {
                            System.out.println("socket close from server");
                            //remove socket data when socket close
                            String key = clientSocket.getRemoteSocketAddress().toString().replace("/", "");
                            T.CLIENT_SOCKETS.remove(key);
                            LogMaster.saveOpenCloseDevie(clientSocket.getRemoteSocketAddress().toString().replace("/", ""), "close");
                            //save device count
                            LogMaster.clearFile();
                            LogMaster.deviceStat("" + T.CLIENT_SOCKETS.size());
                            clientSocket.close();
                            LogMaster.saveDeviceDetails(
                                    "Disconected",
                                    "NA",
                                    String.valueOf(clientSocket.getPort()),
                                    String.valueOf(clientSocket.getLocalPort()),
                                    String.valueOf(clientSocket.getRemoteSocketAddress().toString().replace("/", "")),
                                    "Packet Name: not found,Exception : bytes == -1");
                            break;
                        }
                    } else {
                        //other string AIS 140
                        F.addInvalidString(hexString, clientSocket);
                    }
                }
                //System.out.println("$"+dataArray[i]);
            }
        }
        catch (Exception e)
        {
            LogMaster.saveExceptionLogDetails("ExceuteProtocol.executeProtocolAIS140() 193", "" + e);
        }
    }

    public static void executeProtocolGT06(String hexString, Socket clientSocket)
    {

        try
        {
            String server_created_date = "";
            String client_address = "";
            String imei5, imei6, imei7, imei8, imei9, imei10, imei11;

            String device_string = T.convertAssciiToHex(hexString).substring(1);
            //System.out.println("GT06 Received String : "+device_string);
            if (device_string.contains(" ")) {
                String[] dataArrayGT06 = device_string.split(" ");

                String firstBit = dataArrayGT06[0];
                String secondBit = dataArrayGT06[1];

                if (firstBit.equals("78") && secondBit.equals("78")) {
                    //device_string = hexString;
                    String[] device_string_data = device_string.split(" ");
                    if (device_string_data[0].equals("78") && device_string_data[1].equals("78")) {
                        server_created_date = T.getSystemDateTime();
                        client_address = clientSocket.getRemoteSocketAddress().toString().replace("/", "");

                        if (device_string != null || device_string.length() > 0) {
                            String[] splitarray = device_string.split(" ");
                            if (splitarray.length > 9) {
                                String packet_length = T.hex2decimal(splitarray[2]);
                                String protocol_number = splitarray[3];
                                // Login Packet
                                if (protocol_number.equals("1")) {
                                    //Login Packet
                                    if (splitarray[5].length() == 1) {
                                        imei5 = "0" + splitarray[5];
                                    } else {
                                        imei5 = splitarray[5];
                                    }
                                    if (splitarray[6].length() == 1) {
                                        imei6 = "0" + splitarray[6];
                                    } else {
                                        imei6 = splitarray[6];
                                    }
                                    if (splitarray[7].length() == 1) {
                                        imei7 = "0" + splitarray[7];
                                    } else {
                                        imei7 = splitarray[7];
                                    }
                                    if (splitarray[8].length() == 1) {
                                        imei8 = "0" + splitarray[8];
                                    } else {
                                        imei8 = splitarray[8];
                                    }
                                    if (splitarray[9].length() == 1) {
                                        imei9 = "0" + splitarray[9];
                                    } else {
                                        imei9 = splitarray[9];
                                    }
                                    if (splitarray[10].length() == 1) {
                                        imei10 = "0" + splitarray[10];
                                    } else {
                                        imei10 = splitarray[10];
                                    }
                                    if (splitarray[11].length() == 1) {
                                        imei11 = "0" + splitarray[11];
                                    } else {
                                        imei11 = splitarray[11];
                                    }

                                    String imei = splitarray[4] + "" + imei5 + "" + imei6 + "" + imei7 + "" + imei8 + "" + imei9 + "" + imei10 + "" + imei11;
                                    //System.out.println("GT06 IMEI : "+imei);
                                    String imeiStatus = F.checkImeiExists(imei);
                                    if (imeiStatus.equals("1")) {
                                        try {
                                            T.storeCommandSockets(imei,clientSocket);
                                            //store socket details
                                            T.storeSockets(clientSocket, imei);
                                            String[] carray = {splitarray[2], splitarray[3], splitarray[4], splitarray[5], splitarray[6], splitarray[7], splitarray[8], splitarray[9], splitarray[10], splitarray[11], splitarray[12], splitarray[13]};

                                            int[] aa = new int[carray.length];
                                            int lengthArray = carray.length;
                                            for (int i = 0; i < lengthArray; i++) {
                                                aa[i] = Integer.valueOf(T.stringToHex(carray[i]));
                                            }
                                            String crc_code = T.getCRC16(aa);
                                            String param12, param13, param_crc;
                                            if (splitarray[12].length() == 1) {
                                                param12 = "0" + splitarray[12];
                                            } else {
                                                param12 = splitarray[12];
                                            }
                                            if (splitarray[13].length() == 1) {
                                                param13 = "0" + splitarray[13];
                                            } else {
                                                param13 = splitarray[13];
                                            }
                                            if (crc_code.length() == 1) {
                                                param_crc = "000" + crc_code;
                                            } else if (crc_code.length() == 3) {
                                                param_crc = "0" + crc_code;
                                            } else {
                                                param_crc = crc_code;
                                            }
                                            String response = "78780501" + "" + param12 + "" + param13 + "" + param_crc + "" + "0d0a";
                                            String server_ack = T.hexToAscci(response);
                                            String string_type = "Login Packet";
                                            //Send Device Status
                                            T.updateDeviceStatus(imei, "1");
                                            //call decoding api here to decode final string
                                            String[] dataArrayDec = {Constants.STRING + "#" + device_string, Constants.IMEI + "#" + imei};
                                            String decodedResponse = RequestClass.sendingPostRequest(Constants.STRING_DECODING, dataArrayDec);
                                            String parseStatus = Parsing.parseCommonApi(decodedResponse);

                                            if (parseStatus.equals("1")) {
                                                if (protocol_number != "12") {
                                                    T.writeMessage(imei,clientSocket, server_ack);
                                                }
                                                //store valid string in file
                                                String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + device_string + "\r\n Imei : " + imei + "\r\n String Type :" + string_type + "\r\n Protocol Number : " + protocol_number + "\r\n Packet Length : " + packet_length + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack + "\r\n Decoding Response : " + decodedResponse;
                                                LogMaster.storeValidInvalidString(storeContent, "valid");
                                                System.out.println(imei + " " + server_created_date + " GT06 " + string_type + " decoded");
                                            } else if (parseStatus.equals("0")) {
                                                //store valid string in file
                                                String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + device_string + "\r\n Imei : " + imei + "\r\n String Type :" + string_type + "\r\n Protocol Number : " + protocol_number + "\r\n Packet Length : " + packet_length + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack + "\r\n Decoding Response : " + decodedResponse;
                                                LogMaster.storeValidInvalidString(storeContent, "valid");
                                                System.out.println("Oops ! problem to decode string");
                                            } else {
                                                //store valid string in file
                                                String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + device_string + "\r\n Imei : " + imei + "\r\n String Type :" + string_type + "\r\n Protocol Number : " + protocol_number + "\r\n Packet Length : " + packet_length + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack + "\r\n Decoding Response : " + decodedResponse;
                                                LogMaster.storeValidInvalidString(storeContent, "valid");
                                                System.out.println("Oops ! problem to decode string");
                                            }
                                        } catch (Exception ex) {

                                        }
                                    } else {

                                        T.storeSockets(clientSocket, "NA");
                                        //Send Device Status
                                        T.updateDeviceStatus(imei, "0");
                                        //store invalid string in file
                                        String storeContent = "\r\n Date : " + server_created_date + "\r\n Imei : " + imei + "\r\n String : " + device_string + "\r\n Client Ip :" + client_address;
                                        LogMaster.storeValidInvalidString(storeContent, "invalid");
                                        System.out.println("Invalid IMEI nbumber");
                                    }
                                } else if (protocol_number.equals("12") || protocol_number.equals("13") || protocol_number.equals("26") || protocol_number.equals("AD")) {
                                    String server_ack, imei = null, string_type;
                                    //get imei from collection arraylist
                                    String imeiStatus = T.returnImei(clientSocket);
                                    if (imeiStatus.contains("#")) {
                                        String[] imeiData = imeiStatus.split("#");
                                        if (imeiData.length > 0) {
                                            imei = imeiData[1];
                                        }
                                    }
                                    if (protocol_number.equals("13")) {
                                        String[] carray = {splitarray[2], splitarray[3], splitarray[4], splitarray[5], splitarray[6], splitarray[7], splitarray[8], splitarray[9], splitarray[10]};

                                        int[] aa = new int[carray.length];
                                        int arrayLength = carray.length;
                                        for (int i = 0; i < arrayLength; i++) {
                                            aa[i] = Integer.valueOf(T.stringToHex(carray[i]));
                                        }
                                        String crc_code = T.getCRC16(aa);

                                        String param9, param10, param_crc_hb;
                                        if (splitarray[9].length() == 1) {
                                            param9 = "0" + splitarray[9];
                                        } else {
                                            param9 = splitarray[9];
                                        }
                                        if (splitarray[10].length() == 1) {
                                            param10 = "0" + splitarray[10];
                                        } else {
                                            param10 = splitarray[10];
                                        }
                                        if (crc_code.length() == 1) {
                                            param_crc_hb = "000" + crc_code;
                                        } else if (crc_code.length() == 3) {
                                            param_crc_hb = "0" + crc_code;
                                        } else {
                                            param_crc_hb = crc_code;
                                        }
                                        String response = "78780513" + "" + param9 + "" + param10 + "" + param_crc_hb + "" + "0d0a";
                                        server_ack = T.hexToAscci(response);
                                    } else {
                                        server_ack = "ACK";
                                    }
                                    if (protocol_number.equals("12")) {
                                        string_type = "Location Packet";
                                    } else if (protocol_number.equals("13")) {
                                        string_type = "Heartbeat/Status Packet";
                                    } else if (protocol_number.equals("26")) {
                                        string_type = "Alarm Packet";
                                    } else if (protocol_number.equalsIgnoreCase("ad")) {
                                        string_type = "ADC Packet";
                                    } else {
                                        string_type = "";
                                    }
                                    if (!imei.equals("NA")) {
                                        //store socket details
                                        T.storeSockets(clientSocket, imei);
                                        //call decoding api here
                                        String[] dataArray = {Constants.STRING + "#" + device_string, Constants.IMEI + "#" + imei};
                                        String decodedResponse = RequestClass.sendingPostRequest(Constants.STRING_DECODING, dataArray);
                                        String parseStatus = Parsing.parseCommonApi(decodedResponse);
                                        if (parseStatus.equals("1")) {
                                            //store valid string in file
                                            String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + device_string + "\r\n Imei : " + imei + "\r\n String Type :" + string_type + "\r\n Protocol Number : " + protocol_number + "\r\n Packet Length : " + packet_length + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack + "\r\n Decoding Response : " + decodedResponse;
                                            LogMaster.storeValidInvalidString(storeContent, "valid");
                                            System.out.println(imei + " " + server_created_date + " GT06 " + string_type + " decoded");
                                        } else if (parseStatus.equals("0")) {
                                            //store valid string in file
                                            String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + device_string + "\r\n Imei : " + imei + "\r\n String Type :" + string_type + "\r\n Protocol Number : " + protocol_number + "\r\n Packet Length : " + packet_length + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack + "\r\n Decoding Response : " + decodedResponse;
                                            LogMaster.storeValidInvalidString(storeContent, "valid");
                                            System.out.println("Oops ! problem to decode string");
                                        } else {
                                            //store valid string in file
                                            String storeContent = "\r\n Date : " + server_created_date + "\r\n String : " + device_string + "\r\n Imei : " + imei + "\r\n String Type :" + string_type + "\r\n Protocol Number : " + protocol_number + "\r\n Packet Length : " + packet_length + "\r\n Client Ip : " + client_address + "\r\n Server Ack : " + server_ack + "\r\n Decoding Response : " + decodedResponse;
                                            LogMaster.storeValidInvalidString(storeContent, "valid");
                                            System.out.println("Oops ! problem to decode string");
                                        }
                                        if (protocol_number != "12") {
                                            T.writeMessage(imei,clientSocket, server_ack);
                                        }
                                    } else {
                                        System.out.println("socket close from server");
                                        //remove socket data when socket close
                                        String key = clientSocket.getRemoteSocketAddress().toString().replace("/", "");
                                        T.CLIENT_SOCKETS.remove(key);
                                        LogMaster.saveOpenCloseDevie(clientSocket.getRemoteSocketAddress().toString().replace("/", ""), "close");
                                        //save device count
                                        LogMaster.clearFile();
                                        LogMaster.deviceStat("" + T.CLIENT_SOCKETS.size());
                                        clientSocket.close();
                                        LogMaster.saveDeviceDetails(
                                                "Disconected",
                                                "NA",
                                                String.valueOf(clientSocket.getPort()),
                                                String.valueOf(clientSocket.getLocalPort()),
                                                String.valueOf(clientSocket.getRemoteSocketAddress().toString().replace("/", "")),
                                                "Packet Name: not found,Exception : bytes == -1");
                                        clientSocket.close();
                                    }
                                } else {
                                    System.out.println("Invalid String : " + device_string);
                                    F.addInvalidString(device_string, clientSocket);
                                }
                            } else {
                                System.out.println("Invalid String : " + device_string);
                                F.addInvalidString(device_string, clientSocket);
                            }
                        }
                    } else {
                        System.out.println("Invalid String : " + device_string);
                        F.addInvalidString(device_string, clientSocket);
                    }
                } else {
                    System.out.println("Invalid String : " + hexString);
                    F.addInvalidString(hexString, clientSocket);
                }
            } else {
                System.out.println("Invalid String : " + hexString);
                F.addInvalidString(hexString, clientSocket);
            }
        }
        catch (Exception e)
        {
            LogMaster.saveExceptionLogDetails("ExceuteProtocol.executeProtocolGT06() 467", "" + e);
        }
    }
}
