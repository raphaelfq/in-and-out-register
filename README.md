# In & Out Register - OCR Time Extraction

An Android app that uses OCR (Optical Character Recognition) to extract time information from photos and schedule vibration alarms. Built to help coworkers manage their In & Out point register by automating time tracking from photographed schedules.

## Screenshots

_Screenshots coming soon._

## Features

- Pick an image from the device gallery
- Take a photo directly using the camera
- Extract time values (HH:mm format) from photos using on-device OCR
- Detect and display multiple times found in a single image
- Select which detected times to set alarms for
- Schedule vibration alarms based on extracted times using WorkManager
- View history of past scans with extracted and scheduled times
- Delete individual history entries

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| OCR | Google ML Kit Text Recognition |
| Image Loading | Coil |
| Camera | CameraX |
| Scheduling | WorkManager |
| Persistence | Room Database |
| Navigation | Jetpack Navigation Compose |
| Architecture | MVVM (ViewModel + StateFlow) |
| Build | Gradle with Kotlin DSL, Version Catalog |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 8+
- An Android device or emulator running API 24+

## Build and Run

1. Clone the repository:
   ```bash
   git clone https://github.com/raphaelfq/in-and-out-register.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and let dependencies download.
4. Run the app on a connected device or emulator.

Alternatively, build from the command line:
```bash
./gradlew assembleDebug
```

## How It Works

1. Tap **Pick from Gallery** to select an image or **Take Photo** to capture one with the camera.
2. The selected image is displayed on screen for review.
3. Tap **Analyze and Set Alarm** to run OCR on the image.
4. The app extracts all valid time values matching the `HH:mm` pattern.
5. If multiple times are found, select which ones to set alarms for.
6. Vibration alarms are scheduled via WorkManager based on the selected times.
7. View past scans and their scheduled alarms from the **View History** screen.

## Project Status

This project is a functional MVP with camera capture, OCR extraction, alarm scheduling, and scan history. It works end-to-end for the core flow and includes persistence and multi-time support.

### Planned Improvements

- Configurable alarm offset (currently hardcoded)
- Notification-based alarms in addition to vibration
- Improved OCR accuracy
- Unit and integration tests

## Author

**Raphael Quintanilha** -- [github.com/raphaelfq](https://github.com/raphaelfq)

## License

This project does not currently specify a license.
