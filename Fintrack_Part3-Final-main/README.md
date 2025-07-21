# 💸 Budget Tracker App (FinTrack) - Kotlin + RoomDB

---

## 📱 Overview

**FinTrack** is an offline-first personal finance Android application built using **Kotlin** and **RoomDB (SQLite)**. The app is designed for users to manage daily expenses, set spending goals, track category-wise costs, and visualize budget performance in real time. This application is tailored for South African users and uses the Rand (**R**) as the default currency.


## GitHub Repository:
🔗 https://github.com/ST10304249/Fintrack_Part3-Final.git



---

## 👥 Group Members

- **Philadelphia Nkuna** – ST10304249  
- **Cynthia Panzu** – ST10174327  
- **Risima-Ra-Rifuwo Gala** – ST10380589  

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

##🔗 Demo Video
🎥 https://youtu.be/FsGyUZDJOl8

canva link: https://www.canva.com/design/DAGnOugsKEE/SHzyLr-Yh3gOSrh3XabMDw/edit?utm_content=DAGnOugsKEE&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton
Includes:

##full feature walkthrough.

--Real use-case demonstration.
--Voice-over explanation by the group.
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
📦 APK Download

📱 Click here to download the APK
