# 🛠️ Tuneify Setup & Build Instructions

This document provides developer instructions for setting up, building, and contributing to **Tuneify**.

---

## Prerequisites

- **Android Studio**: Ladybug / Meerkat or newer
- **Android SDK**: API 35 (Build-Tools 35.0.0)
- **JDK**: OpenJDK 17 or 21
- **Git**

---

## Setup Steps

### 1. Clone the Repository
```bash
git clone https://github.com/Tuneify/Tuneify.git
cd Tuneify
```

### 2. Configure `local.properties`
Create a `local.properties` file in the root directory:
```properties
sdk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\sdk
```

### 3. Build Commands

#### Debug Builds (Testing & Development):
```bash
# Optimized for 64-bit ARM devices (Fastest):
.\gradlew assembleArm64GmsDebug

# Universal variant:
.\gradlew assembleUniversalGmsDebug
```

#### Release Builds (Production / GitHub Releases):
```bash
# Optimized ARM64 Release APK:
.\gradlew assembleArm64GmsRelease

# Universal Release APK:
.\gradlew assembleUniversalGmsRelease
```

---

## Release Signing Configuration

For official release builds, configure signing in `gradle.properties` or through environment variables:

```properties
KEYSTORE_PATH=path/to/release.keystore
STORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

---

**Lead Developer**: Vivek ([@Vivek](https://github.com/Vivek))