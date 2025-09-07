# 構建指南

本文檔說明如何從原始碼構建 Info OSD 應用。

## 📋 前置需求

### 開發環境
- **Android Studio**: Hedgehog | 2023.1.1 或更新版本
- **JDK**: 8 或更高版本
- **Android SDK**: API 24-34
- **Git**: 用於版本控制

### Android SDK 組件
確保安裝以下 SDK 組件：
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0
- Android SDK Platform-Tools
- Android Emulator（用於測試）

## 🚀 快速開始

### 1. 克隆專案
```bash
git clone https://github.com/yourusername/InfoOSD.git
cd InfoOSD
```

### 2. 配置 Android SDK
創建 `local.properties` 文件：
```properties
# Android SDK 路徑（請根據您的實際路徑修改）
sdk.dir=/Users/username/Library/Android/sdk

# 如果使用 NDK（可選）
ndk.dir=/Users/username/Library/Android/sdk/ndk/25.1.8937393
```

### 3. 構建專案
```bash
# 清理專案
./gradlew clean

# 構建 Debug 版本
./gradlew assembleDebug

# 構建 Release 版本
./gradlew assembleRelease
```

### 4. 安裝到設備
```bash
# 安裝 Debug 版本
./gradlew installDebug

# 或者手動安裝 APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🔧 詳細構建步驟

### 使用 Android Studio

#### 1. 導入專案
1. 打開 Android Studio
2. 選擇 "Open an existing Android Studio project"
3. 選擇專案根目錄
4. 等待 Gradle 同步完成

#### 2. 配置專案
1. 確認 SDK 路徑正確設定
2. 檢查 Gradle 版本兼容性
3. 同步專案依賴

#### 3. 構建應用
1. 選擇構建變體（Debug/Release）
2. 點擊 "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"
3. 等待構建完成

### 使用命令行

#### 1. 檢查環境
```bash
# 檢查 Java 版本
java -version

# 檢查 Android SDK
echo $ANDROID_HOME

# 檢查 Gradle 版本
./gradlew --version
```

#### 2. 構建不同版本
```bash
# Debug 版本（用於開發和測試）
./gradlew assembleDebug

# Release 版本（用於發布）
./gradlew assembleRelease

# 所有版本
./gradlew assemble
```

#### 3. 運行測試
```bash
# 單元測試
./gradlew test

# 設備測試（需要連接設備或模擬器）
./gradlew connectedAndroidTest

# 所有測試
./gradlew check
```

## 📱 簽名配置

### Debug 簽名
Debug 版本使用 Android 預設的 debug keystore，無需額外配置。

### Release 簽名

#### 1. 生成簽名密鑰
```bash
keytool -genkey -v -keystore info-osd-release.jks \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -alias info-osd-key
```

#### 2. 配置簽名
在 `app/build.gradle` 中添加：
```gradle
android {
    signingConfigs {
        release {
            storeFile file('path/to/info-osd-release.jks')
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

#### 3. 構建簽名版本
```bash
./gradlew assembleRelease
```

## 🧪 測試構建

### 運行測試
```bash
# 運行所有測試
./gradlew test

# 運行特定測試類
./gradlew test --tests com.infoosd.SettingsManagerTest

# 生成測試報告
./gradlew test jacocoTestReport
```

### 代碼品質檢查
```bash
# 運行 Lint 檢查
./gradlew lint

# 查看 Lint 報告
open app/build/reports/lint-results.html
```

## 📦 輸出文件

構建完成後，APK 文件位於：
```
app/build/outputs/apk/
├── debug/
│   └── app-debug.apk
└── release/
    └── app-release.apk
```

## 🔍 故障排除

### 常見問題

#### Gradle 同步失敗
```bash
# 清理 Gradle 緩存
./gradlew clean

# 重新下載依賴
./gradlew --refresh-dependencies

# 檢查網路連接和代理設定
```

#### SDK 路徑問題
確保 `local.properties` 文件中的 SDK 路徑正確：
```properties
sdk.dir=/correct/path/to/android-sdk
```

#### 記憶體不足
在 `gradle.properties` 中增加記憶體配置：
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m
```

#### 依賴衝突
```bash
# 查看依賴樹
./gradlew app:dependencies

# 清理並重新構建
./gradlew clean build
```

### 日誌和調試

#### 查看構建日誌
```bash
# 詳細日誌
./gradlew assembleDebug --info

# 調試日誌
./gradlew assembleDebug --debug

# 堆疊追蹤
./gradlew assembleDebug --stacktrace
```

#### 分析 APK
```bash
# 使用 aapt 分析 APK
aapt dump badging app/build/outputs/apk/debug/app-debug.apk

# 查看 APK 內容
unzip -l app/build/outputs/apk/debug/app-debug.apk
```

## 🚀 持續整合

### GitHub Actions 配置
創建 `.github/workflows/build.yml`：
```yaml
name: Build

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
          
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Run tests
      run: ./gradlew test
      
    - name: Build APK
      run: ./gradlew assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

## 📊 構建優化

### 加速構建
在 `gradle.properties` 中添加：
```properties
# 啟用並行構建
org.gradle.parallel=true

# 啟用構建緩存
org.gradle.caching=true

# 啟用配置緩存
org.gradle.configuration-cache=true

# 增加記憶體
org.gradle.jvmargs=-Xmx4g -XX:MaxPermSize=512m
```

### 減少 APK 大小
在 `app/build.gradle` 中：
```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 
                         'proguard-rules.pro'
        }
    }
    
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/NOTICE.txt'
    }
}
```

## 📋 構建檢查清單

### 發布前檢查
- [ ] 所有測試通過
- [ ] Lint 檢查無嚴重問題
- [ ] 版本號已更新
- [ ] 簽名配置正確
- [ ] ProGuard 規則完整
- [ ] 在多個設備上測試
- [ ] 性能表現符合預期
- [ ] 文檔已更新

### 構建驗證
- [ ] APK 可以正常安裝
- [ ] 應用可以正常啟動
- [ ] 核心功能正常工作
- [ ] 權限請求正常
- [ ] 無崩潰和錯誤

## 📞 支援

如果在構建過程中遇到問題，請：

1. 檢查本文檔的故障排除部分
2. 搜索現有的 GitHub Issues
3. 創建新的 Issue 並提供詳細信息：
   - 操作系統和版本
   - Android Studio 版本
   - JDK 版本
   - 錯誤訊息和日誌
   - 重現步驟

## 📚 相關資源

- [Android Developer Guide](https://developer.android.com/guide)
- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html)
- [Android Studio User Guide](https://developer.android.com/studio/intro)
- [專案開發指南](DEVELOPMENT.md)

---

**祝您構建順利！** 🚀

