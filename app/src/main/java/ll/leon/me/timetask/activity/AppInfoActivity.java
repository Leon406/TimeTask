package ll.leon.me.timetask.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import ll.leon.me.timetask.App;
import ll.leon.me.timetask.BuildConfig;
import ll.leon.me.timetask.R;
import ll.leon.me.timetask.base.recyclerview.AbsAdapter;
import ll.leon.me.timetask.base.recyclerview.ApplicationInfo;
import ll.leon.me.timetask.base.recyclerview.BaseHolderHelper;
import ll.leon.me.timetask.realm.RealmDao;
import me.leon.devsuit.android.AppUtils;
import me.leon.rxbus.RxBus2;

/**
 * Created by Leon on 2017/6/10 0010.
 */

public class AppInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private AbsAdapter<ApplicationInfo> adapter;
    private List<ApplicationInfo> allApps;

    public static void start(Context context) {
        Intent starter = new Intent(context, AppInfoActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    Button btnUnlock;
    Button btnLock;
    TextView tvLockTitle;
    RecyclerView rv;

    private List<ApplicationInfo> list = new ArrayList<>();
    private List<ApplicationInfo> startLists = new ArrayList<>();
    private List<ApplicationInfo> appLists = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_lock_mgr);

        tvLockTitle = findViewById(R.id.tv_lock_title);
        btnLock = findViewById(R.id.btn_lock);
        btnUnlock = findViewById(R.id.btn_unlock);
        btnUnlock.setSelected(true);
        btnUnlock.setOnClickListener(this);
        btnLock.setOnClickListener(this);

        list = updateList();
        startLists = RealmDao.queryAll(ApplicationInfo.class);
        Flowable.fromIterable(list)
                .filter(item -> !isContain(item, startLists))
                .toList()
                .subscribe(appList -> {
                    list = appList;
                    appLists.addAll(list);
                });

        RxBus2.getDefault().registerEvent(Integer.class).subscribe(this::uninsallUpdate);

        tvLockTitle.setText("可选应用数量(" + appLists.size() + ")");

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AbsAdapter<ApplicationInfo>(list, R.layout.item_app) {
            @Override
            protected void convert(BaseHolderHelper holder, ApplicationInfo data, int position) {

                holder.getView(R.id.item).setOnLongClickListener(v -> {
                    Toast.makeText(AppInfoActivity.this, data.appName, Toast.LENGTH_SHORT).show();
                    return true;
                });

                holder.setText(R.id.tv_app_name, data.appName + "--" + data.packageName);
                holder.setText(R.id.tv_version, data.versionName);


                ((ImageView) holder.getView(R.id.iv_logo)).setImageDrawable(AppUtils.getAppIcon(data.packageName));


                holder.setText(R.id.btnUpgrade, data.isRemote ? "删除" : "添加");

                holder.setVisiable(R.id.btnOpen, !data.isRemote);
                holder.setVisiable(R.id.btnDelete, !data.isRemote);

                holder.getView(R.id.btnOpen).setOnClickListener(v -> {
                    if (!AppInfoActivity.this.getPackageName().equals(data.packageName)) {

                        try {
                            AppUtils.launchApp(data.packageName);

                        } catch (Exception e) {
                            Log.d("ErrorInfo", e.getMessage());
                        }
                        adapter.notifyItemChanged(position);
                    }


                });
                holder.getView(R.id.btnDelete).setOnClickListener(v -> {
                    if (!AppInfoActivity.this.getPackageName().equals(data.packageName)) {

                        AppUtils.uninstallApp(data.packageName);
                        curApp = data;
                    }
                });

                holder.getView(R.id.btnUpgrade).setOnClickListener(v -> {
                    if (!AppInfoActivity.this.getPackageName().equals(data.packageName)) {

                        handleData(data);
                        adapter.notifyDataSetChanged();


                    }
                });


            }
        };
        rv.setAdapter(adapter);
    }

    private void uninsallUpdate(Integer integer) {
        if (BuildConfig.DEBUG) Log.d("AppReceiver", "AppReceiver ");
        appLists.remove(curApp);
        list.remove(curApp);
        adapter.notifyDataSetChanged();
        tvLockTitle.setText("可选应用数量(" + appLists.size() + ")");
    }

    private ApplicationInfo curApp;

    private boolean isContain(ApplicationInfo item, List<ApplicationInfo> startLists) {
        AtomicBoolean isContain = new AtomicBoolean(false);
        Flowable.fromIterable(startLists)
                .filter(it -> TextUtils.equals(it.packageName, item.packageName))
                .subscribe(it -> isContain.set(true));
        return isContain.get();
    }

    private void handleData(ApplicationInfo data) {
        int size = !data.isRemote ? appLists.size() - 1 : startLists.size() - 1;
        tvLockTitle.setText(!data.isRemote ? "可选应用数量(" + size + ")"
                : "启动应用数量(" + size + ")");
        try {
            if (data.isRemote) { //删除
                RealmDao.mRealm.executeTransaction(realm -> data.isRemote = false);
                startLists.remove(data);
                RealmDao.deleteItem(data);
//                data.isRemote = false;
                appLists.add(data);
                list.remove(data);
            } else {  //添加
                data.isRemote = true;
                startLists.add(data);
                RealmDao.insertOrUpdate(data);
                appLists.remove(data);
                list.remove(data);
            }

        } catch (Exception e) {
            Toast.makeText(this, "删除失败,请退出该页面后重新再试", Toast.LENGTH_SHORT).show();

            size = size + 1;
            Log.d("AppInfoActivity", e.getMessage());
            tvLockTitle.setText(!data.isRemote ? "可选应用数量(" + size + ")"
                    : "启动应用数量(" + size + ")");
        }

    }

    @NonNull
    private List<ApplicationInfo> updateList() {
        List<AppUtils.AppInfo> appsInfo = App.appsInfo;
        if (appsInfo == null) {
            appsInfo = AppUtils.getAppsInfo();
        }


        List<ApplicationInfo> systemApps = new ArrayList<>();
        List<ApplicationInfo> userApps = new ArrayList<>();

        Observable.fromIterable(appsInfo)
                .map(info -> new ApplicationInfo(info))
                .subscribe(app -> {
                    if (app.isSystem) {
                        systemApps.add(app);
                    } else {
                        userApps.add(app);
                    }
                }, Throwable::printStackTrace);


        allApps = new ArrayList<>();

        allApps.addAll(userApps);

        Observable.fromIterable(allApps)
                .sorted((o1, o2) -> o1.appName.compareTo(o2.appName))
                .toList()
                .subscribe(app -> allApps = app);


        return allApps;
    }

    private boolean isAdd;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_unlock:
                btnUnlock.setSelected(true);
                btnLock.setSelected(false);

                if (isAdd) {
                    adapter.setNewData(appLists);
                    tvLockTitle.setText("可选应用数量(" + appLists.size() + ")");
                }
                isAdd = false;

                break;
            case R.id.btn_lock:
                if (!isAdd) {
                    adapter.setNewData(startLists);
                    tvLockTitle.setText("启动应用数量(" + startLists.size() + ")");
                }

                isAdd = true;
                btnUnlock.setSelected(false);
                btnLock.setSelected(true);


                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus2.getDefault().post("appList");
    }
}
