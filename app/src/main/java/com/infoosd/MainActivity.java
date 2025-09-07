package com.infoosd;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tvServiceStatus, tvPreview;
    private Button btnStartService, btnStopService, btnSettings, btnAbout;
    
    private SettingsManager settingsManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        settingsManager = new SettingsManager(this);
        
        initViews();
        setupClickListeners();
        updateUI();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        updatePreview();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PermissionManager.REQUEST_OVERLAY_PERMISSION) {
            if (PermissionManager.handlePermissionResult(this, requestCode)) {
                // Permission granted, can start service
                Toast.makeText(this, "權限已授予", Toast.LENGTH_SHORT).show();
            }
            updateUI();
        }
    }
    
    private void initViews() {
        tvServiceStatus = findViewById(R.id.tv_service_status);
        tvPreview = findViewById(R.id.tv_preview);
        btnStartService = findViewById(R.id.btn_start_service);
        btnStopService = findViewById(R.id.btn_stop_service);
        btnSettings = findViewById(R.id.btn_settings);
        btnAbout = findViewById(R.id.btn_about);
    }
    
    private void setupClickListeners() {
        // btnStartService 的監聽器在 updateUI() 中動態設置
        btnStopService.setOnClickListener(v -> stopOverlayService());
        btnSettings.setOnClickListener(v -> openSettings());
        btnAbout.setOnClickListener(v -> showAboutDialog());
    }
    
    private void startOverlayService() {
        // Check overlay permission first
        if (!PermissionManager.checkAndRequestPermission(this)) {
            return;
        }
        
        try {
            Intent serviceIntent = new Intent(this, OverlayService.class);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            
            settingsManager.setServiceEnabled(true);
            updateUI();
            
            Toast.makeText(this, "服務已啟動", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "啟動服務失敗", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void stopOverlayService() {
        try {
            Intent serviceIntent = new Intent(this, OverlayService.class);
            stopService(serviceIntent);
            
            settingsManager.setServiceEnabled(false);
            updateUI();
            
            Toast.makeText(this, "服務已停止", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "停止服務失敗", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openSettings() {
        Intent intent = new Intent(MainActivity.this, EnhancedSettingsActivity.class);
        startActivity(intent);
    }
    
    private void showAboutDialog() {
        // 創建包含超連結的HTML文字
        String aboutHtml = "Info OSD 在螢幕懸浮窗中顯示電池電量和當前時間。<br><br>" +
                          "版本 0.1<br><br>" +
                          "作者：廖阿輝<br>" +
                          "網站：<a href='https://ahui3c.com'>https://ahui3c.com</a><br>" +
                          "信箱：<a href='mailto:chehui@gmail.com'>chehui@gmail.com</a><br><br>" +
                          "FB 粉絲團與 Youtube 可以搜尋<br>" +
                          "3C 達人廖阿輝";
        
        // 創建TextView來顯示HTML內容
        TextView aboutTextView = new TextView(this);
        aboutTextView.setText(Html.fromHtml(aboutHtml, Html.FROM_HTML_MODE_LEGACY));
        aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());
        aboutTextView.setPadding(50, 30, 50, 30);
        aboutTextView.setTextSize(16);
        aboutTextView.setLineSpacing(8, 1.0f);
        
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.about))
            .setView(aboutTextView)
            .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
            .show();
    }
    
    private void updateUI() {
        boolean isServiceRunning = isOverlayServiceRunning();
        boolean hasPermission = PermissionManager.hasOverlayPermission(this);
        
        android.util.Log.d("MainActivity", "updateUI - Service running: " + isServiceRunning + ", Has permission: " + hasPermission);
        
        if (isServiceRunning) {
            tvServiceStatus.setText(getString(R.string.service_running));
            tvServiceStatus.setTextColor(ContextCompat.getColor(this, R.color.color_green));
        } else {
            tvServiceStatus.setText(getString(R.string.service_stopped));
            tvServiceStatus.setTextColor(ContextCompat.getColor(this, R.color.color_red));
        }
        
        btnStartService.setEnabled(!isServiceRunning);
        btnStopService.setEnabled(isServiceRunning);
        
        if (!hasPermission && !isServiceRunning) {
            android.util.Log.d("MainActivity", "Setting permission button");
            btnStartService.setText("授予權限");
            btnStartService.setOnClickListener(v -> {
                android.util.Log.d("MainActivity", "Permission button clicked");
                // Show permission dialog with quick access to settings
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("需要權限")
                    .setMessage("Info OSD 需要「顯示在其他應用程式上層」權限才能正常運作。\n\n點擊「前往設定」快速開啟權限設定頁面。")
                    .setPositiveButton("前往設定", (dialog, which) -> {
                        android.util.Log.d("MainActivity", "Opening overlay settings");
                        PermissionManager.openOverlaySettings(this);
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .show();
            });
        } else {
            android.util.Log.d("MainActivity", "Setting start service button");
            btnStartService.setText(getString(R.string.start_service));
            btnStartService.setOnClickListener(v -> startOverlayService());
        }
    }
    
    private void updatePreview() {
        if (tvPreview == null) return;
        
        // Apply current settings to preview
        float textSize = settingsManager.getTextSize();
        int textColor = settingsManager.getTextColor();
        int backgroundColor = settingsManager.getBackgroundColor();
        
        tvPreview.setTextSize(textSize);
        tvPreview.setTextColor(textColor);
        tvPreview.setBackgroundColor(backgroundColor);
        
        // Update preview text with current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        String previewText = String.format("🔋 85%% | %s", currentTime);
        tvPreview.setText(previewText);
    }
    
    private boolean isOverlayServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;
        
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (OverlayService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

