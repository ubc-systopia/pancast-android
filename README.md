# pancast-android
Sample android application that can currently receive and transmit Bluetooth advertisements

Adapted from https://www.youtube.com/watch?v=wLRQ9EClYuA

Prerequisites:
1) An Android device with Bluetooth capabilities (according to the documentation for the Android emulator, it does not support Bluetooth emulation, hence why you'll need an Android phone).

Instructions:
1) Install Android Studio. 
2) Clone this repo.
3) Open it in Android Studio, and wait for the project to index and sync.
4) If nothing went wrong, the toolbar at the top of your screen should have an android face with "app" next to it.
5) To its right is the target, where the app will actually run. Connect your Android device to your PC by USB (or setup your ADB over Wi-Fi if you'd like. YMMV).
6) Verify that you can select your device as the target for installation.
7) Run app, and hope that nothing goes wrong.

The primary function of this application at the moment is that it can receive advertisements (the `Get Advertisements` button. Make sure location permissions is enabled because the app needs it. The reason for this is elaborated in the `Ming's findings` document). Opening the Logcat tab at the bottom of your screen will show you a whole lot of debug messages. Advertisements are logged here.
