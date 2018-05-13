package ll.leon.me.timetask.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import ll.leon.me.timetask.App;
import ll.leon.me.timetask.AppExecuteUtil;
import ll.leon.me.timetask.BuildConfig;
import ll.leon.me.timetask.Constant;
import ll.leon.me.timetask.base.recyclerview.ApplicationInfo;
import ll.leon.me.timetask.realm.RealmDao;
import ll.leon.me.timetask.receiver.AlarmReceiver;
import ll.leon.me.timetask.util.ScreenListener;
import me.leon.devsuit.android.SPUtils;


public class TaskService extends Service {

    public static final String TAG = "TaskService";
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ScreenListener screenListener;

    public TaskService() {
    }

    private boolean isFirstLaunch;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean aBoolean = SPUtils.getInstance().getBoolean(Constant.START, false);
        Log.d(TAG, "============onStart========= " +aBoolean);
        if (screenListener == null) {
            screenListener = new ScreenListener(this);
        }

        screenListener.register();
        doOnThisMethod();

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        int anHour = 60 * 60 * 1000; // 这是一小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + App.INTERVAL;

        Intent i = new Intent(this, AlarmReceiver.class);
        i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void doOnThisMethod() {
        List<ApplicationInfo> applicationInfos = RealmDao.queryAll(ApplicationInfo.class);
        new Thread(new Runnable() {
            @Override
            public void run() {

                int hour = SPUtils.getInstance().getInt(Constant.HOUR);
                int min = SPUtils.getInstance().getInt(Constant.MIN);
                int cout = SPUtils.getInstance().getInt(Constant.COUNT, 0);
                Date date = new Date();

                Date specificDate = new Date(date.getYear(), date.getMonth(), date.getDate(), hour, min);
                Log.d("TaskService", "time: \t" + sdf.format(date)
                        + "\nSpecificTime: \t" + sdf.format(specificDate)
                );


                long deltaMilli = Math.abs(specificDate.getTime() - date.getTime());


                if (hour == date.getHours() && deltaMilli < App.INTERVAL * 0.6) {
                    Log.d("TaskService", "============TimeMatch========= ");
                    Log.d("TaskService", "executed task ");
                    AppExecuteUtil.exeCmd(applicationInfos, cout);
                } else {
                    Log.d("TaskService", "thresHold : " + App.INTERVAL * 0.6
                            + "\tdeltaMin : " + deltaMilli

                    );
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        screenListener.unregister();
        Log.d(TAG, "============onDestroy========= ");
    }
}
