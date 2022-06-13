# pancast-android
Sample android application that can currently receive and transmit Bluetooth advertisements

Adapted from https://www.youtube.com/watch?v=wLRQ9EClYuA

## Prerequisites
An Android device with Bluetooth capabilities (according to the documentation for the Android emulator, it does not support Bluetooth emulation, hence why you'll need an Android phone).

## Build from source
1) Install Android Studio. 
2) Clone this repo.
3) Open it in Android Studio, and wait for the project to index and sync.
4) If nothing went wrong, the toolbar at the top of your screen should have an android face with "app" next to it.
5) To its right is the target, where the app will actually run. Connect your Android device to your PC by USB (or setup your ADB over Wi-Fi if you'd like. YMMV).
6) Verify that you can select your device as the target for installation.
7) Run app, and hope that nothing goes wrong.

## Upload binary
Copy pancast-android/app/build/outputs/apk/debug/app-debug.apk to a public HTTP server.

## Install APK

### From Chrome browser on phone:
Allow Chrome to install unknown apps using the following steps. (Adapted from https://www.lifewire.com/install-apk-on-android-4177185)
1) Go to `Settings` > `Apps & Notifications` > `Menu` (three dots) > `Special access` > `Install unknown apps` > `Chrome` (or whichever browser you use) > Set `Allow from this source` to `On`.
2) Download the APK using your browser from `http://pancast.cs.ubc.ca/apk/`.
3) Go to the `Downloads` folder and tap the APK file.
4) Allow the app any permissions it asks for and tap `Install` at the bottom of the installer window.

## Developer notes

Make sure location permissions is enabled because the app needs it. (The reason
for this is elaborated in the `Ming's findings` document).

Opening the Logcat tab at the bottom of your screen will show you a whole lot of
debug messages. Advertisements are logged here.

This application will error out on phones running an older version of android.
This is likely due to to the bluetooth API requiring the ACCESS_FINE_LOCATION
permission in recent android versions, which was not supported earlier. (I
believe earlier versions require the ACCESS_COURSE_LOCATION permission, and
even older versions don't need location permission at all. Will have to
programatically detect the android version to avoid this issue).
