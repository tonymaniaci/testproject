package com.itel.app.coach6xl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SessionCloseoutActivity extends Activity {

    String start_time; long duration; int schedule_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sessioncloseout);

        Intent i = getIntent();
        start_time = i.getStringExtra("start_time");
        duration= i.getLongExtra("end_time", 0L);
        schedule_id = i.getIntExtra("schedule_id",0);

        try {
            SessionSummary(schedule_id);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void SessionSummary(int scheduleID ) throws ExecutionException, InterruptedException, JSONException {

        DownloadStringTask task = new DownloadStringTask();

        String url_host = "https://dev1.itelecoach.com/home/";
        String url_php = "sessionsummary.php";
        String url_params = "?iOS=Y&schedule_id="+ scheduleID;
        String url = url_host + url_php + url_params;

        task.execute(new String[]{url});

        String close_str = task.get();
        Log.d("*** close_str ***", close_str);
        JSONObject close_json = new JSONObject(close_str);
        String doctor_name = close_json.getString("doctor_name");
        String patient_name = close_json.getString("patient_name");
        String patient_email = close_json.getString("patient_email");

        TextView coach_name = (TextView) findViewById(R.id.coach_name);
        coach_name.setText(doctor_name);
        TextView client_name = (TextView) findViewById(R.id.client_name);
        client_name.setText(patient_name);
        TextView client_email = (TextView) findViewById(R.id.client_email);
        client_email.setText(patient_email);

        TextView start = (TextView) findViewById(R.id.start_time);
        start.setText(start_time);

        int duration_minutes = (int) (duration/60);
        String dur_str = String.format("%s %s", duration_minutes, "minutes");
        TextView dur = (TextView) findViewById(R.id.duration);
        dur.setText(dur_str);

    }

    public void SubmitPassword(View view) {
        Toast.makeText(this, "Submitted" , Toast.LENGTH_SHORT).show();

    }


    public void CancelPassword(View view) {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
    }
}//SessionCloseoutActivity
