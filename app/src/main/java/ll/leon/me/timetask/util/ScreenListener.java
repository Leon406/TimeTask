package ll.leon.me.timetask.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import ll.leon.me.timetask.receiver.ScreenBroadcastReceiver;

public class ScreenListener {
    private Context mContext;
    private ScreenBroadcastReceiver receiver;


    public ScreenListener(Context context) {
        mContext = context;
        receiver = new ScreenBroadcastReceiver();
    }

    public void register() {
        if (receiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(receiver, filter);
        }
    }

    public void unregister() {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }
}