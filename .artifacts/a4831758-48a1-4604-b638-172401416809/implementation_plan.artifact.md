# Migrate to built-in Kotlin (AGP 9.0)

The project is currently using AGP 9.3.1 but still applies the `org.jetbrains.kotlin.android` plugin, which is now built into AGP. This causes a sync error.

## Proposed Changes

### [Gradle Configuration]

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Admin/AndroidStudioProjects/MicQ2/gradle/libs.versions.toml)
- Remove `kotlin-android` plugin definition.
- Replace `kotlin-kapt` with `legacy-kapt` (using the `agp` version).

#### [MODIFY] [build.gradle.kts (root)](file:///C:/Users/Admin/AndroidStudioProjects/MicQ2/build.gradle.kts)
- Remove `kotlin-android` plugin application.
- Replace `kotlin-kapt` with `legacy-kapt`.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/Admin/AndroidStudioProjects/MicQ2/app/build.gradle.kts)
- Remove `kotlin-android` plugin application.
- Replace `kotlin-kapt` with `legacy-kapt`.

## Verification Plan

### Automated Tests
- Run Gradle Sync to verify the error is resolved.
- Build the project (`./gradlew assembleDebug`) to ensure Kotlin compilation and Hilt (kapt) still work as expected.
