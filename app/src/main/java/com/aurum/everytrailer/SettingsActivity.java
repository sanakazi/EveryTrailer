package com.aurum.everytrailer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;

public class SettingsActivity extends AppCompatActivity {

    public static Switch switchButton;
    public static PushManager pushManager;
    boolean broadcastPush = true;
    int flag = 0;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    boolean isNotificationOn = false;

    TextView mTermsAndConditions, mChangePassword;

    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver() {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent) {
//            checkMessage(intent);
        }
    };
    //Push message receiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
        @Override
        protected void onMessageReceive(Intent intent) {
            //JSON_DATA_KEY contains JSON payload of push notification.
//            doOnMessageReceive(intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settingactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        editor = sharedpreferences.edit();

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

        //check launch notification (optional)
        String launchNotificatin = pushManager.getLaunchNotification();
        if (launchNotificatin != null) {
            Log.d("Pushwoosh", "Launch notification received: " + launchNotificatin);
        } else {
            Log.d("Pushwoosh", "No launch notification received");
        }

//        checkMessage(getIntent());

        switchButton = (Switch) findViewById(R.id.switchButton);
        mTermsAndConditions = (TextView) findViewById(R.id.terms_and_conditions_settings);
        mChangePassword = (TextView) findViewById(R.id.change_password_setting);

        isNotificationOn = sharedpreferences.getBoolean("isNotificationOn", false);

        switchButton.setChecked(!isNotificationOn);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    //textView.setText(switchOn);
                    registerReceivers();
                    pushManager.registerForPushNotifications();
                    Log.e("ON", "ON");
                    editor.putBoolean("isNotificationOn", false);
                    editor.commit();
                } else {
                    // textView.setText(switchOff);
                    unregisterReceivers();
                    pushManager.unregisterForPushNotifications();
                    Log.e("OFF", "OFF");
                    editor.putBoolean("isNotificationOn", true);
                    editor.commit();
                }
            }
        });

        if (switchButton.isChecked()) {
            //textView.setText(switchOn);

            //textView.setText(switchOn);
            registerReceivers();
            pushManager.registerForPushNotifications();

            Log.e("ON", "ON");

        } else {
            //textView.setText(switchOff);

            // textView.setText(switchOff);
            unregisterReceivers();
            pushManager.unregisterForPushNotifications();
            Log.e("OFF", "OFF");
        }

        mTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
            }
        });

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
