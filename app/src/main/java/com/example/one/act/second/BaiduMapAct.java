package com.example.one.act.second;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.example.one.App;
import com.example.one.R;
import com.example.one.speail.MyLocationUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BaiduMapAct extends AppCompatActivity {

    Button button;
    TextView textView;

    Button button2;
    TextView textView2;

    Button button3;

    public LocationClient mLocationClient = null;
    private MyLocationUtils myListener = new MyLocationUtils();

    LocationClientOption locationClientOption = new LocationClientOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_map);

        mLocationClient = new LocationClient(this);
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        locationClientOption.setIsNeedAddress(true);
        locationClientOption.setNeedNewVersionRgc(true);

        mLocationClient.setLocOption(locationClientOption);

        mLocationClient.start();

        button = (Button) findViewById(R.id.bt_BaiduMapStart);
        textView = (TextView) findViewById(R.id.tv_BaiduMap);

        button.setOnClickListener(v -> {
            App app = (App) Utils.getApp();
            BDLocation bdLocation = app.getBdLocation();
            if (bdLocation!=null){
                textView.setText(bdLocation.getCity());
            }
        });


        button2 = (Button) findViewById(R.id.btStorage);
        textView2 = (TextView) findViewById(R.id.tvStorage);
        button2.setOnClickListener(v -> {
            textView2.setText(""+getUse()+"/"+getTotal());
            queryWithStorageManager(this);
        });

        button3 = (Button) findViewById(R.id.btClose);
        button3.setOnClickListener(v -> {
            getPingMuSize(this);
        });
    }

    private long getTotal(){
        File path = Environment.getDataDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long size = statFs.getBlockSize();
        long total = statFs.getBlockCount();
        return size*total/1024/1024/1024;
    }

    private long getUse(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize/1024/1024/1024;
    }

    public void getPingMuSize(Context mContext) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidthPx = dm.widthPixels;
        int screenHeighPx = dm.heightPixels;
        int virtualKeyHeight = 0;
        Resources res = getResources();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
            virtualKeyHeight = res.getDimensionPixelSize(resourceId);
        float density = getResources().getDisplayMetrics().density;
        int screenWidthDp = (int) (screenWidthPx / density + 0.5f);
        int screenHeighDp = (int) ((screenHeighPx + virtualKeyHeight) / density + 0.5f);
        LogUtils.d("屏幕高:" + screenWidthPx + "px,屏幕高:" + screenHeighPx + "px,虚拟键高:" + virtualKeyHeight + "px");
        LogUtils.d("屏幕宽:" + screenWidthDp + "dp,屏幕高:" + screenHeighDp + "dp,density:" + density);
    }

    public static void queryWithStorageManager(Context context) {
        //5.0 查外置存储
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        float unit = 1024;
        int version = Build.VERSION.SDK_INT;
        if (version < Build.VERSION_CODES.M) {//小于6.0
            try {
                Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
                StorageVolume[] volumeList = (StorageVolume[]) getVolumeList.invoke(storageManager);
                long totalSize = 0, availableSize = 0;
                if (volumeList != null) {
                    Method getPathFile = null;
                    for (StorageVolume volume : volumeList) {
                        if (getPathFile == null) {
                            getPathFile = volume.getClass().getDeclaredMethod("getPathFile");
                        }
                        File file = (File) getPathFile.invoke(volume);
                        totalSize += file.getTotalSpace();
                        availableSize += file.getUsableSpace();
                    }
                }
                LogUtils.d("totalSize = " + getUnit(totalSize, unit) + " ,availableSize = " + getUnit(availableSize, unit));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {

            try {
                Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");//6.0
                List<Object> getVolumeInfo = (List<Object>) getVolumes.invoke(storageManager);
                long total = 0L, used = 0L;
                for (Object obj : getVolumeInfo) {

                    Field getType = obj.getClass().getField("type");
                    int type = getType.getInt(obj);

                    if (type == 1) {//TYPE_PRIVATE

                        long totalSize = 0L;

                        //获取内置内存总大小
                        if (version >= Build.VERSION_CODES.O) {//8.0
                            unit = 1000;
                            Method getFsUuid = obj.getClass().getDeclaredMethod("getFsUuid");
                            String fsUuid = (String) getFsUuid.invoke(obj);
                            totalSize = getTotalSize(context, fsUuid);//8.0 以后使用
                        } else if (version >= Build.VERSION_CODES.N_MR1) {//7.1.1
                            Method getPrimaryStorageSize = StorageManager.class.getMethod("getPrimaryStorageSize");//5.0 6.0 7.0没有
                            totalSize = (long) getPrimaryStorageSize.invoke(storageManager);
                        }
                        long systemSize = 0L;

                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);

                            if (totalSize == 0) {
                                totalSize = f.getTotalSpace();
                            }
                            systemSize = totalSize - f.getTotalSpace();
                            used += totalSize - f.getFreeSpace();
                            total += totalSize;
                        }
                        LogUtils.d("设备内存大小：" + getUnit(totalSize, unit) + "\n系统大小：" + getUnit(systemSize, unit));
                        LogUtils.d( "totalSize = " + getUnit(totalSize, unit) + " ,used(with system) = " + getUnit(used, unit) + " ,free = " + getUnit(totalSize - used, unit));
                    } else if (type == 0) {//TYPE_PUBLIC
                        //外置存储
                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);
                            used += f.getTotalSpace() - f.getFreeSpace();
                            total += f.getTotalSpace();
                        }
                    } else if (type == 2) {//TYPE_EMULATED

                    }
                }
                LogUtils.d("总内存 total = " + getUnit(total, unit) + "\n已用 used(with system) = " + getUnit(used, 1000) + "\n可用 available = " + getUnit(total - used, unit));
            } catch (SecurityException e) {
//                Log.e(TAG, "缺少权限：permission.PACKAGE_USAGE_STATS");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 进制转换
     */
    public static String getUnit(float size, float base) {
        int index = 0;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        while (size > base && index < 4) {
            size = size / base;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s ", size, units[index]);
    }

    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     */
    @RequiresApi(Build.VERSION_CODES.O)
    public static long getTotalSize(Context context, String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = context.getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

}