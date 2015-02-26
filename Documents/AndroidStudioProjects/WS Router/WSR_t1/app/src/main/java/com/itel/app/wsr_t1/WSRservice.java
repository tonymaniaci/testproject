package com.itel.app.wsr_t1;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.Toast;

import java.io.FileDescriptor;

public class WSRservice extends Service {
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    private class IncomingHandler extends Handler {

        public void handleMessage(Message msg) {

            Toast.makeText(getApplicationContext(), msg.what, Toast.LENGTH_SHORT).show();

            switch (msg.what) {

                default:
                    super.handleMessage(msg);
            }

        }
    }




}//end
