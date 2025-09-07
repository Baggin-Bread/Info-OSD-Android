package com.infoosd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.util.Log;
import android.widget.Toast;

public class ScreenshotManager {
    
    private static final String TAG = "ScreenshotManager";
    public static final int REQUEST_SCREENSHOT_PERMISSION = 1001;
    
    private static Intent screenshotPermissionData = null;
    private static int screenshotPermissionResultCode = -1;
    private static boolean hasScreenshotPermission = false;
    
    public static void requestScreenshotPermission(Activity activity) {
        try {
            MediaProjectionManager projectionManager = 
                (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            
            if (projectionManager != null) {
                Intent permissionIntent = projectionManager.createScreenCaptureIntent();
                activity.startActivityForResult(permissionIntent, REQUEST_SCREENSHOT_PERMISSION);
            } else {
                Toast.makeText(activity, "系統不支援截圖功能", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error requesting screenshot permission", e);
            Toast.makeText(activity, "請求截圖權限失敗", Toast.LENGTH_SHORT).show();
        }
    }
    
    public static boolean handlePermissionResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCREENSHOT_PERMISSION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                screenshotPermissionResultCode = resultCode;
                screenshotPermissionData = data;
                hasScreenshotPermission = true;
                Log.d(TAG, "Screenshot permission granted");
                return true;
            } else {
                hasScreenshotPermission = false;
                Log.d(TAG, "Screenshot permission denied");
                return false;
            }
        }
        return false;
    }
    
    public static boolean hasPermission() {
        return hasScreenshotPermission && screenshotPermissionData != null;
    }
    
    public static void takeScreenshot(Context context) {
        if (!hasPermission()) {
            Toast.makeText(context, "請先授予截圖權限", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            Intent serviceIntent = new Intent(context, ScreenshotService.class);
            serviceIntent.setAction(ScreenshotService.ACTION_START_SCREENSHOT);
            serviceIntent.putExtra(ScreenshotService.EXTRA_RESULT_CODE, screenshotPermissionResultCode);
            serviceIntent.putExtra(ScreenshotService.EXTRA_RESULT_DATA, screenshotPermissionData);
            
            context.startForegroundService(serviceIntent);
            
            Toast.makeText(context, "正在截圖...", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error taking screenshot", e);
            Toast.makeText(context, "截圖失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    public static void stopScreenshot(Context context) {
        try {
            Intent serviceIntent = new Intent(context, ScreenshotService.class);
            serviceIntent.setAction(ScreenshotService.ACTION_STOP_SCREENSHOT);
            context.startService(serviceIntent);
        } catch (Exception e) {
            Log.e(TAG, "Error stopping screenshot", e);
        }
    }
    
    public static void clearPermission() {
        hasScreenshotPermission = false;
        screenshotPermissionData = null;
        screenshotPermissionResultCode = -1;
    }
}

