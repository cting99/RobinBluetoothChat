package cting.com.robin.support.common.utils;


import android.content.Context;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    public static final String TAG = "cting/HttpHelper";

    public static final String downloadUrl(Context context, String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                Log.i(TAG, "downloadUrl: response error:" + responseCode);
                return null;
            }
            return FileHelper.readStream(connection.getInputStream());
        } catch (Exception e) {
            Log.i(TAG, "downloadUrl exception:" + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

}
