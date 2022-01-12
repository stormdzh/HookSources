package com.stormdzh.hooksources.hook;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Description: 描述
 * @Author: duzhenhua3
 * @CreateDate: 10/19/21 3:47 PM
 */
public class HookUtil {
    public static String TAG = "codedzh";

    public static void hookClipboardService(final Context context) throws Exception {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        Field mServiceFiled = ClipboardManager.class.getDeclaredField("mService");
        mServiceFiled.setAccessible(true);    //Step 1: Get the system mService
        final Object mService = mServiceFiled.get(clipboardManager);
        //Step 2: Initialize dynamic proxy objects
        Class aClass = Class.forName("android.content.IClipboard");
        Object proxyInstance = Proxy.newProxyInstance(context.getClass().getClassLoader(), new
                Class[]{aClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Log.d(TAG, "invoke(). method:" + method);
                String name = method.getName();
                if (args != null && args.length > 0) {
                    for (Object arg : args) {
                        Log.d(TAG, "invoke: arg=" + arg);
                    }
                }
                if ("setPrimaryClip".equals(name)) {
                    Object arg = args[0];
                    if (arg instanceof ClipData) {
                        ClipData clipData = (ClipData) arg;
                        int itemCount = clipData.getItemCount();
                        for (int i = 0; i < itemCount; i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Log.i(TAG, "invoke: item=" + item);
                        }
                    }
                    Toast.makeText(context, "Detection of someone setting the pasteboard content", Toast.LENGTH_SHORT).show();
                } else if ("getPrimaryClip".equals(name)) {
                    Toast.makeText(context, "Detection of someone trying to get the contents of the pasteboard", Toast.LENGTH_SHORT).show();
                }            //Operations are handled by sOriginService without intercepting notifications
                return method.invoke(mService, args);

            }
        });    //Step 3: Replace the mService of the system with proxyNotiMng
        Field sServiceField = ClipboardManager.class.getDeclaredField("mService");
        sServiceField.setAccessible(true);
        sServiceField.set(clipboardManager, proxyInstance);

    }
}
