# 💸 Budget Tracker App (FinTrack) - Kotlin + RoomDB

---

## 📱 Overview

**FinTrack** is an offline-first personal finance Android application built using **Kotlin** and **RoomDB (SQLite)**. The app is designed for users to manage daily expenses, set spending goals, track category-wise costs, and visualize budget performance in real time. This application is tailored for South African users and uses the Rand (**R**) as the default currency.


## GitHub Repository:
https://github.com/glodymavula/Fintrack-BudgetApp.git

---

## 🎯 Purpose of the App

The main goal of FinTrack is to:

- Help users develop responsible spending habits.
- Provide offline access to financial tracking without the need for cloud integration.
- Visually present budget performance and statistics in real time.
- Introduce gamification and smart insights to motivate consistent usage.

---

## 🎨 Design Considerations

### 1. **User-Centered UI**
- Designed with simplicity and clarity using **Jetpack Compose** and **XML layouts**.
- UX focused on minimal clicks and intuitive navigation based on Figma prototypes.

### 2. **Offline-First Architecture**
- All data persists locally using RoomDB — suitable for areas with limited connectivity.

### 3. **Secure Storage**
- Expense data, photos, and user credentials are stored internally to avoid data leaks.

### 4. **Gamified Experience**
- XP, badges, and levels add a fun, game-like interaction to an otherwise functional app.

### 5. **Visual Analytics**
- Line charts, pie charts, and budget bars enhance user understanding of financial behavior.

---

## 🛠 GitHub & GitHub Actions


### ⚙️ GitHub Actions
- **Automated Build Testing**: GitHub Actions runs Gradle build on each push.
- **Static Analysis (Lint)**: Ensures code quality and style checks pass.
- **APK Artifact Storage**: Auto-upload generated `.apk` on successful build.

✅ Features
. *User Authentication*
- Secure username/password login stored locally.

. *Expense Tracking*
-Add expense with photo, date, category, and time.
-Custom Categories
-Create, edit, and remove your own categories.

. *Budget Goals*
-Define min/max monthly budgets and track goal status.

. *Bank Card Management (Custom Feature)*
Add and manage personal bank cards securely.

. *Statistics View*
-Interactive pie charts for expense categories.
-Line/bar graphs showing monthly trends.
-Budget performance progress bar (e.g., "Over Budget: 91%").

. *Gamification*
-Earn XP, unlock badges, level up for achieving goals.
-Smart Budgeting (AI-Driven)
-Personalized suggestions based on spending behavior.

##📷 Screenshots


screenshots/pie_chart.png – Category Breakdown Chart

screenshots/monthly_trend.png – Monthly Spending Graph

screenshots/budget_progress.png – Budget Progress Indicator

screenshots/bank_card_feature.png – Bank Card UI


##full feature walkthrough.

--Real use-case demonstration.
--Voice-over explanation by the group.

▶️ How to Run This App Using Android Studio
To run FinTrack locally using Android Studio, follow these steps:

📥 Clone the Repository

git clone https://github.com/glodymavula/Fintrack-BudgetApp.git
Or download the ZIP from GitHub and extract it.

##📂 Open the Project

Launch Android Studio.

Click “Open”, then select the FinTrackApp folder and confirm.

⚙️ Sync Gradle

Wait for Android Studio to sync Gradle files.

If prompted, click “Sync Now”.

Resolve any missing dependencies or SDKs.

##📱 Run on Emulator or Physical Device

Use AVD Manager to launch an emulator, or

Connect your Android phone with USB Debugging enabled.

##▶️ Run the App

Press the green Run button or use Shift + F10.

Choose the connected device or emulator.

##✅ Done!

The app will build and launch on your device.


📦 How to Build and Download the APK in Android Studio
If you'd like to generate the .apk file yourself using Android Studio, follow these steps:

📂 Open the Project in Android Studio

Launch Android Studio.

Click “Open” and select the FinTrackApp folder.

⚙️ Let Gradle Sync

Wait for Android Studio to sync all files and dependencies.

##🧱 Build the APK

In the top menu, go to:
Build > Build Bundle(s) / APK(s) > Build APK(s)

Android Studio will start building the .apk file.

##📁 Locate the APK File

Once the build is complete, a message will appear at the bottom right:

"APK(s) generated successfully."

Click “locate” or manually find it in:

FinTrackApp/app/build/outputs/apk/debug/app-debug.apk
📲 Install the APK on Your Android Device

Transfer the APK to your device via USB, email, or cloud storage.

On your phone:

Enable “Install unknown apps” in Settings.

Open the APK file and tap Install.

                                                                                                                                                          📂 Folder Structure
# FinTrack App Folder Structure
FinTrackApp/
│
├── app/
│   ├── src/main/
│   │   ├── java/...               # Kotlin source files
│   │   └── res/
│   │       ├── layout/            # UI XML layouts
│   │       ├── drawable/          # Icons & Images
│   │       └── values/            # Strings, colors, styles
│
├── screenshots/                   # UI screenshots
├── .github/workflows/             # GitHub Actions CI workflow
├── README.md                      # Project documentation
├── app-release.apk                # Compiled APK (link below)




