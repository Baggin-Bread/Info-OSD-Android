package com.infoosd;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OverlayService extends Service {
    
    private static final String CHANNEL_ID = "OverlayServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    
    private WindowManager windowManager;
    private View overlayView;
    private TextView overlayText;
    private Handler timeUpdateHandler;
    private Runnable timeUpdateRunnable;
    private SettingsManager settingsManager;
    private BatteryReceiver batteryReceiver;
    
    private int batteryLevel = 0;
    private boolean isCharging = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        settingsManager = new SettingsManager(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        timeUpdateHandler = new Handler(Looper.getMainLooper());
        
        createNotificationChannel();
        createOverlayView();
        registerBatteryReceiver();
        startTimeUpdates();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        
        // Handle settings update request
        if (intent != null && intent.getBooleanExtra("update_settings", false)) {
            updateSettings();
        }
        
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
        }
        
        if (timeUpdateHandler != null && timeUpdateRunnable != null) {
            timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
        }
        
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Overlay Service Channel",
                NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setDescription("Channel for overlay service notifications");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
    
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 
                PendingIntent.FLAG_IMMUTABLE : 0
        );
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build();
    }
    
    private void createOverlayView() {
        // Create overlay layout parameters
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        );
        
        // Set position based on user preference
        setOverlayPosition(params);
        
        // Create text view
        overlayText = new TextView(this);
        overlayText.setText(getString(R.string.sample_display_text));
        
        // Apply user settings
        applyUserSettings();
        
        // Add click listener for screenshot functionality
        overlayText.setOnClickListener(v -> {
            android.util.Log.d("OverlayService", "OSD clicked!");
            android.widget.Toast.makeText(this, "OSD 被點擊", android.widget.Toast.LENGTH_SHORT).show();
            
            if (settingsManager.isScreenshotEnabled()) {
                android.util.Log.d("OverlayService", "Screenshot enabled, requesting permission and taking screenshot");
                takeScreenshot();
            } else {
                android.util.Log.d("OverlayService", "Screenshot disabled");
                android.widget.Toast.makeText(this, "截圖功能已關閉", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        
        overlayView = overlayText;
        
        // Add view to window manager
        try {
            windowManager.addView(overlayView, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setOverlayPosition(WindowManager.LayoutParams params) {
        int position = settingsManager.getDisplayPosition();
        
        switch (position) {
            case SettingsManager.POSITION_TOP_LEFT:
                params.gravity = Gravity.TOP | Gravity.START;
                params.x = 20;
                params.y = 100;
                break;
            case SettingsManager.POSITION_TOP_CENTER:
                params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                params.x = 0;
                params.y = 100;
                break;
            case SettingsManager.POSITION_TOP_RIGHT:
                params.gravity = Gravity.TOP | Gravity.END;
                params.x = 20;
                params.y = 100;
                break;
            default:
                params.gravity = Gravity.TOP | Gravity.START;
                params.x = 20;
                params.y = 100;
                break;
        }
    }
    
    private void applyUserSettings() {
        if (overlayText == null) return;
        
        // Apply text size
        float textSize = settingsManager.getTextSize();
        overlayText.setTextSize(textSize);
        
        // Apply text color
        int textColor = settingsManager.getTextColor();
        overlayText.setTextColor(textColor);
        
        // Apply background color
        int backgroundColor = settingsManager.getBackgroundColor();
        overlayText.setBackgroundColor(backgroundColor);
        
        // Set font and shadow for better visibility
        overlayText.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        overlayText.setShadowLayer(2, 1, 1, Color.BLACK);
        
        // Add padding
        int padding = (int) (8 * getResources().getDisplayMetrics().density);
        overlayText.setPadding(padding, padding/2, padding, padding/2);
    }
    
    private void registerBatteryReceiver() {
        batteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        
        registerReceiver(batteryReceiver, filter);
    }
    
    private void startTimeUpdates() {
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateDisplayText();
                timeUpdateHandler.postDelayed(this, 1000); // Update every second
            }
        };
        timeUpdateHandler.post(timeUpdateRunnable);
    }
    
    private void updateDisplayText() {
        if (overlayText == null) return;
        
        // Format current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        
        // Create battery icon based on charging status and level
        String batteryIcon = getBatteryIcon();
        
        // Combine battery and time info
        String displayText = String.format("%s %d%% | %s", 
            batteryIcon, batteryLevel, currentTime);
        
        overlayText.setText(displayText);
    }
    
    private String getBatteryIcon() {
        if (isCharging) {
            return "⚡";
        } else if (batteryLevel > 75) {
            return "🔋";
        } else if (batteryLevel > 50) {
            return "🔋";
        } else if (batteryLevel > 25) {
            return "🪫";
        } else {
            return "🪫";
        }
    }
    
    public void updateSettings() {
        if (overlayView != null && windowManager != null) {
            // Remove current view
            windowManager.removeView(overlayView);
            
            // Recreate with new settings
            createOverlayView();
        }
    }
    
    private void takeScreenshot() {
        android.util.Log.d("OverlayService", "Starting minimal screenshot");
        
        try {
            // 使用最小化的截圖Activity
            Intent screenshotIntent = new Intent(this, MinimalScreenshotActivity.class);
            screenshotIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            screenshotIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            screenshotIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(screenshotIntent);
            
            android.util.Log.d("OverlayService", "Ultra safe screenshot activity started");
            
        } catch (Exception e) {
            android.util.Log.e("OverlayService", "Error starting ultra safe screenshot", e);
            android.widget.Toast.makeText(this, 
                "啟動超級安全截圖失敗: " + e.getMessage(), 
                android.widget.Toast.LENGTH_LONG).show();
        }
    }
    
    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                batteryLevel = (level * 100) / scale;
                
                int status = intent.getIntExtra("status", -1);
                isCharging = (status == 2 || status == 5); // CHARGING or FULL
                
                updateDisplayText();
            }
        }
    }
}

