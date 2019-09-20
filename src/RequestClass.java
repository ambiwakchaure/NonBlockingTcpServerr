import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestClass {

    // HTTP Post request
    public synchronized static String sendingPostRequest(String apiUrl, String[] paramsArray) {

        String returnData = null;

        try {
            URL url = new URL(apiUrl);
            Map<String, Object> params = new LinkedHashMap<>();

            for (int i = 0; i < paramsArray.length; i++) {
                String[] paramIndex = paramsArray[i].split("#");
                params.put(paramIndex[0], paramIndex[1]);

            }
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder data = new StringBuilder();
            for (int c; (c = in.read()) >= 0;) {
                data.append((char) c);
            }

            returnData = data.toString();

        } catch (Exception e) {
            returnData = e.toString();
            LogMaster.saveExceptionLogDetails("RequestClass.sendingPostRequest 54", "" + e);
        }

        return returnData;

    }
}
