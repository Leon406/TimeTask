package ll.leon.me.timetask;

import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ll.leon.me.timetask.base.recyclerview.ApplicationInfo;
import me.leon.devsuit.android.AppUtils;

public class AppExecuteUtil {


    public static void exeCmd(List<ApplicationInfo> list, int count) {

        if (list != null) {
            if (count < 1 || count > list.size()) {
                count = list.size();
            }
            if (BuildConfig.DEBUG) Log.d("TaskUtil", "launch App \t\t" + count);
            Collections.shuffle(list);
            Flowable.fromIterable(list)
                    .map(it -> it.packageName)
                    .take(count)
                    .delay(3, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(AppExecuteUtil::launchApp, Throwable::printStackTrace);
        }


    }


    private static void launchApp(String s) {

        if (BuildConfig.DEBUG) Log.d("TaskUtil", "launch App" + s);
        AppUtils.launchApp(s);


    }
}
