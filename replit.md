# MPL_SPL Android Application

## Project Overview
This is a native Android mobile application built with Kotlin and Gradle. The app appears to be a smart parcel locker management system (MPL/SPL - likely "My Parcel Locker" / "Smart Parcel Locker").

## Project Type
**Native Android Application** - This project cannot run in the Replit environment.

## Why This Project Cannot Run on Replit
1. **Native Android App**: This is not a web application. It's a mobile app that requires compilation to an APK/AAB and installation on an Android device or emulator.

2. **Android SDK Required**: Building Android apps requires the Android SDK, which is not available in the Replit environment.

3. **Proprietary Dependencies**: The project relies on private libraries hosted on a Nexus repository:
   - `hr.sil.android.datacache:android_datacache`
   - `hr.sil.android:view_util`
   - `hr.sil.android.ble.scanner:scan_multi`
   - `hr.sil.android.blecommunicator:android_blecommunicator`
   - `hr.sil.android.rest:core`

4. **External Keystore**: The signing configuration references an external keystore file that is not in the repository.

5. **Firebase Configuration**: Requires `google-services.json` for Firebase/Google services integration.

## Technical Stack
- **Language**: Kotlin
- **Build System**: Gradle 8.9.3
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 35 (Android 15)
- **JVM Target**: Java 17
- **UI Framework**: Mix of Android Views and Jetpack Compose

## How to Build This Project
To build this project, you need:
1. Android Studio (latest stable version)
2. Android SDK with API level 35
3. JDK 17
4. Access to the Nexus repository credentials (already in gradle.properties)
5. The keystore file at `../../../keystores/flexiUserApp.jks`
6. `google-services.json` in the `app/` directory

## Project Structure
```
app/           - Main application module
  src/main/    - Source code and resources
  src/zwick/   - Zwick-specific branding variant
core/          - Core library module with shared functionality
gradle/        - Gradle wrapper files
```

## Recommended Action
To work with this Android project, please use:
- **Android Studio** on your local machine
- **GitHub Actions** for CI/CD builds
- **Firebase App Distribution** or **Google Play Console** for distribution
