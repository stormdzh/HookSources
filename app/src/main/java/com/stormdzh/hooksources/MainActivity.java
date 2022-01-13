package com.stormdzh.hooksources;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.stormdzh.hooksources.hook.HookUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "codedzh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.INTERNET, ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        findViewById(R.id.tvAndroidId).setOnClickListener(this);
        findViewById(R.id.tvAndroidId2).setOnClickListener(this);
        findViewById(R.id.tvIMEI).setOnClickListener(this);
        findViewById(R.id.tvIMEI1).setOnClickListener(this);
        findViewById(R.id.tvIMEI2).setOnClickListener(this);
        findViewById(R.id.tvIMEI3).setOnClickListener(this);
        findViewById(R.id.tvIMEI4).setOnClickListener(this);
        findViewById(R.id.tvMAC).setOnClickListener(this);
        findViewById(R.id.tvMAC1).setOnClickListener(this);
        findViewById(R.id.tvIMSI).setOnClickListener(this);
        findViewById(R.id.tvRecentTasks).setOnClickListener(this);
        findViewById(R.id.tvRuningTasks).setOnClickListener(this);
        findViewById(R.id.tvClipboard).setOnClickListener(this);
        findViewById(R.id.tvLocation).setOnClickListener(this);
        findViewById(R.id.tvSERIAL).setOnClickListener(this);
        findViewById(R.id.tvPrivacyInfo).setOnClickListener(this);

        try {
//            HookUtil.hookClipboardService(this);
            TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            String networkOperatorName = tm.getNetworkOperatorName();
            Log.i(TAG,"networkOperatorName:"+networkOperatorName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvAndroidId:
                getAndroidId(this);
                break;
            case R.id.tvAndroidId2:
                getAndroidId2(this);
                break;
            case R.id.tvMAC:
                getMac(this);
                break;
            case R.id.tvMAC1:
                getLocalMacAddressFromIp();
                break;
            case R.id.tvIMEI:
                getImei(this, 0);
                break;
            case R.id.tvIMEI1:
                getImei(this, 1);
                break;
            case R.id.tvIMEI2:
                getImei(this, 2);
                break;
            case R.id.tvIMEI3:
                getImei(this, 3);
                break;
            case R.id.tvIMEI4:
                getImei(this, 4);
                break;
            case R.id.tvIMSI:
                getIMSI(this);
                break;
            case R.id.tvRecentTasks:
                getRecentTasks();
                break;
            case R.id.tvRuningTasks:
                getRuningTasks();
                break;
            case R.id.tvClipboard:
                getClipboard(this);
                break;
            case R.id.tvLocation:
                getLocation(this);
                break;
            case R.id.tvSERIAL:
//                String user = Build.USER;
//                String serial = Build.SERIAL;
                String serial = Build.SERIAL;
                String release = Build.VERSION.RELEASE;
                String serial1 = Build.MANUFACTURER;
                Log.i("codedzh",serial);
                break;
            case R.id.tvPrivacyInfo:
                startActivity(new Intent(this,PrivacyInfoActivity.class));
                break;
        }

    }

    @SuppressLint("MissingPermission")
    private void getLocation(MainActivity mainActivity) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);;
        locationManager.getLastKnownLocation(ACCESS_COARSE_LOCATION);
    }

    private String getClipboard( Context context) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            ClipData data = cm.getPrimaryClip();
            if (data != null) {
                ClipData.Item item = data.getItemAt(0);
                if (item != null) {
                    //TODO item.getText()部分手机可能会在剪切板没有相关的文本内容返回null
                    return item.getText().toString();
                }
            }
        }
        return "";
    }

    public String getAndroidId2(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        Log.i(TAG, "点击获取 AndroidID：" + ANDROID_ID);
        return ANDROID_ID;
    }

    public String getAndroidId(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i(TAG, "点击获取 AndroidID：" + ANDROID_ID);
        return ANDROID_ID;
    }


    public void getImei(Context context, int type) {
        String imei = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (type == 0) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            imei = tm.getDeviceId();
            Log.i(TAG, "点击获取 getDeviceId()：" + imei);
        } else if (type == 1) {
            imei = tm.getDeviceId(0);
            Log.i(TAG, "点击获取 getDeviceId(0)：" + imei);
        } else if (type == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = tm.getImei(0);
                Log.i(TAG, "点击获取 getImei()：" + imei);
            }
        } else if (type == 3) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = tm.getImei(0);
                Log.i(TAG, "点击获取 getImei(0)：" + imei);
            }
        } else if (type == 4) {
            try {
                Method method = tm.getClass().getMethod("getImei");
                imei = (String) method.invoke(tm);
                Log.i(TAG, "点击获取 反射getImei：" + imei);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //https://blog.csdn.net/qq_43278826/article/details/95216504
    @RequiresApi(api = Build.VERSION_CODES.M)
    public String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "点击获取 getDeviceId ，无权限：READ_PHONE_STATE");
                    return "";
                }
                imei = tm.getDeviceId();
                Log.i(TAG, "点击获取 getDeviceId：" + imei);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei = tm.getImei();
                    Log.i(TAG, "点击获取 getImei：" + imei);
                } else {
                    Method method = tm.getClass().getMethod("getImei");
                    imei = (String) method.invoke(tm);
                    Log.i(TAG, "点击获取 反射getImei：" + imei);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void JudgeSIM(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        //获取当前SIM卡槽数量
        int phoneCount = tm.getPhoneCount();
        //获取当前SIM卡数量
        int activeSubscriptionInfoCount = SubscriptionManager.from(context).getActiveSubscriptionInfoCount();
        List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null) {
            return;
        }
        for (SubscriptionInfo subInfo : activeSubscriptionInfoList) {
            Log.d(TAG, "sim卡槽位置：" + subInfo.getSimSlotIndex());
            try {
                Method method = tm.getClass().getMethod("getImei", int.class);
                String imei = (String) method.invoke(tm, subInfo.getSimSlotIndex());
                Log.d(TAG, "sim卡imei：" + imei);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        Log.d(TAG, "卡槽数量：" + phoneCount);
        Log.d(TAG, "当前SIM卡数量：" + activeSubscriptionInfoCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getIMSI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String imsi = tm.getSubscriberId();
        Log.i(TAG, "点击获取 getSubscriberId：" + imsi);
    }


    /**
     * https://www.cnblogs.com/Amandaliu/archive/2011/11/06/2238177.html
     *
     * @param context
     */
    public void getMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo winfo = wifi.getConnectionInfo();
        String macAddress = winfo.getMacAddress();
        Log.i(TAG, "点击获取 getMacAddress：" + macAddress);
    }

    /**
     * https://cloud.tencent.com/developer/article/1740201
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    private String getLocalMacAddressFromIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
            Log.i(TAG, "点击获取 getHardwareAddress：" + strMacAddr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();//得到下一个元素
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();//得到一个ip地址的列举
                while (inetAddresses.hasMoreElements()) {
                    ip = inetAddresses.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }


    public void getRecentTasks() {
        ActivityManager tasksManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTasks = tasksManager.getRecentTasks(1000, ActivityManager.RECENT_WITH_EXCLUDED);
        Log.i(TAG, "点击获取 recentTasks：" + (recentTasks == null ? 0 : recentTasks.size()));
    }


    public void getRuningTasks() {
        ActivityManager tasksManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> recentTasks = tasksManager.getRunningTasks(1000);
        Log.i(TAG, "点击获取 getRunningTasks：" + (recentTasks == null ? 0 : recentTasks.size()));
    }

}
