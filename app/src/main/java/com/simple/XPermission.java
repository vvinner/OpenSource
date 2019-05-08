package com.simple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限工具类
 * Created by Porster on 17/3/21.
 */

public class XPermission {
    public static final int REQUEST_CODE=1111;
    /**
     * 检查是否有权限使用
     */
    public static boolean hasPermission(Activity act, String premission){
        return ContextCompat.checkSelfPermission(act, premission)
                == PackageManager.PERMISSION_GRANTED;
    }
    public static void requestAllPermission(FragmentActivity act, List<String> premissions){
        requestAllPermission(act,REQUEST_CODE,premissions,null);
    }
    public static void requestAllPermission(FragmentActivity act, List<String> premissions, PermissionResult permissionResult){
        requestAllPermission(act,REQUEST_CODE,premissions,permissionResult);
    }
    public static void requestAllPermission(FragmentActivity act, int requestCode, List<String> premissions){
        requestAllPermission(act,requestCode,premissions,null);
    }

    /**
     * 请求权限，如果有权限则不会提交
     * @param premissions       权限集合
     */
    public static void requestAllPermission(FragmentActivity act, int requestCode, List<String> premissions, PermissionResult permissionResult){
        if(premissions == null ||premissions.isEmpty()){
            return;
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            if (permissionResult != null) {
                permissionResult.Granted(premissions.toArray(new String[premissions.size()]));
            }
            return;
        }
        List<String> prepareRequest=new ArrayList<>();
        List<String> granted=new ArrayList<>();
        for (String premission : premissions) {
            boolean has= hasPermission(act, premission);
            if (!has) {//无权限
                prepareRequest.add(premission);
            }else{
                granted.add(premission);
            }
        }
        if(prepareRequest.isEmpty()){//没有需要申请的权限
            if (permissionResult != null&&granted.size()>0) {
                permissionResult.Granted(granted.toArray(new String[granted.size()]));
            }
            return;
        }
//        ActivityCompat.requestPermissions(act,prepareRequest.toArray(new String[prepareRequest.size()]),requestCode);
        String tag = PermissionFragment.class.getSimpleName();
        PermissionFragment permissionFragment;
        android.support.v4.app.FragmentManager fragmentManager = act.getSupportFragmentManager();

        permissionFragment = (PermissionFragment) fragmentManager.findFragmentByTag(tag);

        if (permissionFragment == null) {
            permissionFragment = new PermissionFragment();
            fragmentManager
                    .beginTransaction()
                    .add(permissionFragment, tag)
                    .commitAllowingStateLoss();
        } else if (permissionFragment.isDetached()) {
            fragmentManager
                    .beginTransaction()
                    .attach(permissionFragment)
                    .commitAllowingStateLoss();
        }
        fragmentManager.executePendingTransactions();

        permissionFragment.setPermissionResult(permissionResult);
        permissionFragment.setGranted(granted);
        permissionFragment.requestPermissions(prepareRequest.toArray(new String[prepareRequest.size()]),requestCode);

    }
    public static class PermissionFragment extends android.support.v4.app.Fragment{
        PermissionResult mPermissionResult;
        List<String> mGranted;

        public void setGranted(List<String> granted) {
            mGranted = granted;
        }

        public void setPermissionResult(PermissionResult permissionResult) {
            mPermissionResult = permissionResult;
        }
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            XPermission.onRequestPermissionsResult(mGranted,requestCode,permissions,grantResults,mPermissionResult);
        }
    }

    public static void onRequestPermissionsResult(List<String> mGranted, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionResult permissionResult){

        List<String> denied=new ArrayList<>();
        List<String> granted=new ArrayList<>();

        for (int i = 0; i < grantResults.length; i++) {
            int state=grantResults[i];
            String permission=permissions[i];

            if(state== PackageManager.PERMISSION_GRANTED){
                granted.add(permission);
            }else{
                denied.add(permission);
            }
        }
        if (mGranted != null) {
            granted.addAll(mGranted);
        }
        permissionResult.Granted(granted.toArray(new String[granted.size()]));
        permissionResult.Denied(denied.toArray(new String[denied.size()]));
    }

    public  interface PermissionResult{
        public void Granted(String[] permission);
        public void Denied(String[] permission);
    }
    public abstract static class  PermissionSingleResult implements PermissionResult{
        @Override
        public void Granted(String[] permission) {
            if(permission.length > 0){
                result(true);
            }
        }
        @Override
        public void Denied(String[] permission) {
            if(permission.length > 0){
                result(false);
            }
        }
        protected abstract void result(boolean accept);
    }

    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return localIntent;
    }
}
