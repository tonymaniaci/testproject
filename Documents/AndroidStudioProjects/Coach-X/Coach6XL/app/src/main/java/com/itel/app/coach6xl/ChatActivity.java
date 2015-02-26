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
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity {
    Messenger myService = null;
    boolean isBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        //Intent intent = new Intent("com.itel.WebSocketService");
        //bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }


    // Handler of incoming messages from service.
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // Log.d(LOGTAG,"IncomingHandler:handleMessage");
            switch (msg.what) {
                case WebSocketService.MSG_SET_INT_VALUE:
                    //Log.d("IntValue", "Int Message: " + msg.arg1);
                    break;
                case WebSocketService.MSG_SET_STRING_VALUE:
                    String str1 = msg.getData().getString("str1");
                    Log.d("*** StrValue ***", str1);
                    TextView msgList = (TextView) findViewById(R.id.textView);
                    String display_msg = msgList.getText() + "\n" + "you: " + str1;
                    msgList.setText(display_msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }



    public void SendMessage(View view) throws JSONException {
        if (!isBound) return;

        //Message msg = Message.obtain();

        Bundle bundle = new Bundle();

        EditText et = (EditText) findViewById(R.id.etxtEnterMsg);
        Editable pwed = et.getText();
        String enteredTxt = pwed.toString();

        TextView msgList = (TextView) findViewById(R.id.textView);
        String display_msg = msgList.getText() + "\n" + "me: " + enteredTxt;
        msgList.setText(display_msg);

        et.setText("");

        String msg_jSon = createJsonMsg(enteredTxt);

        //bundle.putString("Message", msg_jSon);
        bundle.putString("Message", enteredTxt);

        Message msg = Message.obtain(null, WebSocketService.MSG_SET_STRING_VALUE);
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

            Message msg = Message.obtain(null, WebSocketService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            try {
                myService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
        bindService(new Intent(this, WebSocketService.class), myConnection,
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


}//end ChatActivity
