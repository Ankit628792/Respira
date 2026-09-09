# Respira - Environment & Deployment Setup Guide ⚙️

This guide provides instructions for setting up **Respira** locally, configuring production build signing, and deploying the application.

GitHub Repository: [https://github.com/Ankit628792/Respira](https://github.com/Ankit628792/Respira)

---

## 💻 1. Local Development Setup

### Prerequisites

* **Android Studio**: Android Studio Koala / Ladybug or later (2024.1.1+).
* **JDK**: JDK 17 (recommended) or JDK 11.
* **Android SDK**:
  * Minimum SDK: API level 24 (Android 7.0 Nougat)
  * Target SDK: API level 36
  * Compile SDK: API level 36

### Step-by-Step Local Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Ankit628792/Respira.git
   cd Respira
   ```

2. **Configure Environment Variables (Optional)**:
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```

3. **Sync Gradle**:
   * Open Android Studio.
   * Select **File > Open** and select the root `Respira` project directory.
   * Allow Android Studio to sync Gradle dependencies automatically.

4. **Build and Run Debug APK**:
   * Select the `app` run configuration.
   * Click **Run (Shift + F10)** on a connected physical Android device or emulator.
   * Alternatively, assemble the debug APK via command line:
     ```bash
     ./gradlew assembleDebug
     ```
     The output APK will be located at:
     `app/build/outputs/apk/debug/app-debug.apk`

---

## 🧪 2. Running Unit & Screenshot Tests

Respira uses **Robolectric** for local JVM testing and **Roborazzi** for UI screenshot verification without requiring an active emulator.

* **Run Unit Tests**:
  ```bash
  ./gradlew testDebugUnitTest
  ```
* **Verify Screenshot Tests**:
  ```bash
  ./gradlew verifyRoborazziDebug
  ```
* **Record New Reference Screenshots**:
  ```bash
  ./gradlew recordRoborazziDebug
  ```

---

## 📦 3. Production Release Setup & Signing

### Generating a Production Keystore

To build a signed release APK or Android App Bundle (AAB) for Google Play distribution:

1. Generate a upload key and keystore using `keytool`:
   ```bash
   keytool -genkey -v -keystore release_key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Place `release_key.jks` in the root project directory (or set `KEYSTORE_PATH`).

3. Set the keystore credentials as environment variables or in your local `.env`:
   ```bash
   export KEYSTORE_PATH="release_key.jks"
   export STORE_PASSWORD="your_keystore_password"
   export KEY_PASSWORD="your_key_password"
   ```

### Building Signed Release Artifacts

* **Build Signed Release APK**:
  ```bash
  ./gradlew assembleRelease
  ```
  Output APK location:
  `app/build/outputs/apk/release/app-release.apk`

* **Build Signed Android App Bundle (AAB for Google Play)**:
  ```bash
  ./gradlew bundleRelease
  ```
  Output AAB location:
  `app/build/outputs/bundle/release/app-release.aab`

---

## 🚀 4. GitHub & Deployment Setup

### Push to GitHub Repository

If initializing or syncing with your GitHub repository:

```bash
git remote add origin https://github.com/Ankit628792/Respira.git
git branch -M main
git push -u origin main
```

### GitHub Actions CI Workflow (Optional `.github/workflows/android.yml`)

You can automate test verification and build generation by adding a GitHub Actions workflow:

```yaml
name: Android CI Build

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Run Unit & Robolectric Tests
      run: ./gradlew testDebugUnitTest

    - name: Build Debug APK
      run: ./gradlew assembleDebug
```

---

## 🛡 5. Troubleshooting & Common Fixes

* **KSP or Room Compiler Issues**: Ensure your JDK version is set to 17 in Android Studio under **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK**.
* **Clean Build Cache**:
  ```bash
  ./gradlew clean
  ```
