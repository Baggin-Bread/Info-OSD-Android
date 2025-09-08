# Build Guide

This document explains how to build the Info OSD application from source code.

## 📋 Prerequisites

### Development Environment
- **Android Studio**: Hedgehog | 2023.1.1 or newer
- **JDK**: 8 or higher
- **Android SDK**: API 24-34
- **Git**: For version control

### Android SDK Components
Ensure the following SDK components are installed:
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0
- Android SDK Platform-Tools
- Android Emulator (for testing)

## 🚀 Quick Start

### 1. Clone Project
```bash
git clone https://github.com/yourusername/InfoOSD.git
cd InfoOSD
```

### 2. Configure Android SDK
Create `local.properties` file:
```properties
# Android SDK path (modify according to your actual path)
sdk.dir=/Users/username/Library/Android/sdk

# If using NDK (optional)
ndk.dir=/Users/username/Library/Android/sdk/ndk/25.1.8937393
```

### 3. Build Project
```bash
# Clean project
./gradlew clean

# Build Debug version
./gradlew assembleDebug

# Build Release version
./gradlew assembleRelease
```

### 4. Install to Device
```bash
# Install Debug version
./gradlew installDebug

# Install Release version
./gradlew installRelease
```

## 🔧 Detailed Build Instructions

### Environment Setup

#### 1. Install Android Studio
1. Download from [Android Developer website](https://developer.android.com/studio)
2. Install following the official guide
3. Launch Android Studio and complete initial setup

#### 2. Configure SDK Manager
1. Open `Tools > SDK Manager`
2. In SDK Platforms tab, install:
   - Android 14.0 (API 34) - Target version
   - Android 7.0 (API 24) - Minimum version
3. In SDK Tools tab, install:
   - Android SDK Build-Tools 34.0.0
   - Android SDK Platform-Tools
   - Android Emulator
   - Intel x86 Emulator Accelerator (HAXM installer)

#### 3. Configure Project
1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Resolve any dependency issues

### Build Configurations

#### Debug Build
Debug builds include:
- Debug symbols
- Logging enabled
- No code obfuscation
- Faster build time

```bash
./gradlew assembleDebug
```

Output location: `app/build/outputs/apk/debug/app-debug.apk`

#### Release Build
Release builds include:
- Code obfuscation (ProGuard/R8)
- Optimized performance
- Smaller APK size
- Requires signing configuration

```bash
./gradlew assembleRelease
```

Output location: `app/build/outputs/apk/release/app-release.apk`

### Signing Configuration

#### Generate Keystore
```bash
keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release-key
```

#### Configure Signing in build.gradle
```gradle
android {
    signingConfigs {
        release {
            storeFile file('release-key.jks')
            storePassword 'your-store-password'
            keyAlias 'release-key'
            keyPassword 'your-key-password'
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

#### Security Note
- Never commit keystore files to version control
- Store passwords securely
- Use environment variables for CI/CD

```gradle
// Using environment variables
signingConfigs {
    release {
        storeFile file(System.getenv("KEYSTORE_FILE") ?: "release-key.jks")
        storePassword System.getenv("KEYSTORE_PASSWORD")
        keyAlias System.getenv("KEY_ALIAS")
        keyPassword System.getenv("KEY_PASSWORD")
    }
}
```

## 🧪 Testing

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run tests for specific variant
./gradlew testDebugUnitTest
./gradlew testReleaseUnitTest
```

### Instrumentation Tests
```bash
# Run all instrumentation tests
./gradlew connectedAndroidTest

# Run tests on specific device
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.infoosd.ExampleInstrumentedTest
```

### Test Coverage
```bash
# Generate test coverage report
./gradlew jacocoTestReport

# View coverage report
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

## 📦 Build Variants

### Build Types
- **debug**: Development builds with debugging enabled
- **release**: Production builds with optimizations

### Product Flavors (if applicable)
Currently, the project uses a single flavor, but can be extended:

```gradle
android {
    flavorDimensions "version"
    productFlavors {
        free {
            dimension "version"
            applicationIdSuffix ".free"
            versionNameSuffix "-free"
        }
        pro {
            dimension "version"
            applicationIdSuffix ".pro"
            versionNameSuffix "-pro"
        }
    }
}
```

## 🔍 Code Analysis

### Lint Analysis
```bash
# Run lint analysis
./gradlew lint

# View lint report
open app/build/reports/lint-results.html
```

### Static Analysis with SpotBugs
```bash
# Run SpotBugs analysis
./gradlew spotbugsMain

# View SpotBugs report
open app/build/reports/spotbugs/main.html
```

## 🚀 Continuous Integration

### GitHub Actions Example
Create `.github/workflows/build.yml`:

```yaml
name: Build and Test

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
    
    - name: Build Debug APK
      run: ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

## 🐛 Troubleshooting

### Common Build Issues

#### 1. SDK Not Found
**Error**: `SDK location not found`
**Solution**: Create or update `local.properties` with correct SDK path

#### 2. Gradle Sync Failed
**Error**: Various Gradle sync errors
**Solutions**:
```bash
# Clean and rebuild
./gradlew clean
./gradlew build

# Clear Gradle cache
rm -rf ~/.gradle/caches/
```

#### 3. Out of Memory Error
**Error**: `OutOfMemoryError` during build
**Solution**: Increase heap size in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
```

#### 4. Build Tools Version Mismatch
**Error**: Build tools version not found
**Solution**: Update `build.gradle`:
```gradle
android {
    compileSdkVersion 34
    buildToolsVersion "34.0.0"
}
```

### Performance Optimization

#### Build Performance
```properties
# gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
android.useAndroidX=true
android.enableJetifier=true
```

#### APK Size Optimization
```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

## 📱 Device Testing

### Emulator Setup
1. Open AVD Manager in Android Studio
2. Create new virtual device:
   - Device: Pixel 6 (recommended)
   - System Image: Android 14 (API 34)
   - Configuration: Default settings

### Physical Device Testing
1. Enable Developer Options on device
2. Enable USB Debugging
3. Connect device via USB
4. Verify device recognition:
```bash
adb devices
```

### Testing Checklist
- [ ] App installs successfully
- [ ] OSD displays correctly
- [ ] Screenshot function works
- [ ] Settings are saved
- [ ] Permissions are requested properly
- [ ] No crashes or ANRs

## 🔄 Version Management

### Updating Version
Update version in `app/build.gradle`:
```gradle
android {
    defaultConfig {
        versionCode 2
        versionName "0.2.0"
    }
}
```

### Version Naming Convention
- **versionCode**: Integer that increases with each release
- **versionName**: Human-readable version string (e.g., "1.0.0")

### Release Preparation
1. Update version numbers
2. Update CHANGELOG.md
3. Run full test suite
4. Build and test release APK
5. Create Git tag
6. Upload to release platform

## 📚 Additional Resources

### Documentation
- [Android Developer Guide](https://developer.android.com/guide)
- [Gradle Build Tool](https://gradle.org/guides/)
- [ProGuard Manual](https://www.guardsquare.com/proguard/manual)

### Tools
- [APK Analyzer](https://developer.android.com/studio/build/apk-analyzer) - Analyze APK contents
- [Layout Inspector](https://developer.android.com/studio/debug/layout-inspector) - Debug UI layouts
- [Memory Profiler](https://developer.android.com/studio/profile/memory-profiler) - Monitor memory usage

### Community
- [Android Developers Community](https://developer.android.com/community)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
- [Reddit r/androiddev](https://www.reddit.com/r/androiddev/)

---

This build guide should help you successfully build Info OSD from source. For additional help, please create an issue or contact the maintainers.

