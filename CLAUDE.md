# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build, test, and run

The project uses the Gradle wrapper. The Gradle daemon is pinned to **JDK 21** via `gradle/gradle-daemon-jvm.properties` (with the Foojay resolver in `settings.gradle` to auto-provision if absent). App bytecode target is JVM 17 (see `app/build.gradle` `compileOptions` and `kotlin.compilerOptions.jvmTarget`). AGP 9.2.0 / Kotlin 2.3.21 / Gradle 9.4.1.

- `./gradlew assembleDebug` — build the debug APK
- `./gradlew test` — all JVM unit tests (no instrumented test suite is wired up)
- `./gradlew :app:testDebugUnitTest --tests "com.example.android.dogsapp.ui.main.MainViewModelTest"` — single test class
- `./gradlew :app:testDebugUnitTest --tests "*.MainViewModelTest.dogs flow mirrors the repository"` — single test case (backtick names work with the wildcard pattern; the test will still match)
- `./gradlew :app:compileDebugKotlin` — quickest "does it still compile" check after a refactor
- Install on the connected emulator/device:
  `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- Launch:
  `adb shell am start -n com.example.android.dogsapp/.ui.MainActivity`

CI (`.github/workflows/build_pull_request.yml`) runs `assembleDebug` then `test` on push to `master` and on PRs. No lint task is wired; treat compiler + tests as the gate.

## Architecture (the big picture)

Single-activity Android Views app (no Compose). MVVM with a single Hilt module, Room as the source of truth for the UI, StateFlow plumbing throughout.

### Data flow (the one you'll touch most)

```
DogsApi (Retrofit)            Room (DogDao, FavoriteDao)
        │                              │
        │ suspend, called by           │ Flow<...>
        │ DogsRepositoryImpl.refresh() │
        ▼                              ▼
                DogsRepository (interface)
                          │
                          │ Flow<...>, suspend
                          ▼
            HiltViewModel ── exposes StateFlow ──▶ Fragment
                                                   collects via
                                                   lifecycleScope.launch {
                                                     repeatOnLifecycle(STARTED) { … }
                                                   }
```

- **Room is the UI's source of truth.** `DogsRepositoryImpl.dogs` and `.favorites` are `Flow<List<Dog>>` directly mapped from DAO flows. `refresh()` writes the network response to Room in a `@Transaction` (`DogDao.replaceAll`); the UI never reads `DogsResponse` directly.
- **On network error, the cache stays.** `refresh()` throws; `MainViewModel` catches in a coroutine and flips `status` to `ERROR`, but the cached `dogs` flow keeps emitting. This is the offline-first contract.
- **`isFavorite` is read two ways**: as a `Flow<Boolean>` for the UI and as a `suspend fun isFavoriteOnce` for the toggle-or-remove decision. Don't collapse them — the suspend variant avoids a transient subscription just to read once.
- **ViewModels expose StateFlow via `stateIn(viewModelScope, WhileSubscribed(5_000), initial)`.** The 5-second timeout lets configuration changes (rotation, brief screen-off) reuse the upstream subscription instead of restarting Room queries.

### Navigation and parceling

- Single `MainActivity` hosts a `NavHostFragment`; nav graph is `res/navigation/navgraph.xml`. All three fragments (`main`, `details`, `favorites`) live in the same back stack.
- `Dog` is `@Parcelize` because Safe Args still needs Parcelable for `Bundle` marshalling. `DogsResponse` is `@Serializable` (kotlinx.serialization) — these two annotation systems are not interchangeable; pick by boundary (IPC vs network/disk).
- `DetailsViewModel` reads its `dog` from `SavedStateHandle["dog"]` — the Safe Args plugin stores nav args there under their declared name.

### Annotation processing — KSP only

There is no Kapt in this project. Both Hilt and Room go through KSP:

```gradle
ksp "com.google.dagger:hilt-android-compiler:$hilt_version"
ksp "androidx.room:room-compiler:$room_version"
```

Do not add kapt processors. If a library only ships a Kapt processor, either find a KSP fork or wrap the use case differently. The `--add-opens` JDK 17 workaround flags are intentionally **not** in `gradle.properties` — they're a Kapt-only smell.

### Room migrations

The DB is at schema **version 2**, with `MIGRATION_1_2` adding the `favorites` table (defined in `data/local/DogsDatabase.kt` and added to the builder in `ApplicationModule.provideDogsDatabase`). If you change any entity schema, **add a real migration** rather than reaching for `fallbackToDestructiveMigration()` — the whole point of Room is to preserve the offline cache and the user's favorites across app updates.

### View layer

- **View Binding only.** There is no DataBinding XML expression syntax (`<layout>`, `<data>`, `@{}`) anywhere — all binding-adapter logic was inlined into fragments (status icon swap in `MainFragment.renderStatus`, heart drawable swap in `DetailsFragment.renderFavorite`, breed string in `Dog.displayBreedName()`).
- **Fragments use `_binding`/`binding` nullable-with-getter pattern** and null it out in `onDestroyView()`. New fragments should follow this — leaking the binding past view destruction is the easiest way to crash here.
- **No image loading library is referenced from adapters or fragments.** They depend on the `ImageLoader` interface (`common/imaging/`); `CoilImageLoader` is the only file that imports `coil3.*`. Add new image-loading sites through this interface — don't reach for Coil directly.

### Hilt module

There is **one** module, `common/di/application/ApplicationModule`, installed in `SingletonComponent`. Everything is provided via `@Provides` (no `@Binds` interfaces). When adding a new abstraction (e.g., `ImageLoader`, `RefreshManager`), follow the existing pattern: the function takes the concrete `@Inject` class and returns the interface. This keeps `ApplicationModule` as the single place where concrete↔interface mappings live.

### Constructor-injection rule

**ViewModels and other classes depend on interfaces (`DogsRepository`, `ImageLoader`, `RefreshManager`), never on `*Impl` types.** The DI module is the only place that names concrete implementations. This isn't just style — it's how the unit tests work (fakes implement the same interfaces).

## Testing conventions

- **Run on JVM, no Android instrumentation.** All tests live in `app/src/test/`.
- **Coroutines + Main dispatcher**: use the project's `MainDispatcherRule` (sets `Dispatchers.setMain(UnconfinedTestDispatcher())`). Pair it with `runTest { ... }`.
- **Flow assertions**:
  - For "what's the current value" — `viewModel.someStateFlow.value` (works because `StateFlow` always has one).
  - For emission sequences (e.g., initial → updated) — Turbine's `.test { awaitItem() … }`. Be aware that with `stateIn(WhileSubscribed(5_000), initial)` the first emission can be the initial OR the first upstream value depending on timing; existing tests guard with `if (first.isEmpty()) awaitItem() else first`.
- **Fakes, not mocks.** `app/src/test/.../fakes/` has hand-rolled implementations of every DI surface (`FakeDogDao`, `FakeFavoriteDao`, `FakeDogsApi`, `FakeDogsRepository`, `FakeRefreshManager`). Reuse them. Mocking frameworks are deliberately not added.

## Local agent settings

`.claude/settings.local.json` is gitignored and holds personal permission rules (e.g., `Bash(gh repo edit:*)`). If you need to widen permissions for the agent, add them there; don't put them in a tracked `.claude/settings.json` unless the rule is genuinely team-wide.
