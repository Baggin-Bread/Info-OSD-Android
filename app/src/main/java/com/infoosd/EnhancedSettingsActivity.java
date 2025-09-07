package com.infoosd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class EnhancedSettingsActivity extends AppCompatActivity {
    
    private SeekBar seekBarTextSize;
    private TextView tvTextSizeValue;
    private RadioGroup radioGroupPosition;
    private TextView tvPreview;
    private Button btnApply;
    private Button btnReset;
    
    // Color selection buttons
    private Button btnColorWhite, btnColorBlack, btnColorRed, btnColorGreen, btnColorBlue, btnColorYellow;
    private Button btnBgTransparent, btnBgSemiBlack, btnBgSemiWhite;
    
    // Screenshot toggle
    private android.widget.Switch switchScreenshot;
    private Button btnScreenshotHelp;
    
    private SettingsManager settingsManager;
    
    // Current settings
    private float currentTextSize;
    private int currentTextColor;
    private int currentBackgroundColor;
    private int currentPosition;
    private boolean currentScreenshotEnabled;
    
    // Selected color buttons for visual feedback
    private Button selectedTextColorButton;
    private Button selectedBgColorButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_settings);
        
        settingsManager = new SettingsManager(this);
        
        initViews();
        loadCurrentSettings();
        setupListeners();
        updateColorButtonSelection();
        updatePreview();
    }
    
    private void initViews() {
        seekBarTextSize = findViewById(R.id.seekbar_text_size);
        tvTextSizeValue = findViewById(R.id.tv_text_size_value);
        radioGroupPosition = findViewById(R.id.radio_group_position);
        tvPreview = findViewById(R.id.tv_preview);
        btnApply = findViewById(R.id.btn_apply);
        btnReset = findViewById(R.id.btn_reset);
        
        // Text color buttons
        btnColorWhite = findViewById(R.id.btn_color_white);
        btnColorBlack = findViewById(R.id.btn_color_black);
        btnColorRed = findViewById(R.id.btn_color_red);
        btnColorGreen = findViewById(R.id.btn_color_green);
        btnColorBlue = findViewById(R.id.btn_color_blue);
        btnColorYellow = findViewById(R.id.btn_color_yellow);
        
        // Background color buttons
        btnBgTransparent = findViewById(R.id.btn_bg_transparent);
        btnBgSemiBlack = findViewById(R.id.btn_bg_semi_black);
        btnBgSemiWhite = findViewById(R.id.btn_bg_semi_white);
        
        // Screenshot toggle
        switchScreenshot = findViewById(R.id.switch_screenshot);
        btnScreenshotHelp = findViewById(R.id.btn_screenshot_help);
    }
    
    private void loadCurrentSettings() {
        // Load text size
        currentTextSize = settingsManager.getTextSize();
        seekBarTextSize.setMax(40); // 8sp to 48sp
        seekBarTextSize.setProgress((int) (currentTextSize - 8)); // 8sp is minimum
        tvTextSizeValue.setText(String.format("%.0fsp", currentTextSize));
        
        // Load colors
        currentTextColor = settingsManager.getTextColor();
        currentBackgroundColor = settingsManager.getBackgroundColor();
        
        // Load screenshot setting
        currentScreenshotEnabled = settingsManager.isScreenshotEnabled();
        switchScreenshot.setChecked(currentScreenshotEnabled);
        
        // Load position
        currentPosition = settingsManager.getDisplayPosition();
        switch (currentPosition) {
            case SettingsManager.POSITION_TOP_LEFT:
                ((RadioButton) findViewById(R.id.radio_top_left)).setChecked(true);
                break;
            case SettingsManager.POSITION_TOP_CENTER:
                ((RadioButton) findViewById(R.id.radio_top_center)).setChecked(true);
                break;
            case SettingsManager.POSITION_TOP_RIGHT:
                ((RadioButton) findViewById(R.id.radio_top_right)).setChecked(true);
                break;
        }
    }
    
    private void setupListeners() {
        // Text size listener
        seekBarTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTextSize = progress + 8; // 8sp is minimum
                tvTextSizeValue.setText(String.format("%.0fsp", currentTextSize));
                updatePreview();
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Position listener
        radioGroupPosition.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_top_left) {
                currentPosition = SettingsManager.POSITION_TOP_LEFT;
            } else if (checkedId == R.id.radio_top_center) {
                currentPosition = SettingsManager.POSITION_TOP_CENTER;
            } else if (checkedId == R.id.radio_top_right) {
                currentPosition = SettingsManager.POSITION_TOP_RIGHT;
            }
            updatePreview();
        });
        
        // Text color listeners
        btnColorWhite.setOnClickListener(v -> selectTextColor(Color.WHITE, btnColorWhite));
        btnColorBlack.setOnClickListener(v -> selectTextColor(Color.BLACK, btnColorBlack));
        btnColorRed.setOnClickListener(v -> selectTextColor(Color.RED, btnColorRed));
        btnColorGreen.setOnClickListener(v -> selectTextColor(Color.GREEN, btnColorGreen));
        btnColorBlue.setOnClickListener(v -> selectTextColor(Color.BLUE, btnColorBlue));
        btnColorYellow.setOnClickListener(v -> selectTextColor(Color.YELLOW, btnColorYellow));
        
        // Background color listeners
        btnBgTransparent.setOnClickListener(v -> selectBackgroundColor(0x00000000, btnBgTransparent));
        btnBgSemiBlack.setOnClickListener(v -> selectBackgroundColor(0x80000000, btnBgSemiBlack));
        btnBgSemiWhite.setOnClickListener(v -> selectBackgroundColor(0x80FFFFFF, btnBgSemiWhite));
        
        // Screenshot toggle listener
        switchScreenshot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentScreenshotEnabled = isChecked;
        });
        
        // Screenshot help button listener
        btnScreenshotHelp.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("截圖功能說明")
                .setMessage("點擊 OSD 觸發截圖功能說明：\n\n" +
                    "• 開啟此功能後，點擊螢幕上的 OSD 顯示區域即可觸發截圖\n\n" +
                    "• 如果自動截圖失敗，請手動使用：\n" +
                    "  - 電源鍵 + 音量下鍵\n" +
                    "  - 下拉通知欄尋找截圖按鈕\n" +
                    "  - 使用語音助手說「截圖」\n\n" +
                    "• 某些客製化系統可能需要特殊設定\n\n" +
                    "• 可隨時在此設定中開啟或關閉此功能")
                .setPositiveButton("了解", (dialog, which) -> dialog.dismiss())
                .show();
        });
        
        // Apply button
        btnApply.setOnClickListener(v -> applySettings());
        
        // Reset button
        btnReset.setOnClickListener(v -> resetSettings());
    }
    
    private void selectTextColor(int color, Button button) {
        currentTextColor = color;
        
        // Reset previous selection
        if (selectedTextColorButton != null) {
            selectedTextColorButton.setAlpha(1.0f);
        }
        
        // Highlight current selection
        selectedTextColorButton = button;
        button.setAlpha(0.7f);
        
        updatePreview();
    }
    
    private void selectBackgroundColor(int color, Button button) {
        currentBackgroundColor = color;
        
        // Reset previous selection
        if (selectedBgColorButton != null) {
            selectedBgColorButton.setAlpha(1.0f);
        }
        
        // Highlight current selection
        selectedBgColorButton = button;
        button.setAlpha(0.7f);
        
        updatePreview();
    }
    
    private void updateColorButtonSelection() {
        // Find and select current text color button
        Button[] textColorButtons = {btnColorWhite, btnColorBlack, btnColorRed, btnColorGreen, btnColorBlue, btnColorYellow};
        int[] textColors = {Color.WHITE, Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        
        for (int i = 0; i < textColors.length; i++) {
            if (textColors[i] == currentTextColor) {
                selectTextColor(currentTextColor, textColorButtons[i]);
                break;
            }
        }
        
        // Find and select current background color button
        Button[] bgColorButtons = {btnBgTransparent, btnBgSemiBlack, btnBgSemiWhite};
        int[] bgColors = {0x00000000, 0x80000000, 0x80FFFFFF};
        
        for (int i = 0; i < bgColors.length; i++) {
            if (bgColors[i] == currentBackgroundColor) {
                selectBackgroundColor(currentBackgroundColor, bgColorButtons[i]);
                break;
            }
        }
    }
    
    private void updatePreview() {
        if (tvPreview == null) return;
        
        tvPreview.setTextSize(currentTextSize);
        tvPreview.setTextColor(currentTextColor);
        tvPreview.setBackgroundColor(currentBackgroundColor);
        
        // Show position in preview text
        String positionText = "";
        switch (currentPosition) {
            case SettingsManager.POSITION_TOP_LEFT:
                positionText = " (左上)";
                break;
            case SettingsManager.POSITION_TOP_CENTER:
                positionText = " (中央)";
                break;
            case SettingsManager.POSITION_TOP_RIGHT:
                positionText = " (右上)";
                break;
        }
        tvPreview.setText("🔋 85% | 14:30" + positionText);
    }
    
    private void applySettings() {
        // Save settings
        settingsManager.setTextSize(currentTextSize);
        settingsManager.setTextColor(currentTextColor);
        settingsManager.setBackgroundColor(currentBackgroundColor);
        settingsManager.setDisplayPosition(currentPosition);
        settingsManager.setScreenshotEnabled(currentScreenshotEnabled);
        
        // Update overlay service if running
        Intent serviceIntent = new Intent(this, OverlayService.class);
        serviceIntent.putExtra("update_settings", true);
        startService(serviceIntent);
        
        Toast.makeText(this, "設定已套用", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void resetSettings() {
        settingsManager.resetToDefaults();
        loadCurrentSettings();
        updateColorButtonSelection();
        updatePreview();
        Toast.makeText(this, "設定已重設為預設值", Toast.LENGTH_SHORT).show();
    }
}

