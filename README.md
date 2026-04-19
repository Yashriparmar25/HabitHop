# Habit Hop

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Language-Java-007396?logo=openjdk&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue)
![Target SDK](https://img.shields.io/badge/Target%20SDK-35-blue)
![Status](https://img.shields.io/badge/Status-Active%20Development-ff9800)
Habit Hop is a local-first Android habit-building app designed to make daily consistency feel approachable and rewarding.
It combines habit tracking, journaling, mood check-ins, sleep logs, reminders, and progress insights in one cohesive experience.

## Core Features

- Local authentication flow (signup/login with persisted session)
- Profile setup and edit with avatar picker (gallery + built-in avatars)
- Habit creation by category and frequency (daily/weekly)
- Daily progress and streak tracking on dashboard
- Journal entries with mood tagging and same-day history
- Dedicated mood tracker with score, notes, and saved history
- Sleep tracker with bedtime/wake time, quality, duration, and score
- Reminders page for pending habits and quick completion actions
- Reward experience when all daily habits are completed
- First-run onboarding spotlight tour

## Tech Stack

- Android Views + XML layouts
- Java
- Gradle (Kotlin DSL)
- SQLite (`SQLiteOpenHelper`)
- SharedPreferences
- RecyclerView + Material Components

## Architecture Snapshot

- Data layer: `DatabaseHelper` manages users, habits, logs, and journal tables in SQLite
- State/session: SharedPreferences (`HabitKit`) for login session, profile/avatar state, onboarding flag, mood/sleep history
- Presentation: Activity-based UI with dedicated adapters for habits, reminders, journal, mood, and sleep history

## Project Configuration

- Application ID: `com.HabitTracker`
- Compile SDK: `35`
- Target SDK: `35`
- Min SDK: `24`
- Java version: `11`
- Android Gradle Plugin: `8.7.3`
- Kotlin plugin: `2.0.0` (Gradle/plugin configuration present)

## Repository Structure

```text
HabitHop/
|- app/
|  |- src/main/java/com/HabitTracker/   # Activities, adapters, DB helper, receiver
|  |- src/main/res/                     # Layouts, drawables, themes, values
|  |- src/main/AndroidManifest.xml
|  `- build.gradle.kts
|- gradle/libs.versions.toml
|- build.gradle.kts
|- settings.gradle.kts
`- gradlew / gradlew.bat
```

## Permissions

Declared in `app/src/main/AndroidManifest.xml`:

- `READ_MEDIA_IMAGES` (Android 13+ image picker/avatar support)
- `READ_EXTERNAL_STORAGE` (legacy image access for SDK <= 32)
- `POST_NOTIFICATIONS`
- `SCHEDULE_EXACT_ALARM`
- `RECEIVE_BOOT_COMPLETED`
- `CAMERA` (optional, not required on all devices)

## Getting Started

### Prerequisites

- Android Studio (latest stable recommended)
- Android SDK 35
- JDK 11

### Run in Android Studio

1. Clone this repository.
2. Open the project in Android Studio.
3. Wait for Gradle sync to finish.
4. Select an emulator or physical device.
5. Run the `app` module.

### Run via CLI

Windows:

```powershell
.\gradlew.bat assembleDebug
```

macOS/Linux:

```bash
./gradlew assembleDebug
```

Output APK:

```text
app/build/outputs/apk/debug/
```

## Testing

Unit tests:

```powershell
.\gradlew.bat test
```

Instrumented tests (device/emulator required):

```powershell
.\gradlew.bat connectedAndroidTest
```

## Data Storage

- SQLite database: `HabitTracker.db`
  - Tables: `users`, `habits`, `habit_logs`, `journal`
- SharedPreferences file: `HabitKit`

## Future Updates

- Cloud sync and account-backed authentication
- Better recurring/scheduled notification flow
- Habit analytics dashboards
- Export/share progress summaries
