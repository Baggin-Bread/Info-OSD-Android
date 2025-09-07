package com.infoosd;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class SettingsManager {
    
    private static final String PREFS_NAME = "InfoOSDSettings";
    
    // Setting keys
    private static final String KEY_TEXT_SIZE = "text_size";
    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_DISPLAY_POSITION = "display_position";
    private static final String KEY_SERVICE_ENABLED = "service_enabled";
    private static final String KEY_SCREENSHOT_ENABLED = "screenshot_enabled";
    
    // Position constants
    public static final int POSITION_TOP_LEFT = 0;
    public static final int POSITION_TOP_CENTER = 1;
    public static final int POSITION_TOP_RIGHT = 2;
    
    // Default values
    private static final float DEFAULT_TEXT_SIZE = 16.0f;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_BACKGROUND_COLOR = 0x80000000; // Semi-transparent black
    private static final int DEFAULT_DISPLAY_POSITION = POSITION_TOP_LEFT;
    private static final boolean DEFAULT_SERVICE_ENABLED = false;
    private static final boolean DEFAULT_SCREENSHOT_ENABLED = true;
    
    private SharedPreferences preferences;
    
    public SettingsManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    // Text size methods
    public float getTextSize() {
        return preferences.getFloat(KEY_TEXT_SIZE, DEFAULT_TEXT_SIZE);
    }
    
    public void setTextSize(float textSize) {
        preferences.edit().putFloat(KEY_TEXT_SIZE, textSize).apply();
    }
    
    // Text color methods
    public int getTextColor() {
        return preferences.getInt(KEY_TEXT_COLOR, DEFAULT_TEXT_COLOR);
    }
    
    public void setTextColor(int textColor) {
        preferences.edit().putInt(KEY_TEXT_COLOR, textColor).apply();
    }
    
    // Background color methods
    public int getBackgroundColor() {
        return preferences.getInt(KEY_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
    }
    
    public void setBackgroundColor(int backgroundColor) {
        preferences.edit().putInt(KEY_BACKGROUND_COLOR, backgroundColor).apply();
    }
    
    // Display position methods
    public int getDisplayPosition() {
        return preferences.getInt(KEY_DISPLAY_POSITION, DEFAULT_DISPLAY_POSITION);
    }
    
    public void setDisplayPosition(int position) {
        preferences.edit().putInt(KEY_DISPLAY_POSITION, position).apply();
    }
    
    // Service enabled methods
    public boolean isServiceEnabled() {
        return preferences.getBoolean(KEY_SERVICE_ENABLED, DEFAULT_SERVICE_ENABLED);
    }
    
    public void setServiceEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_SERVICE_ENABLED, enabled).apply();
    }
    
    // Screenshot enabled methods
    public boolean isScreenshotEnabled() {
        return preferences.getBoolean(KEY_SCREENSHOT_ENABLED, DEFAULT_SCREENSHOT_ENABLED);
    }
    
    public void setScreenshotEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_SCREENSHOT_ENABLED, enabled).apply();
    }
    
    // Reset to defaults
    public void resetToDefaults() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(KEY_TEXT_SIZE, DEFAULT_TEXT_SIZE);
        editor.putInt(KEY_TEXT_COLOR, DEFAULT_TEXT_COLOR);
        editor.putInt(KEY_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
        editor.putInt(KEY_DISPLAY_POSITION, DEFAULT_DISPLAY_POSITION);
        editor.putBoolean(KEY_SERVICE_ENABLED, DEFAULT_SERVICE_ENABLED);
        editor.putBoolean(KEY_SCREENSHOT_ENABLED, DEFAULT_SCREENSHOT_ENABLED);
        editor.apply();
    }
    
    // Get position name for display
    public String getPositionName(Context context, int position) {
        switch (position) {
            case POSITION_TOP_LEFT:
                return context.getString(R.string.position_top_left);
            case POSITION_TOP_CENTER:
                return context.getString(R.string.position_top_center);
            case POSITION_TOP_RIGHT:
                return context.getString(R.string.position_top_right);
            default:
                return context.getString(R.string.position_top_left);
        }
    }
    
    // Color utility methods
    public static int[] getAvailableTextColors() {
        return new int[] {
            Color.WHITE,
            Color.BLACK,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            0xFFFF8000, // Orange
            Color.GRAY
        };
    }
    
    public static int[] getAvailableBackgroundColors() {
        return new int[] {
            0x80000000, // Semi-transparent black
            0xBF000000, // More opaque black
            0x80FFFFFF, // Semi-transparent white
            0xBFFFFFFF, // More opaque white
            0x80808080, // Semi-transparent gray
            0x00000000  // Transparent
        };
    }
    
    // Validation methods
    public static boolean isValidPosition(int position) {
        return position >= POSITION_TOP_LEFT && position <= POSITION_TOP_RIGHT;
    }
    
    public static boolean isValidTextSize(float textSize) {
        return textSize >= 8.0f && textSize <= 48.0f;
    }
}

