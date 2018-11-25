package com.trippilot.trippilot;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by arden on 2018-09-19.
 */

public class APIConnector extends AsyncTask<String,Void,JSONObject> {
    ProgressDialog dialog;
    public APIConnector(Context context){
        //dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        //dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //dialog.setMessage("로딩중입니다...");
        //dialog.show();
    }
    @Override
    protected JSONObject doInBackground(String... strings) {
        StringBuilder urlBuilder = new StringBuilder(strings[0]);
        HttpURLConnection con = null;
        try {
            //urlBuilder.append("?"+ URLEncoder.encode("ServiceKey","UTF-8") + "=vVokVJS5B6Cv5KAE5fBtarAPnJewMzyr%2FGtZb9Edk47ogW0qlkil7%2BLi8jJuYsStGSkNTuGob3Scq7IeAwCtrw%3D%3D");
            urlBuilder.append("?"+ URLEncoder.encode("ServiceKey","UTF-8") + "=xznBEkMnnvmNXDNEp%2Ft%2Bayh2LDPnkDikORkbNObJHZBB2aznz1PExeDgY19E5zH%2BZ0FA0%2Fhvy0Bbfqutz9N0vQ%3D%3D");
            urlBuilder.append("&" + URLEncoder.encode("MobileOS","UTF-8") + "=AND");
            urlBuilder.append("&" + URLEncoder.encode("MobileApp","UTF-8") + "=api_testing");
            for(int i =1; i<strings.length;i+=2) {
                urlBuilder.append("&"+URLEncoder.encode(strings[i],"UTF-8")+"="+strings[i+1]);
            }
            urlBuilder.append("&_type=json");

            java.net.URL myurl = new URL(urlBuilder.toString());
            con = (HttpURLConnection) myurl.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-type","application/json");
            int response = con.getResponseCode();
            if (response >=200 && response <=300) {
                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(con.getInputStream(),"UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new JSONObject(builder.toString());
            } else {
                Log.e("TAG-API-error", "Connection Error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        //dialog.dismiss();
        super.onPostExecute(jsonObject);
    }
}
