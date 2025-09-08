# Changelog

This document records all important changes to the Info OSD project.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [0.2.0] - 2025-09-08

### Added
- ✨ **Multi-language Interface Support** - Added complete multi-language support system
  - Traditional Chinese (default language)
  - English
  - Simplified Chinese (简体中文)
- 🌐 **Automatic Language Switching** - Automatically selects corresponding interface based on system language settings
- 📱 **Internationalization Resource Structure** - Established complete multi-language resource file architecture
- 🔄 **Version Upgrade** - Application version upgraded to 0.2

### Improved
- 📚 **Multi-language Documentation** - All interface elements have corresponding multi-language translations
- 🎯 **User Experience** - Provides localized experience for users of different languages
- 🔧 **Technical Architecture** - Optimized resource file structure to support multi-language

### Technical Details
- versionCode: 2 → 3
- versionName: "0.1" → "0.2"
- Added language resource directories:
  - `values-en/` - English resources
  - `values-zh-rCN/` - Simplified Chinese resources
- Updated version information in About page for all language versions

## [Unreleased]

### Planned Additions
- CPU usage display option
- Memory usage display option
- More screenshot format support (JPEG, WebP)
- Screenshot quality setting options
- Theme and appearance customization
- Cloud sync functionality
- Multi-language support (English, Japanese, Korean)

### Planned Improvements
- Optimize battery consumption
- Improve screenshot speed
- Enhance UI/UX design
- Strengthen error handling

## [0.1] - 2025-09-07

### Added
- **OSD Display Functionality**
  - Real-time battery percentage display
  - Current time display (24-hour format)
  - Floating window display that doesn't interfere with other apps

- **Screenshot Functionality**
  - Tap OSD to trigger screenshot
  - High-quality screenshots using MediaProjection API
  - Auto-save to Screenshots folder in system gallery
  - PNG format support maintaining original quality

- **Custom Settings**
  - Text size adjustment (12sp - 24sp)
  - Text color selection (White, Black, Red, Green, Blue)
  - OSD display position adjustment
  - Persistent settings data storage

- **Permission Management**
  - Smart overlay permission requests
  - Dynamic MediaProjection permission requests
  - Permission status checking and guidance
  - Quick navigation to system permission settings

- **System Integration**
  - Boot auto-start functionality
  - Real-time battery status change monitoring
  - Automatic system time change updates
  - Foreground service ensures stable operation

- **User Interface**
  - Complete Traditional Chinese interface
  - Clear feature descriptions and introductions
  - Professional app icon design
  - Intuitive settings interface

- **Developer Features**
  - Complete open source code
  - Detailed development documentation
  - MIT open source license
  - Clickable contact information (website, email)

### Technical Features
- **Android Version Support**: 7.0 (API 24) to 14 (API 34)
- **Architecture Support**: ARM64, ARM, x86, x86_64
- **Principle of Least Privilege**: Only requests necessary permissions
- **Memory Optimization**: Low memory footprint, efficient operation
- **Battery Friendly**: Optimized service implementation, minimal battery consumption

### Security
- **Permission Transparency**: Clear explanation of each permission's purpose
- **Data Privacy**: No user data collection
- **Local Processing**: All functions execute on local device
- **Open Source Transparency**: Fully open source, code is auditable

### Known Issues
- First screenshot may take longer on some devices
- Some third-party launchers may require manual icon refresh
- Android 14 devices require additional foreground service permissions

### Fixed Issues
- Fixed MediaProjection callback registration issue
- Resolved VirtualDisplay creation failure problem
- Corrected app icon display issues
- Optimized screenshot service lifecycle management
- Improved permission request user experience

## Development History

### 2025-09-07
- **Project Launch**: Started Info OSD project development
- **Core Feature Implementation**: Completed OSD display and screenshot functionality
- **Permission System**: Implemented dynamic permission management
- **UI Design**: Completed user interface design and Chinese localization
- **Icon Design**: Created professional app icon
- **Testing and Optimization**: Conducted comprehensive testing and performance optimization
- **Documentation**: Completed development documentation and user guides
- **Open Source Release**: Released open source version under MIT license

## Version Naming Convention

### Version Number Format
```
Major.Minor.Patch
Example: 1.2.3
```

### Version Number Description
- **Major**: Incompatible API changes or major architectural changes
- **Minor**: Backward-compatible functional additions
- **Patch**: Backward-compatible bug fixes and minor improvements

### Version Types
- **Alpha**: Internal testing version, incomplete features
- **Beta**: Public testing version, features basically complete
- **RC (Release Candidate)**: Release candidate version, ready for official release
- **Stable**: Stable official version

## Contributing Guidelines

### How to Contribute
1. Fork the project to your GitHub account
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Commit Message Format
```
type(scope): brief description

Detailed description (optional)

Related Issue: #123
```

#### Commit Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation update
- `style`: Code formatting adjustment
- `refactor`: Code refactoring
- `test`: Test-related
- `chore`: Build process or auxiliary tool changes

### Issue Reporting
Please use GitHub Issues to report problems and include:
- Device model and Android version
- Detailed problem description
- Reproduction steps
- Expected vs actual behavior
- Relevant log information (if available)

## Acknowledgments

### Contributors
Thanks to all developers who have contributed to the Info OSD project:
- **Liao Ahui (廖阿輝)** - Project founder and main developer

### Special Thanks
- Android development community for technical support
- Open source community for valuable suggestions and feedback
- All testing users for patient testing and issue reporting

### Open Source Technologies Used
- **Android SDK** - Android development toolkit provided by Google
- **Gradle** - Project build tool
- **JUnit** - Unit testing framework

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact Information

### Project Maintainer
**Liao Ahui (廖阿輝)**
- Website: [https://ahui3c.com](https://ahui3c.com)
- Email: [chehui@gmail.com](mailto:chehui@gmail.com)
- Social: Search "3C 達人廖阿輝" on Facebook and YouTube

### Project Links
- **GitHub**: [Project Repository Link]
- **Issue Tracking**: [GitHub Issues]
- **Discussion**: [GitHub Discussions]

---

**Thank you for your attention and support of the Info OSD project!** 🚀

