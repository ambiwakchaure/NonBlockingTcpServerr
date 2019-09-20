import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Parsing {

    public static String parseImeiDeIdRespons(String response) {
        String imeiStatus = "2";
        try {
            JSONObject jSONObject = new JSONObject(response);

            String success = jSONObject.getString("success");

            if (success.equals("1")) {

                imeiStatus = "1#" + jSONObject.getString("imei");

            } else {
                imeiStatus = "0";
            }

        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("Parsing.parseImeiDeIdRespons 26", "" + e);
        }

        return imeiStatus;
    }

    public static ArrayList<String> parseImeiRespons(String response) {
        ArrayList<String> IMEI = new ArrayList<>();
        try {
            JSONObject jSONObject = new JSONObject(response);

            String success = jSONObject.getString("success");

            if (success.equals("1")) {
                JSONArray jSONArray = jSONObject.getJSONArray("imei");

                for (int i = 0; i < jSONArray.length(); i++) {
                    IMEI.add(jSONArray.get(i).toString());
                }
            }
        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("Parsing.parseImeiRespons 48", "" + e);
        }

        return IMEI;
    }

    public static String checkImeiStatus(String response) {
        String imeiStatus = "2";
        try {
            JSONObject jSONObject = new JSONObject(response);

            String success = jSONObject.getString("success");

            if (success.equals("1")) {

                imeiStatus = "1";

            } else {
                imeiStatus = "0";
            }

        } catch (Exception e) {

            LogMaster.saveExceptionLogDetails("Parsing.checkImeiStatus 71", "" + e);
        }

        return imeiStatus;
    }

    public static String parseCommonApi(String response) {
        String status = "2";
        try {
            JSONObject jSONObject = new JSONObject(response);

            String success = jSONObject.getString("success");

            if (success.equals("1")) {
                status = "1";

            } else {
                status = "0";
            }

        } catch (Exception e) {
            status = "2";
            LogMaster.saveExceptionLogDetails("Parsing.checkImeiStatus 93", "" + e);
        }

        return status;
    }
}
