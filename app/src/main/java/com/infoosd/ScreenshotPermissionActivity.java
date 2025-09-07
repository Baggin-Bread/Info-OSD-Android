package com.infoosd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ScreenshotPermissionActivity extends Activity {
    
    private static final String TAG = "ScreenshotPermissionActivity";
    private static final int REQUEST_SCREENSHOT_PERMISSION = 1001;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make this activity transparent and non-intrusive
        setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        
        // Request screenshot permission immediately
        requestScreenshotPermission();
    }
    
    private void requestScreenshotPermission() {
        try {
            MediaProjectionManager projectionManager = 
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            
            if (projectionManager != null) {
                Intent permissionIntent = projectionManager.createScreenCaptureIntent();
                startActivityForResult(permissionIntent, REQUEST_SCREENSHOT_PERMISSION);
            } else {
                Toast.makeText(this, "系統不支援截圖功能", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error requesting screenshot permission", e);
            Toast.makeText(this, "請求截圖權限失敗", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_SCREENSHOT_PERMISSION) {
            if (resultCode == RESULT_OK && data != null) {
                // Permission granted, start screenshot service immediately
                Log.d(TAG, "Screenshot permission granted, starting screenshot");
                
                Intent serviceIntent = new Intent(this, ScreenshotService.class);
                serviceIntent.setAction(ScreenshotService.ACTION_START_SCREENSHOT);
                serviceIntent.putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode);
                serviceIntent.putExtra(ScreenshotService.EXTRA_RESULT_DATA, data);
                
                startForegroundService(serviceIntent);
                
                Toast.makeText(this, "正在截圖...", Toast.LENGTH_SHORT).show();
                
            } else {
                Log.d(TAG, "Screenshot permission denied");
                Toast.makeText(this, "截圖權限被拒絕", Toast.LENGTH_SHORT).show();
            }
        }
        
        // Always finish this activity
        finish();
    }
    
    @Override
    public void onBackPressed() {
        // Prevent back button from interfering
        super.onBackPressed();
        finish();
    }
}

