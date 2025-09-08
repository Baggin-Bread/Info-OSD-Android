# Info OSD Project Structure

This document provides a detailed explanation of the Info OSD project's directory structure and file organization.

## 📁 Root Directory Structure

```
InfoOSD/
├── app/                          # Main application module
├── gradle/                       # Gradle wrapper files
├── build.gradle                  # Project-level build configuration
├── settings.gradle               # Project settings
├── gradle.properties             # Gradle properties configuration
├── gradlew                       # Gradle wrapper script (Unix)
├── gradlew.bat                   # Gradle wrapper script (Windows)
├── local.properties              # Local configuration (create yourself)
├── .gitignore                    # Git ignore file configuration
├── README.md                     # Project documentation
├── LICENSE                       # Open source license file
├── DEVELOPMENT.md                # Development guide
├── CONTRIBUTING.md               # Contributing guide
├── CHANGELOG.md                  # Version changelog
├── BUILD.md                      # Build guide
└── PROJECT_STRUCTURE.md          # Project structure documentation (this document)
```

## 📱 Application Module (app/)

### Main Directories
```
app/
├── src/                          # Source code directory
│   ├── main/                     # Main source code
│   ├── test/                     # Unit tests
│   └── androidTest/              # Android tests
├── build.gradle                  # Application-level build configuration
├── proguard-rules.pro            # ProGuard obfuscation rules
└── local.properties              # Local configuration
```

### Main Source Code (src/main/)
```
src/main/
├── java/com/infoosd/             # Java source code
│   ├── MainActivity.java         # Main activity
│   ├── SettingsActivity.java     # Settings activity
│   ├── OverlayService.java       # OSD overlay service
│   ├── MinimalScreenshotService.java  # Screenshot service
│   ├── MinimalScreenshotActivity.java # Screenshot permission request
│   ├── SettingsManager.java      # Settings manager
│   ├── PermissionManager.java    # Permission manager
│   ├── BatteryReceiver.java      # Battery status receiver
│   └── BootReceiver.java         # Boot startup receiver
├── res/                          # Resource files
│   ├── layout/                   # UI layout files
│   ├── values/                   # String and style resources
│   ├── values-zh/                # Traditional Chinese resources
│   ├── drawable/                 # Graphic resources
│   ├── mipmap-*/                 # App icons (multi-density)
│   └── xml/                      # XML configuration files
└── AndroidManifest.xml          # Application manifest file
```

## 🎯 Core Components

### Activities
- **MainActivity.java** - Main application entry point
  - User interface initialization
  - Service management
  - Permission handling
  - Settings navigation

- **SettingsActivity.java** - Settings configuration interface
  - Text size adjustment
  - Color selection
  - Position settings
  - Permission management

- **MinimalScreenshotActivity.java** - Screenshot permission request
  - MediaProjection permission handling
  - User guidance for permissions
  - Service communication

### Services
- **OverlayService.java** - Core OSD display service
  - Floating window management
  - Battery status monitoring
  - Time display updates
  - Click event handling

- **MinimalScreenshotService.java** - Screenshot functionality service
  - MediaProjection management
  - VirtualDisplay creation
  - Image capture and processing
  - MediaStore integration

### Managers
- **SettingsManager.java** - Application settings management
  - SharedPreferences operations
  - Default value handling
  - Setting validation
  - Change notifications

- **PermissionManager.java** - Dynamic permission management
  - Permission status checking
  - Permission request handling
  - User guidance for permissions
  - System settings navigation

### Receivers
- **BatteryReceiver.java** - Battery status monitoring
  - Battery level changes
  - Charging state updates
  - Power connection events

- **BootReceiver.java** - Boot startup handling
  - Auto-start functionality
  - Service initialization
  - Permission verification

## 📱 Resources (res/)

### Layout Files (layout/)
```
layout/
├── activity_main.xml             # Main activity layout
├── activity_settings.xml         # Settings activity layout
├── overlay_layout.xml            # OSD overlay layout
└── screenshot_permission_layout.xml  # Permission request layout
```

### Values (values/)
```
values/
├── strings.xml                   # English string resources
├── colors.xml                    # Color definitions
├── styles.xml                    # UI styles and themes
├── dimens.xml                    # Dimension values
└── arrays.xml                    # Array resources
```

### Localization (values-zh/)
```
values-zh/
└── strings.xml                   # Traditional Chinese strings
```

### Drawables (drawable/)
```
drawable/
├── ic_launcher_background.xml    # Launcher icon background
├── ic_launcher_foreground.xml    # Launcher icon foreground
├── button_background.xml         # Button background drawable
└── overlay_background.xml        # OSD background drawable
```

### App Icons (mipmap-*)
```
mipmap-hdpi/
├── ic_launcher.png               # High-density icon
└── ic_launcher_round.png         # Round icon variant

mipmap-mdpi/
├── ic_launcher.png               # Medium-density icon
└── ic_launcher_round.png

mipmap-xhdpi/
├── ic_launcher.png               # Extra high-density icon
└── ic_launcher_round.png

mipmap-xxhdpi/
├── ic_launcher.png               # Extra extra high-density icon
└── ic_launcher_round.png

mipmap-xxxhdpi/
├── ic_launcher.png               # Extra extra extra high-density icon
└── ic_launcher_round.png
```

## 🧪 Testing Structure

