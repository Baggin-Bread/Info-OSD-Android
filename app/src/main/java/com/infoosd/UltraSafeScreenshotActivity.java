package com.infoosd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class UltraSafeScreenshotActivity extends Activity {
    
    private static final String TAG = "UltraSafeActivity";
    private static final int REQUEST_SCREENSHOT = 3001;
    
    private Handler mainHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "UltraSafeScreenshotActivity created");
        
        mainHandler = new Handler();
        
        try {
            // 設置透明主題
            setTheme(android.R.style.Theme_Translucent_NoTitleBar);
            
            // 延遲請求權限，確保Activity完全初始化
            mainHandler.postDelayed(() -> {
                try {
                    requestScreenshotPermissionSafely();
                } catch (Exception e) {
                    Log.e(TAG, "Error in delayed permission request", e);
                    showToastAndFinish("權限請求失敗: " + e.getMessage());
                }
            }, 100);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            showToastAndFinish("Activity初始化失敗");
        }
    }
    
    private void requestScreenshotPermissionSafely() {
        try {
            Log.d(TAG, "Requesting screenshot permission safely");
            
            MediaProjectionManager projectionManager = 
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            
            if (projectionManager == null) {
                Log.e(TAG, "MediaProjectionManager is null");
                showToastAndFinish("系統不支援截圖功能");
                return;
            }
            
            Intent permissionIntent = projectionManager.createScreenCaptureIntent();
            if (permissionIntent == null) {
                Log.e(TAG, "Permission intent is null");
                showToastAndFinish("無法創建權限請求");
                return;
            }
            
            Log.d(TAG, "Starting permission activity");
            startActivityForResult(permissionIntent, REQUEST_SCREENSHOT);
            
        } catch (Exception e) {
            Log.e(TAG, "Error requesting permission", e);
            showToastAndFinish("請求截圖權限失敗: " + e.getMessage());
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Log.d(TAG, "onActivityResult - requestCode: " + requestCode + 
              ", resultCode: " + resultCode + ", data: " + (data != null));
        
        try {
            if (requestCode == REQUEST_SCREENSHOT) {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Log.d(TAG, "Permission granted successfully with data");
                        startUltraSafeScreenshotService(resultCode, data);
                    } else {
                        Log.w(TAG, "Permission granted but data is null, trying without data");
                        // 某些設備可能不返回data，但resultCode是OK的
                        startUltraSafeScreenshotService(resultCode, new Intent());
                    }
                } else {
                    Log.d(TAG, "Permission denied, resultCode: " + resultCode);
                    showToastAndFinish("截圖權限被拒絕");
                }
            } else {
                Log.w(TAG, "Unknown request code: " + requestCode);
                showToastAndFinish("未知的權限請求");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onActivityResult", e);
            showToastAndFinish("處理權限結果失敗: " + e.getMessage());
        }
    }
    
    private void startUltraSafeScreenshotService(int resultCode, Intent data) {
        try {
            Log.d(TAG, "Starting ultra safe screenshot service");
            Log.d(TAG, "ResultCode: " + resultCode);
            Log.d(TAG, "Data: " + (data != null ? data.toString() : "null"));
            
            // 驗證參數
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "Invalid result code: " + resultCode);
                showToastAndFinish("權限結果代碼無效");
                return;
            }
            
            // 創建服務Intent
            Intent serviceIntent = new Intent(this, UltraSafeScreenshotService.class);
            serviceIntent.setAction(UltraSafeScreenshotService.ACTION_TAKE_SCREENSHOT);
            serviceIntent.putExtra(UltraSafeScreenshotService.EXTRA_RESULT_CODE, resultCode);
            
            // 安全地添加data
            if (data != null) {
                try {
                    serviceIntent.putExtra(UltraSafeScreenshotService.EXTRA_RESULT_DATA, data);
                    Log.d(TAG, "Data added to service intent");
                } catch (Exception e) {
                    Log.e(TAG, "Error adding data to intent", e);
                    // 如果data有問題，創建一個空的Intent
                    serviceIntent.putExtra(UltraSafeScreenshotService.EXTRA_RESULT_DATA, new Intent());
                }
            } else {
                // 如果data為null，創建一個空的Intent
                serviceIntent.putExtra(UltraSafeScreenshotService.EXTRA_RESULT_DATA, new Intent());
                Log.d(TAG, "Created empty data intent");
            }
            
            // 啟動服務
            startForegroundService(serviceIntent);
            
            Toast.makeText(this, "正在進行超級安全截圖...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Service started successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error starting service", e);
            showToastAndFinish("啟動截圖服務失敗: " + e.getMessage());
            return;
        }
        
        // 延遲結束Activity，確保服務完全啟動
        mainHandler.postDelayed(() -> {
            try {
                finish();
            } catch (Exception e) {
                Log.e(TAG, "Error finishing activity", e);
            }
        }, 500);
    }
    
    private void showToastAndFinish(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Toast shown: " + message);
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast", e);
        }
        
        // 延遲結束，確保Toast顯示
        mainHandler.postDelayed(() -> {
            try {
                finish();
            } catch (Exception e) {
                Log.e(TAG, "Error finishing activity", e);
            }
        }, 1000);
    }
    
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back pressed");
        try {
            super.onBackPressed();
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error in onBackPressed", e);
        }
    }
    
    @Override
    protected void onDestroy() {
        Log.d(TAG, "Activity destroyed");
        try {
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
        }
        super.onDestroy();
    }
}

