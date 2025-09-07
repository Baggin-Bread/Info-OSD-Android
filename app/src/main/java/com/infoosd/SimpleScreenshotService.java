package com.infoosd;

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

public class SimpleScreenshotService extends Service {
    
    private static final String TAG = "SimpleScreenshotService";
    private static final String CHANNEL_ID = "SimpleScreenshotChannel";
    private static final int NOTIFICATION_ID = 3001;
    
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private int screenWidth;
    private int screenHeight;
    private int screenDensity;
    
    public static final String ACTION_TAKE_SCREENSHOT = "TAKE_SCREENSHOT";
    public static final String EXTRA_RESULT_CODE = "result_code";
    public static final String EXTRA_RESULT_DATA = "result_data";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        
        createNotificationChannel();
        getScreenMetrics();
        
        // 立即啟動前台服務
        startForeground(NOTIFICATION_ID, createNotification("截圖服務準備就緒"));
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");
        
        if (intent != null && ACTION_TAKE_SCREENSHOT.equals(intent.getAction())) {
            int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1);
            Intent resultData = intent.getParcelableExtra(EXTRA_RESULT_DATA);
            
            if (resultCode != -1 && resultData != null) {
                Log.d(TAG, "Starting screenshot with valid parameters");
                takeScreenshot(resultCode, resultData);
            } else {
                Log.e(TAG, "Invalid parameters");
                showToast("截圖參數無效");
                stopSelf();
            }
        } else {
            Log.e(TAG, "Invalid action or null intent");
            stopSelf();
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
        cleanup();
        super.onDestroy();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "簡單截圖服務",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("簡單截圖服務通知");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    private Notification createNotification(String message) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("截圖服務")
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }
    
    private void getScreenMetrics() {
        try {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(metrics);
                screenWidth = metrics.widthPixels;
                screenHeight = metrics.heightPixels;
                screenDensity = metrics.densityDpi;
                
                Log.d(TAG, "Screen metrics - Width: " + screenWidth + ", Height: " + screenHeight + ", Density: " + screenDensity);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting screen metrics", e);
            // 使用默認值
            screenWidth = 1080;
            screenHeight = 1920;
            screenDensity = 480;
        }
    }
    
    private void takeScreenshot(int resultCode, Intent resultData) {
        try {
            Log.d(TAG, "Taking screenshot...");
            
            MediaProjectionManager projectionManager = 
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            
            if (projectionManager == null) {
                Log.e(TAG, "MediaProjectionManager is null");
                showToast("系統不支援截圖功能");
                stopSelf();
                return;
            }
            
            mediaProjection = projectionManager.getMediaProjection(resultCode, resultData);
            if (mediaProjection == null) {
                Log.e(TAG, "Failed to create MediaProjection");
                showToast("無法創建截圖投影");
                stopSelf();
                return;
            }
            
            Log.d(TAG, "MediaProjection created successfully");
            
            // 創建ImageReader
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);
            imageReader.setOnImageAvailableListener(imageAvailableListener, null);
            
            // 創建VirtualDisplay
            virtualDisplay = mediaProjection.createVirtualDisplay(
                "SimpleScreenshot",
                screenWidth, screenHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null, null
            );
            
            if (virtualDisplay == null) {
                Log.e(TAG, "Failed to create VirtualDisplay");
                showToast("無法創建虛擬顯示");
                cleanup();
                stopSelf();
                return;
            }
            
            Log.d(TAG, "VirtualDisplay created successfully");
            
            // 等待一段時間讓虛擬顯示準備好
            new Handler().postDelayed(() -> {
                Log.d(TAG, "Screenshot should be captured now");
            }, 1000);
            
        } catch (Exception e) {
            Log.e(TAG, "Error taking screenshot", e);
            showToast("截圖失敗: " + e.getMessage());
            cleanup();
            stopSelf();
        }
    }
    
    private final ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d(TAG, "Image available!");
            
            Image image = null;
            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Log.d(TAG, "Image acquired, saving...");
                    saveImageSimple(image);
                } else {
                    Log.w(TAG, "No image available");
                    showToast("無法獲取截圖");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing image", e);
                showToast("處理截圖失敗");
            } finally {
                if (image != null) {
                    image.close();
                }
                
                // 延遲停止服務
                new Handler().postDelayed(() -> {
                    cleanup();
                    stopSelf();
                }, 2000);
            }
        }
    };
    
    private void saveImageSimple(Image image) {
        try {
            Log.d(TAG, "Starting to save image");
            
            // 獲取圖像數據
            Image.Plane[] planes = image.getPlanes();
            if (planes == null || planes.length == 0) {
                Log.e(TAG, "No image planes");
                showToast("圖像數據無效");
                return;
            }
            
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * screenWidth;
            
            // 創建Bitmap
            Bitmap bitmap = Bitmap.createBitmap(
                screenWidth + rowPadding / pixelStride, 
                screenHeight, 
                Bitmap.Config.ARGB_8888
            );
            bitmap.copyPixelsFromBuffer(buffer);
            
            // 裁剪Bitmap
            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight);
            
            // 生成文件名
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Simple_Screenshot_" + timeStamp + ".png";
            
            // 保存到應用外部目錄
            File externalDir = getExternalFilesDir("Screenshots");
            if (externalDir == null) {
                // 備用：保存到內部目錄
                externalDir = new File(getFilesDir(), "Screenshots");
            }
            
            if (!externalDir.exists()) {
                externalDir.mkdirs();
            }
            
            File imageFile = new File(externalDir, fileName);
            
            FileOutputStream fos = new FileOutputStream(imageFile);
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            
            // 清理Bitmap
            bitmap.recycle();
            croppedBitmap.recycle();
            
            Log.d(TAG, "Image saved: " + imageFile.getAbsolutePath());
            showToast("截圖已保存: " + fileName + "\n" + imageFile.getAbsolutePath());
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving image", e);
            showToast("保存截圖失敗: " + e.getMessage());
        }
    }
    
    private void cleanup() {
        try {
            if (virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
            }
            
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }
            
            if (mediaProjection != null) {
                mediaProjection.stop();
                mediaProjection = null;
            }
            
            Log.d(TAG, "Cleanup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
    }
    
    private void showToast(String message) {
        new Handler(getMainLooper()).post(() -> 
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        );
    }
}

