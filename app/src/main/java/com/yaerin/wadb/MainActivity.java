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
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by yaerin on 12/7/17.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.state).setOnClickListener((v) -> {
            try {
                String ip = getAddress();
                String port = getPort();
                Process proc = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(proc.getOutputStream());
                if (((Switch) v).isChecked()) {
                    os.writeBytes("setprop service.adb.tcp.port 5555\n");
                    os.writeBytes("stop adbd\n");
                    os.writeBytes("start adbd\n");
                } else {
                    os.writeBytes("setprop service.adb.tcp.port -1\n");
                    os.writeBytes("stop adbd\n");
                    os.writeBytes("start adbd\n");
                }
                os.flush();
                ((Switch) v).setText(((Switch) v).isChecked() ? R.string.enabled : R.string.disabled);
                ((TextView) findViewById(R.id.textView)).setText(((Switch) v).isChecked() ?
                        getString(R.string.help) + getString(R.string.help_text, ip, port) :
                        getString(R.string.help)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String getPort() {
        return "5555";
    }

    private String getAddress() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
        }
        WifiInfo info = manager.getConnectionInfo();
        int i = info.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 24) & 0xFF);
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
                .setNegativeButton(R.string.open_source, (p1, p2) ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Yaerin/WADB"))))
                .setPositiveButton(R.string.ok, (p1, p2) ->
                        p1.dismiss())
                .create()
                .show();
        return true;
    }
}
