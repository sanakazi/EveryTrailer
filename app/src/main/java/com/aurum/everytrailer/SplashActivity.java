package com.aurum.everytrailer;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.aurum.everytrailer.utils.ConnectionDetector;
import com.aurum.everytrailer.utils.Constants;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity {

    PushManager pushManager;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    boolean isNotificationOn = false;

    //Registration receiver
    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver() {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent) {
//            checkMessage(intent);
        }
    };
    ConnectionDetector connectionDetector;
    boolean isLocalSignedIn = false, isSocialSignedIn = false;
    boolean broadcastPush = true;
    //Push message receiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
        @Override
        protected void onMessageReceive(Intent intent) {
            //JSON_DATA_KEY contains JSON payload of push notification.
            //doOnMessageReceive(intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.TWITTER_KEY), getResources().getString(R.string.TWITTER_SECRET));
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_splash);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);

        isLocalSignedIn = sharedpreferences.getBoolean("isLocalSignedIn", false);
        isSocialSignedIn = sharedpreferences.getBoolean("isSocialSignedIn", false);

        isNotificationOn = sharedpreferences.getBoolean("isNotificationOn", false);

        pushManager = PushManager.getInstance(this);
        pushManager.setMultiNotificationMode(this);
        class RichPageListenerImpl implements PushManager.RichPageListener {
            @Override
            public void onRichPageAction(String actionParams) {
                Log.d("Pushwoosh", "Rich page action: " + actionParams);
            }

            @Override
            public void onRichPageClosed() {
                Log.d("Pushwoosh", "Rich page closed");
            }
        }

        pushManager.setRichPageListener(new RichPageListenerImpl());

        //Start push manager, this will count app open for Pushwoosh stats as well
        try {
            pushManager.onStartup(this);
        } catch (Exception e) {
            Log.e("Pushwoosh", e.getLocalizedMessage());
        }

        if (!isNotificationOn) {
            registerReceivers();
            //Register for push!
            pushManager.registerForPushNotifications();
        } else {
            unregisterReceivers();
            //Register for push!
            pushManager.unregisterForPushNotifications();
        }

        //check launch notification (optional)
        String launchNotificatin = pushManager.getLaunchNotification();
        if (launchNotificatin != null) {
            Log.d("Pushwoosh", "Launch notification received: " + launchNotificatin);
        } else {
            Log.d("Pushwoosh", "No launch notification received");
        }

//        checkMessage(getIntent());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // CONNECTION DETECTOR
                AsyncTask<Void, Boolean, Boolean> connectionDetectorTask = new ConnectionDetector()
                        .execute();

                boolean result = false;
                try {
                    result = connectionDetectorTask.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (result) {
                    if (isLocalSignedIn || isSocialSignedIn) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    final Dialog query_alert = new Dialog(
                            SplashActivity.this);
                    query_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    query_alert.setContentView(R.layout.customlayoutok);
                    query_alert.setCancelable(false);
                    TextView txtTextView = (TextView) query_alert
                            .findViewById(R.id.customalert);
                    txtTextView.setText("Opps! Looks like you don't have an internet connection.");
                    Button track_ok = (Button) query_alert.findViewById(R.id.ok_id);

                    track_ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            finish();
                            query_alert.dismiss();
                        }
                    });
                    query_alert.show();
                }
            }
        }, 1000);

        printHashKey();
    }

    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.aurum.everytrailer",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e(Constants.TAG, "KEYHASH: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constants.TAG, "NAME NOT FOUND: " + e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(Constants.TAG, "NO SUCH ALGORITHM: " + e);
        }
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //have to check if we've got new intent as a part of push notification
//        checkMessage(intent);
    }

    //Registration of the receivers
    public void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

        if (broadcastPush)
            registerReceiver(mReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);

        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers() {
        //Unregister receivers on pause
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            // pass.
        }

        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            //pass through
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //Re-register receivers on resume
        registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Unregister receivers on pause
        unregisterReceivers();
    }


    /**
     * Will check PushWoosh extras in this intent, and fire actual method
     *
     * @param intent activity intent
     */
    private void checkMessage(Intent intent) {
        if (null != intent) {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
                doOnMessageReceive(intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            } else if (intent.hasExtra(PushManager.REGISTER_EVENT)) {
                doOnRegistered(intent.getExtras().getString(PushManager.REGISTER_EVENT));
            } else if (intent.hasExtra(PushManager.UNREGISTER_EVENT)) {
                doOnUnregistered(intent.getExtras().getString(PushManager.UNREGISTER_EVENT));
            } else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
                doOnRegisteredError(intent.getExtras().getString(PushManager.REGISTER_ERROR_EVENT));
            } else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
                doOnUnregisteredError(intent.getExtras().getString(PushManager.UNREGISTER_ERROR_EVENT));
            }

            resetIntentValues();
        }
    }

    public void doOnRegistered(String registrationId) {
        // mGeneralStatus.setText(getString(R.string.registered, registrationId));
        Log.e("DEVICE ID : ", registrationId);
    }

    public void doOnRegisteredError(String errorId) {
        //  mGeneralStatus.setText(getString(R.string.registered_error, errorId));
    }

    public void doOnUnregistered(String registrationId) {
        //   mGeneralStatus.setText(getString(R.string.unregistered, registrationId));
    }

    public void doOnUnregisteredError(String errorId) {
        //   mGeneralStatus.setText(getString(R.string.unregistered_error, errorId));
    }

    public void doOnMessageReceive(String message) {
    }

    /**
     * Will check main Activity intent and if it contains any PushWoosh data, will clear it
     */
    private void resetIntentValues() {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
