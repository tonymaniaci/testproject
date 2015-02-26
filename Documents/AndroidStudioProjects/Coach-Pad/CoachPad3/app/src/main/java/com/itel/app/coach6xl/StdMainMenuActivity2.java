package com.itel.app.coach6xl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class StdMainMenuActivity2 extends Activity {
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.std_main_menu_activity);

        startService(new Intent(StdMainMenuActivity2.this,WebSocketServiceLogin.class));
        //startService(new Intent(StdMainMenuActivity.this,WebSocketService2.class));

        TextView greeting = (TextView) findViewById(R.id.greeting);

        Intent i = getIntent();
        String full_name = i.getStringExtra("full_name");
        username = i.getStringExtra("username");
        String type = i.getStringExtra("type");
        Log.v("PASSED FULL_NAME", full_name);
        Log.v("PASSED USERNAME", username );

        // Displaying Received data
        greeting.setText(full_name);

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
            case R.id.action_schedule:
                Toast.makeText(StdMainMenuActivity2.this, "Schedule is Selected", Toast.LENGTH_SHORT).show();
                Intent ScheduleAppointmentScreen = new Intent(this, ScheduleAppointmentActivity.class);
                ScheduleAppointmentScreen.putExtra("username", username);
                startActivity(ScheduleAppointmentScreen);
                return true;
            case R.id.action_waitingroom:
                Toast.makeText(StdMainMenuActivity2.this, "Waiting Room is Selected", Toast.LENGTH_SHORT).show();
                /*
                Intent WaitingRoomScreen = new Intent(this, WaitingRoomActivity.class);
                WaitingRoomScreen.putExtra("username", username);
                startActivity(WaitingRoomScreen);
                */
                return true;
            case R.id.action_waitingroomion:
                Toast.makeText(StdMainMenuActivity2.this, "Waiting Room Images is Selected", Toast.LENGTH_SHORT).show();
                Intent WRActivityIonScreen = new Intent(getApplicationContext(), WaitingRoomIonActivity.class);
                WRActivityIonScreen.putExtra("username", username);
                startActivity(WRActivityIonScreen);
                return true;
            case R.id.action_finance:
                Toast.makeText(StdMainMenuActivity2.this, "Finance is Selected", Toast.LENGTH_SHORT).show();
                Intent FinancialReportsScreen = new Intent(getApplicationContext(), FinancialReportsActivity.class);
                FinancialReportsScreen.putExtra("username", username);
                startActivity(FinancialReportsScreen);
                return true;
            case R.id.action_chat:
                Toast.makeText(StdMainMenuActivity2.this, "Chat is Selected", Toast.LENGTH_SHORT).show();
                Intent ChatActivityScreen = new Intent(this, ChatActivity.class);
                //ScheduleAppointmentScreen.putExtra("username", username);
                startActivity(ChatActivityScreen);
                return true;
            case R.id.action_settings:
                Toast.makeText(StdMainMenuActivity2.this, "Settings is Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_about:
                Toast.makeText(StdMainMenuActivity2.this, "About is Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_logout:
                Toast.makeText(StdMainMenuActivity2.this, "Log Out is Selected", Toast.LENGTH_SHORT).show();
                stopService(new Intent(StdMainMenuActivity2.this,WebSocketServiceLogin.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