### Unit Tests (src/test/)
```
src/test/java/com/infoosd/
├── SettingsManagerTest.java      # Settings manager tests
├── PermissionManagerTest.java    # Permission manager tests
├── BatteryReceiverTest.java      # Battery receiver tests
└── utils/
    └── TestUtils.java            # Test utility classes
```

### Android Tests (src/androidTest/)
```
src/androidTest/java/com/infoosd/
├── MainActivityTest.java         # Main activity UI tests
├── OverlayServiceTest.java       # Overlay service tests
├── ScreenshotServiceTest.java    # Screenshot service tests
└── ExampleInstrumentedTest.java  # Example instrumentation test
```

## 🔧 Configuration Files

### Build Configuration
- **build.gradle (Project)** - Project-level build settings
  - Gradle plugin versions
  - Repository configurations
  - Global dependencies

- **build.gradle (App)** - Application-level build settings
  - Compile and target SDK versions
  - Application ID and version
  - Dependencies and build types
  - Signing configurations

- **gradle.properties** - Gradle properties
  - JVM arguments
  - Android build optimizations
  - Gradle daemon settings

- **settings.gradle** - Project settings
  - Module inclusions
  - Project name configuration

### Android Configuration
- **AndroidManifest.xml** - Application manifest
  - Application components declaration
  - Permissions and features
  - Intent filters
  - Application metadata

- **proguard-rules.pro** - Code obfuscation rules
  - Keep rules for reflection
  - Library-specific rules
  - Optimization settings

## 📚 Documentation Structure

### Main Documentation
- **README.md** - Project overview and quick start
- **README_EN.md** - English version of README
- **DEVELOPMENT.md** - Detailed development guide
- **DEVELOPMENT_EN.md** - English development guide

### Contributing Documentation
- **CONTRIBUTING.md** - Contribution guidelines
- **CONTRIBUTING_EN.md** - English contribution guidelines
- **CHANGELOG.md** - Version history and changes
- **CHANGELOG_EN.md** - English changelog

### Technical Documentation
- **BUILD.md** - Build instructions and configuration
- **BUILD_EN.md** - English build guide
- **PROJECT_STRUCTURE.md** - This document
- **PROJECT_STRUCTURE_EN.md** - English project structure

### Legal Documentation
- **LICENSE** - MIT license text

## 🔄 Version Control

### Git Configuration
- **.gitignore** - Files and directories to ignore
  - Build outputs
  - IDE files
  - Local configuration
  - Temporary files

### Ignored Files and Directories
```
# Build outputs
/build/
/app/build/
*.apk
*.aab

# IDE files
.idea/
*.iml
.vscode/

# Local configuration
local.properties
keystore.properties

# Temporary files
*.tmp
*.log
.DS_Store
```

## 🚀 Build Outputs

### APK Generation
```
app/build/outputs/apk/
├── debug/
│   └── app-debug.apk             # Debug build APK
└── release/
    └── app-release.apk           # Release build APK
```

### Reports and Logs
```
app/build/
├── reports/                      # Build reports
│   ├── lint-results.html         # Lint analysis report
│   ├── tests/                    # Test reports
│   └── jacoco/                   # Code coverage reports
└── outputs/
    ├── logs/                     # Build logs
    └── mapping/                  # ProGuard mapping files
```

## 🔍 Code Organization Principles

### Package Structure
- **Single package approach** - All classes in `com.infoosd`
- **Functional grouping** - Related classes grouped by functionality
- **Clear naming** - Descriptive class and method names

### Naming Conventions
- **Classes** - PascalCase (e.g., `OverlayService`)
- **Methods** - camelCase (e.g., `startScreenCapture`)
- **Variables** - camelCase (e.g., `windowManager`)
- **Constants** - UPPER_SNAKE_CASE (e.g., `DEFAULT_TEXT_SIZE`)

### Code Style
- **Indentation** - 4 spaces
- **Line length** - Maximum 120 characters
- **Braces** - Opening brace on same line
- **Comments** - JavaDoc for public methods

## 📦 Dependencies

### Core Dependencies
```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.google.android.material:material:1.9.0'
}
```

### Test Dependencies
```gradle
testImplementation 'junit:junit:4.13.2'
androidTestImplementation 'androidx.test.ext:junit:1.1.5'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
```

## 🔧 Development Workflow

### File Modification Workflow
1. **Identify component** - Determine which file needs modification
2. **Understand dependencies** - Check related files and components
3. **Make changes** - Implement modifications following code style
4. **Test changes** - Run relevant tests
5. **Update documentation** - Update related documentation if needed

### Adding New Features
1. **Create new classes** - Add to appropriate package
2. **Update manifest** - Add new components if needed
3. **Add resources** - Create necessary layouts and strings
4. **Implement tests** - Add unit and integration tests
5. **Update documentation** - Document new functionality

## 📱 Platform Considerations

### Android Version Support
- **Minimum SDK** - API 24 (Android 7.0)
- **Target SDK** - API 34 (Android 14)
- **Compile SDK** - API 34

### Architecture Support
- ARM64-v8a (64-bit ARM)
- armeabi-v7a (32-bit ARM)
- x86 (32-bit Intel)
- x86_64 (64-bit Intel)

### Screen Density Support
- ldpi (120dpi)
- mdpi (160dpi)
- hdpi (240dpi)
- xhdpi (320dpi)
- xxhdpi (480dpi)
- xxxhdpi (640dpi)

---

This project structure documentation helps developers understand the codebase organization and navigate the project effectively. For questions about specific components, please refer to the relevant source files or create an issue.

