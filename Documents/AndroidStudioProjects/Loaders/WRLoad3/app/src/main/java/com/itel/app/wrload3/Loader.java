package com.itel.app.wrload3;

import android.content.AsyncTaskLoader;
import android.content.Context;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Loader extends AsyncTaskLoader<List<ItemData>> {
    private static final String TAG_WR =   "waitingroom";

    public Loader(Context context) {
        super(context);
    }

    ArrayList<ItemData> itemsData;


    @Override
    public ArrayList<ItemData> loadInBackground() {

        String url_host = "https://dev1.itelecoach.com/home/";
        String url_php = "doctorwaitingroom.php";
        String url_param1 = "?host_id=" + "coach1";
        String url = url_host + url_php + url_param1;


        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the response
        BufferedReader rd = null;
        try {
            rd = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        WR_JSP jParser = new WR_JSP();


        /*
        List<RowData> list = null;
        try {
            list = jParser.parseJSONFromResp(sb.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        /*
        final ArrayList<String> list = new ArrayList<String>();
        line = "";
        try {
            while ((line = rd.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        return itemsData;
    }


}