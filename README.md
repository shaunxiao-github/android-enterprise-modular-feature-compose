# EnterpriseAppCompose

An **enterprise-level Android application template** built with **Jetpack Compose + MVVM**, designed to scale to **hundreds of decoupled feature modules**.

This project demonstrates how to build a **server-driven, modular Android app** where:
- Features are **independent**
- Features **do not depend on each other**
- The app decides **what to show** based on server configuration
- Each feature owns its **UI, resources, and navigation**
- Navigation is **internal-only and secure** (no public deep links)

---

## ✨ Key Features

- Jetpack Compose UI
- MVVM architecture
- RxJava for networking
- Server-driven Home screen
- Feature self-registration (no central routing table)
- Explicit Intent navigation (secure, no exported Activities)
- Mock / QA / Prod environments
- Shared UI design system
- Gradle 9.0.0 + Version Catalog (libs.versions.toml)

---

## 🧠 Core Design Principles

### 1. Features are fully decoupled
- No feature depends on another feature
- The app module never references feature Activities
- Features can be added or removed without modifying Home logic

### 2. Server controls visibility, features control UI
- Server returns only feature IDs
- Feature modules provide:
  - title
  - message
  - icon
  - entry Activity

### 3. Navigation is internal and secure
- Uses explicit intents
- Feature Activities are exported=false
- No other app can launch internal feature screens

---

## 🗂 Module Structure

app/
│
core/
├── common        
├── network       
├── config        
├── navigation    
├── ui            
features/
├── bundle        
feature/
└── payments/
  │   ├── api       
  │   └── impl      
  └─ profile/
      ├─ api
      └─ impl

---

## 🔌 How Decoupling Works

Each feature implements the FeatureEntry interface from core/navigation.

Features register themselves using Java ServiceLoader, so the app discovers them automatically at runtime.
There is no central routing table and no compile-time dependency from app to feature Activities.

The Home screen only works with:
- feature IDs from the server
- FeatureEntry interfaces discovered at runtime

---

## 🔐 Secure Navigation

Navigation uses explicit intents provided by FeatureEntry implementations.
Feature Activities are marked exported=false, which prevents any other app from launching them.

This removes the deep-link attack surface while keeping full internal decoupling.

---

## 🧪 Environments

### Flavors
- mock
- qa
- prod

### Mock flavor
- Uses MockWebServer
- Returns static JSON config
- Runs fully offline

Example response:
{"home":["payments","profile"]}

---

## 🧱 MVVM Architecture

- HomeViewModel loads configuration via RxJava
- ViewModel maps feature IDs to UI rows using FeatureRegistry
- Compose UI observes ViewModel state and renders accordingly

---

## 🎨 Shared UI System

All screens use a shared Compose-based design system:
- EnterpriseTheme
- Shared UI components
- Light and Dark theme support

---

## ➕ Adding a New Feature

1. Create feature modules:
   feature/newfeature/api
   feature/newfeature/impl

2. Define feature ID

3. Create Activity + Compose UI in impl

4. Implement FeatureEntry

5. Register FeatureEntry via META-INF/services

6. Add impl module to features:bundle

7. Server returns the new feature ID

No changes to Home screen are required.

---

## 🧩 Why This Architecture Scales

- Adding features does not require touching app navigation
- Features remain isolated and independently testable
- Security is stronger than deep-link based navigation
- Build times and code ownership scale with team size

---

## 📌 Use Cases

- Enterprise Android applications
- Large multi-team codebases
- Server-driven UI
- Long-lived products with evolving feature sets

---

## 📜 License

MIT (or your preferred license)
