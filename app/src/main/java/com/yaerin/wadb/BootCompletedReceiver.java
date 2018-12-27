package com.yaerin.wadb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) &&
                Utilities.configManager(context, Utilities.CONFIG_KEY_AUTO_RUN, false, true)) {
            Utilities.setWADBState(true);
        }
    }
}