package com.infoosd;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MinimalScreenshotService extends Service {
    
    private static final String TAG = "MinimalScreenshot";
    private static final String CHANNEL_ID = "MinimalChannel";
    private static final int NOTIFICATION_ID = 5001;
    
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private int screenWidth = 1080;
    private int screenHeight = 1920;
    private int screenDensity = 420;
    
    private Handler mainHandler;
    
    public static final String ACTION_TAKE_SCREENSHOT = "MINIMAL_SCREENSHOT";
    public static final String EXTRA_RESULT_CODE = "result_code";
    public static final String EXTRA_RESULT_DATA = "result_data";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MinimalScreenshotService created");
        
        mainHandler = new Handler(Looper.getMainLooper());
        
        try {
            createNotificationChannel();
            getScreenMetrics();
            startForeground(NOTIFICATION_ID, createNotification("最小化截圖服務已啟動"));
            Log.d(TAG, "Service initialization completed");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            stopSelf();
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");
        
        try {
            if (intent != null && ACTION_TAKE_SCREENSHOT.equals(intent.getAction())) {
                int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1);
                Intent resultData = intent.getParcelableExtra(EXTRA_RESULT_DATA);
                
                Log.d(TAG, "Received parameters - resultCode: " + resultCode);
                
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Starting minimal screenshot");
                    
                    final Intent finalResultData = (resultData != null) ? resultData : new Intent();
                    final int finalResultCode = resultCode;
                    
                    // 延遲執行
                    mainHandler.postDelayed(() -> {
                        try {
                            takeMinimalScreenshot(finalResultCode, finalResultData);
                        } catch (Exception e) {
                            Log.e(TAG, "Error in screenshot", e);
                            handleError("截圖失敗: " + e.getMessage());
                        }
                    }, 1000);
                    
                } else {
                    Log.e(TAG, "Invalid result code: " + resultCode);
                    handleError("權限結果代碼無效");
                }
            } else {
                Log.e(TAG, "Invalid action or null intent");
                handleError("服務請求無效");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onStartCommand", e);
            handleError("服務啟動失敗: " + e.getMessage());
        }
        
        return START_NOT_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
        try {
            cleanup();
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
        }
        super.onDestroy();
    }
    
    private void createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "最小化截圖服務",
                    NotificationManager.IMPORTANCE_LOW
                );
                
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating notification channel", e);
        }
    }
    
    private Notification createNotification(String message) {
        try {
            return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("最小化截圖")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        } catch (Exception e) {
            Log.e(TAG, "Error creating notification", e);
            return new Notification();
        }
    }
    
    private void getScreenMetrics() {
        try {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(metrics);
                
                if (metrics.widthPixels > 0 && metrics.heightPixels > 0) {
                    screenWidth = metrics.widthPixels;
                    screenHeight = metrics.heightPixels;
                    screenDensity = metrics.densityDpi;
                    
                    Log.d(TAG, "Screen metrics - Width: " + screenWidth + 
                          ", Height: " + screenHeight + ", Density: " + screenDensity);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting screen metrics", e);
        }
    }
    
    private void takeMinimalScreenshot(int resultCode, Intent resultData) {
        try {
            Log.d(TAG, "Starting minimal screenshot process");
            
            // 步驟1: 創建MediaProjection
            MediaProjectionManager projectionManager = 
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            
            if (projectionManager == null) {
                handleError("MediaProjectionManager不可用");
                return;
            }
            
            mediaProjection = projectionManager.getMediaProjection(resultCode, resultData);
            if (mediaProjection == null) {
                handleError("無法創建MediaProjection");
                return;
            }
            
            // 重要：為MediaProjection註冊回調
            MediaProjection.Callback projectionCallback = new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    super.onStop();
                    Log.d(TAG, "MediaProjection stopped");
                    // 清理資源
                    cleanup();
                }
            };
            
            try {
                mediaProjection.registerCallback(projectionCallback, mainHandler);
                Log.d(TAG, "MediaProjection callback registered successfully");
                
                // 等待回調註冊生效
                Thread.sleep(100);
                
                Log.d(TAG, "MediaProjection created and callback registered successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to register MediaProjection callback", e);
                handleError("MediaProjection回調註冊失敗: " + e.getMessage());
                return;
            }
            
            // 步驟2: 創建ImageReader
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);
            imageReader.setOnImageAvailableListener(minimalImageListener, mainHandler);
            Log.d(TAG, "ImageReader created successfully");
            
            // 步驟3: 創建VirtualDisplay（確保提供回調）
            try {
                Log.d(TAG, "Creating VirtualDisplay with proper callbacks...");
                
                // 創建VirtualDisplay回調
                VirtualDisplay.Callback displayCallback = new VirtualDisplay.Callback() {
                    @Override
                    public void onPaused() {
                        super.onPaused();
                        Log.d(TAG, "VirtualDisplay paused");
                    }
                    
                    @Override
                    public void onResumed() {
                        super.onResumed();
                        Log.d(TAG, "VirtualDisplay resumed");
                    }
                    
                    @Override
                    public void onStopped() {
                        super.onStopped();
                        Log.d(TAG, "VirtualDisplay stopped");
                    }
                };
                
                virtualDisplay = mediaProjection.createVirtualDisplay(
                    "MinimalScreenshot",
                    screenWidth,
                    screenHeight,
                    screenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader.getSurface(),
                    displayCallback,  // 提供VirtualDisplay回調
                    mainHandler       // 使用主線程Handler
                );
                
                if (virtualDisplay != null) {
                    Log.d(TAG, "VirtualDisplay created successfully with proper callbacks");
                } else {
                    handleError("VirtualDisplay創建返回null");
                    return;
                }
                
            } catch (Exception e) {
                Log.e(TAG, "VirtualDisplay creation failed", e);
                handleError("VirtualDisplay創建失敗: " + e.getMessage());
                return;
            }
            
            // 設置超時
            mainHandler.postDelayed(() -> {
                Log.w(TAG, "Screenshot timeout");
                handleError("截圖超時");
            }, 10000);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in takeMinimalScreenshot", e);
            handleError("截圖過程出錯: " + e.getMessage());
        }
    }
    
    private final ImageReader.OnImageAvailableListener minimalImageListener = 
        new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d(TAG, "Image available - starting minimal processing");
            
            Image image = null;
            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Log.d(TAG, "Image acquired successfully");
                    saveImageMinimal(image);
                } else {
                    Log.w(TAG, "No image available, retrying...");
                    // 不顯示錯誤訊息，因為可能是時序問題
                    // 讓超時機制處理
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing image", e);
                // 只有在真正失敗時才顯示錯誤
                if (image == null) {
                    Log.w(TAG, "Image acquisition failed, but screenshot may still succeed");
                } else {
                    handleError("圖像處理失敗: " + e.getMessage());
                }
            } finally {
                if (image != null) {
                    try {
                        image.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing image", e);
                    }
                }
            }
        }
    };
    
    private void saveImageMinimal(Image image) {
        try {
            Log.d(TAG, "Starting minimal image save");
            
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * screenWidth;
            
            Bitmap bitmap = Bitmap.createBitmap(
                screenWidth + rowPadding / pixelStride, 
                screenHeight, 
                Bitmap.Config.ARGB_8888
            );
            bitmap.copyPixelsFromBuffer(buffer);
            
            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight);
            
            // 保存到系統相簿
            saveImageToGallery(croppedBitmap);
            
            // 清理Bitmap
            bitmap.recycle();
            croppedBitmap.recycle();
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving image", e);
            handleError("保存圖像失敗: " + e.getMessage());
        }
    }
    
    private void saveImageToGallery(Bitmap bitmap) {
        try {
            Log.d(TAG, "Starting gallery image save");
            
            // 生成文件名
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "InfoOSD_" + timeStamp + ".png";
            
            // 使用MediaStore API保存到系統相簿
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Screenshots");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
            
            ContentResolver resolver = getContentResolver();
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            
            if (imageUri != null) {
                try (OutputStream out = resolver.openOutputStream(imageUri)) {
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        
                        // 標記為完成
                        values.clear();
                        values.put(MediaStore.Images.Media.IS_PENDING, 0);
                        resolver.update(imageUri, values, null, null);
                        
                        Log.d(TAG, "Screenshot saved to gallery successfully: " + fileName);
                        showToast("截圖已保存到相簿: " + fileName);
                        
                        // 截圖成功，立即停止服務
                        stopSelfSafely();
                        return;
                    }
                }
            }
            
            // 如果MediaStore失敗，嘗試保存到應用目錄作為備用
            saveImageToAppDirectory(bitmap);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to save to gallery", e);
            // 備用方案：保存到應用目錄
            saveImageToAppDirectory(bitmap);
        }
    }
    
    private void saveImageToAppDirectory(Bitmap bitmap) {
        try {
            Log.d(TAG, "Saving to app directory as fallback");
            
            // 創建應用目錄
            File screenshotsDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }
            
            // 生成文件名
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "InfoOSD_" + timeStamp + ".png";
            File imageFile = new File(screenshotsDir, fileName);
            
            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                
                Log.d(TAG, "Screenshot saved to app directory: " + imageFile.getAbsolutePath());
                showToast("截圖已保存: " + fileName);
                
                // 截圖成功，立即停止服務
                stopSelfSafely();
                
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to save to app directory", e);
            showToast("截圖保存失敗: " + e.getMessage());
            stopSelfSafely();
        }
    }
    
    private void stopSelfSafely() {
        try {
            // 取消超時檢查
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
            }
            
            // 清理資源
            cleanup();
            
            // 停止服務
            stopSelf();
            
            Log.d(TAG, "Service stopped safely");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping service", e);
        }
    }
    
    private void cleanup() {
        try {
            if (virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
                Log.d(TAG, "VirtualDisplay released");
            }
            
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
                Log.d(TAG, "ImageReader closed");
            }
            
            if (mediaProjection != null) {
                mediaProjection.stop();
                mediaProjection = null;
                Log.d(TAG, "MediaProjection stopped");
            }
            
            Log.d(TAG, "Cleanup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
    }
    
    private void handleError(String message) {
        Log.e(TAG, "Error: " + message);
        showToast("截圖失敗: " + message);
        
        try {
            cleanup();
        } catch (Exception e) {
            Log.e(TAG, "Error in cleanup after error", e);
        }
        
        stopSelf();
    }
    
    private void showToast(String message) {
        try {
            mainHandler.post(() -> {
                try {
                    Toast.makeText(MinimalScreenshotService.this, message, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e(TAG, "Error showing toast", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error posting toast", e);
        }
    }
}

