package ll.leon.me.timetask.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Flowable;
import ll.leon.me.timetask.App;
import ll.leon.me.timetask.Constant;
import ll.leon.me.timetask.R;
import ll.leon.me.timetask.service.TaskService;
import ll.leon.me.timetask.base.recyclerview.ApplicationInfo;
import ll.leon.me.timetask.realm.RealmDao;
import ll.leon.me.timetask.util.AutoStarSettting;
import me.leon.devsuit.android.AppUtils;
import me.leon.devsuit.android.KeyboardUtils;
import me.leon.devsuit.android.SPUtils;
import me.leon.rxbus.RxBus2;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnsetApp;
    private EditText etHour;
    private Button btnSetTime;
    private EditText etNum;
    private Button btnSetNum;
    private Button btnStarttask;
    private Button btnStoptask;

    private int count;
    private StringBuilder sb;
    private int min;
    private int hour;
    private TextView tvDetail;
    private String appList;
    private EditText etMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewAndListener();
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        // I can control the camera now
                    } else {
                        // Oups permission denied
                        AppUtils.getAppDetailsSettings();
                    }
                });

        sb = new StringBuilder();
        count = SPUtils.getInstance().getInt(Constant.COUNT, 3);
        hour = SPUtils.getInstance().getInt(Constant.HOUR, 12);
        min = SPUtils.getInstance().getInt(Constant.MIN, 0);
        setAppList("");
        tvDetail.setMovementMethod(new ScrollingMovementMethod());
        etNum.setText(String.valueOf(count));
        etHour.setText(getTimeString(hour, true));
        etMin.setText(getTimeString(min, false));
        etHour.clearFocus();
        etNum.clearFocus();
        KeyboardUtils.hideSoftInput(this);

        RxBus2.getDefault().registerEvent(String.class)
                .subscribe(this::setAppList);

        boolean aBoolean = SPUtils.getInstance().getBoolean(Constant.START, false);
        if (aBoolean) {
            startService(new Intent(this, TaskService.class));
        }

    }

    private void initViewAndListener() {
        btnStoptask = (Button) findViewById(R.id.btn_stop_task);
        btnStarttask = (Button) findViewById(R.id.btn_start_task);
        btnSetNum = (Button) findViewById(R.id.btn_setNum);
        etNum = (EditText) findViewById(R.id.et_num);
        btnSetTime = (Button) findViewById(R.id.btn_setTime);
        etHour = (EditText) findViewById(R.id.et_hour);
        etMin = (EditText) findViewById(R.id.et_min);
        btnsetApp = (Button) findViewById(R.id.btn_setApp);
        tvDetail = (TextView) findViewById(R.id.tv_detail);

        findViewById(R.id.btn_auto_start).setOnClickListener(AutoStarSettting::jumpStartInterface);

        btnStoptask.setOnClickListener(this);
        btnStarttask.setOnClickListener(this);
        btnSetNum.setOnClickListener(this);
        btnSetTime.setOnClickListener(this);
        btnsetApp.setOnClickListener(this);
    }


    private void logInfo() {
        sb.delete(0, sb.length());
        sb.append("设置信息:")
                .append("\n\n\t启动APP列表:\n")
                .append(appList)
                .append("\n\n\t定时时间:").append("\n\t\t" + getTimeString(hour, true))
                .append(" : " + getTimeString(min, false))
                .append("\n\n\t启动APP数量:").append("\n\t\t" + count);
        tvDetail.setText(sb.toString());
    }

    private String getTimeString(int aInt, boolean isHour) {
        int time = isHour ? aInt > 24 ? 24 : aInt
                : aInt > 60 ? 60 : aInt;
        return time < 10 ? "0" + time : String.valueOf(time);

    }

    private int getTimeInt(String aSting, boolean isHour) {
        if (TextUtils.isEmpty(aSting)) {
            return isHour ? 12 : 0;
        }
        int aInt = Integer.parseInt(aSting);
        return isHour ? aInt > 23 ? 12 : aInt
                : aInt > 59 ? 00 : aInt;
    }

    private void setAppList(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        List<ApplicationInfo> applicationInfos = RealmDao.queryAll(ApplicationInfo.class);
        Flowable.fromIterable(applicationInfos)
                .map(it -> {
                    stringBuilder.append("\n\t\t\t\t").append(it.appName);
                    return it.appName;
                })
                .toList()
                .subscribe(list -> {
                    appList = stringBuilder.toString();
                    logInfo();
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stop_task:

                App.isStop = true;
                SPUtils.getInstance().put(Constant.START, false);

                Toast.makeText(this, "停止任务", Toast.LENGTH_SHORT).show();
                stopService(new Intent(this, TaskService.class));

                break;
            case R.id.btn_start_task:
                App.isStop = false;
                SPUtils.getInstance().put(Constant.START, true);
                Toast.makeText(this, "已启动服务,可退出到后台", Toast.LENGTH_SHORT).show();
                startService(new Intent(this, TaskService.class));
                SPUtils.getInstance().put("info", sb.toString());
                break;
            case R.id.btn_setNum:
                String trim = etNum.getText().toString().trim();
                count = Integer.parseInt(trim);
                SPUtils.getInstance().put(Constant.COUNT, count);
                break;
            case R.id.btn_setTime:

                hour = getTimeInt(etHour.getText().toString().trim(), true);
                min = getTimeInt(etMin.getText().toString().trim(), false);
                etHour.setText(getTimeString(hour, true));
                etMin.setText(getTimeString(min, false));
                SPUtils.getInstance().put(Constant.HOUR, hour);
                SPUtils.getInstance().put(Constant.MIN, min);
                break;
            case R.id.btn_setApp:
                AppInfoActivity.start(this);
                break;
        }
        logInfo();
        KeyboardUtils.hideSoftInput(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus2.getDefault().unregister();
    }
}
