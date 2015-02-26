package com.itel.app.coach6xl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class PatientDetailActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_activity);

        Intent i = getIntent();
        String clientID = i.getStringExtra("clientID");
        Log.d("ClientID", clientID);

        try {
            try {
                ClientDetails(clientID);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void ClientDetails(String client) throws JSONException, ExecutionException, InterruptedException {


        //Get Client photo
        DownloadStringTask task = new DownloadStringTask();

        String url_host = "https://dev1.itelecoach.com/home/";
        String url_php = "getphoto.php";
        String url_params = "?id="+ client;
        String url = url_host + url_php + url_params;

        task.execute(new String[]{url});

        String pix_str = task.get();
        pix_str = pix_str.trim();

        String image_url = url_host + pix_str;

        DownloadImageTask imagetask = new DownloadImageTask();

        imagetask.execute(new String[]{image_url});
        Bitmap client_image = imagetask.get();

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(client_image);

        //Get client details
        DownloadStringTask client_info = new DownloadStringTask();

        url_host = "https://dev1.itelecoach.com/home/";
        url_php = "getPatientInfoMobile.php";
        url_params = "?id="+ client;
        url = url_host + url_php + url_params;

        client_info.execute(new String[]{url});

        String client_info_str = client_info.get();
        Log.d("client_info_str", client_info_str);

        DisplayClientDetails(client_info_str);

    }


    private void DisplayClientDetails(String clientID) throws JSONException {

        JSONObject client_info_json = new JSONObject(clientID);

        //Client Name
        String first_name = client_info_json.getString("first_name");
        String last_name = client_info_json.getString("last_name");
        String middle_initial = client_info_json.getString("middle_initial");
        String full_name = first_name + " " + middle_initial + " " + last_name;
        final TextView nameTV = (TextView) findViewById(R.id.name);
        nameTV.setText(full_name);

        //DOB
        String dob_month = client_info_json.getString("dob_month");
        String dob_day = client_info_json.getString("dob_day");
        String dob_year = client_info_json.getString("dob_year");
        String full_dob = "DOB: " + dob_month + "/" + dob_day + "/" + dob_year;
        final TextView dobTV = (TextView) findViewById(R.id.Scheduled);
        dobTV.setText(full_dob);

        //Address
        String address = client_info_json.getString("address");
        final TextView addressTV = (TextView) findViewById(R.id.address);
        addressTV.setText(address);
        String city = client_info_json.getString("city");
        String state = client_info_json.getString("state");
        String zip = client_info_json.getString("zip");
        String citystatezip = city + ", " + state + " " + zip;
        final TextView citystatezipTV = (TextView) findViewById(R.id.citystatezip);
        citystatezipTV.setText(citystatezip);

    }

}

