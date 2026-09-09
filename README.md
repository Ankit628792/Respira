# Respira - Breath & Lung Wellness Companion 🫁✨

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.x-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Design-Material%203-purple.svg)](https://m3.material.io/)
[![Room](https://img.shields.io/badge/Storage-Room%20DB-orange.svg)](https://developer.android.com/training/data-storage/room)

**Respira** is a modern, privacy-first Android application designed to promote respiratory wellness, diaphragmatic control, and mindful relaxation through science-backed breathing techniques and interactive lung capacity diagnostics.

GitHub Repository: [https://github.com/Ankit628792/Respira](https://github.com/Ankit628792/Respira)

---

## 🌟 Key Features

* **🫁 Interactive Lung Capacity Test**: Perform guided breath-hold diagnostics to estimate your vital lung volume ($L$), FEV1 (Forced Expiratory Volume in 1 second), Peak Expiratory Flow (PEF), and estimated lung age.
* **🧘 Guided Breathing Techniques**: Practice evidence-based exercises including:
  * **Box Breathing (4-4-4-4)**: Instant stress reduction & focus enhancement.
  * **4-7-8 Relaxing Breath**: Natural sleep aid & nervous system calming.
  * **Diaphragmatic Deep Breathing**: Thoracic expansion & oxygenation.
  * **Awakening Energizing Breath**: Quick vitality & mental alertness.
* **🎵 Real-Time Audio Synthesizer**: Native PCM sine-wave audio feedback synchronized with inhale, hold, and exhale phases.
* **📊 Personal Statistics & History**: Local tracking of session streaks, total mindful minutes, and lung capacity trend curves.
* **🔒 100% Offline & Private**: All data is stored locally on device using Room SQLite with zero tracking or mandatory cloud accounts.
* **🎨 Modern Material Design 3**: Dark and light dynamic themes, smooth canvas animations, glowing lung graphics, and responsive layouts.

---

## 🛠 Tech Stack

* **Language**: [Kotlin](https://kotlinlang.org/)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
* **Architecture**: MVVM (Model-View-ViewModel) + Clean Data Architecture
* **Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) (SQLite) with KSP
* **Asynchronous Flow**: Kotlin Coroutines & `StateFlow`
* **Audio Engine**: Android Native `AudioTrack` PCM Audio Synthesizer
* **Build System**: Gradle (Kotlin DSL `.gradle.kts`)

---

## 📁 Project Structure

```
app/src/main/java/com/respira/
├── audio/            # Native AudioTrack ambient sound synthesizer engine
├── data/
│   ├── db/           # Room Database, DAOs, Converters, & Entities
│   ├── model/        # Data models (Exercise, Sessions, Test Records)
│   └── repository/   # WellnessRepository (Data layer abstraction)
├── ui/
│   ├── components/   # Reusable Compose graphics, charts, and custom visualizers
│   ├── navigation/   # Navigation bar & screen routing
│   ├── screens/      # Feature screens (Home, Guided Session, Test, History, Profile, Settings)
│   └── theme/        # Material 3 Color palette, Typography, & Shapes
└── viewmodel/        # WellnessViewModel & State management
```

---

## 🚀 Getting Started

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Ankit628792/Respira.git
   cd Respira
   ```

2. **Open in Android Studio**:
   Open Android Studio (Ladybug or newer recommended) and select **Open** -> Navigate to the cloned folder.

3. **Build & Run**:
   Connect an Android device or start an emulator running Android 7.0 (API 24) or higher, then click **Run 'app'**.

---

## 📄 Documentation

* [SETUP.md](./SETUP.md): Detailed local setup, build instructions, production key signing, and deployment steps.
* [INFO.md](./INFO.md): In-depth architectural details, database schemas, and audio engine breakdown.
* [USAGE.md](./USAGE.md): Complete user guide and feature walkthrough.

---

## 📜 License

Distributed under the Apache 2.0 License. See `LICENSE` for details.
