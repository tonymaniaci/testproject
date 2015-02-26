package com.itel.app.coach6xl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class StdMainMenuActivity extends Activity {
    private String username;
    Messenger myService = null;
    boolean isBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private static final String LOGTAG = "MyService";

    //public static final int MSG_PRESENCE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.std_main_menu_activity);

        //startService(new Intent(StdMainMenuActivity.this,WebSocketServiceLogin.class));
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
        //setContentView(R.layout.chat_activity);

        //Intent intent = new Intent("com.itel.WebSocketService");
        //bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }


    // Handler of incoming messages from service.
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(LOGTAG,"handleMessage StdMM: " + msg.what);
            switch (msg.what) {
                case WebSocketServiceLogin.MSG_SET_INT_VALUE:
                    //Log.d("IntValue", "Int Message: " + msg.arg1);
                    break;
                case WebSocketServiceLogin.MSG_SET_STRING_VALUE:
                    String str1 = msg.getData().getString("str1");
                    Log.d("*** StrValue ***", str1);
                    TextView msgList = (TextView) findViewById(R.id.textView);
                    String display_msg = msgList.getText() + "\n" + "you: " + str1;
                    msgList.setText(display_msg);
                    break;
                case WebSocketServiceLogin.MSG_PRESENCE:
                    //Log.d("IntValue", "Int Message: " + msg.arg1);
                    break;
                case WebSocketServiceLogin.MSG_SESSION:
                    //Log.d("IntValue", "Int Message: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    public void SendMessage()  {
        if (!isBound) return;

        //Message msg = Message.obtain();

        Bundle bundle = new Bundle();

        /*
        EditText et = (EditText) findViewById(R.id.etxtEnterMsg);
        Editable pwed = et.getText();
        String enteredTxt = pwed.toString();

        TextView msgList = (TextView) findViewById(R.id.textView);
        String display_msg = msgList.getText() + "\n" + "me: " + enteredTxt;
        msgList.setText(display_msg);

        et.setText("");

        String msg_jSon = createJsonMsg(enteredTxt);
        */

        //bundle.putString("Message", msg_jSon);
        bundle.putString("Message", username);

        Message msg = Message.obtain(null, WebSocketServiceLogin.MSG_PRESENCE);
        msg.replyTo = mMessenger;

        msg.setData(bundle);

        try {
            myService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            myService = new Messenger(service);
            isBound = true;

            SendMessage();

            /*
            Message msg = Message.obtain(null, WebSocketServiceLogin.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            try {
                myService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            */
        }

        public void onServiceDisconnected(ComponentName className) {
            myService = null;
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        bindService(new Intent(this, WebSocketServiceLogin.class), myConnection,
                Context.BIND_AUTO_CREATE);
        Log.d("*** SessionActivity ***", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (isBound) {
            unbindService(myConnection);
            isBound = false;
        }
        Log.d("*** SessionActivity ***", "onStop");
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
                Toast.makeText(StdMainMenuActivity.this, "Schedule is Selected", Toast.LENGTH_SHORT).show();
                Intent ScheduleAppointmentScreen = new Intent(this, ScheduleAppointmentActivity.class);
                ScheduleAppointmentScreen.putExtra("username", username);
                startActivity(ScheduleAppointmentScreen);
                return true;
            case R.id.action_waitingroom:
                Toast.makeText(StdMainMenuActivity.this, "Waiting Room is Selected", Toast.LENGTH_SHORT).show();
                /*
                Intent WaitingRoomScreen = new Intent(this, WaitingRoomActivity.class);
                WaitingRoomScreen.putExtra("username", username);
                startActivity(WaitingRoomScreen);
                */
                return true;
            case R.id.action_waitingroomion:
                Toast.makeText(StdMainMenuActivity.this, "Waiting Room Images is Selected", Toast.LENGTH_SHORT).show();
                Intent WRActivityIonScreen = new Intent(getApplicationContext(), WaitingRoomIonActivity.class);
                WRActivityIonScreen.putExtra("username", username);
                startActivity(WRActivityIonScreen);
                return true;
            case R.id.action_finance:
                Toast.makeText(StdMainMenuActivity.this, "Finance is Selected", Toast.LENGTH_SHORT).show();
                Intent FinancialReportsScreen = new Intent(getApplicationContext(), FinancialReportsActivity.class);
                FinancialReportsScreen.putExtra("username", username);
                startActivity(FinancialReportsScreen);
                return true;
            case R.id.action_chat:
                Toast.makeText(StdMainMenuActivity.this, "Chat is Selected", Toast.LENGTH_SHORT).show();
                Intent ChatActivityScreen = new Intent(this, ChatActivity.class);
                //ScheduleAppointmentScreen.putExtra("username", username);
                startActivity(ChatActivityScreen);
                return true;
            case R.id.action_settings:
                Toast.makeText(StdMainMenuActivity.this, "Settings is Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_about:
                Toast.makeText(StdMainMenuActivity.this, "About is Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_logout:
                Toast.makeText(StdMainMenuActivity.this, "Log Out is Selected", Toast.LENGTH_SHORT).show();
                stopService(new Intent(StdMainMenuActivity.this,WebSocketServiceLogin.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

/*
    private String createJsonMsg (String msg) throws JSONException {

        JSONObject jso = new JSONObject();
        JSONObject payload = new JSONObject();

        jso.put("messagetype", "chat");
        jso.put("key", "");
        jso.put("from", "coach1");
        jso.put("to", "clioneqh");

        payload.put("schedule_id","602");
        payload.put("session_id","");
        payload.put("url","");
        payload.put("full_name","Client One");
        payload.put("msg",msg);

        jso.put("payload", payload);

        return jso.toString();


    }

*/


}//end
