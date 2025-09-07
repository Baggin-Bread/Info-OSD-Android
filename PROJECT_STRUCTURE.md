# Info OSD 專案結構

本文檔詳細說明了 Info OSD 專案的目錄結構和文件組織。

## 📁 根目錄結構

```
InfoOSD/
├── app/                          # 主應用模組
├── gradle/                       # Gradle 包裝器文件
├── build.gradle                  # 專案級構建配置
├── settings.gradle               # 專案設定
├── gradle.properties             # Gradle 屬性配置
├── gradlew                       # Gradle 包裝器腳本 (Unix)
├── gradlew.bat                   # Gradle 包裝器腳本 (Windows)
├── local.properties              # 本地配置（需自行創建）
├── .gitignore                    # Git 忽略文件配置
├── README.md                     # 專案說明文檔
├── LICENSE                       # 開源授權文件
├── DEVELOPMENT.md                # 開發指南
├── CONTRIBUTING.md               # 貢獻指南
├── CHANGELOG.md                  # 版本更新日誌
├── BUILD.md                      # 構建指南
└── PROJECT_STRUCTURE.md          # 專案結構說明（本文檔）
```

## 📱 應用模組 (app/)

### 主要目錄
```
app/
├── src/                          # 原始碼目錄
│   ├── main/                     # 主要原始碼
│   ├── test/                     # 單元測試
│   └── androidTest/              # Android 測試
├── build.gradle                  # 應用級構建配置
├── proguard-rules.pro            # ProGuard 混淆規則
└── local.properties              # 本地配置
```

### 主要原始碼 (src/main/)
```
src/main/
├── java/com/infoosd/             # Java 原始碼
│   ├── MainActivity.java         # 主活動
│   ├── SettingsActivity.java     # 設定活動
│   ├── OverlayService.java       # OSD 覆蓋層服務
│   ├── MinimalScreenshotService.java  # 截圖服務
│   ├── MinimalScreenshotActivity.java # 截圖權限請求
│   ├── SettingsManager.java      # 設定管理器
│   ├── PermissionManager.java    # 權限管理器
│   ├── BatteryReceiver.java      # 電池狀態接收器
│   └── BootReceiver.java         # 開機啟動接收器
├── res/                          # 資源文件
│   ├── layout/                   # 布局文件
│   ├── values/                   # 值資源
│   ├── values-zh/                # 繁體中文資源
│   ├── drawable/                 # 圖形資源
│   ├── mipmap-*/                 # 應用圖示（多密度）
│   └── xml/                      # XML 配置文件
└── AndroidManifest.xml           # 應用清單文件
```

## 🎨 資源文件詳解

### 布局文件 (res/layout/)
```
layout/
├── activity_main.xml             # 主界面布局
├── activity_settings.xml         # 設定界面布局
├── activity_enhanced_settings.xml # 增強設定界面
├── activity_simple_settings.xml  # 簡單設定界面
└── overlay_layout.xml            # OSD 覆蓋層布局
```

### 值資源 (res/values/)
```
values/
├── strings.xml                   # 字符串資源
├── colors.xml                    # 顏色資源
├── styles.xml                    # 樣式資源
└── dimens.xml                    # 尺寸資源
```

### 繁體中文資源 (res/values-zh/)
```
values-zh/
└── strings.xml                   # 繁體中文字符串
```

### 圖形資源 (res/drawable/)
```
drawable/
├── color_picker_button.xml       # 顏色選擇按鈕
├── ic_launcher_background.xml    # 啟動圖示背景
├── ic_launcher_foreground.xml    # 啟動圖示前景
└── ic_launcher.xml               # 啟動圖示
```

### 應用圖示 (res/mipmap-*/)
```
mipmap-mdpi/                      # 中密度 (48x48)
├── ic_launcher.png
└── ic_launcher_round.png

mipmap-hdpi/                      # 高密度 (72x72)
├── ic_launcher.png
└── ic_launcher_round.png

mipmap-xhdpi/                     # 超高密度 (96x96)
├── ic_launcher.png
└── ic_launcher_round.png

mipmap-xxhdpi/                    # 超超高密度 (144x144)
├── ic_launcher.png
└── ic_launcher_round.png

mipmap-xxxhdpi/                   # 最高密度 (192x192)
├── ic_launcher.png
└── ic_launcher_round.png
```

## 💻 Java 原始碼詳解

### 核心組件

#### MainActivity.java
- **職責**: 應用主界面，服務控制和設定入口
- **功能**: 
  - 服務啟動/停止控制
  - 權限狀態檢查
  - 設定頁面導航
  - 關於信息顯示

#### OverlayService.java
- **職責**: OSD 懸浮窗服務
- **功能**:
  - 創建和管理懸浮窗視圖
  - 電池狀態監聽和顯示
  - 時間更新和顯示
  - 點擊事件處理（觸發截圖）

#### MinimalScreenshotService.java
- **職責**: 截圖功能服務
- **功能**:
  - MediaProjection 權限管理
  - VirtualDisplay 創建和管理
  - 圖像捕獲和處理
  - 文件保存到系統相簿

#### SettingsActivity.java
- **職責**: 設定界面
- **功能**:
  - OSD 顯示設定（大小、顏色、位置）
  - 設定預覽功能
  - 設定數據保存和讀取

