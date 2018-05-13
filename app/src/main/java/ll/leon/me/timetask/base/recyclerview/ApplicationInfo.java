package ll.leon.me.timetask.base.recyclerview;

import android.graphics.drawable.Drawable;

import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ll.leon.me.timetask.realm.RealmDao;
import me.leon.devsuit.android.AppUtils;

/**
 * Created by Administrator on 2018/3/28.
 */

public class ApplicationInfo extends RealmObject{
    public String appId;
    public String appName;
    public String imageUrl;
    public String downloadUrl;
    @PrimaryKey
    public String packageName;
    public String versionName;
    public boolean isSystem;
    public boolean isRemote;
    public int currentSize;
    public int size;

    public ApplicationInfo() {

    }

    public ApplicationInfo(String appId, String appName, String imageUrl, String downloadUrl, String packageName, String versionName) {
        this.appId = appId;
        this.appName = appName;
        this.imageUrl = imageUrl;
        this.downloadUrl = downloadUrl;
        this.packageName = packageName;
        this.versionName = versionName;
    }

    public ApplicationInfo(AppUtils.AppInfo info) {

        this.appName = info.getName();
        this.packageName = info.getPackageName();
        this.versionName = info.getVersionName();
        this.isSystem = info.isSystem();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationInfo that = (ApplicationInfo) o;
        return isSystem == that.isSystem &&
                isRemote == that.isRemote &&
                currentSize == that.currentSize &&
                size == that.size &&
                Objects.equals(appId, that.appId) &&
                Objects.equals(appName, that.appName) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(downloadUrl, that.downloadUrl) &&
                Objects.equals(packageName, that.packageName) &&
                Objects.equals(versionName, that.versionName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(appId, appName, imageUrl, downloadUrl, packageName, versionName, isSystem, isRemote, currentSize, size);
    }
}
