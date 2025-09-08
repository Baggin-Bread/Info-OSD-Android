# Info OSD - Android 螢幕資訊顯示工具

<div align="center">
  <img src="screenshots/app_icon.png" alt="Info OSD 應用圖示" width="120" height="120">
  
  <h3>🔋 電池電量 | ⏰ 時間顯示 | 📸 截圖功能</h3>
  
  <p>
    <strong>Info OSD 是一個可以以 OSD 方式顯示現在的時間與剩餘電量的小工具程式，完全免費！</strong>
  </p>
  
  <p>
    <a href="https://github.com/ahui3c/Info-OSD-Android/releases/latest">
      <img src="https://img.shields.io/github/v/release/ahui3c/Info-OSD-Android?style=for-the-badge&logo=android&logoColor=white&color=green" alt="最新版本">
    </a>
    <a href="https://github.com/ahui3c/Info-OSD-Android/releases/latest">
      <img src="https://img.shields.io/github/downloads/ahui3c/Info-OSD-Android/total?style=for-the-badge&logo=download&logoColor=white&color=blue" alt="下載次數">
    </a>
    <a href="https://github.com/ahui3c/Info-OSD-Android/blob/main/LICENSE">
      <img src="https://img.shields.io/github/license/ahui3c/Info-OSD-Android?style=for-the-badge&color=orange" alt="授權">
    </a>
  </p>
</div>

---

## 🌐 語言選擇 / Language

- **繁體中文** | [English](README_EN.md)

---

## 📱 應用截圖

### 🏠 OSD 顯示效果
在任何畫面都能清楚顯示電池電量和當前時間，不干擾正常使用

<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src="screenshots/osd_display_home.png" alt="主畫面 OSD 顯示" width="280">
        <br>
        <sub><b>主畫面顯示</b></sub>
      </td>
      <td align="center">
        <img src="screenshots/osd_display_apps.png" alt="應用列表 OSD 顯示" width="280">
        <br>
        <sub><b>應用列表顯示</b></sub>
      </td>
    </tr>
  </table>
</div>

### 🎮 應用界面
簡潔直觀的中文界面，輕鬆管理所有功能

<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src="screenshots/main_interface.png" alt="主界面" width="280">
        <br>
        <sub><b>主界面</b></sub>
      </td>
      <td align="center">
        <img src="screenshots/settings_interface.png" alt="設定界面" width="280">
        <br>
        <sub><b>設定界面</b></sub>
      </td>
    </tr>
  </table>
</div>

### 📸 截圖功能展示
點擊 OSD 即可觸發截圖，自動保存到系統相簿

<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src="screenshots/screenshot_demo.png" alt="截圖功能演示" width="280">
        <br>
        <sub><b>遊戲中截圖演示</b></sub>
      </td>
      <td align="center">
        <img src="screenshots/screenshot_help.png" alt="截圖功能說明" width="280">
        <br>
        <sub><b>截圖功能說明</b></sub>
      </td>
    </tr>
  </table>
</div>

---

## ✨ 功能特色

### 🎯 核心功能
- **🔋 電池電量顯示** - 實時顯示電池電量百分比
- **⏰ 時間顯示** - 顯示當前時間（24小時制）
- **📸 截圖功能** - 點擊 OSD 觸發截圖，自動保存到系統相簿
- **⚙️ 自定義設置** - 可調整文字大小、顏色和顯示位置
- **🔐 權限管理** - 智能的動態權限請求和管理

### 🌟 用戶體驗
- **🌏 完整中文化** - 所有界面元素都是繁體中文
- **🎨 專業圖示** - 精美的應用圖示設計
- **📱 系統整合** - 完美整合系統相簿、瀏覽器和郵件
- **🚀 低資源消耗** - 高效的服務實現，不影響系統性能
- **💯 完全免費** - 無廣告、無內購、無使用限制

---

## 🛠️ 技術規格

### 系統需求
- **Android 版本**: 7.0 (API 24) 或更高
- **目標 SDK**: Android 14 (API 34)
- **架構支援**: ARM64, ARM, x86, x86_64
- **權限需求**: 懸浮窗權限、媒體投影權限

### 技術特性
- **MediaProjection API** - 用於截圖功能
- **MediaStore API** - 用於圖片保存到系統相簿
- **前台服務** - 確保 OSD 穩定運行
- **動態權限** - 智能的權限請求管理
- **多密度支援** - 適配各種螢幕解析度

---

## 🚀 快速開始

