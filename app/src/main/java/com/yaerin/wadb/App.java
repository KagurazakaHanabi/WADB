package com.yaerin.wadb;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;

import com.yaerin.support.util.Crashlytics;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Crashlytics(this));
    }

    public static boolean isActivated() {
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

    public static String getIpAddress(Context context) {
        try {
            int i = ((WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE))
                    .getConnectionInfo()
                    .getIpAddress();
            return (i & 0xFF) + "." +
                    ((i >> 8) & 0xFF) + "." +
                    ((i >> 16) & 0xFF) + "." +
                    ((i >> 24) & 0xFF);
        } catch (Exception e) {
            return "0.0.0.0";
        }
    }

    public static String getServicePort() {
        return "5555";
    }
}
