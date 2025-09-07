package com.infoosd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;

public class PermissionManager {
    
    private static final String TAG = "PermissionManager";
    public static final int REQUEST_OVERLAY_PERMISSION = 1001;
    
    /**
     * Check if the app has overlay permission
     */
    public static boolean hasOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true; // Permission not required for older versions
    }
    
    /**
     * Request overlay permission from user
     */
    public static void requestOverlayPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                showPermissionDialog(activity);
            }
        }
    }
    
    /**
     * Show permission explanation dialog
     */
    private static void showPermissionDialog(Activity activity) {
        new AlertDialog.Builder(activity)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.overlay_permission_message)
            .setPositiveButton(R.string.grant_permission, (dialog, which) -> {
                openOverlaySettings(activity);
            })
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                dialog.dismiss();
                Log.d(TAG, "User cancelled permission request");
            })
            .setCancelable(false)
            .show();
    }
    
    /**
     * Open system overlay settings
     */
    public static void openOverlaySettings(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            } catch (Exception e) {
                Log.e(TAG, "Failed to open overlay settings", e);
                
                // Fallback to general app settings
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivity(intent);
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to open app settings", ex);
                }
            }
        }
    }
    
    /**
     * Handle permission result
     */
    public static boolean handlePermissionResult(Activity activity, int requestCode) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (hasOverlayPermission(activity)) {
                Log.d(TAG, "Overlay permission granted");
                return true;
            } else {
                Log.d(TAG, "Overlay permission denied");
                showPermissionDeniedDialog(activity);
                return false;
            }
        }
        return false;
    }
    
    /**
     * Show permission denied dialog
     */
    private static void showPermissionDeniedDialog(Activity activity) {
        new AlertDialog.Builder(activity)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.permission_denied)
            .setPositiveButton(R.string.grant_permission, (dialog, which) -> {
                openOverlaySettings(activity);
            })
            .setNegativeButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }
    
    /**
     * Check and request permission if needed
     */
    public static boolean checkAndRequestPermission(Activity activity) {
        if (hasOverlayPermission(activity)) {
            return true;
        } else {
            requestOverlayPermission(activity);
            return false;
        }
    }
}

