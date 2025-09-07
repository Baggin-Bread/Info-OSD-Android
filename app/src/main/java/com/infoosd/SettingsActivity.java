package com.infoosd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    
    private SeekBar seekBarTextSize;
    private TextView tvTextSizeValue;
    private GridLayout gridTextColors;
    private GridLayout gridBackgroundColors;
    private RadioGroup radioGroupPosition;
    private TextView tvPreview;
    private Button btnApply;
    private Button btnReset;
    
    private SettingsManager settingsManager;
    
    // Current settings
    private float currentTextSize;
    private int currentTextColor;
    private int currentBackgroundColor;
    private int currentPosition;
    
    // Selected color buttons
    private View selectedTextColorButton;
    private View selectedBackgroundColorButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        
        settingsManager = new SettingsManager(this);
        
        initViews();
        loadCurrentSettings();
        setupListeners();
        updatePreview();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void initViews() {
        seekBarTextSize = findViewById(R.id.seekbar_text_size);
        tvTextSizeValue = findViewById(R.id.tv_text_size_value);
        gridTextColors = findViewById(R.id.grid_text_colors);
        gridBackgroundColors = findViewById(R.id.grid_background_colors);
        radioGroupPosition = findViewById(R.id.radio_group_position);
        tvPreview = findViewById(R.id.tv_preview);
        btnApply = findViewById(R.id.btn_apply);
        btnReset = findViewById(R.id.btn_reset);
        
        setupColorGrids();
    }
    
    private void setupColorGrids() {
        // Setup text color grid
        int[] textColors = SettingsManager.getAvailableTextColors();
        for (int i = 0; i < textColors.length; i++) {
            View colorButton = createColorButton(textColors[i], true, i);
            gridTextColors.addView(colorButton);
        }
        
        // Setup background color grid
        int[] backgroundColors = SettingsManager.getAvailableBackgroundColors();
        for (int i = 0; i < backgroundColors.length; i++) {
            View colorButton = createColorButton(backgroundColors[i], false, i);
            gridBackgroundColors.addView(colorButton);
        }
    }
    
    private View createColorButton(int color, boolean isTextColor, int index) {
        Button button = new Button(this);
        
        // Set button size
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = (int) (48 * getResources().getDisplayMetrics().density);
        params.height = (int) (48 * getResources().getDisplayMetrics().density);
        params.setMargins(8, 8, 8, 8);
        button.setLayoutParams(params);
        
        // Set button appearance
        if (color == 0x00000000) { // Transparent
            button.setBackgroundColor(Color.WHITE);
            button.setText("T");
            button.setTextColor(Color.BLACK);
        } else {
            button.setBackgroundColor(color);
        }
        
        // Add border
        button.setPadding(4, 4, 4, 4);
        
        // Set click listener
        button.setOnClickListener(v -> {
            if (isTextColor) {
                selectTextColor(color, button);
            } else {
                selectBackgroundColor(color, button);
            }
        });
        
        return button;
    }
    
    private void selectTextColor(int color, View button) {
        currentTextColor = color;
        
        // Update selection visual
        if (selectedTextColorButton != null) {
            selectedTextColorButton.setScaleX(1.0f);
            selectedTextColorButton.setScaleY(1.0f);
        }
        
        selectedTextColorButton = button;
        button.setScaleX(1.2f);
        button.setScaleY(1.2f);
        
        updatePreview();
    }
    
    private void selectBackgroundColor(int color, View button) {
        currentBackgroundColor = color;
        
        // Update selection visual
        if (selectedBackgroundColorButton != null) {
            selectedBackgroundColorButton.setScaleX(1.0f);
            selectedBackgroundColorButton.setScaleY(1.0f);
        }
        
        selectedBackgroundColorButton = button;
        button.setScaleX(1.2f);
        button.setScaleY(1.2f);
        
        updatePreview();
    }
    
    private void loadCurrentSettings() {
        // Load text size
        currentTextSize = settingsManager.getTextSize();
        seekBarTextSize.setProgress((int) (currentTextSize - 8)); // 8sp is minimum
        tvTextSizeValue.setText(String.format("%.0fsp", currentTextSize));
        
        // Load colors
        currentTextColor = settingsManager.getTextColor();
        currentBackgroundColor = settingsManager.getBackgroundColor();
        
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
        
        // Select current colors in grids
        selectCurrentColors();
    }
    
    private void selectCurrentColors() {
        // Select current text color
        int[] textColors = SettingsManager.getAvailableTextColors();
        for (int i = 0; i < textColors.length; i++) {
            if (textColors[i] == currentTextColor) {
                View button = gridTextColors.getChildAt(i);
                selectTextColor(currentTextColor, button);
                break;
            }
        }
        
        // Select current background color
        int[] backgroundColors = SettingsManager.getAvailableBackgroundColors();
        for (int i = 0; i < backgroundColors.length; i++) {
            if (backgroundColors[i] == currentBackgroundColor) {
                View button = gridBackgroundColors.getChildAt(i);
                selectBackgroundColor(currentBackgroundColor, button);
                break;
            }
        }
    }
    
    private void setupListeners() {
        // Text size seekbar
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
        
        // Position radio group
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
        
        // Apply button
        btnApply.setOnClickListener(v -> applySettings());
        
        // Reset button
        btnReset.setOnClickListener(v -> resetSettings());
    }
    
    private void updatePreview() {
        if (tvPreview == null) return;
        
        // Apply current settings to preview
        tvPreview.setTextSize(currentTextSize);
        tvPreview.setTextColor(currentTextColor);
        tvPreview.setBackgroundColor(currentBackgroundColor);
        
        // Update preview text with current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        String previewText = String.format("🔋 85%% | %s", currentTime);
        tvPreview.setText(previewText);
        
        // Update position in preview container
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvPreview.getLayoutParams();
        switch (currentPosition) {
            case SettingsManager.POSITION_TOP_LEFT:
                params.gravity = Gravity.TOP | Gravity.START;
                break;
            case SettingsManager.POSITION_TOP_CENTER:
                params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                break;
            case SettingsManager.POSITION_TOP_RIGHT:
                params.gravity = Gravity.TOP | Gravity.END;
                break;
        }
        tvPreview.setLayoutParams(params);
    }
    
    private void applySettings() {
        // Save settings
        settingsManager.setTextSize(currentTextSize);
        settingsManager.setTextColor(currentTextColor);
        settingsManager.setBackgroundColor(currentBackgroundColor);
        settingsManager.setDisplayPosition(currentPosition);
        
        // Update overlay service if running
        Intent updateIntent = new Intent(this, OverlayService.class);
        updateIntent.putExtra("update_settings", true);
        startService(updateIntent);
        
        Toast.makeText(this, "設定已套用", Toast.LENGTH_SHORT).show();
        
        // Return to main activity
        finish();
    }
    
    private void resetSettings() {
        settingsManager.resetToDefaults();
        loadCurrentSettings();
        updatePreview();
        
        Toast.makeText(this, "設定已重設", Toast.LENGTH_SHORT).show();
    }
}

