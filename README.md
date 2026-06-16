# 🏥 Medical Management System

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Min SDK](https://img.shields.io/badge/Min%20SDK-23-blue?style=for-the-badge)
![Target SDK](https://img.shields.io/badge/Target%20SDK-36-brightgreen?style=for-the-badge)

**A role-based Android application for managing clinic appointments, patient histories, and medical billing — powered by Firebase.**

*Submitted for CS-512 Mobile App Development*  
*University of Agriculture, Faisalabad*  
*Instructor: Prof. Waseem*

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Data Models](#-data-models)
- [Firebase Collections](#-firebase-collections)
- [Getting Started](#-getting-started)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Screenshots](#-screenshots)
- [Usage Guide](#-usage-guide)

---

## 🌟 Overview

The **Medical Management System** is a full-featured Android application designed to streamline the workflow of a medical clinic. It provides a **dual-role interface** — one for **Patients** and one for **Doctors** — enabling seamless appointment booking, medical record management, treatment tracking, and billing, all backed by **Firebase Authentication** and **Cloud Firestore** in real-time.

---

## ✨ Features

### 👤 Patient Features
| Feature | Description |
|---|---|
| 📅 **Book Appointments** | Browse available doctors by specialization and schedule appointments with a date & time picker |
| 💊 **Treatment History** | View a complete log of past diagnoses, prescriptions, and treatment progress |
| 🧾 **Bills History** | Access all generated medical bills with descriptions and amounts |
| 👤 **Profile Dashboard** | View personal information including name, email, phone, and age |

### 🩺 Doctor Features
| Feature | Description |
|---|---|
| 📋 **Pending Appointments** | Review and approve/manage incoming appointment requests from patients |
| 📆 **Today's Appointments** | See a focused view of all appointments scheduled for the current day |
| 📝 **Update Patient History** | Record disease diagnosis, prescription, and treatment progress for each appointment |
| 💰 **Generate Bills** | Create itemized bills with description and amount for completed consultations |
| 🗂️ **Patient History** | Search and review the full medical history of all patients |
| 👤 **Profile Dashboard** | View professional profile including name, email, phone, and specialization |

### 🔐 Authentication
- Secure email/password login via **Firebase Authentication**
- Role-based routing — patients and doctors land on their respective dashboards automatically
- Registration supports both **Patient** and **Doctor** roles with role-specific fields (age vs. specialization)

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java |
| **UI Framework** | Android Views (XML layouts) + Material Design 3 |
| **Navigation** | `BottomNavigationView` |
| **Authentication** | Firebase Authentication (Email/Password) |
| **Database** | Firebase Cloud Firestore (NoSQL) |
| **List Views** | RecyclerView with custom Adapters |
| **Build System** | Gradle (Kotlin DSL) |
| **Min SDK** | API 23 (Android 6.0 Marshmallow) |
| **Target SDK** | API 36 |

---

## 🏗 Architecture

The app follows a straightforward **Activity-based MVC pattern**:

```
┌──────────────────────────────────────────────┐
│                   Firebase                    │
│   ┌─────────────────┐  ┌──────────────────┐  │
│   │  Authentication │  │   Cloud Firestore│  │
│   └────────┬────────┘  └────────┬─────────┘  │
└────────────┼────────────────────┼────────────┘
             │                    │
┌────────────▼────────────────────▼────────────┐
│                 Android App                   │
│                                               │
│  ┌──────────────┐     ┌───────────────────┐   │
│  │    Models    │     │    Activities     │   │
│  │  User.java   │◄────│  LoginActivity    │   │
│  │  Appointment │     │  PatientHome      │   │
│  └──────────────┘     │  DoctorHome       │   │
│                       │  BookAppointment  │   │
│  ┌──────────────┐     │  GenerateBill     │   │
│  │   Adapters   │     │  ...etc           │   │
│  │ AppointmentA │◄────└───────────────────┘   │
│  │ DoctorAppAdp │                             │
│  │ PatientHistA │                             │
│  └──────────────┘                             │
└──────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
MedicalManagementSystem/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/medicalmanagement/
│   │       │   │
│   │       │   ├── models/                         # Data Models
│   │       │   │   ├── User.java                   # Patient & Doctor user model
│   │       │   │   └── Appointment.java             # Full appointment data model
│   │       │   │
│   │       │   ├── MainActivity.java               # Splash / entry point
│   │       │   ├── LoginActivity.java              # Firebase email/password login
│   │       │   ├── RegisterActivity.java           # Role-based registration
│   │       │   │
│   │       │   ├── PatientHomeActivity.java        # Patient dashboard & profile
│   │       │   ├── BookAppointmentActivity.java    # Appointment booking with date/time picker
│   │       │   ├── BillsHistoryActivity.java       # Patient's billing records
│   │       │   ├── TreatmentHistoryActivity.java   # Patient's treatment log
│   │       │   │
│   │       │   ├── DoctorHomeActivity.java         # Doctor dashboard & profile
│   │       │   ├── PendingAppointmentsActivity.java# Manage incoming appointments
│   │       │   ├── TodaysAppointmentsActivity.java # Today's schedule
│   │       │   ├── HistoryUpdateActivity.java      # Update diagnosis/prescription
│   │       │   ├── GenerateBillActivity.java       # Create and preview medical bills
│   │       │   ├── PatientHistoryActivity.java     # Browse all patient records
│   │       │   │
│   │       │   ├── AppointmentAdapter.java         # RecyclerView adapter for appointments
│   │       │   ├── DoctorAppointmentAdapter.java   # Doctor-side appointment adapter
│   │       │   └── PatientHistoryAdapter.java      # Patient history list adapter
│   │       │
│   │       ├── res/                                # XML Layouts, Drawables, Strings
│   │       └── AndroidManifest.xml
│   │
│   ├── build.gradle.kts                           # App-level dependencies
│   └── google-services.json                       # Firebase configuration
│
├── build.gradle.kts                               # Project-level build config
├── settings.gradle.kts
└── gradle.properties
```

---

## 📊 Data Models

### `User`
```java
String uid              // Firebase Auth UID (document ID)
String name             // Full name
String email            // Login email
String phone            // Contact number
String role             // "patient" | "doctor"
int    age              // Patient only
String specialization   // Doctor only
```

### `Appointment`
```java
String  appointmentId      // Auto-generated Firestore document ID
String  patientId          // Patient's UID
String  doctorId           // Doctor's UID
String  patientName        // Denormalized patient name
String  doctorName         // Denormalized doctor name
String  date               // Format: YYYY-MM-DD
String  time               // Format: HH:mm
String  status             // "pending" | "approved" | "completed"
String  disease            // Diagnosis (filled by doctor)
String  prescription       // Medication (filled by doctor)
String  progress           // Treatment progress notes
double  billAmount         // Bill total in PKR
String  billDescription    // Itemized description
boolean billGenerated      // Whether a bill has been issued
```

---

## 🗄 Firebase Collections

```
Firestore Database
│
├── users/                       (Collection)
│   └── {uid}/                   (Document — one per registered user)
│       ├── uid
│       ├── name
│       ├── email
│       ├── phone
│       ├── role                 ("patient" or "doctor")
│       ├── age                  (patients only)
│       └── specialization       (doctors only)
│
└── appointments/                (Collection)
    └── {appointmentId}/         (Document — one per appointment)
        ├── appointmentId
        ├── patientId
        ├── doctorId
        ├── patientName
        ├── doctorName
        ├── date
        ├── time
        ├── status
        ├── disease
        ├── prescription
        ├── progress
        ├── billAmount
        ├── billDescription
        └── billGenerated
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 17**
- **Android SDK** with API 23–36 installed
- A **Firebase project** with:
  - Authentication (Email/Password provider enabled)
  - Cloud Firestore (in test or production mode)

### Setup & Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/MedicalManagementSystem.git
   cd MedicalManagementSystem
   ```

2. **Connect Firebase**
   - Go to [Firebase Console](https://console.firebase.google.com/) and create a new project
   - Register your Android app with package name: `com.example.medicalmanagement`
   - Download `google-services.json` and place it in `app/google-services.json`
   - Enable **Email/Password** sign-in under *Authentication → Sign-in method*
   - Create a **Cloud Firestore** database

3. **Open in Android Studio**
   - Open the project root directory in Android Studio
   - Let Gradle sync complete

4. **Run the app**
   - Connect an Android device or start an emulator (API 23+)
   - Click **Run ▶** or press `Shift + F10`

---

## 📱 Usage Guide

### As a Patient
1. **Register** → Select *Patient* role → Fill in name, email, password, phone, and age
2. **Login** → Lands on *Patient Dashboard*
3. Navigate using the **bottom navigation bar**:
   - **Profile** — View your personal info
   - **Book Appointment** — Pick a doctor, date, and time → submit
   - **Bills** — View all generated bills from your consultations
   - **Treatment** — View your full diagnosis and prescription history

### As a Doctor
1. **Register** → Select *Doctor* role → Fill in name, email, password, phone, and specialization
2. **Login** → Lands on *Doctor Dashboard*
3. Navigate using the **bottom navigation bar**:
   - **Profile** — View your professional info
   - **Pending** — Approve or manage incoming patient appointments
   - **Today** — View today's confirmed appointments; update history or generate bill
   - **Patient History** — Browse and search all patient medical records

---

## 👨‍💻 Author

> **University of Agriculture, Faisalabad**  
> Department of Computer Science  
> Course: **CS-512 — Mobile App Development**  
> Instructor: **Prof. Waseem**

---

<div align="center">

Made with ❤️ using Android Studio & Firebase

</div>
