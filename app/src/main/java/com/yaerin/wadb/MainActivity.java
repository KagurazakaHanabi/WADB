package com.yaerin.wadb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yaerin on 12/7/17.
 */

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Switch mSwitch;
    private TextView mTextView;

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
                    os.writeBytes(String.format("setprop service.adb.tcp.port %s\n", getPort()));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            try {
                Process proc = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(proc.getOutputStream());
                os.writeBytes("getprop service.adb.tcp.port\n");
                os.flush();
                os.close();
                InputStreamReader reader = new InputStreamReader(proc.getInputStream());
                char[] chars = new char[5];
                reader.read(chars);
                reader.close();
                String result = new String(chars);
                runOnUiThread(() -> {
                    if (!result.matches("[0-9]+\\n")) {
                        setChecked(false);
                    } else {
                        setChecked(!result.contains("-1"));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setChecked(boolean checked) {
        mSwitch.setChecked(checked);
        mSwitch.setText(checked ? R.string.enabled : R.string.disabled);
        mTextView.setText(checked ?
                getString(R.string.help) + getString(R.string.help_text, getAddress(), getPort()) :
                getString(R.string.help)
        );
    }

    private String getPort() {
        return "5555";
    }

    private String getAddress() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        int i = info.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 24) & 0xFF);
    }

    private void showDonateDialog() {
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.mm_reward_qrcode_1519050412694);
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel())
                .setView(iv)
                .create()
                .show();
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
                .setNeutralButton(R.string.donate, (dialog, which) -> showDonateDialog())
                .create()
                .show();
        return true;
    }
}
