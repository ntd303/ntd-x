# NTd_303 Social 📱✨

NTd_303 Social is an enterprise-grade, high-performance, mobile-first social media client and platform architecture inspired by Twitter/X. Featuring a stunning, premium **Bento Grid** design system, offline-first local cache synchronization via SQLite Room, real-time messaging, and comprehensive administrative oversight dashboards.

This repository contains the complete Android application codebase, build configuration files, architecture blueprints, and deployment pipeline guides.

---

## 🎨 Bento Grid Visual Identity & Design System

The application utilizes a dark-mode **Bento Grid** visual philosophy that optimizes information density, hierarchy, and scanability:

*   **Premium Color Palette**: Backed by high-contrast tones (Deep Slate Background `#111318`, Dark Surface Card `#1A1C1E`, Accent Light Blue `#A8C7FA`, and Neon Green `#22C55E` system telemetry indicators).
*   **Tactile Borders & Shapes**: Dynamic 24.dp corner rounding on Bento panels, framed with sleek, subtle border outlines (`#44474E`) that create a physical feel on high-density displays.
*   **Intuitive Visual Spacing**: Spaced with an 8dp/10dp Grid to maintain beautiful negative space while packing vital telemetry, feed cards, trending hashtags, active messaging bubbles, and user composer elements.

---

## 🏗️ Technical Architecture & Features

1.  **Decoupled Micro-Monolith Architecture**: Structured to support sub-100ms API response latencies, clean repository boundaries, and highly resilient state transitions.
2.  **Offline-First State Engine**: Core feeds, active user profiles, system configurations, and draft queues are stored locally inside an optimized **SQLite Room Database** before synchronizing with external backends.
3.  **Real-Time Subsystems**: Integrates high-efficiency connection managers for WebSockets (Socket.io) to support instant message dispatch, typing status feedback, and notification push events.
4.  **Bento Telemetry Panel**: Live system dashboards featuring system uptime status displays (99.9% simulation), interactive trending hashtags, and quick-access active chat clusters directly on the home screen.
5.  **Multi-Role Experience**: Custom administrative access dashboards allowing verified moderators or administrator roles (`admin`) to directly manage, shadowban, flag, or approve platform metrics.

---

## 📂 Project Directory Structure

```
NTd303-Social/
├── .gitignore                  # Robust ignore definitions for Android and Gradle builds
├── .env.example                # Base template for platform environment variables
├── build.gradle.kts            # Project-level Gradle build configuration
├── settings.gradle.kts         # Multi-project Gradle build layout definitions
├── gradle.properties           # Gradle daemon and performance properties
├── NTD303_SOCIAL_BLUEPRINT.md  # Comprehensive server-side micro-services blueprint
├── README.md                   # Enterprise setup, Git, and deployment guides
│
└── app/
    ├── build.gradle.kts        # App-module dependencies and Android build configs
    ├── proguard-rules.pro      # Code shrinking, obfuscation, and optimization rules
    └── src/
        └── main/
            ├── AndroidManifest.xml   # Application identity, permission mappings, & activities
            ├── java/com/example/     # Kotlin Source Files
            │   ├── MainActivity.kt   # Core layout, Jetpack Navigation host, and scaffold
            │   ├── data/
            │   │   ├── local/        # Room Database, DAOs, and User/Post Entities
            │   │   └── repository/   # Repository Pattern layer for local/network boundaries
            │   └── ui/
            │       ├── components/   # BentoGridDashboard, PostCards, and design atoms
            │       ├── screens/      # Feature screens (Auth, Home, Explore, DM, Admin, Profile)
            │       └── theme/        # Material Design 3 theme typography, shapes, and color palettes
            └── res/
                └── values/
                    └── strings.xml   # System strings, application labels, and localizable items
```

---

## 🔑 Environment Variables Setup

The app coordinates API endpoints and keys using the **Secrets Gradle Plugin** and local configuration files. 

### Local Development Setup
1. Duplicate `.env.example` to create a local environment file named `.env` in the project root folder:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and fill in your keys:
   ```env
   GEMINI_API_KEY=AIzaSyD-Your-Actual-Gemini-API-Key-Here
   BACKEND_API_URL=https://api.ntd303.social
   ```
