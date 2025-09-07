package com.infoosd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MinimalScreenshotActivity extends Activity {
    
    private static final String TAG = "MinimalScreenshot";
    private static final int REQUEST_SCREENSHOT = 1001;
    
    private MediaProjectionManager projectionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MinimalScreenshotActivity created");
        
        try {
            projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            
            if (projectionManager != null) {
                Log.d(TAG, "Requesting screenshot permission");
                Intent captureIntent = projectionManager.createScreenCaptureIntent();
                startActivityForResult(captureIntent, REQUEST_SCREENSHOT);
            } else {
                Log.e(TAG, "MediaProjectionManager is null");
                showToast("無法獲取截圖管理器");
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            showToast("初始化失敗: " + e.getMessage());
            finish();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Log.d(TAG, "onActivityResult - requestCode: " + requestCode + 
              ", resultCode: " + resultCode + ", data: " + (data != null ? "not null" : "null"));
        
        try {
            if (requestCode == REQUEST_SCREENSHOT) {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Screenshot permission granted, starting service");
                    startMinimalScreenshotService(resultCode, data);
                } else {
                    Log.w(TAG, "Screenshot permission denied");
                    showToast("截圖權限被拒絕");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onActivityResult", e);
            showToast("處理權限結果失敗: " + e.getMessage());
        } finally {
            finish();
        }
    }
    
    private void startMinimalScreenshotService(int resultCode, Intent resultData) {
        try {
            Intent serviceIntent = new Intent(this, MinimalScreenshotService.class);
            serviceIntent.setAction(MinimalScreenshotService.ACTION_TAKE_SCREENSHOT);
            serviceIntent.putExtra(MinimalScreenshotService.EXTRA_RESULT_CODE, resultCode);
            serviceIntent.putExtra(MinimalScreenshotService.EXTRA_RESULT_DATA, resultData);
            
            Log.d(TAG, "Starting MinimalScreenshotService");
            startForegroundService(serviceIntent);
            
            showToast("開始最小化截圖...");
            
        } catch (Exception e) {
            Log.e(TAG, "Error starting service", e);
            showToast("啟動截圖服務失敗: " + e.getMessage());
        }
    }
    
    private void showToast(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast", e);
        }
    }
}

