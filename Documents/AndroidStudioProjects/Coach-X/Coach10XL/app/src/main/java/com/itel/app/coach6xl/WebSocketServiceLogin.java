package com.itel.app.coach6xl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.WebSocket;

import java.util.ArrayList;
import java.util.List;

public class WebSocketServiceLogin extends Service {

    String message;
    private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
    private int mValue = 0; // Holds last value set by a client.
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_INT_VALUE = 3;
    public static final int MSG_SET_STRING_VALUE = 4;
    public static final int MSG_SESSION = 5;

    final Messenger myMessenger = new Messenger(new IncomingHandler());
    private static final String LOGTAG = "MyService";
    private int counter = 0, incrementBy = 1;
    private int sendMsgs = 0;
    boolean notsentMsg = true;

    private static final String WSURL = "https://dev1.itelepsych.com:9031/PubSub";
    private static final String ORIGIN = "https://dev1.itelepsych.com";

    //use 10.0.2.2 for default AVD and 10.0.3.2 for Genymotion
    //private static final String WSURL = "http://10.0.3.2:1883/chat";
    //private static final String ORIGIN = "http://10.0.3.2";
    WebSocket WSC = null;


    @Override
    public IBinder onBind(Intent intent) {
        return myMessenger.getBinder();
    }



    public void onCreate(){
        super.onCreate();
        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();

        //Send Presence Message
        message = "client1";
        connectWSC();
    }

    public void onDestroy(){
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            //Log.d(LOGTAG,"handleMessage: " + msg.what);

            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_STRING_VALUE:
                    Bundle data1 = msg.getData();
                    String dataString1 = data1.getString("Message");
                    message = dataString1;
                    //startWSC();
                    WSC.send(message);
                    break;
                case MSG_SESSION:
                    Bundle data2 = msg.getData();
                    String dataString2 = data2.getString("Message");
                    message = dataString2;
                    //startWSC();
                    WSC.send(message);
                    break;
                default:

                    super.handleMessage(msg);
            }
        }

    }

    private void connectWSC() {

        AsyncHttpGet get = new AsyncHttpGet(WSURL);
        get.addHeader("Origin", ORIGIN);

        AsyncHttpClient.getDefaultInstance().websocket(get, null, new AsyncHttpClient.WebSocketConnectCallback() {

            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    Log.d("*** webSocket error ***", ex.toString());
                    ex.printStackTrace();
                    return;
                }

                WSC = webSocket;

                webSocket.send(message);


                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d("*** StringAvailable ***", s);

                        sendNotification(s);


                    }
                });


            }
        });

    }



    private void sendNotification(String msg) {

        if (msg.equals("join_session")) {
            joinSession_Notif(msg);
            return;
        }

        else if (msg.equals("chat")) {
            chat_Notif(msg);
            return;
        }

        sendMessage(msg);

    }


    private void sendMessage(String s) {

        //Log.d("*** sendMsgs: ", String.valueOf(sendMsgs++));
        //Log.d("*** No of mClients: ", String.valueOf(mClients.size()));
        Log.d("*** sendMessage ***",  s);

        for (Messenger messenger : mClients) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("str1", s);
                Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
                msg.setData(bundle);
                messenger.send(msg);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list.
                mClients.remove(messenger);
            }
        }

    }


    private void joinSession_Notif(String msg) {
        Intent intent = new Intent(WebSocketServiceLogin.this, LoginActivity.class);
        intent.putExtra("join_msg", msg);

        //PendingIntent pIntent = PendingIntent.getActivity(WebSocketService.this, 0, intent, 0);
        PendingIntent pIntent = PendingIntent.getActivity(WebSocketServiceLogin.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title =    "Appointment";
        String content =   "Client Arrived";

        Notification noti = new Notification.Builder(WebSocketServiceLogin.this)
                .setContentTitle(title)
                        //.setContentText(from).setSmallIcon(R.drawable.coach)
                .setContentText(content).setSmallIcon(R.drawable.coach)
                .setContentIntent(pIntent)
                        //.addAction(R.drawable.icon, "Start", pIntent)
                        //.addAction(R.drawable.icon, "Waiting Room", pIntent)
                        //.addAction(R.drawable.icon, "Call", pIntent).build();
                .addAction(R.drawable.icon, "Login iTeleCoach", pIntent).build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);


    }

    private void chat_Notif(String msg) {

        Intent intent = new Intent(WebSocketServiceLogin.this, LoginActivity.class);
        intent.putExtra("join_msg", msg);

        //PendingIntent pIntent = PendingIntent.getActivity(WebSocketService.this, 0, intent, 0);
        PendingIntent pIntent = PendingIntent.getActivity(WebSocketServiceLogin.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title =    "Message";
        String content =   "Chat";

        Notification noti = new Notification.Builder(WebSocketServiceLogin.this)
                .setContentTitle(title)
                        //.setContentText(from).setSmallIcon(R.drawable.coach)
                .setContentText(content).setSmallIcon(R.drawable.coach)
                .setContentIntent(pIntent)
                        //.addAction(R.drawable.icon, "Start", pIntent)
                        //.addAction(R.drawable.icon, "Waiting Room", pIntent)
                        //.addAction(R.drawable.icon, "Call", pIntent).build();
                .addAction(R.drawable.icon, "Login iTeleCoach", pIntent).build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);


    }


}//end WebSocketService

