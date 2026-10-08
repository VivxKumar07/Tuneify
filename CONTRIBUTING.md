# 🤝 Contributing to Tuneify

Thank you for your interest in contributing to **Tuneify**! This document provides guidelines for contributing code, reporting bugs, and submitting improvements.

---

## Getting Started

### Prerequisites
- **Android Studio**: Ladybug / Meerkat or newer
- **JDK**: OpenJDK 17 or 21
- **Android SDK**: API 35

### Code Style & Architecture
- Code is written entirely in **Kotlin** and **Jetpack Compose**.
- Architecture follows unidirectional data flow (MVI / MVVM) with Kotlin Flows and Coroutines.
- Material 3 Expressive guidelines are followed for all components.

---

## Submitting Pull Requests

1. **Fork & Branch**: Create a feature branch with a descriptive name.
2. **Conventional Commits**: Format commit messages cleanly (`feat: ...`, `fix: ...`, `refactor: ...`).
3. **Verify Builds**: Ensure the project compiles cleanly via `./gradlew check`.
4. **Open PR**: Submit your PR with a clear summary and screenshots of UI changes.

---

**Lead Developer & Maintainer**: Vivek ([@Vivek](https://github.com/Vivek))