### 工具類

#### SettingsManager.java
- **職責**: 設定數據管理
- **功能**:
  - SharedPreferences 操作封裝
  - 設定值的讀取和保存
  - 預設值管理

#### PermissionManager.java
- **職責**: 權限管理工具
- **功能**:
  - 權限狀態檢查
  - 權限請求引導
  - 系統設定頁面跳轉

### 廣播接收器

#### BatteryReceiver.java
- **職責**: 電池狀態監聽
- **功能**:
  - 電池電量變化監聽
  - 充電狀態監聽
  - 電池信息更新通知

#### BootReceiver.java
- **職責**: 開機自啟動
- **功能**:
  - 系統開機事件監聽
  - 自動啟動 OSD 服務

## 🔧 配置文件詳解

### AndroidManifest.xml
```xml
<!-- 應用基本信息 -->
<application
    android:name=".InfoOSDApplication"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name">
    
    <!-- 活動聲明 -->
    <activity android:name=".MainActivity" />
    <activity android:name=".SettingsActivity" />
    
    <!-- 服務聲明 -->
    <service android:name=".OverlayService" />
    <service android:name=".MinimalScreenshotService" />
    
    <!-- 廣播接收器聲明 -->
    <receiver android:name=".BatteryReceiver" />
    <receiver android:name=".BootReceiver" />
</application>

<!-- 權限聲明 -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION" />
```

### build.gradle (應用級)
```gradle
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.infoosd"
        minSdk 24
        targetSdk 34
        versionCode 2
        versionName "0.1"
    }
    
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 
                         'proguard-rules.pro'
        }
    }
}
```

### gradle.properties
```properties
# 專案屬性
android.useAndroidX=true
android.enableJetifier=true

# 構建優化
org.gradle.jvmargs=-Xmx2048m
org.gradle.parallel=true
org.gradle.caching=true
```

## 🧪 測試結構

### 單元測試 (src/test/)
```
test/java/com/infoosd/
├── SettingsManagerTest.java      # 設定管理器測試
├── PermissionManagerTest.java    # 權限管理器測試
└── utils/
    └── TimeUtilsTest.java        # 時間工具測試
```

### Android 測試 (src/androidTest/)
```
androidTest/java/com/infoosd/
├── MainActivityTest.java         # 主界面測試
├── OverlayServiceTest.java       # OSD 服務測試
└── ScreenshotServiceTest.java    # 截圖服務測試
```

## 📦 構建輸出

### 構建目錄結構
```
app/build/
├── outputs/
│   ├── apk/
│   │   ├── debug/
│   │   │   └── app-debug.apk
│   │   └── release/
│   │       └── app-release.apk
│   └── logs/
├── reports/
│   ├── lint-results.html
│   └── tests/
└── tmp/
```

## 🔍 重要文件說明

### 配置文件
- **local.properties**: 本地 SDK 路徑配置（需自行創建）
- **gradle.properties**: Gradle 構建屬性
- **proguard-rules.pro**: 代碼混淆規則

### 文檔文件
- **README.md**: 專案概述和使用說明
- **DEVELOPMENT.md**: 詳細開發指南
- **CONTRIBUTING.md**: 貢獻者指南
- **CHANGELOG.md**: 版本更新記錄
- **BUILD.md**: 構建說明

### 版本控制
- **.gitignore**: Git 忽略文件配置
- **LICENSE**: MIT 開源授權

## 🎯 關鍵設計模式

### 服務模式
- **OverlayService**: 長期運行的前台服務
- **MinimalScreenshotService**: 按需啟動的截圖服務

### 單例模式
- **SettingsManager**: 全局設定管理
- **PermissionManager**: 權限狀態管理

### 觀察者模式
- **BatteryReceiver**: 電池狀態變化監聽
- **BootReceiver**: 系統事件監聽

### 工廠模式
- **NotificationHelper**: 通知創建和管理

## 📊 代碼統計

### 文件數量
- **Java 文件**: 9 個核心類
- **布局文件**: 5 個 XML 布局
- **資源文件**: 20+ 個資源文件
- **配置文件**: 5 個配置文件

### 代碼行數（估算）
- **Java 代碼**: ~2000 行
- **XML 資源**: ~800 行
- **配置文件**: ~200 行
- **文檔**: ~5000 行

## 🔧 開發工具配置

### Android Studio 設定
- **代碼風格**: 使用 Android 標準風格
- **Lint 檢查**: 啟用所有建議檢查
- **版本控制**: Git 整合

### 建議插件
- **Android Parcelable code generator**: 自動生成 Parcelable 代碼
- **ADB Idea**: ADB 命令快捷操作
- **Genymotion**: 模擬器整合

## 📚 學習資源

### 相關技術
- **MediaProjection API**: 螢幕捕獲技術
- **WindowManager**: 懸浮窗管理
- **Foreground Service**: 前台服務
- **SharedPreferences**: 數據持久化

### 參考文檔
- [Android Developer Guide](https://developer.android.com/guide)
- [Material Design Guidelines](https://material.io/design)
- [Android Architecture Guide](https://developer.android.com/jetpack/guide)

---

這個專案結構設計遵循 Android 開發最佳實踐，具有良好的可維護性和擴展性。每個組件都有明確的職責分工，便於理解和修改。

