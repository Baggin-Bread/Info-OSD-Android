# Info OSD 開發指南

本文檔提供 Info OSD 專案的詳細開發指南，幫助開發者快速上手和貢獻代碼。

## 📋 目錄

- [開發環境設定](#開發環境設定)
- [專案架構](#專案架構)
- [核心組件詳解](#核心組件詳解)
- [開發流程](#開發流程)
- [測試指南](#測試指南)
- [發布流程](#發布流程)
- [常見問題](#常見問題)

## 🛠️ 開發環境設定

### 必要工具

#### Android Studio
- **版本**: Android Studio Hedgehog | 2023.1.1 或更新
- **下載**: [Android Studio 官網](https://developer.android.com/studio)

#### Android SDK
- **最低 API**: 24 (Android 7.0)
- **目標 API**: 34 (Android 14)
- **構建工具**: 34.0.0

#### Java 開發環境
- **JDK 版本**: JDK 8 或更高
- **語言級別**: Java 8

### 環境配置步驟

#### 1. 安裝 Android Studio
```bash
# macOS (使用 Homebrew)
brew install --cask android-studio

# Windows
# 下載並安裝 .exe 文件

# Linux
# 下載並解壓 .tar.gz 文件
```

#### 2. 配置 Android SDK
在 Android Studio 中：
1. 打開 `Tools > SDK Manager`
2. 安裝以下組件：
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android Emulator
   - Android SDK Platform-Tools

#### 3. 創建 local.properties
在專案根目錄創建 `local.properties` 文件：
```properties
# Android SDK 路徑
sdk.dir=/Users/username/Library/Android/sdk

# NDK 路徑（可選）
ndk.dir=/Users/username/Library/Android/sdk/ndk/25.1.8937393
```

#### 4. 配置模擬器或實體設備
**模擬器設定**:
1. 打開 `Tools > AVD Manager`
2. 創建新的虛擬設備
3. 選擇 API 24+ 的系統映像

**實體設備設定**:
1. 啟用開發者選項
2. 開啟 USB 調試
3. 連接設備並授權

## 🏗️ 專案架構

### 整體架構圖
```
┌─────────────────────────────────────────┐
│                UI Layer                 │
│  ┌─────────────┐  ┌─────────────────┐   │
│  │ MainActivity│  │ SettingsActivity│   │
│  └─────────────┘  └─────────────────┘   │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│              Service Layer              │
│  ┌─────────────┐  ┌─────────────────┐   │
│  │OverlayService│  │ScreenshotService│   │
│  └─────────────┘  └─────────────────┘   │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│               Data Layer                │
│  ┌─────────────┐  ┌─────────────────┐   │
│  │SettingsManager│ │PermissionManager│  │
│  └─────────────┘  └─────────────────┘   │
└─────────────────────────────────────────┘
```

### 模組劃分

#### UI 模組
- **MainActivity**: 主界面，服務控制和設定入口
- **SettingsActivity**: 設定界面，OSD 自定義選項
- **MinimalScreenshotActivity**: 截圖權限請求界面

#### Service 模組
- **OverlayService**: OSD 懸浮窗服務
- **MinimalScreenshotService**: 截圖功能服務

#### Utility 模組
- **SettingsManager**: 設定數據管理
- **PermissionManager**: 權限管理工具

#### Receiver 模組
- **BatteryReceiver**: 電池狀態監聽
- **BootReceiver**: 開機自啟動

## 🔧 核心組件詳解

### OverlayService

#### 職責
- 創建和管理 OSD 懸浮窗
- 監聽電池狀態變化
- 更新時間顯示
- 處理用戶點擊事件

#### 關鍵方法
```java
public class OverlayService extends Service {
    
    // 創建懸浮窗視圖
    private void createOverlayView() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        // 設定視圖屬性和事件監聽
    }
    
    // 更新電池信息
    private void updateBatteryInfo(int level) {
        if (batteryText != null) {
            batteryText.setText(level + "%");
        }
    }
    
    // 更新時間顯示
    private void updateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        if (timeText != null) {
            timeText.setText(currentTime);
        }
    }
}
```

#### 生命週期管理
```java
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
    createNotificationChannel();
    startForeground(NOTIFICATION_ID, createNotification());
    createOverlayView();
    return START_STICKY; // 服務被殺死後自動重啟
}

@Override
public void onDestroy() {
    if (overlayView != null && windowManager != null) {
        windowManager.removeView(overlayView);
    }
    super.onDestroy();
}
```

### MinimalScreenshotService

#### 職責
- 管理 MediaProjection 權限
- 創建虛擬顯示器
- 捕獲螢幕圖像
- 保存截圖到系統相簿

#### 截圖流程
```java
public class MinimalScreenshotService extends Service {
    
    // 1. 註冊 MediaProjection 回調
    private void setupMediaProjection(Intent data, int resultCode) {
        MediaProjectionManager manager = getSystemService(MediaProjectionManager.class);
        mediaProjection = manager.getMediaProjection(resultCode, data);
        
        MediaProjection.Callback callback = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                cleanup();
            }
        };
        mediaProjection.registerCallback(callback, mainHandler);
    }
    
    // 2. 創建虛擬顯示器
    private void createVirtualDisplay() {
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        imageReader.setOnImageAvailableListener(this::onImageAvailable, backgroundHandler);
        
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "MinimalScreenshot", width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.getSurface(),
            displayCallback, mainHandler
        );
    }
    
    // 3. 處理圖像數據
    private void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        if (image != null) {
            processImageMinimal(image);
            image.close();
        }
    }
    
    // 4. 保存到系統相簿
    private void saveToGallery(Bitmap bitmap) {
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.RELATIVE_PATH, 
                  Environment.DIRECTORY_PICTURES + "/Screenshots");
        
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // 寫入圖像數據
    }
}
```

### SettingsManager

#### 職責
- 管理應用設定數據
- 提供設定值的讀取和保存
- 處理設定預設值

#### 實現細節
```java
public class SettingsManager {
    private static final String PREFS_NAME = "InfoOSDSettings";
    private SharedPreferences prefs;
    
    // 設定鍵值常量
    public static final String KEY_TEXT_SIZE = "text_size";
    public static final String KEY_TEXT_COLOR = "text_color";
    public static final String KEY_POSITION_X = "position_x";
    public static final String KEY_POSITION_Y = "position_y";
    
    // 讀取設定值
    public int getTextSize() {
        return prefs.getInt(KEY_TEXT_SIZE, 16); // 預設 16sp
    }
    
    // 保存設定值
    public void setTextSize(int size) {
        prefs.edit().putInt(KEY_TEXT_SIZE, size).apply();
    }
    
    // 重設為預設值
    public void resetToDefaults() {
        prefs.edit().clear().apply();
    }
}
```

## 🔄 開發流程

### Git 工作流程

#### 分支策略
```
main (穩定版本)
├── develop (開發分支)
│   ├── feature/new-osd-options
│   ├── feature/screenshot-enhancement
│   └── bugfix/permission-issue
└── release/v0.2 (發布分支)
```

#### 提交規範
```bash
# 功能開發
git commit -m "feat: 添加 CPU 使用率顯示功能"

# 問題修復
git commit -m "fix: 修復 Android 14 權限問題"

# 文檔更新
git commit -m "docs: 更新開發指南"

# 代碼重構
git commit -m "refactor: 優化截圖服務性能"
```

### 開發步驟

#### 1. 創建功能分支
```bash
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name
```

#### 2. 開發和測試
```bash
# 編寫代碼
# 運行測試
./gradlew test
./gradlew connectedAndroidTest

# 提交更改
git add .
git commit -m "feat: 描述您的功能"
```

#### 3. 合併到開發分支
```bash
git checkout develop
git merge feature/your-feature-name
git push origin develop
```

### 代碼審查清單

#### 功能性檢查
- [ ] 功能按預期工作
- [ ] 處理了邊界情況
- [ ] 錯誤處理完善
- [ ] 性能表現良好

#### 代碼品質檢查
- [ ] 代碼風格一致
- [ ] 變數命名清晰
- [ ] 註釋充分
- [ ] 無重複代碼

#### Android 特定檢查
- [ ] 權限使用正確
- [ ] 生命週期處理正確
- [ ] 記憶體洩漏檢查
- [ ] 多螢幕適配

## 🧪 測試指南

### 單元測試

#### 測試結構
```
app/src/test/java/com/infoosd/
├── SettingsManagerTest.java
├── PermissionManagerTest.java
└── utils/
    └── TimeUtilsTest.java
```

#### 示例測試
```java
@RunWith(JUnit4.class)
public class SettingsManagerTest {
    
    private SettingsManager settingsManager;
    private Context mockContext;
    
    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        settingsManager = new SettingsManager(mockContext);
    }
    
    @Test
    public void testDefaultTextSize() {
        assertEquals(16, settingsManager.getTextSize());
    }
    
    @Test
    public void testSetAndGetTextSize() {
        settingsManager.setTextSize(20);
        assertEquals(20, settingsManager.getTextSize());
    }
}
```

### 整合測試

#### UI 測試
```java
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    
    @Rule
    public ActivityTestRule<MainActivity> activityRule = 
        new ActivityTestRule<>(MainActivity.class);
    
    @Test
    public void testStartServiceButton() {
        onView(withId(R.id.startServiceButton))
            .perform(click());
        
        // 驗證服務已啟動
        onView(withText("服務已啟動"))
            .check(matches(isDisplayed()));
    }
}
```

### 手動測試

#### 功能測試清單
```markdown
## OSD 顯示測試
- [ ] OSD 正確顯示電池電量
- [ ] OSD 正確顯示當前時間
- [ ] OSD 位置可以調整
- [ ] OSD 文字大小可以調整
- [ ] OSD 文字顏色可以調整

## 截圖功能測試
- [ ] 點擊 OSD 觸發截圖
- [ ] 權限請求正常顯示
- [ ] 截圖成功保存到相簿
- [ ] 截圖品質符合預期
- [ ] 多次截圖功能正常

## 權限管理測試
- [ ] 懸浮窗權限請求正常
- [ ] MediaProjection 權限請求正常
- [ ] 權限被拒絕時的處理
- [ ] 權限設定頁面跳轉正常

## 系統整合測試
- [ ] 開機自啟動功能
- [ ] 電池狀態變化響應
- [ ] 系統時間變化響應
- [ ] 記憶體使用合理
- [ ] CPU 使用率正常
```

### 性能測試

#### 記憶體使用監控
```bash
# 使用 adb 監控記憶體使用
adb shell dumpsys meminfo com.infoosd

# 使用 Android Studio Profiler
# 1. 連接設備
# 2. 選擇應用
# 3. 監控 Memory 使用情況
```

#### 電池消耗測試
```bash
# 監控電池使用情況
adb shell dumpsys batterystats com.infoosd

# 重設電池統計
adb shell dumpsys batterystats --reset
```

## 📦 發布流程

### 版本號管理

#### 語義化版本控制
```
主版本號.次版本號.修訂號
例如: 1.2.3

主版本號: 不兼容的 API 修改
次版本號: 向下兼容的功能性新增
修訂號: 向下兼容的問題修正
```

#### 版本配置
```gradle
// app/build.gradle
android {
    defaultConfig {
        versionCode 1      // 內部版本號，每次發布遞增
        versionName "0.1"  // 用戶可見版本號
    }
}
```

### 構建發布版本

#### 1. 準備發布
```bash
# 更新版本號
# 更新 CHANGELOG.md
# 運行完整測試套件
./gradlew test connectedAndroidTest

# 代碼檢查
./gradlew lint
```

#### 2. 生成簽名密鑰
```bash
keytool -genkey -v -keystore info-osd-release.jks \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -alias info-osd-key
```

#### 3. 配置簽名
```gradle
// app/build.gradle
android {
    signingConfigs {
        release {
            storeFile file('info-osd-release.jks')
            storePassword 'your-store-password'
            keyAlias 'info-osd-key'
            keyPassword 'your-key-password'
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 
                         'proguard-rules.pro'
        }
    }
}
```

#### 4. 構建發布版本
```bash
./gradlew assembleRelease
```

#### 5. 驗證 APK
```bash
# 檢查 APK 簽名
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk

# 檢查 APK 內容
aapt dump badging app/build/outputs/apk/release/app-release.apk
```

### 發布檢查清單

#### 代碼品質
- [ ] 所有測試通過
- [ ] 代碼審查完成
- [ ] Lint 檢查無嚴重問題
- [ ] ProGuard 配置正確

#### 功能驗證
- [ ] 核心功能正常工作
- [ ] 權限請求正常
- [ ] 在多個設備上測試
- [ ] 性能表現符合預期

#### 文檔更新
- [ ] README.md 更新
- [ ] CHANGELOG.md 更新
- [ ] API 文檔更新
- [ ] 用戶指南更新

## ❓ 常見問題

### 開發環境問題

#### Q: Android Studio 無法識別 SDK
**A**: 檢查 `local.properties` 文件中的 SDK 路徑是否正確：
```properties
sdk.dir=/correct/path/to/android-sdk
```

#### Q: Gradle 同步失敗
**A**: 嘗試以下解決方案：
```bash
# 清理專案
./gradlew clean

# 重新整理依賴
./gradlew --refresh-dependencies

# 檢查網路連接和代理設定
```

### 權限相關問題

#### Q: 懸浮窗權限無法獲取
**A**: 確保在 AndroidManifest.xml 中聲明了正確的權限：
```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

並在代碼中正確請求權限：
```java
if (!Settings.canDrawOverlays(this)) {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                              Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
}
```

#### Q: MediaProjection 權限請求失敗
**A**: 檢查是否正確實現了權限請求流程：
```java
MediaProjectionManager manager = getSystemService(MediaProjectionManager.class);
Intent intent = manager.createScreenCaptureIntent();
startActivityForResult(intent, REQUEST_MEDIA_PROJECTION);
```

### 性能優化問題

#### Q: OSD 顯示卡頓
**A**: 優化更新頻率和視圖操作：
```java
// 使用 Handler 控制更新頻率
private static final int UPDATE_INTERVAL = 1000; // 1秒更新一次

private void scheduleUpdate() {
    handler.postDelayed(this::updateDisplay, UPDATE_INTERVAL);
}
```

#### Q: 記憶體使用過高
**A**: 檢查是否有記憶體洩漏：
```java
// 正確釋放資源
@Override
public void onDestroy() {
    if (overlayView != null && windowManager != null) {
        windowManager.removeView(overlayView);
        overlayView = null;
    }
    super.onDestroy();
}
```

### 截圖功能問題

#### Q: 截圖保存失敗
**A**: 檢查存儲權限和 MediaStore API 使用：
```java
// Android 10+ 使用 MediaStore API
ContentValues values = new ContentValues();
values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
values.put(MediaStore.Images.Media.RELATIVE_PATH, 
          Environment.DIRECTORY_PICTURES + "/Screenshots");
```

#### Q: VirtualDisplay 創建失敗
**A**: 確保 MediaProjection 回調正確註冊：
```java
MediaProjection.Callback callback = new MediaProjection.Callback() {
    @Override
    public void onStop() {
        cleanup();
    }
};
mediaProjection.registerCallback(callback, handler);
```

## 📚 參考資源

### Android 官方文檔
- [Android Developer Guide](https://developer.android.com/guide)
- [MediaProjection API](https://developer.android.com/reference/android/media/projection/MediaProjection)
- [WindowManager](https://developer.android.com/reference/android/view/WindowManager)
- [Foreground Services](https://developer.android.com/guide/components/foreground-services)

### 最佳實踐
- [Android App Architecture Guide](https://developer.android.com/jetpack/guide)
- [Android Performance Best Practices](https://developer.android.com/topic/performance)
- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)

### 工具和庫
- [Android Studio](https://developer.android.com/studio)
- [Gradle Build Tool](https://gradle.org/)
- [JUnit Testing Framework](https://junit.org/)
- [Espresso UI Testing](https://developer.android.com/training/testing/espresso)

---

這份開發指南涵蓋了 Info OSD 專案開發的各個方面。如果您有任何問題或建議，歡迎提交 Issue 或 Pull Request！

