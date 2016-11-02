package com.kingnewblesdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.hdr.yolanda.kingnewblesdk.app.R;
import com.kitnew.ble.QNApiManager;
import com.kitnew.ble.QNResultCallback;
import com.kitnew.ble.utils.QNLog;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.stepTestBtn).setOnClickListener(this);
        findViewById(R.id.simpleConnectBtn).setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                startActivity(new Intent(MainActivity.this, AutoTestActivity.class));
            }
        }, 300);

        QNLog.log("MainActivity 启动");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stepTestBtn:
                startActivity(new Intent(this, ScanActivity.class));
                break;
            case R.id.simpleConnectBtn:
                startActivity(new Intent(this, AutoTestActivity.class));
                break;
        }
    }
}
