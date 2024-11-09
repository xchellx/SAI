package com.aefyr.sai.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.aefyr.sai.BuildConfig;

import java.util.HashMap;

public class PermissionsUtils {
    public static final int REQUEST_CODE_STORAGE_PERMISSIONS = 322;
    public static final int REQUEST_CODE_SHIZUKU = 1337;
    public static final int REQUEST_CODE_MANAGE_STORAGE = 342;

    private static final String[] storagePermissions = (Build.VERSION.SDK_INT > 33
            ? new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
            }
            :  new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
    );
    private static final HashMap<Integer, ActivityResultLauncher<String[]>> launchers = new HashMap<>();

    public static boolean checkAndRequestStoragePermissions(Activity a) {
        return checkAndRequestPermissions(a, storagePermissions, REQUEST_CODE_STORAGE_PERMISSIONS);
    }

    public static boolean checkAndRequestStoragePermissions(Fragment f) {
        return checkAndRequestPermissions(f, storagePermissions, REQUEST_CODE_STORAGE_PERMISSIONS);
    }

    public static boolean checkAndRequestShizukuPermissions(Activity a) {
        return checkAndRequestPermissions(a, new String[]{"moe.shizuku.manager.permission.API_V23"}, REQUEST_CODE_SHIZUKU);
    }

    public static boolean checkAndRequestShizukuPermissions(Fragment f) {
        return checkAndRequestPermissions(f, new String[]{"moe.shizuku.manager.permission.API_V23"}, REQUEST_CODE_SHIZUKU);
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    private static boolean checkAndRequestPermissions(Activity a, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        for (String permission : permissions) {
            if ((ActivityCompat.checkSelfPermission(a, permission)) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(a, permissions, requestCode);
                return false;
            }
        }

        return true;
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    private static boolean checkAndRequestPermissions(Fragment f, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        for (String permission : permissions) {
            if ((ActivityCompat.checkSelfPermission(f.requireContext(), permission)) == PackageManager.PERMISSION_DENIED) {
                f.requestPermissions(permissions, requestCode);
                return false;
            }
        }

        return true;
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    public static boolean requestManageStoragePerm(Activity a) {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                ActivityCompat.startActivityForResult(a, new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID)), REQUEST_CODE_MANAGE_STORAGE, null);
                return false;
            } else
                return true;
        } else
            return true;
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    public static boolean requestManageStoragePerm(Fragment f) {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                f.startActivityForResult(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID)), REQUEST_CODE_MANAGE_STORAGE);
                return false;
            } else
                return true;
        } else
            return true;
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    public static boolean handleManageStoragePerm(Activity a, int requestCode) {
        if (Build.VERSION.SDK_INT >= 30) {
            if (requestCode == REQUEST_CODE_MANAGE_STORAGE) {
                a.onRequestPermissionsResult(REQUEST_CODE_STORAGE_PERMISSIONS,
                    new String[] {
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    },
                    new int[] {
                            Environment.isExternalStorageManager()
                                ? PackageManager.PERMISSION_GRANTED
                                : PackageManager.PERMISSION_DENIED
                    }
                );
                return true;
            } else
                return false;
        } else
            return false;
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    public static boolean handleManageStoragePerm(Fragment f, int requestCode) {
        if (Build.VERSION.SDK_INT >= 30) {
            if (requestCode == REQUEST_CODE_MANAGE_STORAGE) {
                f.onRequestPermissionsResult(REQUEST_CODE_STORAGE_PERMISSIONS,
                        new String[] {
                                Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        },
                        new int[] {
                                Environment.isExternalStorageManager()
                                        ? PackageManager.PERMISSION_GRANTED
                                        : PackageManager.PERMISSION_DENIED
                        }
                );
                return true;
            } else
                return false;
        } else
            return false;
    }

}
