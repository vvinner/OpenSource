package com.simple;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void single(View view) {
        XPermission.requestAllPermission(this, Collections.singletonList(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), new XPermission.PermissionSingleResult() {
            @Override
            protected void result(boolean accept) {
                if (accept) {
                    //Do something
                    Toast.makeText(MainActivity.this,"accept",Toast.LENGTH_SHORT).show();
                }else{
                    //可以弹窗提示用户去系统权限管理开启,操作后在onActivityResult重新检查权限是否开启
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(MainActivity.this);
                    localBuilder.setTitle("提示");
                    localBuilder.setIcon(R.mipmap.ic_launcher);
                    localBuilder.setMessage("应用需要存储卡读写权限才可使用，前往开启");
                    localBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                        {
                            Intent intent=XPermission.getAppDetailSettingIntent(MainActivity.this);
                            startActivityForResult(intent,10);
                        }
                    });
                    localBuilder.setCancelable(false).create();
                    localBuilder.show();
                }
            }
        });
    }

    public void multi(View view) {
        XPermission.requestAllPermission(this, Arrays.asList(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
        ), new XPermission.PermissionResult() {
            @Override
            public void Granted(String[] permission) {
                Log.w("Granted",Arrays.toString(permission));
                //Do something
                boolean camera = false;
                boolean readPhoneState = false;

                for (String per : permission) {
                    if(TextUtils.equals(per,Manifest.permission.CAMERA)){
                        camera=true;
                    }else if(TextUtils.equals(per,Manifest.permission.READ_PHONE_STATE)){
                        readPhoneState=true;
                    }
                }
                if(camera && readPhoneState){
                    Toast.makeText(MainActivity.this,"Necessary permission",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void Denied(String[] permission) {
                Log.w("Denied",Arrays.toString(permission));
                //Do something
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            single(null);
        }
    }
}
