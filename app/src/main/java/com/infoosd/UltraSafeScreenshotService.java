package com.infoosd;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UltraSafeScreenshotService extends Service {
    
    private static final String TAG = "UltraSafeScreenshot";
    private static final String CHANNEL_ID = "UltraSafeChannel";
    private static final int NOTIFICATION_ID = 4001;
    
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private int screenWidth = 1080;  // 默認值
    private int screenHeight = 1920; // 默認值
    private int screenDensity = 480; // 默認值
    
    private Handler mainHandler;
    private boolean isCapturing = false;
    
    public static final String ACTION_TAKE_SCREENSHOT = "ULTRA_SAFE_SCREENSHOT";
    public static final String EXTRA_RESULT_CODE = "result_code";
    public static final String EXTRA_RESULT_DATA = "result_data";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "UltraSafeScreenshotService created");
        
        mainHandler = new Handler(Looper.getMainLooper());
        
        try {
            createNotificationChannel();
            getScreenMetrics();
            startForeground(NOTIFICATION_ID, createNotification("超級安全截圖服務已啟動"));
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
                if (isCapturing) {
                    Log.w(TAG, "Already capturing, ignoring request");
                    showToast("截圖正在進行中，請稍候");
                    return START_NOT_STICKY;
                }
                
                int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1);
                Intent resultData = intent.getParcelableExtra(EXTRA_RESULT_DATA);
                
                Log.d(TAG, "Received parameters - resultCode: " + resultCode + 
                      ", resultData: " + (resultData != null ? "not null" : "null"));
                
                // 更寬鬆的參數驗證
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Starting ultra safe screenshot with valid result code");
                    isCapturing = true;
                    
                    // 如果resultData為null，創建一個空的Intent
                    final Intent finalResultData;
                    if (resultData == null) {
                        Log.w(TAG, "ResultData is null, creating empty intent");
                        finalResultData = new Intent();
                    } else {
                        finalResultData = resultData;
                    }
                    
                    final int finalResultCode = resultCode;
                    
                    // 延遲執行，確保服務完全啟動
                    mainHandler.postDelayed(() -> {
                        try {
                            takeScreenshotSafely(finalResultCode, finalResultData);
                        } catch (Exception e) {
                            Log.e(TAG, "Error in delayed screenshot", e);
                            handleError("延遲截圖失敗: " + e.getMessage());
                        }
                    }, 500);
                    
                } else {
                    Log.e(TAG, "Invalid result code: " + resultCode);
                    handleError("權限結果代碼無效: " + resultCode);
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
                    "超級安全截圖服務",
                    NotificationManager.IMPORTANCE_LOW
                );
                channel.setDescription("超級安全截圖服務通知");
                
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                    Log.d(TAG, "Notification channel created");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating notification channel", e);
        }
    }
    
    private Notification createNotification(String message) {
        try {
            return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("超級安全截圖")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        } catch (Exception e) {
            Log.e(TAG, "Error creating notification", e);
            // 返回一個基本的通知
            return new Notification();
        }
    }
    
    private void getScreenMetrics() {
        try {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(metrics);
                
                // 驗證獲取的數值
                if (metrics.widthPixels > 0 && metrics.heightPixels > 0) {
                    screenWidth = metrics.widthPixels;
                    screenHeight = metrics.heightPixels;
                    screenDensity = metrics.densityDpi;
                    
                    Log.d(TAG, "Screen metrics - Width: " + screenWidth + 
                          ", Height: " + screenHeight + ", Density: " + screenDensity);
                } else {
                    Log.w(TAG, "Invalid screen metrics, using defaults");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting screen metrics, using defaults", e);
        }
    }
    
    private void takeScreenshotSafely(int resultCode, Intent resultData) {
        try {
            Log.d(TAG, "Starting safe screenshot process");
            
            // 檢查參數
            if (resultData == null) {
                handleError("結果數據為空");
                return;
            }
            
            MediaProjectionManager projectionManager = 
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            
            if (projectionManager == null) {
                handleError("MediaProjectionManager不可用");
                return;
            }
            
            // 創建MediaProjection
            try {
                mediaProjection = projectionManager.getMediaProjection(resultCode, resultData);
                if (mediaProjection == null) {
                    handleError("無法創建MediaProjection");
                    return;
                }
                Log.d(TAG, "MediaProjection created successfully");
            } catch (Exception e) {
                handleError("創建MediaProjection失敗: " + e.getMessage());
                return;
            }
            
            // 創建ImageReader
            try {
                createImageReaderSafely();
                if (imageReader == null) {
                    handleError("無法創建ImageReader");
                    return;
                }
                Log.d(TAG, "ImageReader created successfully");
            } catch (Exception e) {
                handleError("創建ImageReader失敗: " + e.getMessage());
                return;
            }
            
            // 創建VirtualDisplay
            try {
                createVirtualDisplaySafely();
                if (virtualDisplay == null) {
                    handleError("無法創建VirtualDisplay");
                    return;
                }
                Log.d(TAG, "VirtualDisplay created successfully");
            } catch (Exception e) {
                handleError("創建VirtualDisplay失敗: " + e.getMessage());
                return;
            }
            
            Log.d(TAG, "All components created, waiting for image...");
            
            // 設置超時機制
            mainHandler.postDelayed(() -> {
                if (isCapturing) {
                    Log.w(TAG, "Screenshot timeout");
                    handleError("截圖超時");
                }
            }, 10000); // 10秒超時
            
        } catch (Exception e) {
            Log.e(TAG, "Error in takeScreenshotSafely", e);
            handleError("截圖過程出錯: " + e.getMessage());
        }
    }
    
    private void createImageReaderSafely() throws Exception {
        try {
            // 使用保守的參數
            imageReader = ImageReader.newInstance(
                screenWidth, 
                screenHeight, 
                PixelFormat.RGBA_8888, 
                1  // 只使用1個緩衝區
            );
            
            imageReader.setOnImageAvailableListener(safeImageListener, null);
            Log.d(TAG, "ImageReader configured with safe parameters");
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating ImageReader", e);
            throw e;
        }
    }
    
    private void createVirtualDisplaySafely() throws Exception {
        try {
            if (mediaProjection == null || imageReader == null) {
                throw new IllegalStateException("MediaProjection or ImageReader is null");
            }
            
            Log.d(TAG, "Creating VirtualDisplay with parameters:");
            Log.d(TAG, "Width: " + screenWidth + ", Height: " + screenHeight + ", Density: " + screenDensity);
            Log.d(TAG, "Surface: " + (imageReader.getSurface() != null ? "valid" : "null"));
            
            // 嘗試最簡單的創建方法，使用背景線程Handler
            Handler backgroundHandler = new Handler(Looper.getMainLooper());
            
            virtualDisplay = mediaProjection.createVirtualDisplay(
                "UltraSafeScreenshot",
                screenWidth, 
                screenHeight, 
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                new VirtualDisplay.Callback() {
                    @Override
                    public void onPaused() {
                        Log.d(TAG, "VirtualDisplay paused");
                    }
                    
                    @Override
                    public void onResumed() {
                        Log.d(TAG, "VirtualDisplay resumed");
                    }
                    
                    @Override
                    public void onStopped() {
                        Log.d(TAG, "VirtualDisplay stopped");
                    }
                },
                backgroundHandler
            );
            
            if (virtualDisplay != null) {
                Log.d(TAG, "VirtualDisplay created successfully");
            } else {
                throw new RuntimeException("VirtualDisplay creation returned null");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating VirtualDisplay", e);
            throw e;
        }
    }
    
    private final ImageReader.OnImageAvailableListener safeImageListener = 
        new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d(TAG, "Image available - starting safe processing");
            
            Image image = null;
            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Log.d(TAG, "Image acquired successfully");
                    saveImageUltraSafe(image);
                } else {
                    Log.w(TAG, "No image available from reader");
                    handleError("無法獲取圖像");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in image processing", e);
                handleError("圖像處理失敗: " + e.getMessage());
            } finally {
                if (image != null) {
                    try {
                        image.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing image", e);
                    }
                }
                
                // 延遲清理，確保所有操作完成
                mainHandler.postDelayed(() -> {
                    try {
                        cleanup();
                        stopSelf();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in delayed cleanup", e);
                    }
                }, 1000);
            }
        }
    };
    
    private void saveImageUltraSafe(Image image) {
        try {
            Log.d(TAG, "Starting ultra safe image save");
            
            // 檢查圖像有效性
            if (image.getWidth() <= 0 || image.getHeight() <= 0) {
                handleError("圖像尺寸無效");
                return;
            }
            
            Image.Plane[] planes = image.getPlanes();
            if (planes == null || planes.length == 0) {
                handleError("圖像平面無效");
                return;
            }
            
            ByteBuffer buffer = planes[0].getBuffer();
            if (buffer == null || buffer.remaining() == 0) {
                handleError("圖像緩衝區無效");
                return;
            }
            
            Log.d(TAG, "Image validation passed, creating bitmap");
            
            // 創建Bitmap（使用最安全的方法）
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * screenWidth;
            
            Bitmap bitmap = null;
            Bitmap croppedBitmap = null;
            
            try {
                bitmap = Bitmap.createBitmap(
                    screenWidth + rowPadding / pixelStride, 
                    screenHeight, 
                    Bitmap.Config.ARGB_8888
                );
                
                buffer.rewind(); // 重置緩衝區位置
                bitmap.copyPixelsFromBuffer(buffer);
                
                // 裁剪Bitmap
                croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight);
                
                Log.d(TAG, "Bitmap created successfully, saving to file");
                
                // 保存文件
                String fileName = "UltraSafe_" + 
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png";
                
                boolean saved = saveToFileSafely(croppedBitmap, fileName);
                
                if (saved) {
                    showToast("截圖保存成功: " + fileName);
                    Log.d(TAG, "Screenshot saved successfully");
                } else {
                    handleError("文件保存失敗");
                }
                
            } finally {
                // 確保Bitmap被回收
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                if (croppedBitmap != null && !croppedBitmap.isRecycled()) {
                    croppedBitmap.recycle();
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in saveImageUltraSafe", e);
            handleError("保存圖像失敗: " + e.getMessage());
        }
    }
    
    private boolean saveToFileSafely(Bitmap bitmap, String fileName) {
        FileOutputStream fos = null;
        try {
            // 創建保存目錄
            File saveDir = getExternalFilesDir("UltraSafeScreenshots");
            if (saveDir == null) {
                saveDir = new File(getFilesDir(), "UltraSafeScreenshots");
            }
            
            if (!saveDir.exists()) {
                boolean created = saveDir.mkdirs();
                Log.d(TAG, "Directory created: " + created);
            }
            
            File imageFile = new File(saveDir, fileName);
            Log.d(TAG, "Saving to: " + imageFile.getAbsolutePath());
            
            fos = new FileOutputStream(imageFile);
            boolean compressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            
            if (compressed && imageFile.exists() && imageFile.length() > 0) {
                Log.d(TAG, "File saved successfully, size: " + imageFile.length() + " bytes");
                return true;
            } else {
                Log.e(TAG, "File compression failed or file is empty");
                return false;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving file", e);
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing file stream", e);
                }
            }
        }
    }
    
    private void cleanup() {
        try {
            isCapturing = false;
            
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
            
            Log.d(TAG, "Cleanup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
    }
    
    private void handleError(String message) {
        Log.e(TAG, "Error: " + message);
        isCapturing = false;
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
                    Toast.makeText(UltraSafeScreenshotService.this, message, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e(TAG, "Error showing toast", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error posting toast", e);
        }
    }
}

