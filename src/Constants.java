public interface Constants
{
    String SERVER_IP = "192.168.0.102";
    Integer SERVER_PORT = 6556;

    String API_URL = "http://localhost/vts_decoding_restapi/api/TSVTSDecoding/";//separate ais decoding logic

    //GT06
    String STRING_DECODING = API_URL + "execDecodeRawString";
    //TS101
    String DECODE_TS101_STRING = API_URL + "execDecodeTS101RawString";
    //AIS140
    String DECODE_AIS140_STRING = API_URL + "execDecodeAIS140RawString";
    //All Protocol Online status for device
    String UPDATE_DEVICE_STATUS = API_URL + "updateDeviceStatus";


    String STRING = "string";
    String IMEI = "imei";
    String DEVICE_STAT = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/device_statistics_" + SERVER_PORT + ".txt";
    String DEVICE_STRING = "string";
    String DEVICE_STATUS = "status";
    //store exception details
    String EXCEPTION_LOG_MASTER = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/execption/log_";
    //store jar process id
    String PROCESS_IDS = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/process/log_";
    //store all connected and disconnected devices log
    String All_DEVICE_LOG = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/all_device/log_";
    //connected device log
    String OPEN_DEVICES_LOG = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/open_device/log_";
    //disconnected device log
    String CLOSE_DEVICES_LOG = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/close_device/log_";
    //invalid string log
    String INVALID_STRINGS = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/garbage/log_";
    //valid string log
    String VALID_STRINGS = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/string_gtc06/log_";
    //protocol TS 101 valid string log
    String TS_101_STRINGS = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/string_ts101/log_";
    //AIS 140
    String AIS_140_STRINGS = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/string_ais140/log_";
    //store imei files path
    String VALIDATE_IMEI = "c:/xampp/htdocs/vts_decoding_restapi/device_master/";
    String WRITE_LOG = "c:/xampp/htdocs/vts_decoding_restapi/vts_log/" + SERVER_PORT + "/write_cmd/log_";
}
