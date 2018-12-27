package com.yaerin.wadb;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.service.quicksettings.Tile;

import static com.yaerin.wadb.Utilities.INTENT_ACTION_ADB_STATE;
import static com.yaerin.wadb.Utilities.getIpAddress;
import static com.yaerin.wadb.Utilities.isActivated;
import static com.yaerin.wadb.Utilities.setWADBState;

@TargetApi(Build.VERSION_CODES.N)
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
        setWADBState(!isActivated());
        new Handler().postDelayed(() -> {
            updateTile();
            sendBroadcast(new Intent(INTENT_ACTION_ADB_STATE));
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
