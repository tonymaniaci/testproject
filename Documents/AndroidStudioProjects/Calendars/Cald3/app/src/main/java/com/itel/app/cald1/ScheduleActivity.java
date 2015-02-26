package com.itel.app.cald1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class ScheduleActivity extends ActionBarActivity {

    ArrayAdapter<JsonObject> itemAdapter;
    private String username;
    private String select_date;

    //JSON Node Names
    private static final String TAG_JID = "events";      /* json data identifier */

    //private static final String TAG_11 =     "start";      /* level 1 */
    //private static final String TAG_12 =     "end";   /* level 1 */
    private static final String TAG_13 = "title";      /* level 1 */
    private static final String TAG_14 = "duration";   /* level 1 */
    private static final String TAG_15 = "startText";   /* level 1 */
    private static final String TAG_16 = "endText";   /* level 1 */
    private static final String TAG_17 = "attendee_id";   /* level 1 */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TextView greeting = (TextView) findViewById(R.id.greeting);

        Intent i = getIntent();
        // Receiving the Data
        //String full_name = i.getStringExtra("full_name");
        username = i.getStringExtra("username");
        select_date = i.getStringExtra("selectedDate");
        //username = "coach1";
        //Log.v("PASSED FULL_NAME", full_name);
        Log.d("PASSED USERNAME", username);

        // Displaying Received data
        //greeting.setText(full_name);

        // Enable global Ion logging
        //Ion.getDefault(this).setLogging("ion-sample", Log.DEBUG);

        //select_date = "2015-02-18";
        Log.d("*** select_date ***", select_date);

        // create a tweet adapter for our list view
        itemAdapter = new ArrayAdapter<JsonObject>(this, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.activity_schedule_detail, null);


                JsonObject item = getItem(position);

                //String start = item.get(TAG_11).getAsString();
                //String end = item.get(TAG_12).getAsString();
                String title = item.get(TAG_13).getAsString();

                String duration = item.get(TAG_14).getAsString();
                String dur_mins = duration + " min Web Session ";

                String startText = item.get(TAG_15).getAsString();
                String start_time = startText.substring(11, 16);

                String endText = item.get(TAG_16).getAsString();
                String end_time = endText.substring(11, 16);

                String attendee_id = item.get(TAG_17).getAsString();
                String pat_id = " ID: " + attendee_id;
                TextView client_title = (TextView) convertView.findViewById(R.id.patient_name);
                client_title.setText(title);
                client_title.setBackgroundResource(R.color.green);

                TextView session_duration = (TextView) convertView.findViewById(R.id.duration);
                session_duration.setText(dur_mins);

                TextView start_text = (TextView) convertView.findViewById(R.id.startTime);
                start_text.setText(start_time);

                TextView end_text = (TextView) convertView.findViewById(R.id.endTime);
                end_text.setText(end_time);

                TextView attendee = (TextView) convertView.findViewById(R.id.patient_id);
                attendee.setText(pat_id);

                final String url_host = "https://dev1.itelecoach.com/home/";
                String url_php = "getphoto.php";
                String url_params = "?id=" + attendee_id;
                String url_pix = url_host + url_php + url_params;

                final View finalConvertView = convertView;
                final Future<String> stringFuture = Ion.with(getContext())
                        .load(url_pix)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                if (e != null) {
                                    Toast.makeText(getApplicationContext(), "Error loading tweets", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                //Log.d("*** result ***", result);
                                String pix_str = result.trim();
                                String photo_url = url_host + pix_str;
                                //Log.d("*** photo_url ***", photo_url);


                                ImageView imageView = (ImageView) finalConvertView.findViewById(R.id.image);

                                Ion.with(imageView)
                                        // use a placeholder google_image if it needs to load from the network
                                        .placeholder(R.drawable.twitter)
                                                // load the url
                                                // .load(imageUrl);
                                        .load(photo_url);

                            }
                        });


                return convertView;

            }
        };

        // basic setup of the ListView and adapter
        setContentView(R.layout.activity_schedule);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(itemAdapter);

        // authenticate and do the first load
        //getCredentials();
        load2();
    }

    //Future<JsonArray> loading;
    Future<JsonObject> loading;

    private void load2() {
        // don't attempt to load more if a load is already in progress
        if (loading != null && !loading.isDone() && !loading.isCancelled())
            return;

        //https://www.itelepsych.com/home/getEvents.php?iOS=Y&host_id=(the doctor id)
        String url_host = "https://dev1.itelecoach.com/home/";
        String url_php = "getEvents.php";
        String url_params = "?iOS=Y&host_id=" + username;
        String url = url_host + url_php + url_params;

        Future<JsonObject> JsonObject = Ion.with(this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // this is called back onto the ui thread, no Activity.runOnUiThread or Handler.post necessary.
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Error loading tweets", Toast.LENGTH_LONG).show();
                            return;
                        }

                        JsonArray result_jsa = result.getAsJsonArray(TAG_JID);
                        // add the tweets

                        for (int i = 0; i < result_jsa.size(); i++) {

                            JsonObject result_jso = result_jsa.get(i).getAsJsonObject();

                            String startText = result_jso.get(TAG_15).getAsString();
                            //Log.d("*** startText ***", startText);

                            String yyyy_mm_dd = startText.substring(0, 10);
                            //Log.d("*** yyyy_mm_dd ***", yyyy_mm_dd);

                            if (select_date.equals(yyyy_mm_dd)) {
                                itemAdapter.add(result_jso);
                            }
                        }

                        int num_items = itemAdapter.getCount();
                        if (num_items == 0) {
                            Toast.makeText(getApplicationContext(), "No Appointments", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

}
