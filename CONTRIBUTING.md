# 貢獻指南

感謝您對 Info OSD 專案的興趣！我們歡迎各種形式的貢獻，包括但不限於代碼、文檔、問題回報和功能建議。

## 📋 目錄

- [行為準則](#行為準則)
- [如何貢獻](#如何貢獻)
- [開發環境設定](#開發環境設定)
- [提交指南](#提交指南)
- [代碼規範](#代碼規範)
- [測試要求](#測試要求)
- [文檔貢獻](#文檔貢獻)
- [問題回報](#問題回報)
- [功能請求](#功能請求)

## 🤝 行為準則

### 我們的承諾

為了營造一個開放且友善的環境，我們作為貢獻者和維護者承諾，無論年齡、體型、殘疾、族裔、性別認同和表達、經驗水平、國籍、個人外觀、種族、宗教或性取向如何，參與我們專案和社群的每個人都能享有無騷擾的體驗。

### 我們的標準

有助於創造正面環境的行為包括：
- 使用友善和包容的語言
- 尊重不同的觀點和經驗
- 優雅地接受建設性批評
- 專注於對社群最有利的事情
- 對其他社群成員表現出同理心

不可接受的行為包括：
- 使用性化的語言或圖像，以及不受歡迎的性關注或騷擾
- 惡意評論、人身攻擊或政治攻擊
- 公開或私下騷擾
- 未經明確許可，發布他人的私人資訊
- 在專業環境中可能被認為不適當的其他行為

### 執行

如果您遇到不當行為，請聯繫專案維護者 chehui@gmail.com。所有投訴都會被審查和調查，並將根據情況做出適當的回應。

## 🚀 如何貢獻

### 貢獻類型

我們歡迎以下類型的貢獻：

#### 🐛 問題修復
- 修復已知的 bug
- 改善錯誤處理
- 提升應用穩定性

#### ✨ 新功能
- 添加新的 OSD 顯示選項
- 改善截圖功能
- 增強用戶界面

#### 📚 文檔改進
- 改善 README 和開發指南
- 添加代碼註釋
- 翻譯文檔到其他語言

#### 🧪 測試
- 編寫單元測試
- 進行設備兼容性測試
- 性能測試和優化

#### 🎨 設計
- 改善用戶界面設計
- 優化用戶體驗
- 創建圖示和圖形資源

### 貢獻流程

1. **Fork 專案**
   ```bash
   # 在 GitHub 上 fork 專案
   # 克隆您的 fork
   git clone https://github.com/yourusername/InfoOSD.git
   cd InfoOSD
   ```

2. **設定上游倉庫**
   ```bash
   git remote add upstream https://github.com/original/InfoOSD.git
   ```

3. **創建功能分支**
   ```bash
   git checkout -b feature/your-feature-name
   ```

4. **進行開發**
   - 編寫代碼
   - 添加測試
   - 更新文檔

5. **提交更改**
   ```bash
   git add .
   git commit -m "feat: 添加您的功能描述"
   ```

6. **推送分支**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **創建 Pull Request**
   - 在 GitHub 上創建 Pull Request
   - 填寫詳細的描述
   - 等待代碼審查

## 🛠️ 開發環境設定

### 必要工具
- **Android Studio**: Hedgehog | 2023.1.1 或更新
- **Android SDK**: API 24-34
- **JDK**: 8 或更高
- **Git**: 最新版本

### 設定步驟
1. **克隆專案**
   ```bash
   git clone https://github.com/yourusername/InfoOSD.git
   cd InfoOSD
   ```

2. **配置 Android SDK**
   創建 `local.properties` 文件：
   ```properties
   sdk.dir=/path/to/your/android-sdk
   ```

3. **安裝依賴**
   ```bash
   ./gradlew build
   ```

4. **運行測試**
   ```bash
   ./gradlew test
   ```

詳細的開發環境設定請參考 [DEVELOPMENT.md](DEVELOPMENT.md)。

## 📝 提交指南

### 提交訊息格式

我們使用 [Conventional Commits](https://www.conventionalcommits.org/) 格式：

```
<類型>[可選範圍]: <描述>

[可選正文]

[可選頁腳]
```

#### 類型
- `feat`: 新功能
- `fix`: 問題修復
- `docs`: 文檔更新
- `style`: 代碼格式調整（不影響功能）
- `refactor`: 代碼重構
- `test`: 測試相關
- `chore`: 建構過程或輔助工具的變動
- `perf`: 性能改進
- `ci`: CI/CD 相關變更

#### 範圍（可選）
- `ui`: 用戶界面
- `service`: 服務層
- `permission`: 權限管理
- `screenshot`: 截圖功能
- `settings`: 設定功能

#### 示例
```bash
feat(screenshot): 添加 JPEG 格式支援
fix(ui): 修復設定頁面布局問題
docs: 更新 README 安裝說明
test(service): 添加 OverlayService 單元測試
```

### 分支命名規範

- `feature/功能名稱`: 新功能開發
- `bugfix/問題描述`: 問題修復
- `hotfix/緊急修復`: 緊急修復
- `docs/文檔更新`: 文檔相關更新
- `refactor/重構描述`: 代碼重構

示例：
```bash
feature/cpu-usage-display
bugfix/permission-request-crash
hotfix/screenshot-memory-leak
docs/update-development-guide
refactor/optimize-overlay-service
```

## 💻 代碼規範

### Java 代碼風格

#### 命名規範
```java
// 類名：大駝峰命名法
public class OverlayService extends Service {
    
    // 常量：全大寫，下劃線分隔
    private static final String TAG = "OverlayService";
    private static final int NOTIFICATION_ID = 1001;
    
    // 變數：小駝峰命名法
    private WindowManager windowManager;
    private View overlayView;
    
    // 方法：小駝峰命名法
    private void createOverlayView() {
        // 實現
    }
}
```

#### 代碼格式
```java
// 縮排：4個空格
public class ExampleClass {
    
    // 方法間空行分隔
    public void method1() {
        if (condition) {
            // 大括號不換行
            doSomething();
        }
    }
    
    public void method2() {
        // 另一個方法
    }
}
```

#### 註釋規範
```java
/**
 * 創建 OSD 懸浮窗視圖
 * 
 * @param context 應用上下文
 * @return 創建的視圖，如果失敗則返回 null
 */
private View createOverlayView(Context context) {
    // 單行註釋說明具體實現
    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    
    /* 
     * 多行註釋說明複雜邏輯
     * 設定懸浮窗參數
     */
    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    
    return overlayView;
}
```

### XML 資源規範

#### 布局文件
```xml
<!-- 使用有意義的 ID 名稱 -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">
    
    <TextView
        android:id="@+id/titleText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/app_name"
        android:textSize="18sp"
        android:textStyle="bold" />
        
</LinearLayout>
```

#### 字符串資源
```xml
<!-- 使用描述性的鍵名 -->
<resources>
    <string name="app_name">Info OSD</string>
    <string name="service_start_button">啟動服務</string>
    <string name="service_stop_button">停止服務</string>
    <string name="permission_overlay_required">需要懸浮窗權限</string>
</resources>
```

### 代碼品質檢查

#### Lint 檢查
```bash
# 運行 Lint 檢查
./gradlew lint

# 查看 Lint 報告
open app/build/reports/lint-results.html
```

#### 代碼審查清單
- [ ] 代碼風格符合規範
- [ ] 變數和方法命名清晰
- [ ] 添加了適當的註釋
- [ ] 沒有重複代碼
- [ ] 錯誤處理完善
- [ ] 性能考慮合理
- [ ] 安全性檢查通過

## 🧪 測試要求

### 測試類型

#### 單元測試
```java
@RunWith(JUnit4.class)
public class SettingsManagerTest {
    
    private SettingsManager settingsManager;
    
    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getTargetContext();
        settingsManager = new SettingsManager(context);
    }
    
    @Test
    public void testDefaultValues() {
        assertEquals(16, settingsManager.getTextSize());
        assertEquals(Color.WHITE, settingsManager.getTextColor());
    }
    
    @Test
    public void testSetAndGetValues() {
        settingsManager.setTextSize(20);
        assertEquals(20, settingsManager.getTextSize());
    }
}
```

#### 整合測試
```java
@RunWith(AndroidJUnit4.class)
public class OverlayServiceTest {
    
    @Test
    public void testServiceStartAndStop() {
        Context context = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(context, OverlayService.class);
        
        // 測試服務啟動
        context.startService(intent);
        
        // 驗證服務狀態
        // ...
        
        // 測試服務停止
        context.stopService(intent);
    }
}
```

### 測試覆蓋率

我們目標達到以下測試覆蓋率：
- **單元測試**: 80% 以上
- **整合測試**: 60% 以上
- **核心功能**: 90% 以上

### 運行測試
```bash
# 運行所有測試
./gradlew test

# 運行特定測試類
./gradlew test --tests SettingsManagerTest

# 運行設備測試
./gradlew connectedAndroidTest

# 生成測試報告
./gradlew jacocoTestReport
```

## 📚 文檔貢獻

### 文檔類型

#### 代碼文檔
- 在代碼中添加清晰的註釋
- 為公共 API 編寫 Javadoc
- 說明複雜算法的實現邏輯

#### 用戶文檔
- 更新 README.md
- 改善安裝和使用指南
- 添加常見問題解答

#### 開發者文檔
- 更新 DEVELOPMENT.md
- 添加架構說明
- 編寫 API 文檔

### 文檔風格指南

#### Markdown 格式
```markdown
# 一級標題

## 二級標題

### 三級標題

- 無序列表項目
- 另一個項目

1. 有序列表項目
2. 另一個項目

**粗體文字**
*斜體文字*
`代碼片段`

```java
// 代碼區塊
public void example() {
    System.out.println("Hello World");
}
```

#### 中文寫作規範
- 使用繁體中文
- 中英文之間加空格
- 使用全形標點符號
- 保持語言簡潔明瞭

## 🐛 問題回報

### 回報前檢查

在提交新問題前，請：
1. 搜索現有的 Issues，確認問題未被回報
2. 確認您使用的是最新版本
3. 嘗試重現問題

### 問題模板

請使用以下模板回報問題：

```markdown
## 問題描述
簡潔明瞭地描述問題

## 重現步驟
1. 打開應用
2. 點擊 '...'
3. 看到錯誤

## 預期行為
描述您期望發生的情況

## 實際行為
描述實際發生的情況

## 環境信息
- 設備型號: [例如 Samsung Galaxy S21]
- Android 版本: [例如 Android 12]
- 應用版本: [例如 v0.1]

## 附加信息
- 錯誤日誌
- 截圖
- 其他相關信息
```

### 問題標籤

我們使用以下標籤分類問題：
- `bug`: 確認的問題
- `enhancement`: 功能改進
- `question`: 問題諮詢
- `documentation`: 文檔相關
- `good first issue`: 適合新手的問題
- `help wanted`: 需要幫助的問題

## 💡 功能請求

### 請求前考慮

在提交功能請求前，請考慮：
1. 功能是否符合專案目標
2. 是否有其他用戶需要此功能
3. 實現的複雜度和維護成本

### 功能請求模板

```markdown
## 功能描述
清楚描述您想要的功能

## 問題解決
說明此功能解決了什麼問題

## 建議解決方案
描述您認為應該如何實現

## 替代方案
描述您考慮過的其他解決方案

## 附加信息
任何其他相關信息、截圖或參考資料
```

## 🎯 優先級指南

### 高優先級
- 安全性問題
- 數據丟失問題
- 應用崩潰問題
- 核心功能失效

### 中優先級
- 性能問題
- 用戶體驗改進
- 新功能開發
- 文檔更新

### 低優先級
- 代碼重構
- 測試改進
- 開發工具優化

## 📞 聯絡方式

如果您有任何問題或需要幫助，可以通過以下方式聯絡我們：

### 專案維護者
**廖阿輝**
- 信箱: [chehui@gmail.com](mailto:chehui@gmail.com)
- 網站: [https://ahui3c.com](https://ahui3c.com)

### 社群討論
- GitHub Issues: 技術問題和 bug 回報
- GitHub Discussions: 一般討論和功能建議

## 🙏 致謝

感謝所有為 Info OSD 專案做出貢獻的開發者！您的貢獻讓這個專案變得更好。

### 貢獻者名單
- **廖阿輝** - 專案創始人和主要維護者

---

**再次感謝您對 Info OSD 專案的貢獻！** 🚀

