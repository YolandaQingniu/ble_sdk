package com.kingnewblesdk.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.hdr.yolanda.kingnewblesdk.app.R;
import com.kitnew.ble.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hdr on 15/12/9.
 */
public class ScanActivity extends AppCompatActivity implements View.OnClickListener {

    QNBleApi qnBleApi;

    RecyclerView recyclerView;
    Button scanBtn;
    RadioGroup sexRg;
    EditText idEt;
    EditText heightEt;
    EditText birthdayEt;

    final List<QNBleDevice> devices = new ArrayList<>();
    DeviceListAdapter deviceListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);

        initViews();
        initApi();


    }


    void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceListAdapter = new DeviceListAdapter();
        recyclerView.setAdapter(deviceListAdapter);

        scanBtn = (Button) findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);

        idEt = (EditText) findViewById(R.id.idEt);
        heightEt = (EditText) findViewById(R.id.heightEt);
        birthdayEt = (EditText) findViewById(R.id.birthdayEt);

        sexRg = (RadioGroup) findViewById(R.id.sexRG);
    }

    void initApi() {
        qnBleApi = QNApiManager.getApi(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanBtn:
                if (qnBleApi.isScanning()) {
                    doStopScan();
                } else {
                    doStartScan();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        doStopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qnBleApi.disconnectAll();
    }

    void doStopScan() {
        if (!qnBleApi.isScanning()) {
            return;
        }
        scanBtn.setText("开始扫描");
        qnBleApi.stopScan();

    }

    void doStartScan() {
        if (qnBleApi.isScanning()) {
            return;
        }
        scanBtn.setText("停止扫描");
        devices.clear();
        deviceListAdapter.notifyDataSetChanged();
        qnBleApi.startLeScan(null, null, new QNBleScanCallback() {
            @Override
            public void onCompete(int errorCode) {
                Log.i("hdr-ble", "执行结果:" + errorCode);
            }

            @Override
            public void onScan(QNBleDevice bleDevice) {
                Log.i("hdr-ble", "name:" + bleDevice.getDeviceName() + " mac:" + bleDevice.getMac()
                        + "model:" + bleDevice.getModel());
                devices.add(bleDevice);
                deviceListAdapter.notifyItemInserted(devices.size() - 1);
            }
        });
        hideInput();

    }

    public void hideInput() { // 不显示键盘
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0); // 强制隐藏键盘
        }
    }

    class DeviceListAdapter extends RecyclerView.Adapter<ScanActivity.DeviceViewHolder> {

        LayoutInflater inflater;

        DeviceListAdapter() {
            inflater = LayoutInflater.from(ScanActivity.this);
        }

        @Override
        public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = inflater.inflate(R.layout.device_list_item, parent, false);
            return new DeviceViewHolder(v);
        }

        @Override
        public void onBindViewHolder(DeviceViewHolder holder, int position) {
            holder.init(devices.get(position));
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }
    }

    void doConnect(QNBleDevice qnBleDevice) {
        QNUser user = buildUser();
        if (user == null) {
            return;
        }
        startActivity(ConnectActivity.getCallIntent(this, user, qnBleDevice));
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

    class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTv;
        TextView macTv;
        Button connectBtn;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            macTv = (TextView) itemView.findViewById(R.id.macTv);
            connectBtn = (Button) itemView.findViewById(R.id.connectBtn);
            connectBtn.setText("连接");
            connectBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            QNBleDevice device = devices.get(getAdapterPosition());
            doConnect(device);
        }

        void init(QNBleDevice device) {
            nameTv.setText(device.getModel());
            macTv.setText(device.getMac());
        }
    }
}