### 📥 下載安裝
1. **下載 APK** - 從 [Releases 頁面](https://github.com/ahui3c/Info-OSD-Android/releases/latest) 下載最新版本
2. **安裝應用** - 允許未知來源安裝，然後安裝 APK 檔案
3. **授予權限** - 首次啟動時授予懸浮窗顯示權限
4. **開始使用** - 點擊「啟動服務」按鈕即可開始使用

### 🎮 使用說明
1. **啟動服務** - 在主界面點擊「啟動服務」
2. **查看 OSD** - 螢幕上會顯示電池電量和時間
3. **截圖功能** - 點擊 OSD 區域即可觸發截圖
4. **自定義設置** - 在設定頁面調整顯示選項

---

## 🏗️ 專案結構

```
InfoOSD/
├── app/
│   ├── src/main/
│   │   ├── java/com/infoosd/
│   │   │   ├── MainActivity.java              # 主活動
│   │   │   ├── SettingsActivity.java          # 設定活動
│   │   │   ├── OverlayService.java            # OSD 覆蓋層服務
│   │   │   ├── MinimalScreenshotService.java  # 截圖服務
│   │   │   ├── MinimalScreenshotActivity.java # 截圖權限請求
│   │   │   ├── SettingsManager.java           # 設定管理器
│   │   │   ├── PermissionManager.java         # 權限管理器
│   │   │   ├── BatteryReceiver.java           # 電池狀態接收器
│   │   │   └── BootReceiver.java              # 開機啟動接收器
│   │   ├── res/
│   │   │   ├── layout/                        # 界面布局文件
│   │   │   ├── values/                        # 字符串和樣式資源
│   │   │   ├── values-zh/                     # 繁體中文資源
│   │   │   ├── drawable/                      # 圖形資源
│   │   │   └── mipmap-*/                      # 應用圖示（多密度）
│   │   └── AndroidManifest.xml               # 應用清單文件
│   ├── build.gradle                          # 應用構建配置
│   └── proguard-rules.pro                    # 代碼混淆規則
├── gradle/                                   # Gradle 包裝器
├── build.gradle                              # 專案構建配置
├── settings.gradle                           # 專案設定
├── gradle.properties                         # Gradle 屬性
├── local.properties                          # 本地配置（需自行創建）
├── README.md                                 # 專案說明文檔
├── LICENSE                                   # 開源授權
└── DEVELOPMENT.md                            # 開發指南
```

---

## 🔧 開發指南

### 環境準備
1. **Android Studio** - 建議使用最新版本
2. **Android SDK** - API 24 或更高
3. **Java/Kotlin** - 支援 Java 8 或更高

### 構建步驟
1. **克隆專案**
   ```bash
   git clone https://github.com/ahui3c/Info-OSD-Android.git
   cd Info-OSD-Android
   ```

2. **配置 Android SDK**
   創建 `local.properties` 文件：
   ```properties
   sdk.dir=/path/to/your/android-sdk
   ```

3. **構建 APK**
   ```bash
   ./gradlew assembleDebug
   ```

4. **安裝到設備**
   ```bash
   ./gradlew installDebug
   ```

### 詳細開發指南
更多開發資訊請參考：
- [開發指南 (DEVELOPMENT.md)](DEVELOPMENT.md) | [English Version](DEVELOPMENT_EN.md)
- [構建指南 (BUILD.md)](BUILD.md) | [English Version](BUILD_EN.md)
- [專案結構 (PROJECT_STRUCTURE.md)](PROJECT_STRUCTURE.md) | [English Version](PROJECT_STRUCTURE_EN.md)

---

## 🤝 貢獻指南

我們歡迎各種形式的貢獻！請參考：
- [貢獻指南 (CONTRIBUTING.md)](CONTRIBUTING.md) | [English Version](CONTRIBUTING_EN.md)

### 如何貢獻
1. **Fork 專案** - 創建專案的分支
2. **創建功能分支** - `git checkout -b feature/new-feature`
3. **提交更改** - `git commit -am 'Add new feature'`
4. **推送分支** - `git push origin feature/new-feature`
5. **創建 Pull Request** - 提交合併請求

### 問題回報
請使用 GitHub Issues 回報問題，包含：
- 設備型號和 Android 版本
- 詳細的問題描述
- 重現步驟
- 相關的日誌信息

---

## 📄 授權

本專案採用 MIT 授權條款 - 詳見 [LICENSE](LICENSE) 文件

---

## 👨‍💻 作者

**廖阿輝**
- 網站: [https://ahui3c.com](https://ahui3c.com)
- 信箱: [chehui@gmail.com](mailto:chehui@gmail.com)
- 社群: FB 粉絲團與 Youtube 搜尋「3C 達人廖阿輝」

---

## 🙏 致謝

感謝所有為這個專案做出貢獻的開發者和使用者。

---

## 📚 相關資源

### Android 開發文檔
- [Android Developer Guide](https://developer.android.com/guide)
- [MediaProjection API](https://developer.android.com/reference/android/media/projection/MediaProjection)
- [Foreground Services](https://developer.android.com/guide/components/foreground-services)

### 專案文檔
- [版本更新日誌 (CHANGELOG.md)](CHANGELOG.md) | [English Version](CHANGELOG_EN.md)

---

## 🔄 版本歷史

### v0.2 (2025-09-08)
- ✨ **多國語言界面支援** - 新增繁體中文、英文、簡體中文三種語言
- 🌐 自動語言切換功能
- 📱 完整的國際化資源結構
- 🔄 版本號升級和技術架構優化

### v0.1 (2025-09-07)
- ✨ 初始版本發布
- ✨ OSD 電池和時間顯示功能
- ✨ 截圖功能實現
- ✨ 自定義設置支援
- ✨ 完整中文化界面
- ✨ 專業圖示設計
- ✨ 系統相簿整合

---

## 🚧 未來計劃

### 短期目標
- [ ] 添加更多 OSD 顯示選項（CPU、記憶體使用率）
- [ ] 支援更多截圖格式和品質設定
- [ ] 添加主題和外觀自定義
- [ ] 優化電池消耗

### 長期目標
- [ ] 支援 Android 15+ 新特性
- [ ] 添加雲端同步功能
- [ ] 開發 Wear OS 版本
- [ ] 國際化支援（多語言）

---

## 📞 支援

如果您在使用過程中遇到問題或有建議，歡迎：
- 創建 GitHub Issue
- 發送郵件到 chehui@gmail.com
- 在社群媒體上聯繫我們

---

<div align="center">
  <h3>🚀 讓您的 Android 設備更加智能便捷！</h3>
  
  <p>
    <a href="https://github.com/ahui3c/Info-OSD-Android/releases/latest">
      <img src="https://img.shields.io/badge/下載-最新版本-brightgreen?style=for-the-badge&logo=android" alt="下載最新版本">
    </a>
  </p>
</div>

