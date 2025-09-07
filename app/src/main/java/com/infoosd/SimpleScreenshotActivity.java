package com.infoosd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SimpleScreenshotActivity extends Activity {
    
    private static final String TAG = "SimpleScreenshotActivity";
    private static final int REQUEST_SCREENSHOT = 2001;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "Activity created");
        
        // 設置透明主題
        setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        
        // 立即請求截圖權限
        requestScreenshotPermission();
    }
    
    private void requestScreenshotPermission() {
        try {
            Log.d(TAG, "Requesting screenshot permission");
            
            MediaProjectionManager projectionManager = 
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            
            if (projectionManager != null) {
                Intent permissionIntent = projectionManager.createScreenCaptureIntent();
                startActivityForResult(permissionIntent, REQUEST_SCREENSHOT);
            } else {
                Log.e(TAG, "MediaProjectionManager is null");
                Toast.makeText(this, "系統不支援截圖功能", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error requesting permission", e);
            Toast.makeText(this, "請求截圖權限失敗", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Log.d(TAG, "onActivityResult - requestCode: " + requestCode + ", resultCode: " + resultCode);
        
        if (requestCode == REQUEST_SCREENSHOT) {
            if (resultCode == RESULT_OK && data != null) {
                Log.d(TAG, "Permission granted, starting screenshot service");
                
                // 啟動簡單截圖服務
                Intent serviceIntent = new Intent(this, SimpleScreenshotService.class);
                serviceIntent.setAction(SimpleScreenshotService.ACTION_TAKE_SCREENSHOT);
                serviceIntent.putExtra(SimpleScreenshotService.EXTRA_RESULT_CODE, resultCode);
                serviceIntent.putExtra(SimpleScreenshotService.EXTRA_RESULT_DATA, data);
                
                try {
                    startForegroundService(serviceIntent);
                    Toast.makeText(this, "正在截圖...", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "Error starting service", e);
                    Toast.makeText(this, "啟動截圖服務失敗", Toast.LENGTH_SHORT).show();
                }
                
            } else {
                Log.d(TAG, "Permission denied");
                Toast.makeText(this, "截圖權限被拒絕", Toast.LENGTH_SHORT).show();
            }
        }
        
        // 總是結束Activity
        finish();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

