# Architecture

This document describes the internal architecture of DogsApp. For the contributor-facing "how do I work here" guide, see [CONTRIBUTING.md](CONTRIBUTING.md). For agent-facing onboarding, see [CLAUDE.md](CLAUDE.md). The user-facing project intro is in [README.md](README.md).

## Overview

DogsApp is a single-activity Android Views app implementing **offline-first MVVM** with a Hilt-managed object graph. Room is the source of truth for the UI; the network only writes to Room. Everything below the ViewModel exposes Kotlin `Flow`, and ViewModels expose `StateFlow` collected by fragments under lifecycle awareness.

```
┌────────────────────────────────────────────────────────────────────┐
│  Fragment (collects under repeatOnLifecycle)                       │
│  ├─ MainFragment      ─┐                                           │
│  ├─ DetailsFragment    │ View Binding only                         │
│  └─ FavoritesFragment  ┘                                           │
└─────────────┬──────────────────────────────────────────────────────┘
              │ StateFlow<*>
┌─────────────▼──────────────────────────────────────────────────────┐
│  HiltViewModel                                                     │
│  ├─ MainViewModel       (drives refresh, status, navigation)       │
│  ├─ DetailsViewModel    (dog from SavedStateHandle, isFavorite)    │
│  └─ FavoritesViewModel  (favorites list + isEmpty state)           │
└─────────────┬──────────────────────────────────────────────────────┘
              │ Flow<*>, suspend fun
┌─────────────▼──────────────────────────────────────────────────────┐
│  DogsRepository (interface)                                        │
│    ├─ dogs:      Flow<List<Dog>>      (mapped from DogDao)         │
│    ├─ favorites: Flow<List<Dog>>      (mapped from FavoriteDao)    │
│    ├─ isFavorite(url): Flow<Boolean>  (live per-row)               │
│    ├─ toggleFavorite(url): suspend    (one-shot read + write)      │
│    └─ refresh(): suspend              (network → Room, throws)     │
└──┬────────────────────────────────────────────────┬────────────────┘
   │ writes (one direction)                         │ reads (the only direction
   │                                                │ the UI cares about)
┌──▼─────────────────┐                       ┌──────▼─────────────┐
│  DogsApi (Retrofit │                       │  Room DAOs         │
│  + kotlinx.serial- │                       │  ├─ DogDao         │
│  ization JSON)     │                       │  └─ FavoriteDao    │
└────────────────────┘                       └────────────────────┘
```

## Layers and responsibilities

### `data/network/`
Retrofit interface (`DogsApi`) and its kotlinx.serialization-typed responses. Only `DogsRepositoryImpl.refresh()` calls this layer; nothing else should ever see a `DogsResponse` instance.

### `data/local/`
Room layer. Entities, DAOs (`DogDao`, `FavoriteDao`), the `DogsDatabase` class, and migration definitions (`MIGRATION_1_2` adds the `favorites` table).

Key invariants:
- DAOs return `Flow` for observations and `suspend` for one-shot operations.
- Replacing the dogs list is a `@Transaction` (`DogDao.replaceAll`) so observers see one atomic update.
- `FavoriteDao` exposes both `isFavorite(url): Flow<Boolean>` (for UI subscription) and `isFavoriteOnce(url): suspend` (for toggle decision-making). These intentionally co-exist.

### `data/domain/`
Plain data types crossing layer boundaries.
- `Dog(imageUrl: String)` — `@Parcelize` because Safe Args still needs Parcelable for `Bundle` marshalling.
- `DogsResponse` — `@Serializable` (kotlinx.serialization) because Retrofit uses its converter.
- `Dog.displayBreedName()` is a pure top-level extension. View code calls it directly; the function is fully unit-tested.

### `data/repository/`
Interface `DogsRepository` + implementation. The implementation is the only class that touches both the network API and Room. Repositories return `Flow` to consumers and stay free of Android types (no `LiveData`, no `Context`).

### `common/`
- `common/di/application/ApplicationModule` — the single Hilt module, `@InstallIn(SingletonComponent::class)`. Concrete implementations are constructor-injected and exposed via interfaces here.
- `common/imaging/` — the `ImageLoader` abstraction + `CoilImageLoader` implementation. Adapters and fragments depend on the interface only.

### `ui/`
Three feature packages: `main`, `details`, `favorites`. Each owns its fragment, ViewModel, and (where applicable) adapter. Cross-feature reuse goes through `DogsAdapter` (used by both `main` and `favorites`).

## State management story

### `StateFlow` with `WhileSubscribed(5_000)`

```kotlin
val dogs: StateFlow<List<Dog>> = dogsRepository.dogs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
```

