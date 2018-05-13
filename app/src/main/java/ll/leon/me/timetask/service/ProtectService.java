package ll.leon.me.timetask.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import ll.leon.me.timetask.R;
import ll.leon.me.timetask.activity.MainActivity;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by Leon on 2017/6/14 0014.
 */

public class ProtectService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TaskProtectService", "服务启动");


        Notification notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;

        notification.tickerText = "后台守护";

        Intent openIntent = new Intent("android.intent.action.MAIN");
        openIntent.addCategory("android.intent.category.LAUNCHER");
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        openIntent.setComponent(componentName);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 16666, openIntent, FLAG_UPDATE_CURRENT);
        //同一个任务栈
        notification.contentIntent = pendingIntent;
        notification.contentView = new RemoteViews(getPackageName(), R.layout.item_notification);
        startForeground(16666, notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
