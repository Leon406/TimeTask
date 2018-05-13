package ll.leon.me.timetask;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.reactivestreams.Subscription;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ll.leon.me.timetask.realm.RealmDao;
import ll.leon.me.timetask.service.ProtectService;
import ll.leon.me.timetask.service.TaskService;
import me.leon.devsuit.android.AppUtils;
import me.leon.devsuit.android.CrashUtils;
import me.leon.devsuit.android.Utils;

public class App extends Application {
    public static Context ctx;
    public static Realm realm;
    public static List<AppUtils.AppInfo> appsInfo;
    public static boolean isStop;
    public static long INTERVAL = BuildConfig.DEBUG ? 15 * 1000 : 5 * 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        Utils.init(this);
        initAppList();
        initRealm();
        CrashUtils.init("/sdcard/crash");
        //极光推送
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);
        Intent protectService = new Intent(this, ProtectService.class);
        protectService.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        startService(protectService);
        Intent taskService = new Intent(this, TaskService.class);
        taskService.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        startService(taskService);

    }

    private void initRealm() {
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("app.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);
        RealmDao.setmRealm(realm);
    }

    private void initAppList() {

        Flowable.just(0)
                .doOnSubscribe(this::getAppList)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void getAppList(Subscription subscription) {
        Log.d("ThreadInfo", Thread.currentThread().getName());
        appsInfo = AppUtils.getAppsInfo();
    }
}
