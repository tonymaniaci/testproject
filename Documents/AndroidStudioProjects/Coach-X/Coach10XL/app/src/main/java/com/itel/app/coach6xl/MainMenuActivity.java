package com.itel.app.coach6xl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.concurrent.ExecutionException;

public class MainMenuActivity extends Activity {
    private static final String TAG_11 =     "payload";        /* level 1 */
    private static final String TAG_12 =     "from";           /* level 1 */
    private static final String TAG_13 =     "to";           /* level 1 */

    private static final String TAG_21 =     "full_name";      /* level 2 */
    private static final String TAG_22 =     "tokapikey";      /* level 2 */
    private static final String TAG_23 =     "toksessionid";   /* level 2 */

    private static final String TAG_99 =     "token";   /* from json from tomaketoken.php */

    private String client = null;
    private String coach = null;
    private String apikey = null;
    private String session_id = null;
    private String token = null;
    private int schedule_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        Intent i = getIntent();
        client = i.getStringExtra("client");
        coach = i.getStringExtra("coach");
        apikey = i.getStringExtra("apikey");
        session_id = i.getStringExtra("sessionid");
        schedule_id = i.getIntExtra("schedule_id", 0);

        try {
            SessionRow();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void SessionRow() throws JSONException, ExecutionException, InterruptedException {

        //Get waiting room details
        DownloadStringTask client_info = new DownloadStringTask();

        String url_host = "https://dev1.itelecoach.com/home/";
        String url_php = "sessionDetail.php";
        String url_params = "?session_id="+ schedule_id;
        String url = url_host + url_php + url_params;

        client_info.execute(new String[]{url});

        String client_info_str = client_info.get();
        Log.d("client_info_str", client_info_str);

        SessionDetail(client_info_str);

    }


    public void StartSession(View view) {
        Log.d("*** StartSession ***", "StartSession button pressed");

        try {
            GenToken();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent opentok = new Intent(getApplicationContext(), OpentokActivity.class);

        opentok.putExtra("apikey", apikey);
        opentok.putExtra("sessionid",session_id );
        opentok.putExtra("token", token);
        opentok.putExtra("schedule_id", schedule_id);


        Log.d("*** NRA apikey ***", apikey);
        Log.d("*** NRA sessionid ***", session_id);
        Log.d("*** NRA token ***", token);
        Log.d("*** NRA from ***", client);
        Log.d("*** NRA from ***", coach);

        startActivity(opentok);

    }


    private void GenToken() throws ExecutionException, InterruptedException, JSONException {

        DownloadStringTask task = new DownloadStringTask();

        String url_host = "https://dev1.itelecoach.com/home/";
        String url_php = "Opentok-PHP/tokmaketoken.php";
        String url_params = "?iOS=Y&session_id="+ session_id;
        String url = url_host + url_php + url_params;

        task.execute(new String[]{url});

        String token_str = task.get();
        JSONObject token_json = new JSONObject(token_str);

        token = token_json.getString(TAG_99);

    }

    public void DisplayWaitingRoom(View view) {
        Log.d("*** DisplayWaitingRoom ***", "DisplayWaitingRoom button pressed");
        Intent WRActivityIonScreen = new Intent(getApplicationContext(), WaitingRoomIonActivity.class);
        Log.d("*** username ***", coach);
        WRActivityIonScreen.putExtra("username", coach);
        startActivity(WRActivityIonScreen);
    }

    private void SessionDetail(String client_info_str) throws JSONException, ExecutionException, InterruptedException {

        JSONObject client_info_json = new JSONObject(client_info_str);
        JSONArray jsonArray = client_info_json.optJSONArray("waitingroom");

        String row = jsonArray.getString(0);
        JSONObject object1 = (JSONObject) new JSONTokener(row).nextValue();

        String attendee_name = object1.getString("attendee_name");//json
        //String actual_end_tm = object1.getString("actual_end_tm");
        String status = object1.getString("status");//json
        //String arrive_tm = object1.getString("arrive_tm");//json
        //String actual_start_tm = object1.getString("actual_start_tm");
        //String duration = object1.getString("duration");
        //String tokapikey = object1.getString("tokapikey");
        //String toktoken = object1.getString("toktoken");
        String sched_start_time  = object1.getString("date_rfc2822");
        String session_length  = object1.getString("session_length");

        JSONObject object2 = (JSONObject) new JSONTokener(attendee_name).nextValue();
        String patient = object2.getString("name");
        String photo_url = object2.getString("photo_url");

        JSONObject object3 = (JSONObject) new JSONTokener(status).nextValue();
        String session_closed = object3.getString("session_closed");
        String online_status = object3.getString("online_status");

        //Get Client photo
        String url_host = "https://dev1.itelecoach.com/home/";
        String image_url = url_host + photo_url;
        DownloadImageTask imagetask = new DownloadImageTask();
        imagetask.execute(new String[]{image_url});
        Bitmap client_image = imagetask.get();
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(client_image);

        final TextView nameTV = (TextView) findViewById(R.id.name);
        nameTV.setText(patient);

        final TextView scheduledTV = (TextView) findViewById(R.id.Scheduled);
        scheduledTV.setText(sched_start_time);

        final TextView durationTV = (TextView) findViewById(R.id.tbDuration);
        durationTV.setText(session_length + " " + "Minutes");

        String status_msg = "Offline";

        if (online_status.equals("1")) status_msg = "Online";

        final TextView statusTV = (TextView) findViewById(R.id.tbStatus);
        statusTV.setText(status_msg);

    }


    public void DisplayClient(View view) {

        Intent patientDetail = new Intent(getApplicationContext(), PatientDetailActivity.class);
        patientDetail.putExtra("clientID", client);
        startActivity(patientDetail);

    }


} //end NotificationReceiverActivity