3. During runtime, these secrets are injected into your build config automatically. Access them in Kotlin using:
   ```kotlin
   val apiKey = BuildConfig.GEMINI_API_KEY
   val apiUrl = BuildConfig.BACKEND_API_URL
   ```

*Warning: Never commit your `.env` file containing actual active keys to a public GitHub repository. It is securely listed inside `.gitignore`.*

---

## 💻 Git & GitHub Setup Instructions

Prepare and push your repository to GitHub using these standard commands:

### 1. Initialize Local Git Repository
From your project root directory, run:
```bash
# Initialize git
git init

# Verify git ignores are correct
git status

# Stage all project files
git add .

# Create initial commit
git commit -m "feat: initial commit with bento grid theme, room offline cache, and real-time scaffolds"
```

### 2. Connect and Push to GitHub
1. Go to your [GitHub account](https://github.com) and create a new repository (do not initialize with a README, `.gitignore`, or license, as we have already configured them).
2. Grab your remote URL (e.g., `git@github.com:username/ntd303-social.git` or `https://github.com/username/ntd303-social.git`).
3. Link and push your local commits:
   ```bash
   # Add remote destination
   git remote add origin https://github.com/username/ntd303-social.git

   # Set default branch to main
   git branch -M main

   # Push to the remote origin
   git push -u origin main
   ```

---

## 📦 Release Build & Signing Instructions

To distribute your application via the Google Play Store or install a optimized production build, you must generate a signed Release APK or Android App Bundle (AAB).

### 1. Generate a Keystore File
If you do not already have a release keystore, generate one using the Java `keytool` command:
```bash
keytool -genkey -v -keystore release.keystore -alias ntd303_key -keyalg RSA -keysize 2048 -validity 10000
```
Follow the interactive prompt to secure it with passwords and fill out organizational information. Store this keystore file safely and **never** commit it to GitHub!

### 2. Configure gradle.properties
Create or edit `local.properties` or `gradle.properties` (non-tracked) to supply the signing configurations safely:
```properties
RELEASE_STORE_FILE=/path/to/your/release.keystore
RELEASE_STORE_PASSWORD=your_store_password_here
RELEASE_KEY_ALIAS=ntd303_key
RELEASE_KEY_PASSWORD=your_key_password_here
```

---

## 🚀 Building AAB & APK Packages

Android App Bundles (AAB) are the standard publishing format for Google Play. They allow Google Play to generate optimized APKs specific to each user's device configuration, significantly reducing download sizes.

### Generate Android App Bundle (.aab)
Execute the Gradle command in your project terminal:
```bash
# Build release AAB package
gradle :app:bundleRelease
```
Once the build task completes successfully, you will find the generated app bundle located here:
`app/build/outputs/bundle/release/app-release.aab`

### Generate Signed Release APK (.apk)
If you require a direct direct-installable APK for testing:
```bash
# Build release APK package
gradle :app:assembleRelease
```
The compiled output is located here:
`app/build/outputs/apk/release/app-release.apk`

---

## 🛠️ Diagnostics, Testing, & Continuous Integration

To run automated checks, unit tests, and validation steps locally or in a GitHub Actions pipeline, execute the following commands:

```bash
# Run Kotlin compilation and syntax validation
gradle compileDebugKotlin

# Run all local JVM unit tests (including Robolectric)
gradle :app:testDebugUnitTest

# Format code standards (if configured)
gradle lint
```

### Suggested GitHub Actions Pipeline (`.github/workflows/android.yml`)
To automate your builds, copy this standard action into your repository:
```yaml
name: Android CI/CD

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Repository
        uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Create .env from secrets
        run: echo "GEMINI_API_KEY=${{ secrets.GEMINI_API_KEY }}" > .env

      - name: Grant Execute Permission to Gradle
        run: chmod +x gradlew || true

      - name: Compile and Build Applet
        run: ./gradlew assembleDebug
```

---

## 📄 License & Attribution

This platform blueprint, design layouts, database architecture maps, and source files are property of **NTd_303 Social Media Systems**. Distributed under the MIT License. See `LICENSE` inside your repository for more details.
