package com.itel.app.coach6xl;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class OpentokActivity extends Activity implements
        Session.SessionListener, Publisher.PublisherListener,
        Subscriber.VideoListener{

    private static final String LOGTAG = "demo-hello-world";
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private ArrayList<Stream> mStreams;
    protected Handler mHandler = new Handler();

    private RelativeLayout mPublisherViewContainer;
    private RelativeLayout mSubscriberViewContainer;

    // Spinning wheel for loading subscriber view
    private ProgressBar mLoadingSub;

    private boolean resumeHasRun = false;

    private NotificationCompat.Builder mNotifyBuilder;
    NotificationManager mNotificationManager;
    private int notificationId;

    private String API_KEY = null;
    private String SESSION_ID = null;
    private int schedule_id = 0;
    private String TOKEN = null;
    private boolean SUBSCRIBE_TO_SELF = false;

    private String start_time = null;
    private long start_time_SystemClock = 0L;
    private long end_time_SystemClock = 0L;
    private long duration = 0L;

    private String reason = "DK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGTAG, "ON_CREATE");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.opentok_layout);

        Intent i = getIntent();

        API_KEY = i.getStringExtra("apikey");
        SESSION_ID = i.getStringExtra("sessionid");
        TOKEN  = i.getStringExtra("token");
        schedule_id = i.getIntExtra("schedule_id",0);

        SUBSCRIBE_TO_SELF = false;

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisherview);
        mSubscriberViewContainer = (RelativeLayout) findViewById(R.id.subscriberview);
        mLoadingSub = (ProgressBar) findViewById(R.id.loadingSpinner);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mStreams = new ArrayList<Stream>();
        sessionConnect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Remove publisher & subscriber views because we want to reuse them
        if (mSubscriber != null) {
            mSubscriberViewContainer.removeView(mSubscriber.getView());
        }
        reloadInterface();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOGTAG, "ON_PAUSE");

        if (mSession != null) {
            mSession.onPause();

            if (mSubscriber != null) {
                mSubscriberViewContainer.removeView(mSubscriber.getView());
            }
        }

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(this.getTitle())
                .setContentText(getResources().getString(R.string.notification))
                .setSmallIcon(R.drawable.ic_launcher).setOngoing(true);

        Intent notificationIntent = new Intent(this, OpentokActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        mNotifyBuilder.setContentIntent(intent);
        notificationId = (int) System.currentTimeMillis();
        mNotificationManager.notify(notificationId, mNotifyBuilder.build());

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOGTAG, "ON_RESUME");

        if (!resumeHasRun) {
            resumeHasRun = true;
            return;
        } else {
            if (mSession != null) {
                mSession.onResume();
            }
        }
        mNotificationManager.cancel(notificationId);

        reloadInterface();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOGTAG, "ON_STOP");

        if (isFinishing()) {
            mNotificationManager.cancel(notificationId);
            if (mSession != null) {
                mSession.disconnect();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(LOGTAG, "ON_BACKPRESSED");

        if (mSession != null) {
            mSession.disconnect();
        }
        super.onBackPressed();
        end_time_SystemClock = SystemClock.uptimeMillis();
        Intent sessionClose = new Intent(getApplicationContext(), SessionCloseoutActivity.class);
        duration = (end_time_SystemClock - start_time_SystemClock)/1000L;

        sessionClose.putExtra("start_time", start_time);    //String
        sessionClose.putExtra("duration", duration);        //Long
        sessionClose.putExtra("schedule_id",schedule_id);   //Int
        startActivity(sessionClose);
    }

    public void reloadInterface() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSubscriber != null) {
                    attachSubscriberView(mSubscriber);
                }
            }
        }, 500);
    }

    private void sessionConnect() {
        Log.d("*** OpenTokConfig.API_KEY ***", API_KEY);
        Log.d("**** OpenTokConfig.SESSION_ID ****", SESSION_ID);
        Log.d("**** OpenTokConfig.TOKEN ****", TOKEN);

        if (mSession == null) {
            mSession = new Session(OpentokActivity.this, API_KEY, SESSION_ID);
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        }
    }

    @Override
    public void onConnected(Session session) {
        Log.i(LOGTAG, "Connected to the session.");

        Time now = new Time();
        now.setToNow();
        start_time = now.format("%k:%M:%S");

        start_time_SystemClock = SystemClock.uptimeMillis();
        if (mPublisher == null) {
            mPublisher = new Publisher(OpentokActivity.this, "publisher");
            mPublisher.setPublisherListener(this);
            attachPublisherView(mPublisher);
            mSession.publish(mPublisher);
        }
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOGTAG, "Disconnected from the session.");
        if (mPublisher != null) {
            mPublisherViewContainer.removeView(mPublisher.getView());
        }

        if (mSubscriber != null) {
            mSubscriberViewContainer.removeView(mSubscriber.getView());
        }

        mPublisher = null;
        mSubscriber = null;
        mStreams.clear();
        mSession = null;
    }

    private void subscribeToStream(Stream stream) {
        mSubscriber = new Subscriber(OpentokActivity.this, stream);
        mSubscriber.setVideoListener(this);
        mSession.subscribe(mSubscriber);
        // start loading spinning
        mLoadingSub.setVisibility(View.VISIBLE);
    }

    private void unsubscribeFromStream(Stream stream) {
        mStreams.remove(stream);
        if (mSubscriber.getStream().getStreamId().equals(stream.getStreamId())) {
            mSubscriberViewContainer.removeView(mSubscriber.getView());
            mSubscriber = null;
            if (!mStreams.isEmpty()) {
                subscribeToStream(mStreams.get(0));
            }
        }
    }

    private void attachSubscriberView(Subscriber subscriber) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels, getResources()
                .getDisplayMetrics().heightPixels);
        mSubscriberViewContainer.addView(mSubscriber.getView(), layoutParams);
        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
    }

    private void attachPublisherView(Publisher publisher) {
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                320, 240);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
                RelativeLayout.TRUE);
        layoutParams.bottomMargin = dpToPx(8);
        layoutParams.rightMargin = dpToPx(8);
        mPublisherViewContainer.addView(mPublisher.getView(), layoutParams);
    }

    @Override
    public void onError(Session session, OpentokError exception) {
        Log.i(LOGTAG, "Session exception: " + exception.getMessage());
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        if (!SUBSCRIBE_TO_SELF) {
            mStreams.add(stream);
            if (mSubscriber == null) {
                subscribeToStream(stream);
            }
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (!SUBSCRIBE_TO_SELF) {
            if (mSubscriber != null) {
                unsubscribeFromStream(stream);
            }
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisher, Stream stream) {
        if (SUBSCRIBE_TO_SELF) {
            mStreams.add(stream);
            if (mSubscriber == null) {
                subscribeToStream(stream);
            }
        }
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisher, Stream stream) {
        if ((SUBSCRIBE_TO_SELF && mSubscriber != null)) {
            unsubscribeFromStream(stream);
        }
    }

    @Override
    public void onError(PublisherKit publisher, OpentokError exception) {
        Log.i(LOGTAG, "Publisher exception: " + exception.getMessage());
    }
/*
    @Override
    public void onVideoDisabled(SubscriberKit subscriber) {
        Log.i(LOGTAG,
                "Video quality changed. It is disabled for the subscriber.");
    }
*/
    @Override
    public void onVideoDataReceived(SubscriberKit subscriber) {
        Log.i(LOGTAG, "First frame received");

        // stop loading spinning
        mLoadingSub.setVisibility(View.GONE);
        attachSubscriberView(mSubscriber);
    }

    @Override
    public void onVideoDisabled(SubscriberKit subscriberKit, String s) {
        Log.i(LOGTAG, "Video disabled:" + reason);
    }

    @Override
    public void onVideoEnabled(SubscriberKit subscriberKit, String s) {
        Log.i(LOGTAG,"Video enabled:" + reason);
    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriberKit) {
        Log.i(LOGTAG, "Video may be disabled soon due to network quality degradation. Add UI handling here.");
    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriberKit) {
        Log.i(LOGTAG, "Video may no longer be disabled as stream quality improved. Add UI handling here.");
    }

    /**
     * Converts dp to real pixels, according to the screen density.
     *
     * @param dp
     *            A number of density-independent pixels.
     * @return The equivalent number of real pixels.
     */
    private int dpToPx(int dp) {
        double screenDensity = this.getResources().getDisplayMetrics().density;
        return (int) (screenDensity * (double) dp);
    }

}

