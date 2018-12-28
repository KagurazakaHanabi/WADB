package com.yaerin.wadb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import static com.yaerin.wadb.Utilities.INTENT_ACTION_ADB_STATE;
import static com.yaerin.wadb.Utilities.PREF_AUTO_RUN;
import static com.yaerin.wadb.Utilities.getIpAddress;
import static com.yaerin.wadb.Utilities.getServicePort;
import static com.yaerin.wadb.Utilities.isActivated;
import static com.yaerin.wadb.Utilities.setWadbState;

/**
 * Created by yaerin on 12/7/17.
 */

public class MainActivity extends Activity {

    private Switch mSwitch;
    private TextView mTextView;

    private StateReceiver mReceiver = new StateReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwitch = findViewById(R.id.state);
        mTextView = findViewById(R.id.textView);
        mSwitch.setOnCheckedChangeListener((v, checked) -> {
            if (setWadbState(checked)) {
                setChecked(checked);
            }
        });
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Switch autoRun = findViewById(R.id.autoRun);
        autoRun.setOnCheckedChangeListener((v, checked) -> pref.edit().putBoolean(PREF_AUTO_RUN, checked).apply());
        autoRun.setChecked(pref.getBoolean(PREF_AUTO_RUN, false));
        registerReceiver(mReceiver, new IntentFilter(INTENT_ACTION_ADB_STATE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            boolean activated = isActivated();
            runOnUiThread(() -> setChecked(activated));
        }).start();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle(R.string.about)
                .setMessage(R.string.about_text)
                .setNegativeButton(R.string.open_source, (dialog, which) ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Yaerin/WADB"))))
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel())
                .create()
                .show();
        return true;
    }

    private void setChecked(boolean checked) {
        mSwitch.setChecked(checked);
        mSwitch.setText(checked ? R.string.enabled : R.string.disabled);
        mTextView.setText(checked
                ? getString(R.string.help) + getString(R.string.help_text, getIpAddress(this), getServicePort())
                : getString(R.string.help)
        );
    }

    private class StateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setChecked(isActivated());
        }
    }
}
