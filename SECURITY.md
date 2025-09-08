# Security Policy / 安全政策

## Supported Versions / 支援版本

We release patches for security vulnerabilities. Which versions are eligible for receiving such patches depends on the CVSS v3.0 Rating:

我們會為安全漏洞發布修補程式。哪些版本有資格接收此類修補程式取決於 CVSS v3.0 評級：

| Version | Supported / 支援 |
| ------- | ------------------ |
| 0.1.x   | :white_check_mark: |

## Reporting a Vulnerability / 回報漏洞

### English

If you discover a security vulnerability, please report it to us responsibly. We appreciate your efforts to improve the security of our project.

**Please do NOT report security vulnerabilities through public GitHub issues.**

Instead, please send an email to [chehui@gmail.com](mailto:chehui@gmail.com) with the following information:

- **Subject**: Security Vulnerability Report - Info OSD
- **Description**: A detailed description of the vulnerability
- **Steps to Reproduce**: Clear steps to reproduce the issue
- **Impact**: Potential impact of the vulnerability
- **Suggested Fix**: If you have suggestions for fixing the issue

### Response Timeline

- **Initial Response**: Within 48 hours
- **Status Update**: Within 7 days
- **Resolution**: Depends on severity and complexity

### What to Expect

1. **Acknowledgment**: We will acknowledge receipt of your report
2. **Investigation**: We will investigate and validate the vulnerability
3. **Fix Development**: We will develop and test a fix
4. **Disclosure**: We will coordinate disclosure with you
5. **Credit**: We will credit you in our security advisories (if desired)

---

### 繁體中文

如果您發現安全漏洞，請負責任地向我們回報。我們感謝您為改善專案安全性所做的努力。

**請勿透過公開的 GitHub Issues 回報安全漏洞。**

請發送電子郵件至 [chehui@gmail.com](mailto:chehui@gmail.com)，並包含以下資訊：

- **主旨**: 安全漏洞回報 - Info OSD
- **描述**: 漏洞的詳細描述
- **重現步驟**: 重現問題的清楚步驟
- **影響**: 漏洞的潛在影響
- **建議修復**: 如果您有修復問題的建議

### 回應時間表

- **初始回應**: 48 小時內
- **狀態更新**: 7 天內
- **解決方案**: 取決於嚴重性和複雜性

### 預期流程

1. **確認**: 我們將確認收到您的回報
2. **調查**: 我們將調查並驗證漏洞
3. **修復開發**: 我們將開發並測試修復方案
4. **披露**: 我們將與您協調披露事宜
5. **致謝**: 我們將在安全公告中致謝您（如果您希望）

## Security Best Practices / 安全最佳實踐

### For Users / 用戶

- **Download from Official Sources**: Only download the app from official GitHub releases
- **Verify Permissions**: Review app permissions before installation
- **Keep Updated**: Always use the latest version of the app
- **Report Issues**: Report any suspicious behavior immediately

### For Developers / 開發者

- **Code Review**: All code changes must be reviewed
- **Dependency Updates**: Keep dependencies up to date
- **Security Testing**: Perform security testing before releases
- **Secure Coding**: Follow secure coding practices

## Known Security Considerations / 已知安全考量

### Permissions / 權限

The app requires the following sensitive permissions:

應用需要以下敏感權限：

- **SYSTEM_ALERT_WINDOW**: For overlay display / 用於覆蓋層顯示
- **FOREGROUND_SERVICE_MEDIA_PROJECTION**: For screenshot functionality / 用於截圖功能

### Data Handling / 數據處理

- **No Data Collection**: The app does not collect or transmit user data / 應用不收集或傳輸用戶數據
- **Local Processing**: All operations are performed locally / 所有操作都在本地執行
- **No Network Access**: The app does not require internet permissions / 應用不需要網路權限

### Privacy / 隱私

- **Screenshot Storage**: Screenshots are saved locally to the device gallery / 截圖保存在設備相簿中
- **No Analytics**: No usage analytics or tracking / 無使用分析或追蹤
- **Open Source**: Full source code is available for audit / 完整原始碼可供審計

## Security Updates / 安全更新

Security updates will be released as soon as possible after a vulnerability is confirmed and fixed. Users will be notified through:

安全更新將在漏洞確認並修復後儘快發布。用戶將透過以下方式收到通知：

- GitHub Security Advisories
- Release Notes
- README Updates

## Contact / 聯絡

For security-related questions or concerns:

有關安全相關問題或疑慮：

- **Email**: [chehui@gmail.com](mailto:chehui@gmail.com)
- **Subject**: Security - Info OSD
- **Response Time**: Within 48 hours

---

Thank you for helping keep Info OSD and our users safe!

感謝您幫助保持 Info OSD 和我們用戶的安全！

