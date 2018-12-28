package com.yaerin.wadb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import static com.yaerin.wadb.Utilities.PREF_AUTO_RUN;
import static com.yaerin.wadb.Utilities.getIpAddress;
import static com.yaerin.wadb.Utilities.getServicePort;
import static com.yaerin.wadb.Utilities.setWadbState;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) && pref.getBoolean(PREF_AUTO_RUN, false)) {
            if (setWadbState(true)) {
                String text = context.getString(R.string.adb_running, getIpAddress(context), getServicePort());
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        }
    }
}