# Info OSD Development Guide

This document provides a detailed development guide for the Info OSD project to help developers get started quickly and contribute code.

## 📋 Table of Contents

- [Development Environment Setup](#development-environment-setup)
- [Project Architecture](#project-architecture)
- [Core Component Details](#core-component-details)
- [Development Workflow](#development-workflow)
- [Testing Guide](#testing-guide)
- [Release Process](#release-process)
- [FAQ](#faq)

## 🛠️ Development Environment Setup

### Required Tools

#### Android Studio
- **Version**: Android Studio Hedgehog | 2023.1.1 or newer
- **Download**: [Android Studio Official Website](https://developer.android.com/studio)

#### Android SDK
- **Minimum API**: 24 (Android 7.0)
- **Target API**: 34 (Android 14)
- **Build Tools**: 34.0.0

#### Java Development Environment
- **JDK Version**: JDK 8 or higher
- **Language Level**: Java 8

### Environment Configuration Steps

#### 1. Install Android Studio
```bash
# macOS (using Homebrew)
brew install --cask android-studio

# Windows
# Download and install .exe file

# Linux
# Download and extract .tar.gz file
```

#### 2. Configure Android SDK
In Android Studio:
1. Open `Tools > SDK Manager`
2. Install the following components:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android Emulator
   - Android SDK Platform-Tools
   - Intel x86 Emulator Accelerator (HAXM installer)

#### 3. Create local.properties
Create `local.properties` file in project root:
```properties
sdk.dir=/path/to/your/android-sdk
```

### IDE Configuration

#### Code Style Settings
1. Open `File > Settings > Editor > Code Style`
2. Import Android code style:
   - Download from: [Android Code Style](https://source.android.com/setup/contribute/code-style)
   - Import XML configuration file

#### Plugin Recommendations
- **Android ButterKnife Zelezny** - View binding generation
- **ADB Idea** - ADB command shortcuts
- **Android Drawable Importer** - Drawable resource management
- **Android Material Design Icon Generator** - Icon generation

## 🏗️ Project Architecture

### Overall Architecture

Info OSD follows the standard Android app architecture pattern:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Business     │    │      Data       │
│     Layer       │◄──►│     Layer       │◄──►│     Layer       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
│                 │    │                 │    │                 │
│ • Activities    │    │ • Services      │    │ • SharedPrefs   │
│ • Fragments     │    │ • Managers      │    │ • MediaStore    │
│ • Views         │    │ • Receivers     │    │ • System APIs   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Module Structure

#### Core Modules
- **UI Module** - User interface components
- **Service Module** - Background services
- **Manager Module** - Business logic managers
- **Receiver Module** - System event receivers
- **Utils Module** - Utility classes

#### Dependency Flow
```
UI Layer → Manager Layer → Service Layer → System APIs
```

## 🔧 Core Component Details

### 1. MainActivity
Main application entry point, responsible for:
- User interface initialization
- Permission request handling
- Service lifecycle management
- Settings page navigation

```java
public class MainActivity extends AppCompatActivity {
    private SettingsManager settingsManager;
    private PermissionManager permissionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeComponents();
        setupUI();
        checkPermissions();
    }
    
    private void initializeComponents() {
        settingsManager = new SettingsManager(this);
        permissionManager = new PermissionManager(this);
    }
}
```

### 2. OverlayService
Core OSD display service:

#### Key Features
- Floating window management
- Battery status monitoring
- Time display updates
- Click event handling

#### Implementation Details
```java
public class OverlayService extends Service {
    private WindowManager windowManager;
    private View overlayView;
    private BatteryReceiver batteryReceiver;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createOverlayView();
        registerBatteryReceiver();
        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }
    
    private void createOverlayView() {
        // Create floating window
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        
        overlayView = LayoutInflater.from(this)
            .inflate(R.layout.overlay_layout, null);
        windowManager.addView(overlayView, params);
    }
}
```

### 3. MinimalScreenshotService
Screenshot functionality service:

#### Core Process
1. **Permission Request** - MediaProjection permission
2. **Virtual Display** - Create screen capture display
3. **Image Capture** - Use ImageReader for capture
4. **File Saving** - Save to MediaStore

#### Implementation
```java
public class MinimalScreenshotService extends Service {
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    
    private void startScreenCapture() {
        imageReader = ImageReader.newInstance(
            screenWidth, screenHeight, 
            PixelFormat.RGBA_8888, 1
        );
        
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            screenWidth, screenHeight, screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.getSurface(), null, null
        );
        
        imageReader.setOnImageAvailableListener(
            new ImageAvailableListener(), backgroundHandler
        );
    }
}
```

### 4. SettingsManager
Application settings management:

#### Functionality
- SharedPreferences operations
- Default value management
- Setting validation
- Change notifications

```java
public class SettingsManager {
    private SharedPreferences preferences;
    private static final String PREF_NAME = "InfoOSDSettings";
    
    // Setting keys
    public static final String KEY_TEXT_SIZE = "text_size";
    public static final String KEY_TEXT_COLOR = "text_color";
    public static final String KEY_POSITION_X = "position_x";
    public static final String KEY_POSITION_Y = "position_y";
    
    public int getTextSize() {
        return preferences.getInt(KEY_TEXT_SIZE, DEFAULT_TEXT_SIZE);
    }
    
    public void setTextSize(int size) {
        preferences.edit().putInt(KEY_TEXT_SIZE, size).apply();
        notifySettingsChanged();
    }
}
```

### 5. PermissionManager
Dynamic permission management:

#### Supported Permissions
- `SYSTEM_ALERT_WINDOW` - Overlay permission
- `FOREGROUND_SERVICE_MEDIA_PROJECTION` - Screenshot service
- `READ_MEDIA_IMAGES` - Media access

```java
public class PermissionManager {
    public boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(context);
    }
    
    public void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
```

## 🔄 Development Workflow

### 1. Feature Development Process

#### Step 1: Create Feature Branch
```bash
git checkout -b feature/new-feature-name
```

#### Step 2: Implement Feature
1. Create necessary classes
2. Update AndroidManifest.xml if needed
3. Add UI components
4. Implement business logic
5. Add tests

#### Step 3: Testing
```bash
# Unit tests
./gradlew test

# Integration tests
./gradlew connectedAndroidTest

# Manual testing on device
./gradlew installDebug
```

#### Step 4: Code Review
1. Self-review code
2. Run static analysis
3. Check code style
4. Verify documentation

#### Step 5: Submit Pull Request
```bash
git add .
git commit -m "feat: add new feature description"
git push origin feature/new-feature-name
```

### 2. Bug Fix Process

#### Step 1: Reproduce Issue
1. Understand problem description
2. Create reproduction steps
3. Identify root cause

#### Step 2: Create Fix Branch
```bash
git checkout -b fix/issue-description
```

#### Step 3: Implement Fix
1. Write failing test (if applicable)
2. Implement fix
3. Verify test passes
4. Test on multiple devices

#### Step 4: Submit Fix
```bash
git commit -m "fix: resolve issue description"
git push origin fix/issue-description
```

## 🧪 Testing Guide

### Unit Testing

#### Test Structure
```
app/src/test/java/com/infoosd/
├── managers/
│   ├── SettingsManagerTest.java
│   └── PermissionManagerTest.java
├── services/
│   ├── OverlayServiceTest.java
│   └── ScreenshotServiceTest.java
└── utils/
    └── UtilsTest.java
```

#### Example Test
```java
@RunWith(MockitoJUnitRunner.class)
public class SettingsManagerTest {
    @Mock
    private Context mockContext;
    
    @Mock
    private SharedPreferences mockPreferences;
    
    private SettingsManager settingsManager;
    
    @Before
    public void setUp() {
        when(mockContext.getSharedPreferences(anyString(), anyInt()))
            .thenReturn(mockPreferences);
        settingsManager = new SettingsManager(mockContext);
    }
    
    @Test
    public void testGetTextSize_ReturnsDefaultValue() {
        when(mockPreferences.getInt(anyString(), anyInt()))
            .thenReturn(SettingsManager.DEFAULT_TEXT_SIZE);
        
        int textSize = settingsManager.getTextSize();
        
        assertEquals(SettingsManager.DEFAULT_TEXT_SIZE, textSize);
    }
}
```

### Integration Testing

#### Device Testing
```bash
# Run on connected device
./gradlew connectedAndroidTest

# Run on specific device
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.infoosd.ExampleInstrumentedTest
```

#### Test Categories
- **UI Tests** - User interface interaction
- **Service Tests** - Background service functionality
- **Permission Tests** - Permission request handling
- **Integration Tests** - Component interaction

### Manual Testing Checklist

#### Core Functionality
- [ ] OSD display appears correctly
- [ ] Battery level updates in real-time
- [ ] Time display is accurate
- [ ] Screenshot function works
- [ ] Settings are saved and applied
- [ ] Permissions are requested properly

#### Edge Cases
- [ ] Low battery scenarios
- [ ] Screen rotation
- [ ] App backgrounding/foregrounding
- [ ] System reboot
- [ ] Permission denial handling

#### Performance Testing
- [ ] Memory usage monitoring
- [ ] Battery consumption testing
- [ ] CPU usage analysis
- [ ] Network usage (if applicable)

## 📦 Release Process

### 1. Version Management

#### Version Naming Convention
```
vMAJOR.MINOR.PATCH
```
- **MAJOR** - Breaking changes
- **MINOR** - New features
- **PATCH** - Bug fixes

#### Update Version
In `app/build.gradle`:
```gradle
android {
    defaultConfig {
        versionCode 1
        versionName "0.1.0"
    }
}
```

### 2. Build Configuration

#### Debug Build
```bash
./gradlew assembleDebug
```

#### Release Build
```bash
./gradlew assembleRelease
```

#### Signing Configuration
```gradle
android {
    signingConfigs {
        release {
            storeFile file('release-key.jks')
            storePassword 'store-password'
            keyAlias 'key-alias'
            keyPassword 'key-password'
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### 3. Release Checklist

#### Pre-release
- [ ] All tests pass
- [ ] Code review completed
- [ ] Documentation updated
- [ ] Version number updated
- [ ] Changelog updated
- [ ] Release notes prepared

#### Release
- [ ] Create release build
- [ ] Sign APK
- [ ] Test on multiple devices
- [ ] Upload to GitHub Releases
- [ ] Update README badges
- [ ] Announce release

#### Post-release
- [ ] Monitor crash reports
- [ ] Respond to user feedback
- [ ] Plan next version
- [ ] Update project roadmap

## 🔍 Code Quality

### Static Analysis

#### Android Lint
```bash
./gradlew lint
```

#### FindBugs/SpotBugs
```bash
./gradlew spotbugsMain
```

### Code Style

#### Formatting Rules
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Use camelCase for variables and methods
- Use PascalCase for classes

#### Naming Conventions
```java
// Classes
public class OverlayService extends Service { }

// Methods
public void startScreenCapture() { }

// Variables
private WindowManager windowManager;
private static final String TAG = "OverlayService";

// Constants
public static final int DEFAULT_TEXT_SIZE = 16;
```

### Documentation Standards

#### JavaDoc Comments
```java
/**
 * Creates and displays the OSD overlay view.
 * 
 * @param context The application context
 * @param settings The display settings to apply
 * @return true if overlay was created successfully, false otherwise
 * @throws SecurityException if overlay permission is not granted
 */
public boolean createOverlay(Context context, DisplaySettings settings) {
    // Implementation
}
```

## 🐛 Debugging Guide

### Common Issues

#### 1. Overlay Not Displaying
**Symptoms**: OSD doesn't appear on screen
**Causes**:
- Missing overlay permission
- Incorrect window parameters
- Service not running

**Solutions**:
```java
// Check permission
if (!Settings.canDrawOverlays(context)) {
    requestOverlayPermission();
    return;
}

// Verify window parameters
WindowManager.LayoutParams params = new WindowManager.LayoutParams(
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Correct type
    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
    PixelFormat.TRANSLUCENT
);
```

#### 2. Screenshot Function Fails
**Symptoms**: Screenshot not captured or saved
**Causes**:
- Missing media projection permission
- Incorrect virtual display setup
- MediaStore API issues

**Solutions**:
```java
// Check MediaProjection permission
if (mediaProjection == null) {
    requestScreenCapturePermission();
    return;
}

// Proper ImageReader setup
imageReader = ImageReader.newInstance(
    displayMetrics.widthPixels,
    displayMetrics.heightPixels,
    PixelFormat.RGBA_8888,
    1
);
```

#### 3. Battery Level Not Updating
**Symptoms**: Battery percentage doesn't change
**Causes**:
- BatteryReceiver not registered
- Intent filter incorrect
- Service stopped

**Solutions**:
```java
// Register battery receiver
IntentFilter filter = new IntentFilter();
filter.addAction(Intent.ACTION_BATTERY_CHANGED);
registerReceiver(batteryReceiver, filter);
```

### Debugging Tools

#### ADB Commands
```bash
# View logs
adb logcat -s InfoOSD

# Install APK
adb install -r app-debug.apk

# Clear app data
adb shell pm clear com.infoosd

# Check permissions
adb shell dumpsys package com.infoosd
```

#### Android Studio Debugger
1. Set breakpoints in code
2. Run in debug mode
3. Use variable inspector
4. Step through execution

## 📚 Additional Resources

### Android Development
- [Android Developer Guide](https://developer.android.com/guide)
- [Android API Reference](https://developer.android.com/reference)
- [Material Design Guidelines](https://material.io/design)

### Testing
- [Android Testing Guide](https://developer.android.com/training/testing)
- [Espresso Testing Framework](https://developer.android.com/training/testing/espresso)
- [Mockito Framework](https://site.mockito.org/)

### Tools
- [Android Studio](https://developer.android.com/studio)
- [Gradle Build Tool](https://gradle.org/)
- [Git Version Control](https://git-scm.com/)

## 🤝 Contributing Guidelines

### Code Contribution
1. Fork the repository
2. Create feature branch
3. Follow coding standards
4. Add tests for new features
5. Update documentation
6. Submit pull request

### Issue Reporting
When reporting issues, please include:
- Device model and Android version
- App version
- Detailed steps to reproduce
- Expected vs actual behavior
- Relevant logs or screenshots

### Feature Requests
For new feature requests:
- Describe the use case
- Explain the expected behavior
- Consider implementation complexity
- Discuss with maintainers first

---

This development guide should help you get started with Info OSD development. For questions or clarifications, please create an issue or contact the maintainers.

