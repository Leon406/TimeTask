package ll.leon.me.timetask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import ll.leon.me.timetask.BuildConfig;
import ll.leon.me.timetask.Constant;
import ll.leon.me.timetask.service.TaskService;
import me.leon.devsuit.android.SPUtils;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        boolean aBoolean = SPUtils.getInstance().getBoolean(Constant.START, false);
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, "AlarmReceiver:  " + aBoolean, Toast.LENGTH_SHORT).show();
        }
        Log.d("TaskAlarmReceiver", "reboot" + aBoolean);
        if (aBoolean) {
            Intent i = new Intent(context, TaskService.class);
            context.startService(i);
        }
    }
}