The 5-second timeout means:
- On rotation or brief screen-off, the upstream subscription is preserved — Room queries don't restart.
- After 5 seconds with no subscriber (genuine backgrounding), upstream is cancelled, releasing the Room observer.

This is the standard Android recipe for replacing LiveData; do not change to `Eagerly` unless you have a specific reason — `Eagerly` keeps the upstream alive for the ViewModel's whole lifetime.

### Collecting in fragments

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch { viewModel.dogs.collect { adapter.submitList(it) } }
        launch { viewModel.status.collect { renderStatus(it) } }
        launch { viewModel.navigateToDetail.collect { … } }
    }
}
```

`repeatOnLifecycle(STARTED)` cancels collection on `STOP` and re-launches it on `START`. Each inner `launch` is its own coroutine so a slow upstream doesn't starve the others.

### One-shot UI events (navigation)

Per-fragment `MutableStateFlow<Dog?>` carries the "navigate to detail" event. The fragment calls `viewModel.onDogDetailNavigated()` after navigating so the same dog isn't re-pushed on rotation. This is a pragmatic alternative to event channels for a tiny app; for a larger surface, `Channel`/`SharedFlow(replay=0)` would be a better fit.

## Persistence and migrations

Room is at **schema version 2**. The migration history:

| From → To | Migration                              | What it does                          |
| --------- | -------------------------------------- | ------------------------------------- |
| —         | (initial, version 1)                   | Creates `dogs` table                  |
| 1 → 2     | `MIGRATION_1_2` (in `DogsDatabase.kt`) | Adds `favorites(imageUrl PK)` table   |

If you change any entity schema:
1. Bump the version in `@Database(...)`.
2. Write a `Migration(oldVersion, newVersion)` object alongside `MIGRATION_1_2`.
3. Register it with `.addMigrations(...)` in `ApplicationModule.provideDogsDatabase`.

Do **not** reach for `fallbackToDestructiveMigration()`. The offline cache and the user's favorites are the entire point of having Room here.

## Dependency injection

A single Hilt module (`ApplicationModule`) provides every interface. Pattern:

```kotlin
@Provides
fun provideImageLoader(coilImageLoader: CoilImageLoader): ImageLoader = coilImageLoader
```

- Concrete implementations are `@Inject constructor` classes — Hilt instantiates them.
- The module function takes the concrete type and returns the interface — that's the only place the binding from interface→impl exists.
- This avoids `@Binds` modules + `abstract class` boilerplate while keeping concrete types hidden from consumers.

ViewModels are `@HiltViewModel`-annotated with `@Inject constructor`; fragments retrieve them via `by viewModels()`. `DetailsViewModel` additionally takes `SavedStateHandle` so it can read the Safe-Args `dog` argument with `savedStateHandle["dog"]`.

## Image loading: the `ImageLoader` abstraction

```
DogsAdapter / DetailsFragment
        │ depends on
        ▼
ImageLoader (interface)  ◀── CoilImageLoader (the only Coil consumer)
```

Adapters and fragments never import `coil3.*`. To swap image libraries, write a new `ImageLoader` impl and re-bind in `ApplicationModule`. The `placeholder`/`error` drawables live inside `CoilImageLoader`, not in calling code.

## Annotation processing: KSP only

Both Hilt and Room are processed via KSP, not Kapt:

```gradle
ksp "com.google.dagger:hilt-android-compiler:$hilt_version"
ksp "androidx.room:room-compiler:$room_version"
```

The decision is intentional:
- KSP is faster and avoids JDK-17 reflection surprises (the Kapt-only `--add-opens` flags were removed from `gradle.properties`).
- AGP 9 bundles Kotlin support, so the `org.jetbrains.kotlin.android` plugin is **not applied**. Use `kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_17 } }` instead of the deprecated `kotlinOptions {}` block.

If you add a library that only ships a Kapt processor, find a KSP fork, contribute one, or pick a different library — don't reintroduce Kapt.

## Why these choices (very short)

- **Room + Flow + StateFlow** — single source of truth, offline-first, lifecycle-correct UI collection.
- **kotlinx.serialization** over Moshi — first-class Kotlin support, no kotlinpoet-metadata stalls when bumping Kotlin.
- **Coil 3** over Glide — coroutine-native, no Kapt processor, smaller surface.
- **View Binding** over Data Binding XML expressions — explicit code paths in fragments, no XML-side logic, easier refactors.
- **Single Hilt module** — sample size doesn't justify multi-module DI graphs.
- **Pure helpers like `displayBreedName()`** — testable without instrumentation.
