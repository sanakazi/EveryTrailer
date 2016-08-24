package com.aurum.everytrailer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pushwoosh.BasePushMessageReceiver;

/**
 * Created by VarunBarve on 12/3/2015.
 */
public class SilentPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Pushwoosh", "[SilentPushReceiver] " + intent.getStringExtra(BasePushMessageReceiver.JSON_DATA_KEY));

        Log.d("dileep.......", "" + intent.getStringExtra(BasePushMessageReceiver.JSON_DATA_KEY));
    }
}
