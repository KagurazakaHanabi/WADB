package com.yaerin.wadb;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.support.annotation.RequiresApi;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.yaerin.wadb.App.getIpAddress;
import static com.yaerin.wadb.App.getServicePort;
import static com.yaerin.wadb.App.isActivated;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TileService extends android.service.quicksettings.TileService {

    @Override
    public void onTileAdded() {
        updateTile();
    }

    @Override
    public void onStartListening() {
        updateTile();
    }

    @Override
    public void onClick() {
        getQsTile().setState(Tile.STATE_UNAVAILABLE);
        getQsTile().updateTile();
        try {
            Process proc = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(proc.getOutputStream());
            if (!isActivated()) {
                os.writeBytes(String.format("setprop service.adb.tcp.port %s\n", getServicePort()));
            } else {
                os.writeBytes("setprop service.adb.tcp.port -1\n");
            }
            os.writeBytes("stop adbd\n");
            os.writeBytes("start adbd\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(() -> {
            updateTile();
            sendBroadcast(new Intent("com.yaerin.intent.ADB_STATE"));
        }, 500);
    }

    private void updateTile() {
        boolean b = isActivated();
        getQsTile().setState(b ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().setIcon(b ?
                Icon.createWithResource(this, R.drawable.ic_qs_network_adb_on) :
                Icon.createWithResource(this, R.drawable.ic_qs_network_adb_off)
        );
        getQsTile().setLabel(b ? getIpAddress(this) : getString(R.string.app_name));
        getQsTile().updateTile();
    }
}
