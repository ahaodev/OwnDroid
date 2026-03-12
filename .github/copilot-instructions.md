# OwnDroid – Copilot Instructions

## Build Commands

```bash
# Debug APK (signed with testkey)
./gradlew assembleDebug

# Release APK (signed with testkey)
./gradlew assembleRelease

# Build with custom keystore
./gradlew build \
  -PStoreFile="/path/to/keystore.jks" \
  -PStorePassword="..." \
  -PKeyPassword="..." \
  -PKeyAlias="..."
```

There are no unit tests or lint checks — lint is explicitly disabled in `app/build.gradle.kts` (`lint.disable += "All"`).

CI runs on push to the `dev` branch (see `.github/workflows/build.yml`).

## Architecture

Single-module, **feature-based MVVM** with Jetpack Compose and Navigation3.

- **UI layer:** Compose + Material3. No XML layouts.
- **State:** `MutableStateFlow` in ViewModels, collected via `collectAsStateWithLifecycle()`.
- **DI:** Manual — `AppContainer` (singleton in `MyApplication`) creates all repositories, state, and a `MyViewModelFactory`. There is no Hilt or Koin.
- **Navigation:** Jetpack Navigation3 (`androidx.navigation3`). Destinations are a sealed class hierarchy in `ui/navigation/Destination.kt`, each annotated with `@Serializable`. Route registration is in `ui/navigation/EntryProvider.kt`.
- **Privilege elevation:** `PrivilegeHelper` wraps Shizuku, Dhizuku, and root shell (libsu). All Device Policy Manager calls go through `ph.safeDpmCall { }`.
- **Database:** Raw SQLite via `MyDbHelper` (extends `SQLiteOpenHelper`). Repositories sit on top.
- **Settings persistence:** `SettingsRepository` serializes a `MySettings` data class to `settings.json` in the app files directory using `kotlinx.serialization`.

## Feature Module Structure

Each feature lives under `app/src/main/java/com/bintianqi/owndroid/feature/<domain>/`:

```
feature/<domain>/
├── <Feature>ViewModel.kt   # StateFlow state, PrivilegeHelper calls
├── <Feature>Screen.kt      # @Composable UI, receives NavBackStack
├── <Feature>Repository.kt  # Data access (if needed)
└── <Feature>Model.kt       # Data classes (if needed)
```

Current feature domains: `applications`, `system`, `network`, `work_profile`, `password`, `privilege`, `users`, `user_restriction`, `settings`.

## Key Conventions

### Adding a new screen
1. Add a `@Serializable` data object/class to the sealed class in `ui/navigation/Destination.kt`.
2. Register it in `ui/navigation/EntryProvider.kt` inside `myEntryProvider`.
3. Create the `@Composable` screen function with `backstack: NavBackStack<Destination>` as the first parameter.
4. Create a matching ViewModel (if needed), add it to `MyViewModelFactory`, and wire dependencies through `AppContainer`.

### ViewModel conventions
- Constructor receives `application: MyApplication`, `ph: PrivilegeHelper`, `privilegeState: StateFlow<PrivilegeStatus>`, and any needed repositories/channels.
- All DPM calls use `ph.safeDpmCall { dpm.someMethod() }`.
- New ViewModels must be added to `MyViewModelFactory.create()`.

### Navigation
- Use `backstack.add(Destination.SomeDestination)` to navigate forward.
- Use `backstack.removeLastOrNull()` to go back.
- Never use `NavController` — this project uses Navigation3's `NavBackStack` directly.

### Compose patterns
- State is collected with `val foo by viewModel.fooState.collectAsStateWithLifecycle()`.
- Reusable components are in `ui/Components.kt`.
- Navigation transitions are defined in `ui/NavTransition.kt`.

## Key Libraries (from `gradle/libs.versions.toml`)

| Library | Purpose |
|---|---|
| Compose BOM 2026.02.00 | Compose UI foundation |
| Material3 | UI components & theming |
| Navigation3 1.0.1 | Screen navigation |
| Lifecycle / ViewModel 2.10.0 | State management |
| Shizuku 13.1.5 | Privileged shell API |
| Dhizuku-API 2.5.4 | Delegated device admin |
| libsu 6.0.0 | Root shell execution |
| hiddenapibypass 6.1 | Access hidden Android APIs |
| kotlinx-serialization-json 1.10.0 | JSON & navigation destinations |
| Accompanist 0.37.3 | Permissions, drawable painter |

## MCP Servers

### android-dpm-dev

Provides Android Device Policy Manager skills for interacting with and querying DPM functionality. Requires only ADB or root access — no Shizuku or Dhizuku needed.

```json
{
  "mcpServers": {
    "android-dpm-dev": {
      "command": "node",
      "args": ["/home/ahao/StudioProjects/android-dpm-dev/dist/index.js"]
    }
  }
}
```

## SDK & Language Targets

- **compileSdk / targetSdk:** 36 (Android 15)
- **minSdk:** 23 (Android 6.0)
- **Kotlin:** 2.3.10
- **JVM target:** 21
- **AGP:** 9.0.1
