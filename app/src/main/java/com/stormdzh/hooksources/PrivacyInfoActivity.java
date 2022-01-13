package com.stormdzh.hooksources;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

/**
 * @Description: 描述
 * @Author: duzhenhua3
 * @CreateDate: 1/12/22 8:34 PM
 */
public class PrivacyInfoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_info);

        String privacyInfo = getPrivacyInfo();


        System.out.println(privacyInfo);
        Log.i("codedzh",privacyInfo);
    }

    private String getPrivacyInfo() {
        StringBuilder sbs = new StringBuilder();

        //系统版本
        String buildDISPLAY = getBuildDISPLAY();
        sbs.append(buildDISPLAY).append("\n");

        String systemVersion = getSystemVersion();
        sbs.append(systemVersion).append("\n");

        String iccid = getICCID();
        sbs.append(iccid).append("\n");

        String deviceName = getSystemDevice();
        sbs.append(deviceName).append("\n");

        String mAndroidID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        sbs.append(mAndroidID).append("\n");

        String deviceBrand = getDeviceBrand();
        sbs.append(deviceBrand).append("\n");


        return sbs.toString();
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }


    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getBuildDISPLAY() {
        return Build.DISPLAY;
    }

    /**
     * 获取ICCID
     *
     * @return ICCID
     */
    public  String getICCID() {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);// 取得相关系统服务
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        String icc = telephonyManager.getSimSerialNumber();  //取出 ICCID
        return icc;
    }

    /**
     * 获取手机设备名
     *
     * @return  手机设备名
     */
    public static String getSystemDevice() {
        return Build.DEVICE;
    }

}
