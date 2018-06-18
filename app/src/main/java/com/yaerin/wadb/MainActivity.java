package com.yaerin.wadb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.yaerin.wadb.Utilities.getIpAddress;
import static com.yaerin.wadb.Utilities.getServicePort;
import static com.yaerin.wadb.Utilities.isActivated;

/**
 * Created by yaerin on 12/7/17.
 */

public class MainActivity extends Activity {

    private Switch mSwitch;
    private TextView mTextView;

    private StateReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwitch = findViewById(R.id.state);
        mTextView = findViewById(R.id.textView);
        mSwitch.setOnClickListener(v -> {
            try {
                Process proc = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(proc.getOutputStream());
                if (mSwitch.isChecked()) {
                    os.writeBytes(String.format("setprop service.adb.tcp.port %s\n", getServicePort()));
                } else {
                    os.writeBytes("setprop service.adb.tcp.port -1\n");
                }
                os.writeBytes("stop adbd\n");
                os.writeBytes("start adbd\n");
                os.flush();
                setChecked(mSwitch.isChecked());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mReceiver = new StateReceiver();
        registerReceiver(mReceiver, new IntentFilter("com.yaerin.intent.ADB_STATE"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setChecked(isActivated());
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
        mTextView.setText(checked ?
                getString(R.string.help) + getString(R.string.help_text, getIpAddress(this), getServicePort()) :
                getString(R.string.help)
        );
    }

    private class StateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setChecked(isActivated());
        }
    }
}
