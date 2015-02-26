package com.itel.app.wrion6xl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class WSRIonActivity extends Activity {
    ArrayAdapter<JsonObject> itemAdapter;
    private String username;

    //JSON Node Names
    private static final String TAG_JID =   "waitingroom";      /* json data identifier */

    private static final String TAG_11 =     "attendee_id";      /* level 1 */
    private static final String TAG_12 =     "session_length";   /* level 1 */

    private static final String TAG_21 =     "formatted_tm";      /* level 2 ( sched_tm ) */
    private static final String TAG_22 =     "date_rfc2822";     /* level 2 ( arrive_tm ) */
    private static final String TAG_23 =     "name";             /* level 2 ( attendee_name ) */
    private static final String TAG_24 =     "photo_url";        /* level 2 ( attendee_name ) */
    private static final String TAG_25 =     "online_status";    /* level 2 ( status ) */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //TextView greeting = (TextView) findViewById(R.id.greeting);

        Intent i = getIntent();
        // Receiving the Data
        //String full_name = i.getStringExtra("full_name");
        username = i.getStringExtra("username");
        //Log.v("PASSED FULL_NAME", full_name);
        //Log.d("PASSED USERNAME", username);


        // Displaying Received data
        //greeting.setText(full_name);

        // Enable global Ion logging
        //Ion.getDefault(this).setLogging("ion-sample", Log.DEBUG);

        // create a tweet adapter for our list view
        itemAdapter = new ArrayAdapter<JsonObject>(this, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_layout, null);

                // we're near the end of the list adapter, so load more items
                /*
                if (position >= getCount() - 3)
                    load2();
                */
                // grab the item (or retweet)
                JsonObject item = getItem(position);

                String attendee_id    = item.get(TAG_11).getAsString();
                String session_length = item.get(TAG_12).getAsString();

                JsonObject sched_tm = item.getAsJsonObject("sched_tm");
                String formatted_tm = sched_tm.get(TAG_21).getAsString();

                JsonObject arrive_tm = item.getAsJsonObject("arrive_tm");
                String date_rfc2822 = arrive_tm.get(TAG_22).getAsString();

                JsonObject attendee_name = item.getAsJsonObject("attendee_name");
                String name = attendee_name.get(TAG_23).getAsString();

                String photo_url_part = attendee_name.get(TAG_24).getAsString();
                String base_url = "https://dev1.itelecoach.com/home/";
                String photo_url = base_url + photo_url_part;

                JsonObject status = item.getAsJsonObject("status");
                String online_status = status.get(TAG_25).getAsString();


                ImageView imageView = (ImageView)convertView.findViewById(R.id.image);

                // Use Ion's builder set the google_image on an ImageView from a URL

                // start with the ImageView


                Ion.with(imageView)
                        // use a placeholder google_image if it needs to load from the network
                        .placeholder(R.drawable.twitter)
                                // load the url
                                // .load(imageUrl);
                        .load(photo_url);

                // and finally, set the name and text
                TextView handle = (TextView)convertView.findViewById(R.id.handle);
                handle.setText(formatted_tm);

                TextView text = (TextView)convertView.findViewById(R.id.tweet);
                text.setText(name);

                return convertView;

            }
        };

        // basic setup of the ListView and adapter
        setContentView(R.layout.activity_main);
        ListView listView = (ListView)findViewById(R.id.list);
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

        String url_host = "https://dev1.itelecoach.com/home/";
        String url_php = "doctorwaitingroom.php";
        String url_params = "?host_id="+ username;
        String url = url_host + url_php + url_params;


        /*
        if (itemAdapter.getCount() > 0) {
            // load from the "last" id
            JsonObject last = itemAdapter.getItem(itemAdapter.getCount() - 1);
            url += "&max_id=" + last.get("id_str").getAsString();
        }
        */

        //loading = Ion.with(this)
        Ion.with(this)
                //.setHeader("Authorization", "Bearer " + accessToken)
                //.asJsonArray()
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // this is called back onto the ui thread, no Activity.runOnUiThread or Handler.post necessary.
                        if (e != null) {
                            Toast.makeText(WSRIonActivity.this, "Error loading waiting room", Toast.LENGTH_LONG).show();
                            return;
                        }

                        JsonArray result_jsa = result.getAsJsonArray(TAG_JID);
                        // add the tweets

                        for (int i = 0; i < result_jsa.size(); i++) {
                            itemAdapter.add(result_jsa.get(i).getAsJsonObject());
                        }

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.action_logout:
                Toast.makeText(WSRIonActivity.this, "Log Out is Selected", Toast.LENGTH_SHORT).show();
                stopService(new Intent(WSRIonActivity.this,WSRouter.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }








}//END
