package com.itel.app.coach6xl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends Activity {
    private String client = null;
    private String coach = null;
    private String apikey = null;
    private String sessionid = null;
    private int schedule_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_pw);
        /*
        Intent i = getIntent();
        username = i.getStringExtra("coach");
        */
        Intent i = getIntent();
        String join_msg = i.getStringExtra("join_msg");
        Log.d("NRA join_msg", join_msg);

        try {
            ParseJSON(join_msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void ParseJSON(String msg) throws JSONException {

        JSONObject msg_jsob = new JSONObject(msg);
        client = msg_jsob.getString("from");
        coach =  msg_jsob.getString("to");

        String payload = msg_jsob.getString("payload");
        JSONObject object1 = (JSONObject) new JSONTokener(payload).nextValue();
        apikey = object1.getString("tokapikey");
        sessionid = object1.getString("toksessionid");
        schedule_id = object1.getInt("schedule_id");
    }


    public void SubmitPassword(View view) throws JSONException, ExecutionException, InterruptedException {

        EditText pw = (EditText) findViewById(R.id.txtPassword);
        Editable pwed = pw.getText();
        String password = pwed.toString();

        DownloadStringTask task = new DownloadStringTask();

        //String url = "https://dev1.itelecoach.com/home/ios_process_login.php";

        String url_host = "https://dev1.itelecoach.com/home/";
        String url_php = "ios_process_login.php";
        String url_param1 = "?username="+ coach;
        String url_param2 = "&password="+ password;
        String url = url_host + url_php + url_param1 + url_param2;

        Log.d("*** login url ***", url);

        task.execute(new String[]{url});

        String result = task.get();
        JSONObject result_json = new JSONObject(result);

        Log.d("*** login result ***", result);

        String error = result_json.getString("error");

        if ("0".equals(error)) {
            String type = result_json.getString("type");
            String full_name = result_json.getString("full_name");
            String greeting = "Welcome to iTeleCoach" + " " + full_name; //Welcome to iTelecoach, Dr xxxx"
            Toast.makeText(this, greeting, Toast.LENGTH_SHORT).show();

            Intent nextScreen = new Intent(getApplicationContext(), MainMenuActivity.class);
            nextScreen.putExtra("client", client);
            nextScreen.putExtra("coach", coach);
            nextScreen.putExtra("apikey", apikey);
            nextScreen.putExtra("sessionid", sessionid);
            nextScreen.putExtra("schedule_id", schedule_id);

            startActivity(nextScreen);

        }

        else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void CancelPassword(View view) {

    }

}//end LoginActivity