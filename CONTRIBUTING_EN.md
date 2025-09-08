# Contributing Guide

Thank you for your interest in the Info OSD project! We welcome all forms of contributions, including but not limited to code, documentation, issue reports, and feature suggestions.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
- [Development Environment Setup](#development-environment-setup)
- [Submission Guidelines](#submission-guidelines)
- [Code Standards](#code-standards)
- [Testing Requirements](#testing-requirements)
- [Documentation Contributions](#documentation-contributions)
- [Issue Reporting](#issue-reporting)
- [Feature Requests](#feature-requests)

## 🤝 Code of Conduct

### Our Pledge

In the interest of fostering an open and welcoming environment, we as contributors and maintainers pledge to make participation in our project and our community a harassment-free experience for everyone, regardless of age, body size, disability, ethnicity, gender identity and expression, level of experience, nationality, personal appearance, race, religion, or sexual identity and orientation.

### Our Standards

Examples of behavior that contributes to creating a positive environment include:
- Using welcoming and inclusive language
- Being respectful of differing viewpoints and experiences
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards other community members

Examples of unacceptable behavior include:
- The use of sexualized language or imagery and unwelcome sexual attention or advances
- Trolling, insulting/derogatory comments, and personal or political attacks
- Public or private harassment
- Publishing others' private information without explicit permission
- Other conduct which could reasonably be considered inappropriate in a professional setting

### Enforcement

Project maintainers are responsible for clarifying the standards of acceptable behavior and are expected to take appropriate and fair corrective action in response to any instances of unacceptable behavior.

## 🚀 How to Contribute

### Types of Contributions

We welcome several types of contributions:

#### 🐛 Bug Reports
- Report bugs using GitHub Issues
- Include detailed reproduction steps
- Provide system information and logs

#### 💡 Feature Requests
- Suggest new features or improvements
- Explain the use case and expected behavior
- Discuss implementation approach

#### 🔧 Code Contributions
- Fix bugs or implement new features
- Follow coding standards and best practices
- Include tests for new functionality

#### 📚 Documentation
- Improve existing documentation
- Add examples and tutorials
- Translate documentation to other languages

#### 🎨 Design Contributions
- Improve UI/UX design
- Create icons and graphics
- Suggest design improvements

### Getting Started

1. **Fork the Repository**
   ```bash
   git clone https://github.com/yourusername/Info-OSD-Android.git
   cd Info-OSD-Android
   ```

2. **Set Up Development Environment**
   - Install Android Studio
   - Configure Android SDK
   - Set up emulator or test device

3. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/issue-description
   ```

4. **Make Changes**
   - Write code following our standards
   - Add tests for new functionality
   - Update documentation as needed

5. **Test Your Changes**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

6. **Submit Pull Request**
   - Push your branch to GitHub
   - Create a Pull Request
   - Fill out the PR template

## 🛠️ Development Environment Setup

### Prerequisites

- **Android Studio**: Latest stable version
- **Android SDK**: API 24+ (Android 7.0+)
- **JDK**: Java 8 or higher
- **Git**: For version control

### Setup Steps

1. **Install Android Studio**
   Download from [Android Developer website](https://developer.android.com/studio)

2. **Configure SDK**
   - Open SDK Manager in Android Studio
   - Install Android SDK Platform 34
   - Install Android SDK Build-Tools 34.0.0

3. **Clone and Setup Project**
   ```bash
   git clone https://github.com/ahui3c/Info-OSD-Android.git
   cd Info-OSD-Android
   ```

4. **Create local.properties**
   ```properties
   sdk.dir=/path/to/your/android-sdk
   ```

5. **Build Project**
   ```bash
   ./gradlew build
   ```

### IDE Configuration

#### Recommended Plugins
- Android ButterKnife Zelezny
- ADB Idea
- Android Drawable Importer

#### Code Style
Import Android code style settings:
1. Go to `File > Settings > Editor > Code Style`
2. Import the provided code style XML

## 📝 Submission Guidelines

### Pull Request Process

1. **Before Submitting**
   - Ensure your code follows our coding standards
   - Run all tests and ensure they pass
   - Update documentation if needed
   - Rebase your branch on the latest main branch

2. **PR Title Format**
   ```
   type(scope): description
   
   Examples:
   feat(overlay): add battery percentage display
   fix(screenshot): resolve permission handling issue
   docs(readme): update installation instructions
   ```

3. **PR Description Template**
   ```markdown
   ## Description
   Brief description of changes

   ## Type of Change
   - [ ] Bug fix
   - [ ] New feature
   - [ ] Documentation update
   - [ ] Performance improvement

   ## Testing
   - [ ] Unit tests pass
   - [ ] Integration tests pass
   - [ ] Manual testing completed

   ## Screenshots (if applicable)
   Add screenshots to help explain your changes

   ## Checklist
   - [ ] Code follows style guidelines
   - [ ] Self-review completed
   - [ ] Documentation updated
   - [ ] Tests added/updated
   ```

### Commit Message Guidelines

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
type(scope): description

[optional body]

[optional footer]
```

#### Types
- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, etc.)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks

#### Examples
```bash
feat(overlay): add customizable text color options
fix(screenshot): resolve MediaProjection permission issue
docs(contributing): add code review guidelines
test(service): add unit tests for OverlayService
```

## 📏 Code Standards

### Java Coding Standards

#### Naming Conventions
```java
// Classes: PascalCase
public class OverlayService extends Service { }

// Methods: camelCase
public void startScreenCapture() { }

// Variables: camelCase
private WindowManager windowManager;

// Constants: UPPER_SNAKE_CASE
private static final String TAG = "OverlayService";
public static final int DEFAULT_TEXT_SIZE = 16;
```

#### Code Formatting
- **Indentation**: 4 spaces (no tabs)
- **Line Length**: Maximum 120 characters
- **Braces**: Opening brace on same line
- **Imports**: Group and sort imports

#### Example Code Style
```java
public class ExampleClass {
    private static final String TAG = "ExampleClass";
    private static final int DEFAULT_VALUE = 10;
    
    private Context context;
    private SettingsManager settingsManager;
    
    public ExampleClass(Context context) {
        this.context = context;
        this.settingsManager = new SettingsManager(context);
    }
    
    public void performAction(String parameter) {
        if (parameter == null || parameter.isEmpty()) {
            Log.w(TAG, "Invalid parameter provided");
            return;
        }
        
        // Implementation logic here
        processParameter(parameter);
    }
    
    private void processParameter(String parameter) {
        // Private method implementation
    }
}
```

### XML Formatting

#### Layout Files
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">
    
    <TextView
        android:id="@+id/titleText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/app_name"
        android:textSize="18sp"
        android:textStyle="bold" />
        
</LinearLayout>
```

#### String Resources
```xml
<resources>
    <string name="app_name">Info OSD</string>
    <string name="start_service">Start Service</string>
    <string name="stop_service">Stop Service</string>
</resources>
```

### Documentation Standards

#### JavaDoc Comments
```java
/**
 * Creates and displays the OSD overlay view on the screen.
 * 
 * This method initializes the overlay window with the current settings
 * and displays it as a floating window over other applications.
 * 
 * @param context The application context used for window management
 * @param settings The display settings to apply to the overlay
 * @return true if the overlay was created successfully, false otherwise
 * @throws SecurityException if overlay permission is not granted
 * @throws IllegalStateException if the service is not properly initialized
 * 
 * @since 1.0
 * @see SettingsManager
 * @see PermissionManager#hasOverlayPermission()
 */
public boolean createOverlay(Context context, DisplaySettings settings) 
        throws SecurityException, IllegalStateException {
    // Method implementation
}
```

#### Inline Comments
```java
public void updateBatteryLevel() {
    // Register battery receiver to monitor battery changes
    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Intent batteryStatus = context.registerReceiver(null, filter);
    
    if (batteryStatus != null) {
        // Extract battery level from intent extras
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        
        // Calculate battery percentage
        float batteryPct = level * 100 / (float) scale;
        updateDisplayText(String.format("%.0f%%", batteryPct));
    }
}
```

## 🧪 Testing Requirements

### Test Categories

#### Unit Tests
- Test individual components in isolation
- Mock external dependencies
- Focus on business logic

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
    public void getTextSize_ReturnsCorrectValue() {
        // Given
        when(mockPreferences.getInt(SettingsManager.KEY_TEXT_SIZE, 
            SettingsManager.DEFAULT_TEXT_SIZE)).thenReturn(20);
        
        // When
        int textSize = settingsManager.getTextSize();
        
        // Then
        assertEquals(20, textSize);
    }
}
```

#### Integration Tests
- Test component interactions
- Use Android testing framework
- Test on real devices/emulators

```java
@RunWith(AndroidJUnit4.class)
public class OverlayServiceTest {
    @Rule
    public ServiceTestRule serviceRule = new ServiceTestRule();
    
    @Test
    public void testServiceStartsCorrectly() throws TimeoutException {
        Intent serviceIntent = new Intent(
            ApplicationProvider.getApplicationContext(),
            OverlayService.class
        );
        
        IBinder binder = serviceRule.bindService(serviceIntent);
        OverlayService service = ((OverlayService.LocalBinder) binder).getService();
        
        assertNotNull(service);
        assertTrue(service.isRunning());
    }
}
```

### Test Coverage Requirements

- **Minimum Coverage**: 70% for new code
- **Critical Components**: 90% coverage required
  - SettingsManager
  - PermissionManager
  - Core service logic

### Running Tests

```bash
# Unit tests
./gradlew test

# Integration tests
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

## 📚 Documentation Contributions

### Types of Documentation

#### Code Documentation
- JavaDoc for public APIs
- Inline comments for complex logic
- README updates for new features

#### User Documentation
- Installation guides
- Usage instructions
- Troubleshooting guides

#### Developer Documentation
- Architecture decisions
- API documentation
- Contributing guidelines

### Documentation Standards

#### Writing Style
- Use clear, concise language
- Include examples where helpful
- Keep content up-to-date
- Use proper grammar and spelling

#### Markdown Formatting
```markdown
# Main Heading

## Section Heading

### Subsection Heading

**Bold text** for emphasis
*Italic text* for subtle emphasis
`Code snippets` in backticks

```java
// Code blocks with language specification
public void example() {
    // Code here
}
```

- Bullet points for lists
- Numbered lists for procedures

[Links](https://example.com) to external resources
```

### Translation Contributions

We welcome translations of documentation:

1. **Create Language Directory**
   ```
   docs/
   ├── en/          # English (default)
   ├── zh-tw/       # Traditional Chinese
   ├── zh-cn/       # Simplified Chinese
   └── ja/          # Japanese
   ```

2. **Translation Guidelines**
   - Maintain original structure
   - Adapt examples for local context
   - Keep technical terms consistent
   - Update language selector in README

## 🐛 Issue Reporting

### Before Reporting

1. **Search Existing Issues**
   - Check if the issue already exists
   - Look for similar problems
   - Check closed issues for solutions

2. **Gather Information**
   - Device model and Android version
   - App version and build number
   - Steps to reproduce the issue
   - Expected vs actual behavior

### Issue Template

```markdown
## Bug Report

**Describe the Bug**
A clear description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected Behavior**
A clear description of what you expected to happen.

**Screenshots**
If applicable, add screenshots to help explain your problem.

**Device Information:**
- Device: [e.g. Samsung Galaxy S21]
- OS: [e.g. Android 12]
- App Version: [e.g. 1.0.0]

**Additional Context**
Add any other context about the problem here.

**Logs**
If applicable, include relevant log output:
```
Paste logs here
```
```

### Issue Labels

We use labels to categorize issues:

- **Type Labels**
  - `bug` - Something isn't working
  - `enhancement` - New feature or request
  - `documentation` - Documentation improvements
  - `question` - Further information is requested

- **Priority Labels**
  - `critical` - Blocks core functionality
  - `high` - Important but not blocking
  - `medium` - Normal priority
  - `low` - Nice to have

- **Status Labels**
  - `needs-triage` - Needs initial review
  - `in-progress` - Currently being worked on
  - `needs-testing` - Ready for testing
  - `blocked` - Cannot proceed

## 💡 Feature Requests

### Before Requesting

1. **Check Existing Requests**
   - Search open issues for similar requests
   - Check project roadmap
   - Review closed feature requests

2. **Consider the Scope**
   - Does it fit the project goals?
   - Is it technically feasible?
   - Would it benefit most users?

### Feature Request Template

```markdown
## Feature Request

**Is your feature request related to a problem?**
A clear description of what the problem is. Ex. I'm always frustrated when [...]

**Describe the solution you'd like**
A clear description of what you want to happen.

**Describe alternatives you've considered**
A clear description of any alternative solutions or features you've considered.

**Use Cases**
Describe specific scenarios where this feature would be useful:
1. Use case 1
2. Use case 2

**Implementation Ideas**
If you have ideas about how this could be implemented, please share them.

**Additional Context**
Add any other context or screenshots about the feature request here.
```

### Feature Discussion Process

1. **Initial Review** - Maintainers review feasibility
2. **Community Discussion** - Gather feedback from users
3. **Design Phase** - Create technical design if approved
4. **Implementation** - Assign to developer or accept contributions
5. **Testing** - Thorough testing before release

## 🔄 Review Process

### Code Review Guidelines

#### For Contributors
- **Self-Review First**
  - Check your own code before submitting
  - Ensure tests pass
  - Verify documentation is updated

- **Respond to Feedback**
  - Address reviewer comments promptly
  - Ask questions if feedback is unclear
  - Make requested changes or explain why not

#### For Reviewers
- **Be Constructive**
  - Provide specific, actionable feedback
  - Explain the reasoning behind suggestions
  - Acknowledge good practices

- **Focus Areas**
  - Code correctness and logic
  - Performance implications
  - Security considerations
  - Code style and consistency
  - Test coverage

### Review Checklist

- [ ] Code follows project standards
- [ ] Tests are included and pass
- [ ] Documentation is updated
- [ ] No breaking changes (or properly documented)
- [ ] Performance impact considered
- [ ] Security implications reviewed
- [ ] Backward compatibility maintained

## 🎉 Recognition

### Contributors

We recognize contributors in several ways:

- **Contributors File** - Listed in CONTRIBUTORS.md
- **Release Notes** - Mentioned in changelog
- **GitHub** - Automatic contributor recognition
- **Social Media** - Highlighted on project social accounts

### Types of Contributions Recognized

- Code contributions
- Documentation improvements
- Bug reports and testing
- Feature suggestions
- Community support
- Translation work
- Design contributions

## 📞 Getting Help

### Communication Channels

- **GitHub Issues** - For bugs and feature requests
- **GitHub Discussions** - For general questions and ideas
- **Email** - chehui@gmail.com for private matters

### Response Times

- **Issues** - We aim to respond within 48 hours
- **Pull Requests** - Initial review within 1 week
- **Questions** - Response within 24-48 hours

### Community Guidelines

- Be respectful and professional
- Help others when you can
- Share knowledge and experiences
- Follow the code of conduct

---

Thank you for contributing to Info OSD! Your contributions help make this project better for everyone. 🚀

