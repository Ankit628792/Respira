# Respira - Technical Specification & Architecture Document 📐

## 1. Executive Summary

**Respira** is an offline-first, native Android wellness application designed for diaphragmatic breathing training, pulmonary health diagnostics, and respiratory mindfulness. It leverages modern Jetpack Compose for declarative UI rendering, native PCM audio synthesis for real-time haptics/audio breath cues, and Room SQLite for local data persistence.

* **Package Namespace**: `com.respira`
* **Application ID**: `com.delanki.respira`
* **GitHub Repository**: [https://github.com/Ankit628792/Respira](https://github.com/Ankit628792/Respira)

---

## 2. Technical Stack & Dependencies

| Layer | Technology / Library | Version / Details |
| :--- | :--- | :--- |
| **Language** | Kotlin | 2.x |
| **UI Engine** | Jetpack Compose | Material 3 (M3) Design System |
| **State Engine** | Android ViewModel & `StateFlow` | `collectAsStateWithLifecycle` |
| **Local Database** | Room Database | SQLite via Kotlin Symbol Processing (KSP) |
| **Audio Engine** | Android `AudioTrack` | Real-time PCM Float Sine-Wave Synthesizer |
| **Testing** | Robolectric & Roborazzi | Local JVM unit tests & screenshot testing |

---

## 3. Application Architecture

Respira follows **MVVM (Model-View-ViewModel)** with single-source-of-truth repository pattern:

```
                  +-------------------------+
                  |  Jetpack Compose Views  |
                  |  (Screens & Graphics)   |
                  +------------+------------+
                               |
                   Observes    | Invokes
                  StateFlow    | Actions
                               v
                  +-------------------------+
                  |    WellnessViewModel    |
                  +------------+------------+
                               |
                               v
                  +-------------------------+
                  |   WellnessRepository    |
                  +----+---------------+----+
                       |               |
          Reads/Writes |               | Synthesizes
                       v               v
            +------------------+  +-------------------+
            |  Room AppDatabase|  | SoundSynthesizer  |
            |   (SQLite DAOs)  |  |  (AudioTrack PCM) |
            +------------------+  +-------------------+
```

---

## 4. Local Database Schema (Room SQLite)

### Table: `breathing_sessions`
Stores records of completed guided breathing routines.
* `id` (INTEGER, Primary Key, Auto-Increment)
* `exerciseId` (TEXT, Not Null) - e.g. `"box_breathing"`, `"4_7_8_relax"`
* `durationSeconds` (INTEGER, Not Null)
* `completedCycles` (INTEGER, Not Null)
* `completedTimestamp` (INTEGER, Not Null)
* `notes` (TEXT, Nullable)

### Table: `lung_tests`
Stores records of breath-hold lung capacity diagnostics.
* `id` (INTEGER, Primary Key, Auto-Increment)
* `timestamp` (INTEGER, Not Null)
* `holdTimeSeconds` (INTEGER, Not Null)
* `capacityLiters` (REAL, Not Null)
* `improvementPercent` (REAL, Not Null)
* `note` (TEXT, Not Null)

### Table: `exercise_definitions`
Pre-seeded catalog of available breathing techniques.
* `id` (TEXT, Primary Key)
* `title` (TEXT, Not Null)
* `category` (TEXT, Not Null)
* `durationFormatted` (TEXT, Not Null)
* `description` (TEXT, Not Null)
* `benefits` (TEXT, Not Null)
* `inhaleSec` (INTEGER, Not Null)
* `holdSec` (INTEGER, Not Null)
* `exhaleSec` (INTEGER, Not Null)
* `holdOutSec` (INTEGER, Not Null)

---

## 5. Medical Diagnostics & Calculation Formulas

### Estimated Vital Capacity ($VC$)
Determined from maximum breath-hold duration ($T_{hold}$ in seconds):
$$VC (L) = \min\left(2.5 + \left(T_{hold} \times 0.04\right), 6.5\right)$$

### Estimated FEV1 (Forced Expiratory Volume in 1 second)
$$FEV1 (L) = \text{Round}(VC \times 0.85, 1)$$

### Estimated Lung Age
$$\text{Lung Age} = \text{Clamp}\left(35 - \left(T_{hold} \times 0.25\right), 18, 55\right)$$

---

## 6. Sound Synthesizer Engine

The `SoundSynthesizer` class (`com.respira.audio.SoundSynthesizer`) generates real-time audio cues without external audio files.

* **Sample Rate**: $44,100\text{ Hz}$
* **Buffer Allocation**: Continuous PCM $16\text{-bit}$ audio stream
* **Frequency Modulation**:
  * **Inhale**: Ascending pitch bend ($220\text{ Hz} \to 440\text{ Hz}$)
  * **Hold**: Steady harmonic drone ($440\text{ Hz}$)
  * **Exhale**: Descending pitch bend ($440\text{ Hz} \to 180\text{ Hz}$)
