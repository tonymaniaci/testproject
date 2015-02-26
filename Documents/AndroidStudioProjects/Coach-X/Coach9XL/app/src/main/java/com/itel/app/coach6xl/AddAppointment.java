package com.itel.app.coach6xl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AddAppointment extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_appointment);

        TextView txt_ClientName = (TextView) findViewById(R.id.txt_ClientName);
        TextView txt_Appt_Date = (TextView) findViewById(R.id.txt_Appt_Date);
        TextView txt_Start_Time = (TextView) findViewById(R.id.txt_Start_Time);
        TextView txt_End_Time = (TextView) findViewById(R.id.txt_End_Time);
        TextView txt_Duration = (TextView) findViewById(R.id.txt_Duration);

        Intent i = getIntent();

        // Displaying Received data
        txt_ClientName.setText("Client One");
        txt_Appt_Date.setText(i.getStringExtra("appt_date"));
        txt_Start_Time.setText(i.getStringExtra("start_time"));
        txt_End_Time.setText(i.getStringExtra("end_time"));
        txt_Duration.setText(i.getStringExtra("duration"));

    }




}
