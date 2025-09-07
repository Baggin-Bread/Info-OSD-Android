package com.infoosd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BatteryReceiver extends BroadcastReceiver {
    
    private static final String TAG = "BatteryReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        
        if (action == null) return;
        
        Log.d(TAG, "Received battery action: " + action);
        
        switch (action) {
            case Intent.ACTION_BATTERY_CHANGED:
                handleBatteryChanged(context, intent);
                break;
            case Intent.ACTION_BATTERY_LOW:
                handleBatteryLow(context, intent);
                break;
            case Intent.ACTION_BATTERY_OKAY:
                handleBatteryOkay(context, intent);
                break;
            case Intent.ACTION_POWER_CONNECTED:
                handlePowerConnected(context, intent);
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                handlePowerDisconnected(context, intent);
                break;
        }
    }
    
    private void handleBatteryChanged(Context context, Intent intent) {
        int level = intent.getIntExtra("level", 0);
        int scale = intent.getIntExtra("scale", 100);
        int percentage = (level * 100) / scale;
        
        int status = intent.getIntExtra("status", -1);
        boolean isCharging = (status == 2 || status == 5); // CHARGING or FULL
        
        int health = intent.getIntExtra("health", 1);
        int temperature = intent.getIntExtra("temperature", 0);
        int voltage = intent.getIntExtra("voltage", 0);
        
        Log.d(TAG, String.format("Battery: %d%%, Charging: %b, Health: %d, Temp: %d, Voltage: %d", 
            percentage, isCharging, health, temperature, voltage));
        
        // Send battery info to overlay service if running
        Intent serviceIntent = new Intent(context, OverlayService.class);
        serviceIntent.putExtra("battery_level", percentage);
        serviceIntent.putExtra("is_charging", isCharging);
        serviceIntent.putExtra("battery_health", health);
        serviceIntent.putExtra("battery_temperature", temperature);
        serviceIntent.putExtra("battery_voltage", voltage);
        
        // Only send if service should be running
        SettingsManager settingsManager = new SettingsManager(context);
        if (settingsManager.isServiceEnabled()) {
            try {
                context.startService(serviceIntent);
            } catch (Exception e) {
                Log.e(TAG, "Failed to start overlay service", e);
            }
        }
    }
    
    private void handleBatteryLow(Context context, Intent intent) {
        Log.d(TAG, "Battery low warning received");
        // Could add special handling for low battery
    }
    
    private void handleBatteryOkay(Context context, Intent intent) {
        Log.d(TAG, "Battery okay status received");
        // Could add special handling for battery okay
    }
    
    private void handlePowerConnected(Context context, Intent intent) {
        Log.d(TAG, "Power connected");
        // Update charging status
        updateChargingStatus(context, true);
    }
    
    private void handlePowerDisconnected(Context context, Intent intent) {
        Log.d(TAG, "Power disconnected");
        // Update charging status
        updateChargingStatus(context, false);
    }
    
    private void updateChargingStatus(Context context, boolean isCharging) {
        Intent serviceIntent = new Intent(context, OverlayService.class);
        serviceIntent.putExtra("charging_status_changed", true);
        serviceIntent.putExtra("is_charging", isCharging);
        
        SettingsManager settingsManager = new SettingsManager(context);
        if (settingsManager.isServiceEnabled()) {
            try {
                context.startService(serviceIntent);
            } catch (Exception e) {
                Log.e(TAG, "Failed to update charging status", e);
            }
        }
    }
}

