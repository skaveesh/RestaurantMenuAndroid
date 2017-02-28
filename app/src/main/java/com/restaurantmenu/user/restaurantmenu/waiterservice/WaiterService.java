package com.restaurantmenu.user.restaurantmenu.waiterservice;

import android.content.Context;
import android.net.Uri;

import com.restaurantmenu.user.restaurantmenu.trustcacert.TrustCACertificate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by samintha on 2/28/2017.
 */

public class WaiterService extends TrustCACertificate {

    public WaiterService(Context applicationContext) {
        super(applicationContext);
    }

    public String performHttpsPostCall(String endPoint, Map<String,String> params) {

        String finalResult = null;

        try {
            final String buildUrl = "https://lowcost-env.vewbk9mn2u.us-west-2.elasticbeanstalk.com/bend/restaurantwaiter/service/"+endPoint;
            URL url = new URL(buildUrl);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");


            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);

            writer.flush();
            writer.close();
            conn.connect();

            if (conn.getResponseCode() == 200) { //need to specify status codes otherwise it will throw FileNotFoundException
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                finalResult = br.readLine();
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                finalResult = br.readLine();
            }

            os.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return finalResult;
    }
}
