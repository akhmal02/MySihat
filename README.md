# 🫀 MySihat — Smart Healthcare & Non-Communicable Disease (NCD) Self-Monitoring System

![Platform](https://img.shields.io/badge/Platform-Android%20(Kotlin%20%2B%20Jetpack%20Compose)-0D47A1?style=for-the-badge&logo=android)
![Backend](https://img.shields.io/badge/Cloud%20Backend-Supabase%20PostgreSQL%20%2B%20SQLite-10B981?style=for-the-badge&logo=supabase)
![Design](https://img.shields.io/badge/UI%20Design-Material%203%20%2B%20Soft%20Glassmorphism-1976D2?style=for-the-badge)
![Coverage](https://img.shields.io/badge/Coverage-All%2016%20Malaysian%20States%20%26%20Federal%20Territories-EF4444?style=for-the-badge)

**MySihat** is a modern **Native Android** healthcare monitoring application built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Supabase Cloud PostgreSQL**. It empowers patients across Malaysia to monitor Non-Communicable Diseases (NCDs) such as **Type 2 Diabetes Mellitus** and **Hypertension**, manage daily medication schedules, locate public hospitals and health clinics (*Klinik Kesihatan*) nationwide, book clinical appointments, and export medical reports in PDF format.

---

## 🌟 Key Features

### 1. 📊 Health Dashboard & Digital Health Pass (`Home Tab`)
- **MySihat Digital Health Pass**: Displays the patient's Full Name, MyKad (National ID), unique MySihat ID, Current Health Status, Clinical Diagnosis, and an interactive Digital Patient QR Code.
- **Live Vitals Summary**: Instant overview of the latest Blood Glucose (`mmol/L`) and Blood Pressure (`mmHg`) readings with a **3-Tier Clinical Indicator** (`LOW`, `NORMAL`, `HIGH`).
- **Daily Medication Tracker**: Tracks prescribed medications, dosage instructions, scheduled intake times, remaining pill stock counts, and allows adding, updating, or deleting medications.
- **Live Clinic Appointment Countdown**: Real-time countdown timer (*Days, Hours, Minutes, Seconds*) to the patient's upcoming clinic or hospital appointment.

### 2. 🩸 Vitals Logging & Trend Analytics (`Vitals Tab`)
- **Blood Glucose Logger (`mmol/L`)**:
  - Supports clinical meal contexts: *Fasting (Morning)*, *Post-Breakfast*, *Before Lunch*, *Post-Lunch*, *Before Dinner*, and *Post-Dinner*.
  - Built-in clinical alert reminding patients to check their post-meal blood glucose **2 hours after eating**.
- **Blood Pressure (`mmHg`) & Pulse (`BPM`) Logger**:
  - Records Systolic, Diastolic, and Heart Rate (Pulse) readings with automatic classification based on Malaysian Clinical Practice Guidelines (CPG).
- **Interactive Trend Charts**: Visualizes historical blood glucose and blood pressure trends alongside target clinical thresholds.

### 3. 🗺️ Nationwide Healthcare Map & Facility Directory (`Map Tab`)
- **Complete Coverage Across All 16 States & Federal Territories (Peninsular Malaysia, Sabah & Sarawak)**:
  - Includes Government Hospitals and Health Clinics (*Klinik Kesihatan*) across Perlis, Kedah, Penang, Perak, Selangor, WP Kuala Lumpur, WP Putrajaya, Negeri Sembilan, Melaka, Johor, Pahang, Terengganu, Kelantan, Sabah, Sarawak, and WP Labuan.
- **Live Interactive GPS Map**:
  - Real-time Leaflet OpenStreetMap integration with automatic distance calculation (`km`) from the user's live GPS coordinates.
  - Filter facilities by State and search instantly by hospital/clinic name, district, or medical specialty.
- **Official Clinical Services Directory**:
  - Displays comprehensive facility details including offered medical services (*Outpatient Department, NCD & Diabetes Clinic, Orthopedics, Ophthalmology, 24-Hour Emergency & Trauma, Pharmacy, Pathology Lab, Rehabilitation*, etc.), operating hours, direct phone dialer (`Direct Call`), and one-tap **Google Maps** navigation.

### 4. 📅 Clinical Appointment Booking & Management (`Appointment Tab`)
- Select any Government Hospital or Health Clinic (*Klinik Kesihatan*) across Malaysia.
- Choose the specific Department / Clinic Type (*Diabetes & NCD Specialist Clinic, Outpatient Department, Ophthalmology Clinic, Orthopedic Clinic, Blood Lab*, etc.).
- Interactive Calendar Date Picker and official clinic time slots (`08:00 AM`, `09:00 AM`, `10:00 AM`, `11:00 AM`, `12:00 PM`, `02:00 PM`, `03:00 PM`, `04:00 PM`).

### 5. 👤 Patient Profile, Biometrics & Official PDF Medical Report (`Profile Tab`)
- **Smart Registration & Cloud Authentication**:
  - **Gender Dropdown Selector** (`Male` / `Female`).
  - **Automatic Age Calculation**: Automatically derives the patient's age from the first 2 digits (`YY`) of their Malaysian MyKad / IC number.
  - **Clean Empty-State New Accounts**: Newly registered accounts start **100% empty (`-`)** with zero pre-filled fake biometrics or records.
  - **Dynamic BMI Calculator**: Automatically calculates BMI (`weight_kg / height_m²`) once the patient enters their height and weight.
- **Official Clinical PDF Report Exporter**:
  - Generates a formatted medical summary PDF containing patient demographics, blood glucose logs, blood pressure logs, and active medications for sharing with physicians.
- **5-Second Soft Glass (Frosted Glassmorphism) Notifications**:
  - Displays an animated frosted-glass notification banner (`Success` or `Alert`) with a 5-second countdown progress bar for every user action (*Add Record, Update, Delete, Search, Login, Register, Logout, Download PDF*).
- **Bilingual Interface**: Instant toggle between **English (`EN`)** and **Bahasa Melayu (`BM`)**.

---

## 🩺 Clinical Standards (Malaysian CPG Thresholds)

All health evaluations in **MySihat** follow a clear **3-Level Clinical Classification** aligned with the *Malaysian Clinical Practice Guidelines (CPG)* for Type 2 Diabetes Mellitus and Hypertension:

| Health Parameter | 🔵 LOW (`RENDAH`) | 🟢 NORMAL (`NORMAL`) | 🔴 HIGH (`TINGGI`) |
| :--- | :---: | :---: | :---: |
| **Blood Glucose (Fasting / Pre-Meal)** | `< 4.0 mmol/L` | `4.0 – 7.0 mmol/L` | `> 7.0 mmol/L` |
| **Blood Glucose (2 Hours Post-Meal)** | `< 4.0 mmol/L` | `4.0 – 8.5 mmol/L` | `> 8.5 mmol/L` |
| **Blood Pressure (Systolic / Diastolic)** | `< 90 / 60 mmHg` | `90/60 – 139/89 mmHg` | `≥ 140 / 90 mmHg` |

---

## 🛠️ System Architecture & Tech Stack

| Layer | Technology Used |
| :--- | :--- |
| **Language** | Kotlin (`100% Native Android`) |
| **UI Framework** | Jetpack Compose (Material Design 3 + Custom Soft Glassmorphism UI) |
| **Cloud Backend & Auth** | **Supabase** (`Supabase Auth REST API` + `PostgREST PostgreSQL Database`) |
| **Local Offline-First Storage** | Android `SQLiteOpenHelper` (`mysihat_patient_cloud_v2.db`) with strict per-user account isolation |
| **Map & Geolocation Engine** | Leaflet.js + OpenStreetMap WebView Engine + Android `LocationManager` GPS |
| **Document Export** | Android Native `PdfDocument` API + `FileProvider` |
| **Build System** | Gradle Kotlin DSL (`build.gradle.kts`) |

---

## 🗄️ Project Structure

```text
MySihat/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── assets/leaflet/                      # Offline-ready Leaflet map engine & HTML templates
│   │   ├── java/com/example/mysihat/
│   │   │   ├── MainActivity.kt                  # Application entry point & session initialization
│   │   │   ├── data/
│   │   │   │   ├── Models.kt                    # Data models & Malaysian CPG classification logic
│   │   │   │   ├── SupabaseDatabaseManager.kt   # Live Supabase Auth/REST engine & SQLite cache
│   │   │   │   ├── MockDataRepository.kt        # Nationwide Malaysian hospital/clinic directory & state manager
│   │   │   │   ├── NotificationService.kt       # 5-second Soft Glass notification & alarm service
│   │   │   │   ├── PdfExportService.kt          # Native PDF clinical report generator
│   │   │   │   └── LanguageManager.kt           # Bilingual state manager (English / Malay)
│   │   │   └── ui/
│   │   │       ├── navigation/
│   │   │       │   └── MainAppContainer.kt      # Bottom navigation bar & Soft Glass banner host
│   │   │       ├── components/
│   │   │       │   ├── CommonComponents.kt      # 3D App Logo, Digital Pass Card, Soft Glass Banner
│   │   │       │   └── ModernIcons.kt           # Custom vector canvas medical icons
│   │   │       ├── screens/
│   │   │       │   ├── SplashScreen.kt          # Animated startup splash screen
│   │   │       │   ├── AuthScreen.kt            # Login & Registration (Gender Dropdown & IC Age)
│   │   │       │   ├── DashboardScreen.kt       # Home Dashboard & Medication Tracker
│   │   │       │   ├── VitalsScreen.kt          # Blood Glucose & Blood Pressure Logger + Charts
│   │   │       │   ├── MapScreen.kt             # Nationwide Hospital & Klinik Kesihatan GPS Map
│   │   │       │   ├── AppointmentsScreen.kt    # Clinical Appointment Scheduler
│   │   │       │   └── ProfileScreen.kt         # Patient Biometrics, Emergency Contact & PDF Export
│   │   │       └── theme/
│   │   │           ├── Color.kt                 # Healthcare color palette
│   │   │           ├── Theme.kt                 # Material 3 Light Theme configuration
│   │   │           └── Type.kt                  # Typography definitions
│   │   └── res/                                 # Custom 3D Heart-ECG-Leaf launcher icons & XML resources
├── supabase/
│   └── RUN_THIS_IN_SUPABASE_SQL_EDITOR.sql      # Complete PostgreSQL schema for Supabase Cloud
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Getting Started (Installation & Build Guide)

### Prerequisites
- **Android Studio** (Koala / Ladybug or newer)
- **JDK 17+** & **Android SDK (API 26 – API 35)**
- Physical Android device (*USB Debugging enabled*) or Android Emulator

### Step 1: Clone the Repository
```bash
git clone https://github.com/akhmal02/MySihat.git
cd MySihat
```

### Step 2: Configure Supabase Cloud Tables
1. Open your **Supabase Project Dashboard**.
2. Navigate to **SQL Editor** → **New Query**.
3. Copy and run the SQL script in [`supabase/RUN_THIS_IN_SUPABASE_SQL_EDITOR.sql`](supabase/RUN_THIS_IN_SUPABASE_SQL_EDITOR.sql) to create the `profiles`, `blood_glucose_records`, `blood_pressure_records`, `medications`, and `appointments` tables.

### Step 3: Build & Run in Android Studio
1. Open **Android Studio** → **Open** → Select the `MySihat` project folder.
2. Wait for **Gradle Sync** to finish.
3. Connect an Android device or start an Android Emulator.
4. Click **Run ▶** (or run `./gradlew assembleDebug` in the terminal to generate `app/build/outputs/apk/debug/app-debug.apk`).

---

## 🔐 Test & Demo Accounts

You can **Register a New Account** directly in the app (which starts completely empty in Supabase) or sign in with the pre-configured sample patient account below:

| Account Type | Email | Password | Description |
| :--- | :--- | :--- | :--- |
| **New User Account** | *(Register any email)* | *(Min. 6 chars)* | Starts **100% empty (`-`)**; age is auto-calculated from the first 2 digits of the MyKad/IC |
| **Sample Patient Account** | `ahmad.zikri@gmail.com` | `Zikri@2026*` | Pre-loaded with sample Blood Glucose, Blood Pressure, Medication, and Appointment records |

---

## 📌 Disclaimer
**MySihat** is an independent self-monitoring healthcare application developed for academic, digital health innovation, and personal wellness tracking purposes. The readings and clinical indicators provided in this application are for informational reference only and do not replace professional medical advice, diagnosis, or treatment from a licensed physician.
