package com.kingnewblesdk.demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.hdr.yolanda.kingnewblesdk.app.R;
import com.kitnew.ble.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by hdr on 15/12/21.
 */
public class AutoTestActivity extends AppCompatActivity implements View.OnClickListener, QNBleCallback {

    RecyclerView recyclerView;
    Button connectBtn;
    RadioGroup sexRg;
    EditText idEt;
    EditText heightEt;
    EditText birthdayEt;

    TextView statusTv;
    TextView weightTv;
    TextView modelTv;

    IndicatorAdapter indicatorAdapter;

    QNBleApi bleApi;

    BroadcastReceiver bleStateListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter.isEnabled()) {
                doConnect();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auto_test);

        initViews();

        bleApi = QNApiManager.getApi(this);

        registerReceiver(bleStateListener, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    void initViews() {

        connectBtn = (Button) findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(this);

        idEt = (EditText) findViewById(R.id.idEt);
        heightEt = (EditText) findViewById(R.id.heightEt);
        birthdayEt = (EditText) findViewById(R.id.birthdayEt);

        sexRg = (RadioGroup) findViewById(R.id.sexRG);

        statusTv = (TextView) findViewById(R.id.statusTv);
        weightTv = (TextView) findViewById(R.id.weightTv);
        modelTv = (TextView) findViewById(R.id.modelTv);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        indicatorAdapter = new IndicatorAdapter(this);
        recyclerView.setAdapter(indicatorAdapter);
    }

    QNUser buildUser() {
        String id = idEt.getText().toString();
        String errorString = null;
        Date birthday = null;
        if (id.trim().equals("")) {
            errorString = "请填写有效的用户id";
        } else if (heightEt.getText().length() == 0) {
            errorString = "请填写有效的身高";
        } else {
            String birthdayString = birthdayEt.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
            try {
                birthday = dateFormat.parse(birthdayString);
            } catch (Exception e) {
                errorString = "请按照 yyyy-M-d 的格式输入生日";
            }
        }

        if (errorString != null) {
            Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
            return null;
        }
        int height = Integer.parseInt(heightEt.getText().toString());
        int gender;
        if (sexRg.getCheckedRadioButtonId() == R.id.sexMan) {
            gender = 1;
        } else {
            gender = 0;
        }

        return new QNUser(id, height, gender, birthday);

    }

    @Override
    protected void onResume() {
        super.onResume();
        doConnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        doDisconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bleStateListener);
    }

    @Override
    public void onClick(View v) {
        if (connectBtn.getText().equals("连接")) {
            doConnect();
        } else {
            doDisconnect();
        }
    }

    private void doDisconnect() {
        bleApi.disconnectAll();
    }

    private void doConnect() {
        QNUser user = buildUser();
        if (user == null) {
            return;
        }

        bleApi.autoConnect(user.getId(), user.getHeight(), user.getGender(), user.getBirthday(), this);
    }

    @Override
    public void onConnectStart(QNBleDevice bleDevice) {
        statusTv.setText("正在连接");
        connectBtn.setText("断开");
        Log.i("hdr", "连接的设备是:" + bleDevice.getDeviceName() + " 地址:" + bleDevice.getMac());
    }

    @Override
    public void onConnected(QNBleDevice bleDevice) {
        statusTv.setText("连接成功");
        if (bleDevice.getModel() != null) {
            modelTv.setText("型号: " + bleDevice.getModel());
        }
    }

    @Override
    public void onDisconnected(QNBleDevice bleDevice) {
        statusTv.setText("连接已断开");
        connectBtn.setText("连接");
        modelTv.setText("");
    }

    @Override
    public void onUnsteadyWeight(QNBleDevice bleDevice, float weight) {
        weightTv.setText("体重" + weight + "kg");
    }

    @Override
    public void onReceivedData(QNBleDevice bleDevice, QNData data) {
        // 把秤的型号参数也显示出来
        statusTv.setText("测量完成");
        indicatorAdapter.setQnData(data);
    }

    @Override
    public void onReceivedStoreData(QNBleDevice bleDevice, List<QNData> datas) {

    }

    @Override
    public void onCompete(int errorCode) {
        Log.i("hdr", "执行结果:" + errorCode);
    }
}
