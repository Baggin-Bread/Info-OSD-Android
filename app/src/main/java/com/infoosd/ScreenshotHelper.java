package com.infoosd;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ScreenshotHelper {
    
    private static final String TAG = "ScreenshotHelper";
    
    public static void takeScreenshot(Context context) {
        try {
            // Method 1: Try to trigger screenshot via shell command
            boolean success = triggerScreenshotViaShell();
            
            if (!success) {
                // Method 2: Try to open screenshot via system UI
                success = triggerScreenshotViaSystemUI(context);
            }
            
            if (!success) {
                // Method 3: Show manual instruction
                showManualInstruction(context);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showManualInstruction(context);
        }
    }
    
    private static boolean triggerScreenshotViaShell() {
        try {
            // Try multiple shell commands
            String[] commands = {
                "screencap -p /sdcard/screenshot.png",
                "input keyevent 120", // KEYCODE_SYSRQ
                "input keyevent 26 25", // POWER + VOLUME_DOWN
                "input keyevent 25 26", // VOLUME_DOWN + POWER
                "am start -a android.intent.action.SCREENSHOT"
            };
            
            for (String command : commands) {
                try {
                    Process process = Runtime.getRuntime().exec(command);
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        return true;
                    }
                } catch (Exception e) {
                    // Continue to next command
                    continue;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    private static boolean triggerScreenshotViaSystemUI(Context context) {
        try {
            // Try to open system screenshot UI
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SCREENSHOT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            try {
                // Alternative: Try to open camera app for screenshot
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }
    
    private static void showManualInstruction(Context context) {
        Toast.makeText(context, 
            "自動截圖失敗\n請手動使用：電源鍵 + 音量下鍵\n或下拉通知欄尋找截圖按鈕", 
            Toast.LENGTH_LONG).show();
    }
    
    public static void showScreenshotTip(Context context) {
        Toast.makeText(context, 
            "截圖小提示：\n• 電源鍵 + 音量下鍵\n• 下拉通知欄找截圖按鈕\n• 使用語音助手說「截圖」", 
            Toast.LENGTH_LONG).show();
    }
}

