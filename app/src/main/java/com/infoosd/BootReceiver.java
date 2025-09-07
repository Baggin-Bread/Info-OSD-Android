package com.infoosd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    
    private static final String TAG = "BootReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        
        if (action == null) return;
        
        Log.d(TAG, "Received boot action: " + action);
        
        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
                handleBootCompleted(context);
                break;
            case Intent.ACTION_MY_PACKAGE_REPLACED:
            case Intent.ACTION_PACKAGE_REPLACED:
                handlePackageReplaced(context, intent);
                break;
        }
    }
    
    private void handleBootCompleted(Context context) {
        Log.d(TAG, "Device boot completed");
        
        SettingsManager settingsManager = new SettingsManager(context);
        
        // Only start service if it was enabled before reboot
        if (settingsManager.isServiceEnabled()) {
            startOverlayService(context);
        }
    }
    
    private void handlePackageReplaced(Context context, Intent intent) {
        String packageName = intent.getDataString();
        
        if (packageName != null && packageName.contains(context.getPackageName())) {
            Log.d(TAG, "App package replaced: " + packageName);
            
            SettingsManager settingsManager = new SettingsManager(context);
            
            // Restart service if it was enabled
            if (settingsManager.isServiceEnabled()) {
                startOverlayService(context);
            }
        }
    }
    
    private void startOverlayService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, OverlayService.class);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
            
            Log.d(TAG, "Overlay service started successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to start overlay service", e);
        }
    }
}

