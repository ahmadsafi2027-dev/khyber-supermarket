# خوراکه فروشی سوپر مارکیت خیبر — Android project

This is an Android Studio project source package, **not a compiled APK**.

## Build an APK
1. Install Android Studio on a computer.
2. Extract this ZIP.
3. Open the extracted `KhyberSupermarketAndroid` folder in Android Studio.
4. Let Gradle sync finish. Use JDK 17.
5. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. The debug APK is normally created at `app/build/outputs/apk/debug/app-debug.apk`.

## App details
- Package: `com.khyber.supermarket`
- Offline WebView app; no Internet permission is requested.
- Customer/debt/payment data is saved locally and mirrored to a private app backup file after each change.
- Login: `admin` / `Mohammad@1234` (change on first login).
- Currency: Afghan afghani.
- Backup export uses Android's share chooser; choose a file-saving app/location.

## Important limitations of this prototype
- The interface is primarily Dari. Pashto translation is not complete yet.
- This project has not been compiled or tested on a physical Android phone in this environment.
- Android's private automatic backup is on the same phone. Export a backup file regularly to another safe place to protect against loss or damage of the phone.
- The default login is a basic local app gate, not enterprise-grade device security.

## Build from Android phone/tablet using GitHub Actions
A workflow is included at `.github/workflows/build-apk.yml`.
1. Create a GitHub account and a new repository.
2. Upload the extracted project files and folders to the repository (including the hidden `.github` folder).
3. Open the repository's Actions tab and run "Build Khyber Supermarket APK" (or wait for the push build).
4. When the workflow succeeds, open the run and download the `Khyber-Supermarket-APK` artifact.
5. Extract the downloaded artifact ZIP; it contains `app-debug.apk`.
