# Quiz of Kings

Android quiz app for playing multiple-choice trivia games with a local account.

## What It Does

- Registers and logs in local users.
- Saves user settings in Room.
- Fetches questions from Open Trivia DB.
- Falls back to a cached game that matches the current quiz settings.
- Stores scores and shows the top five users by best score.

## Setup

Install:

- Android Studio
- JDK 11
- Android SDK Platform 32

Build from PowerShell:

```powershell
cd "C:\Users\imanm\Downloads\GitHub\Mobile-Programming-HWs\HW2"
.\gradlew.bat :app:assembleDebug
```

The debug APK is created at:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## Run

Use Android Studio, or install on a connected device:

```powershell
adb devices
.\gradlew.bat :app:installDebug
```

## Implementation Notes

- Login and registration: `MainActivity`
- Game start and API request: `GameActivity`
- Question flow and scoring: `QuestionsActivity`
- Profile fields: `ProfileActivity`
- User settings: `SettingsActivity`
- Score table: `ScoreboardActivity`
- Local database: Room entities and DAOs under `com.sharif.quizofkings`

The app uses `https://opentdb.com/api.php` for live trivia questions. Cached games are stored locally per user email.
