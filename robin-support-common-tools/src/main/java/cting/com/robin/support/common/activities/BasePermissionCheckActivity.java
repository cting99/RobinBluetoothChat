package cting.com.robin.support.common.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cting.com.robin.support.common.utils.PermissionHelper;

public class BasePermissionCheckActivity extends AppCompatActivity {

    public static String TAG = "cting/act/";
    protected boolean mPermissionReady;

    public BasePermissionCheckActivity() {
        TAG += getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionReady = PermissionHelper.getInstance().checkPermission(this, getRequestPermission());
        if (mPermissionReady) {
            onPermissionReady();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionAllGranted = PermissionHelper.getInstance().onPermissionResult(this, requestCode, permissions, grantResults);
        if (permissionAllGranted) {
            onPermissionReady();
        }
    }

    protected String[] getRequestPermission() {
        return PermissionHelper.REQUEST_PERMISSIONS;
    }

    protected void onPermissionReady() {

    }
}
