package com.example.poctbluetoothdevicesdk;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.demo.poctbluetoothdevicesdk.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.demo.poctbluetoothdevicesdk.databinding.ActivityMainBinding;
import com.zjf.bluetoothdevicetoolkitcore.*;
import com.zjf.bluetoothdevicetoolkitcore.action.DeviceActionManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initView();

        if (!Environment.checkPermissions(this)) {
            log("没有操作权限！！！请在应用程序权限管理设置中打开app相关操作权限");
            return;
        }

        ininDevices();
    }

    private void initView() {
        dataBinding.tvLog.setMovementMethod(new ScrollingMovementMethod());
        dataBinding.spDevices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                log(((TextView) view).getText().toString());
                switch (position) {
                    case 0:
                        DeviceActionManager.getInstance().switch2Ogm112Auto();
                        break;
                    case 1:
                        DeviceActionManager.getInstance().switch2Ogm202Auto();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void ininDevices() {
        DeviceActionManager.getInstance().init(getApplicationContext());
        DeviceActionManager.getInstance().setOnGetRecordListener(result -> {
            log("接收数据"+result.toString());
        });
        DeviceActionManager.getInstance().switch2Ogm112Auto();
    }

    public void onStart(View view) {
        if (!checkEnvironment()) return;

        dataBinding.spDevices.setEnabled(false);
        dataBinding.btnStart.setEnabled(false);

        scanDevices();
    }


    public void onStop(View view) {
        if (!checkEnvironment()) return;

        dataBinding.spDevices.setEnabled(true);
        dataBinding.btnStart.setEnabled(true);

        DeviceActionManager.getInstance().stop();
        log("操作停止！！！");
    }


    private void log(final String value) {
        runOnUiThread(() -> {
            dataBinding.tvLog.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+ " " + value + "\n" + dataBinding.tvLog.getText());
        });
        Log.i("LogUtil", "log: \r\n" + value);
    }

    private void scanDevices() {
        log("扫描中...");
        DeviceActionManager.getInstance().scan(device->{
            log("找到设备：" + device.getName() + " " + device.getAddress());
            DeviceActionManager.getInstance().getCurrentDeviceAction().connect(device.getAddress(), connectResult -> {
                log("连接"+connectResult.toString());
                if(!connectResult.data){
                    runOnUiThread(()->{
                        onStop(null);
                    });
                }
            });
        });
    }

    private boolean checkEnvironment() {
        switch (Environment.check(this)){
            case PermissionsForbidden:
                log("没有操作权限！！！请在应用程序权限管理设置中打开app相关操作权限");
                return false;
            case BluetoothNotOpen:
                log("请打开蓝牙！！！");
                return false;
            case LocationNotOpen:
                log("请打开定位！！！");
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Environment.PermissionCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults.length > 0 && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Environment.checkPermissions(this);
                    return;
                }
            }
            ininDevices();
        }
    }
}
