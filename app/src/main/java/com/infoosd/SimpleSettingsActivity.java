package com.infoosd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SimpleSettingsActivity extends AppCompatActivity {
    
    private SeekBar seekBarTextSize;
    private TextView tvTextSizeValue;
    private RadioGroup radioGroupPosition;
    private TextView tvPreview;
    private Button btnApply;
    private Button btnReset;
    
    private SettingsManager settingsManager;
    
    // Current settings
    private float currentTextSize;
    private int currentPosition;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_settings);
        
        settingsManager = new SettingsManager(this);
        
        initViews();
        loadCurrentSettings();
        setupListeners();
        updatePreview();
    }
    
    private void initViews() {
        seekBarTextSize = findViewById(R.id.seekbar_text_size);
        tvTextSizeValue = findViewById(R.id.tv_text_size_value);
        radioGroupPosition = findViewById(R.id.radio_group_position);
        tvPreview = findViewById(R.id.tv_preview);
        btnApply = findViewById(R.id.btn_apply);
        btnReset = findViewById(R.id.btn_reset);
    }
    
    private void loadCurrentSettings() {
        // Load text size
        currentTextSize = settingsManager.getTextSize();
        seekBarTextSize.setMax(40); // 8sp to 48sp
        seekBarTextSize.setProgress((int) (currentTextSize - 8)); // 8sp is minimum
        tvTextSizeValue.setText(String.format("%.0fsp", currentTextSize));
        
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
        
        // Apply button
        btnApply.setOnClickListener(v -> {
            applySettings();
        });
        
        // Reset button
        btnReset.setOnClickListener(v -> {
            resetSettings();
        });
    }
    
    private void updatePreview() {
        if (tvPreview == null) return;
        
        tvPreview.setTextSize(currentTextSize);
        tvPreview.setText("🔋 85% | 14:30");
        
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
        settingsManager.setDisplayPosition(currentPosition);
        
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
        updatePreview();
        Toast.makeText(this, "設定已重設為預設值", Toast.LENGTH_SHORT).show();
    }
}

