package com.yaerin.wadb;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import static android.content.Context.WIFI_SERVICE;

class Utilities {

    static boolean isActivated() {
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
            proc.destroy();
            String result = new String(chars);
            return result.matches("[0-9]+\\n") && !result.contains("-1");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    static String getIpAddress(Context context) {
        WifiManager wm = ((WifiManager) context.getApplicationContext()
                .getSystemService(WIFI_SERVICE));
        if (wm != null) {
            int i = wm.getConnectionInfo().getIpAddress();
            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    i & 0xFF, (i >> 8) & 0xFF, (i >> 16) & 0xFF, (i >> 24) & 0xFF);
        } else {
            return "0.0.0.0";
        }
    }

    static String getServicePort() {
        return "5555";
    }
}
