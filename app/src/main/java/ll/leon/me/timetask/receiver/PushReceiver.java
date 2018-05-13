package ll.leon.me.timetask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import ll.leon.me.timetask.activity.MainActivity;
import ll.leon.me.timetask.service.TaskService;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Author:  Leon
 * Desc:    自定义推送接受器
 */

public class PushReceiver extends BroadcastReceiver {

    private static final String TAG = "TaskPushReceiver";

    @Override
    public void onReceive(Context context, Intent data) {
        Log.d(TAG, "action : \t" + data.getAction());
        if (data.getAction().equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }

        if (data.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            Bundle bundle = data.getExtras();

            processCustomMessage(context, bundle);
        }

        context.startService(new Intent(context, TaskService.class));

    }


    private void processCustomMessage(Context context, Bundle bundle) {
//        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);

        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String key1 = null, key2 = null, key3 = null;

        if (extras != null) {
            JSONObject extrasJson = null;
            try {
                extrasJson = new JSONObject(extras);

                key1 = extrasJson.optString(Command.KEY1);
                key2 = extrasJson.optString(Command.KEY2);
                key3 = extrasJson.optString(Command.KEY3);
                Log.d(TAG, "extrasJson:" + extrasJson);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        Log.d("TaskCMD", "command : " + message
                + "\n " + Command.KEY1 + " : " + key1
                + "\n " + Command.KEY2 + " : " + key2
                + "\n " + Command.KEY3 + " : " + key3

        );


    }


}
