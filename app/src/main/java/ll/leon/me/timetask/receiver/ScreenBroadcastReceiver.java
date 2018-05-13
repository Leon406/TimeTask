package ll.leon.me.timetask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ll.leon.me.timetask.service.TaskService;

public class ScreenBroadcastReceiver extends BroadcastReceiver {


    public static final String TAG = "TaskScreenReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {

                Log.e(TAG, "ScreenBroadcastReceiver --> ACTION_SCREEN_ON");

            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {

                Log.e(TAG, "ScreenBroadcastReceiver --> ACTION_SCREEN_OFF");

            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {

                Log.e(TAG, "ScreenBroadcastReceiver --> ACTION_USER_PRESENT");

            }

            Intent service = new Intent(context, TaskService.class);
            service.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startService(service);

        }
    }
}

