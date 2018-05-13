package ll.leon.me.timetask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import me.leon.rxbus.RxBus2;

public class AppReceiver extends BroadcastReceiver {
    private final String TAG = "TaskAppReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();

        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.d(TAG, "--------安装成功" + packageName);
//            Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.d(TAG, "--------替换成功" + packageName);
//            Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG).show();

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.d(TAG, "--------卸载成功" + packageName);
//            Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();

            RxBus2.getDefault().post(0);
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF)) {
            Log.d(TAG, "--------锁屏");
//            Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_SCREEN_ON)) {
            Log.d(TAG, "--------亮屏");
//            Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
        }
    }

}  