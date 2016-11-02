package com.kingnewblesdk.demo;

import android.app.Application;
import android.util.Log;
import com.kitnew.ble.QNApiManager;
import com.kitnew.ble.QNResultCallback;
import com.kitnew.ble.utils.QNLog;

/**
 * Created by hdr on 16/1/14.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QNLog.DEBUG = true;
        QNLog.log("application启动");

        //123456789是测试版的appid，第二个Boolean参数为true则是发布模式的SDK，false则是开发版的
        QNApiManager.getApi(getApplicationContext()).initSDK("123456789",false , new QNResultCallback() {
            @Override
            public void onCompete(int errorCode) {
                Log.i("hdr", "执行结果校验:" + errorCode);
            }
        });
    }
}
