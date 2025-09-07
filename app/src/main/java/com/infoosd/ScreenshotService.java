package com.infoosd;

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

public class ScreenshotService extends Service {
    
    private static final String TAG = "ScreenshotService";
    private static final String CHANNEL_ID = "ScreenshotServiceChannel";
    private static final int NOTIFICATION_ID = 2001;
    
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private int screenWidth;
    private int screenHeight;
    private int screenDensity;
    
    public static final String ACTION_START_SCREENSHOT = "com.infoosd.START_SCREENSHOT";
    public static final String ACTION_STOP_SCREENSHOT = "com.infoosd.STOP_SCREENSHOT";
    public static final String EXTRA_RESULT_CODE = "result_code";
    public static final String EXTRA_RESULT_DATA = "result_data";
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        getScreenMetrics();
        
        // 立即啟動前台服務，避免ForegroundServiceDidNotStartInTimeException
        startForeground(NOTIFICATION_ID, createNotification("截圖服務已啟動"));
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            
            if (ACTION_START_SCREENSHOT.equals(action)) {
                int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1);
                Intent resultData = intent.getParcelableExtra(EXTRA_RESULT_DATA);
                
                if (resultCode != -1 && resultData != null) {
                    // 更新通知狀態
                    updateNotification("正在準備截圖...");
                    startScreenshot(resultCode, resultData);
                } else {
                    Log.e(TAG, "Invalid screenshot parameters");
                    showError("截圖參數無效");
                    stopSelf();
                }
            } else if (ACTION_STOP_SCREENSHOT.equals(action)) {
                stopScreenshot();
                stopSelf();
            }
        } else {
            Log.e(TAG, "Service started with null intent");
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
        super.onDestroy();
        stopScreenshot();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Screenshot Service Channel",
                NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setDescription("Channel for screenshot service notifications");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
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
    
    private void updateNotification(String message) {
        try {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                Notification notification = createNotification(message);
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating notification", e);
        }
    }
    
    private void getScreenMetrics() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
            screenDensity = metrics.densityDpi;
        }
    }
    
    private void startScreenshot(int resultCode, Intent resultData) {
        try {
            updateNotification("正在初始化截圖組件...");
            
            MediaProjectionManager projectionManager = 
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            
            if (projectionManager != null) {
                mediaProjection = projectionManager.getMediaProjection(resultCode, resultData);
                
                if (mediaProjection != null) {
                    Log.d(TAG, "MediaProjection created successfully");
                    updateNotification("正在創建虛擬顯示...");
                    
                    // Add callback to monitor MediaProjection state
                    mediaProjection.registerCallback(new MediaProjection.Callback() {
                        @Override
                        public void onStop() {
                            super.onStop();
                            Log.d(TAG, "MediaProjection stopped");
                        }
                    }, null);
                    
                    // Create components in sequence with error checking
                    createImageReader();
                    if (imageReader != null) {
                        createVirtualDisplay();
                        if (virtualDisplay != null) {
                            updateNotification("正在捕獲截圖...");
                            // Take screenshot after components are ready
                            captureScreen();
                        } else {
                            Log.e(TAG, "Failed to create VirtualDisplay");
                            showError("無法創建虛擬顯示");
                            stopScreenshot();
                            stopSelf();
                        }
                    } else {
                        Log.e(TAG, "Failed to create ImageReader");
                        showError("無法創建圖像讀取器");
                        stopScreenshot();
                        stopSelf();
                    }
                } else {
                    Log.e(TAG, "Failed to create MediaProjection");
                    showError("無法創建截圖投影");
                    stopSelf();
                }
            } else {
                Log.e(TAG, "MediaProjectionManager is null");
                showError("系統不支援截圖功能");
                stopSelf();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception starting screenshot", e);
            showError("截圖權限不足");
            stopSelf();
        } catch (Exception e) {
            Log.e(TAG, "Error starting screenshot", e);
            showError("啟動截圖失敗: " + e.getMessage());
            stopSelf();
        }
    }
    
    private void createImageReader() {
        // Use RGBA_8888 format for better compatibility
        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Log.d(TAG, "onImageAvailable called!");
                
                Image image = null;
                try {
                    image = reader.acquireLatestImage();
                    if (image != null) {
                        Log.d(TAG, "Image acquired successfully - Width: " + image.getWidth() + 
                              ", Height: " + image.getHeight() + ", Format: " + image.getFormat());
                        
                        // Check if image has valid data
                        Image.Plane[] planes = image.getPlanes();
                        if (planes != null && planes.length > 0) {
                            ByteBuffer buffer = planes[0].getBuffer();
                            Log.d(TAG, "Image buffer size: " + buffer.remaining() + " bytes");
                            
                            if (buffer.remaining() > 0) {
                                saveImage(image);
                            } else {
                                Log.e(TAG, "Image buffer is empty");
                                showError("截圖數據為空");
                            }
                        } else {
                            Log.e(TAG, "No image planes available");
                            showError("圖像數據無效");
                        }
                    } else {
                        Log.w(TAG, "No image available from ImageReader");
                        showError("截圖捕獲失敗：無圖像數據");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing image", e);
                    showError("處理截圖失敗: " + e.getMessage());
                } finally {
                    if (image != null) {
                        image.close();
                    }
                    // Stop the service after taking screenshot with delay
                    new Handler().postDelayed(() -> {
                        Log.d(TAG, "Stopping screenshot service...");
                        stopScreenshot();
                        stopSelf();
                    }, 3000);
                }
            }
        }, null);
        
        Log.d(TAG, "ImageReader created - Width: " + screenWidth + ", Height: " + screenHeight);
    }
    
    private void createVirtualDisplay() {
        if (mediaProjection == null || imageReader == null) {
            Log.e(TAG, "MediaProjection or ImageReader is null");
            showError("創建虛擬顯示失敗：缺少必要組件");
            return;
        }
        
        try {
            virtualDisplay = mediaProjection.createVirtualDisplay(
                "InfoOSD_ScreenCapture",
                screenWidth, 
                screenHeight, 
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                new VirtualDisplay.Callback() {
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
                }, 
                null
            );
            
            if (virtualDisplay != null) {
                Log.d(TAG, "VirtualDisplay created successfully");
            } else {
                Log.e(TAG, "Failed to create VirtualDisplay");
                showError("創建虛擬顯示失敗");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating VirtualDisplay", e);
            showError("創建虛擬顯示失敗: " + e.getMessage());
        }
    }
    
    private void captureScreen() {
        if (imageReader == null || virtualDisplay == null) {
            Log.e(TAG, "ImageReader or VirtualDisplay is null, cannot capture");
            showError("截圖失敗：組件未初始化");
            return;
        }
        
        Log.d(TAG, "Triggering screen capture...");
        
        // Add a longer delay to ensure the virtual display is ready
        new Handler().postDelayed(() -> {
            try {
                // The image will be captured automatically when the virtual display renders
                Log.d(TAG, "Virtual display should now be capturing...");
                
                // Add a timeout mechanism
                new Handler().postDelayed(() -> {
                    if (imageReader != null) {
                        Log.w(TAG, "Screenshot timeout, attempting manual trigger");
                        // Try to manually trigger image capture if needed
                    }
                }, 5000); // 5 second timeout
                
            } catch (Exception e) {
                Log.e(TAG, "Error during screen capture", e);
                showError("截圖過程中發生錯誤: " + e.getMessage());
            }
        }, 1000); // Wait 1 second for virtual display to be ready
    }
    
    private void saveImage(Image image) {
        Log.d(TAG, "Starting to save image...");
        updateNotification("正在保存截圖文件...");
        
        try {
            Image.Plane[] planes = image.getPlanes();
            if (planes == null || planes.length == 0) {
                Log.e(TAG, "No image planes available");
                showError("圖像數據無效");
                return;
            }
            
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * screenWidth;
            
            Log.d(TAG, "Image info - Width: " + screenWidth + ", Height: " + screenHeight + 
                  ", PixelStride: " + pixelStride + ", RowStride: " + rowStride);
            
            // Create bitmap from image buffer
            Bitmap bitmap = Bitmap.createBitmap(
                screenWidth + rowPadding / pixelStride, 
                screenHeight, 
                Bitmap.Config.ARGB_8888
            );
            bitmap.copyPixelsFromBuffer(buffer);
            
            // Crop the bitmap to remove padding
            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight);
            
            // Generate filename with timestamp
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "InfoOSD_Screenshot_" + timeStamp + ".png";
            
            Log.d(TAG, "Generated filename: " + fileName);
            
            // Try multiple save locations
            String savedPath = null;
            
            // Method 1: Save to app's external files directory (no permission needed)
            savedPath = saveToAppExternalDir(croppedBitmap, fileName);
            
            // Method 2: If external dir fails, save to internal storage
            if (savedPath == null) {
                savedPath = saveToInternalDir(croppedBitmap, fileName);
            }
            
            // Method 3: Try MediaStore for newer Android versions
            if (savedPath == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                savedPath = saveUsingMediaStore(croppedBitmap, fileName);
            }
            
            bitmap.recycle();
            croppedBitmap.recycle();
            
            if (savedPath != null) {
                Log.d(TAG, "Screenshot saved successfully: " + savedPath);
                updateNotification("截圖保存成功！");
                showSuccess("截圖已保存: " + fileName + "\n位置: " + savedPath);
            } else {
                Log.e(TAG, "All save methods failed");
                updateNotification("截圖保存失敗");
                showError("保存截圖失敗：所有保存方法都失敗了");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving image", e);
            updateNotification("截圖保存失敗");
            showError("保存截圖失敗: " + e.getMessage());
        }
    }
    
    private String saveToAppExternalDir(Bitmap bitmap, String fileName) {
        try {
            Log.d(TAG, "Attempting to save to app external directory...");
            
            File externalDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (externalDir == null) {
                Log.w(TAG, "External files directory not available");
                return null;
            }
            
            File screenshotDir = new File(externalDir, "Screenshots");
            if (!screenshotDir.exists()) {
                boolean created = screenshotDir.mkdirs();
                Log.d(TAG, "Screenshot directory created: " + created);
            }
            
            File imageFile = new File(screenshotDir, fileName);
            Log.d(TAG, "Saving to: " + imageFile.getAbsolutePath());
            
            FileOutputStream fos = new FileOutputStream(imageFile);
            boolean compressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            
            if (compressed && imageFile.exists() && imageFile.length() > 0) {
                Log.d(TAG, "File saved successfully, size: " + imageFile.length() + " bytes");
                return imageFile.getAbsolutePath();
            } else {
                Log.e(TAG, "File compression failed or file is empty");
                return null;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving to app external directory", e);
            return null;
        }
    }
    
    private String saveToInternalDir(Bitmap bitmap, String fileName) {
        try {
            Log.d(TAG, "Attempting to save to internal directory...");
            
            File internalDir = new File(getFilesDir(), "Screenshots");
            if (!internalDir.exists()) {
                boolean created = internalDir.mkdirs();
                Log.d(TAG, "Internal screenshot directory created: " + created);
            }
            
            File imageFile = new File(internalDir, fileName);
            Log.d(TAG, "Saving to internal: " + imageFile.getAbsolutePath());
            
            FileOutputStream fos = new FileOutputStream(imageFile);
            boolean compressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            
            if (compressed && imageFile.exists() && imageFile.length() > 0) {
                Log.d(TAG, "Internal file saved successfully, size: " + imageFile.length() + " bytes");
                return imageFile.getAbsolutePath();
            } else {
                Log.e(TAG, "Internal file compression failed or file is empty");
                return null;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving to internal directory", e);
            return null;
        }
    }
    
    private String saveUsingMediaStore(Bitmap bitmap, String fileName) {
        try {
            Log.d(TAG, "Attempting to save using MediaStore...");
            
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Screenshots");
            }
            
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            
            if (imageUri != null) {
                OutputStream outputStream = resolver.openOutputStream(imageUri);
                if (outputStream != null) {
                    boolean compressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    
                    if (compressed) {
                        Log.d(TAG, "MediaStore save successful: " + imageUri.toString());
                        return imageUri.toString();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving using MediaStore", e);
        }
        return null;
    }
    
    private void stopScreenshot() {
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
    }
    
    private void showSuccess(String message) {
        new Handler(getMainLooper()).post(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            
            // Create success notification
            createSuccessNotification(message);
        });
    }
    
    private void showError(String message) {
        new Handler(getMainLooper()).post(() -> {
            Toast.makeText(this, "錯誤: " + message, Toast.LENGTH_LONG).show();
            
            // Create error notification
            createErrorNotification(message);
        });
    }
    
    private void createSuccessNotification(String message) {
        try {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("截圖成功")
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();
                
                notificationManager.notify(NOTIFICATION_ID + 1, notification);
                
                // Auto-dismiss after 3 seconds
                new Handler().postDelayed(() -> {
                    notificationManager.cancel(NOTIFICATION_ID + 1);
                }, 3000);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating success notification", e);
        }
    }
    
    private void createErrorNotification(String message) {
        try {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("截圖失敗")
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
                
                notificationManager.notify(NOTIFICATION_ID + 2, notification);
                
                // Auto-dismiss after 5 seconds
                new Handler().postDelayed(() -> {
                    notificationManager.cancel(NOTIFICATION_ID + 2);
                }, 5000);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating error notification", e);
        }
    }
}

