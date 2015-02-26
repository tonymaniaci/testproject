package com.itel.app.coach6xl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... urls) {

        Bitmap bmp =null;
        for (String url : urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                /*
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
                */
                bmp = BitmapFactory.decodeStream(content);
                if (null != bmp)
                    return bmp;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        //textView.setText(result);
        //processValue(result);
    }
    /*
    public String processValue(Object result) {
        //Log.v("PROCESS_VALUE", result.toString());
        return (String) result;

    }
    */

} //end DownloadWebPageTask

