# DogsApp

[![Android CI](https://github.com/Tarek-Bohdima/DogsApp/actions/workflows/build_pull_request.yml/badge.svg?branch=master)](https://github.com/Tarek-Bohdima/DogsApp/actions/workflows/build_pull_request.yml)
[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![minSdk](https://img.shields.io/badge/minSdk-26-blue)](app/build.gradle)
[![targetSdk](https://img.shields.io/badge/targetSdk-34-blue)](app/build.gradle)

A small Android sample app that fetches and displays random dog photos from the public [dog.ceo](https://dog.ceo/dog-api/) API.

## Screenshots

| Main grid | Dog details | Offline (cached) |
| --- | --- | --- |
| <img src="docs/screenshots/main.png" alt="Main grid of random dog photos" width="240"/> | <img src="docs/screenshots/details.png" alt="Dog details screen showing breed" width="240"/> | <img src="docs/screenshots/offline.png" alt="Cached dog list served from Room while offline" width="240"/> |

The offline panel is captured with Wi-Fi and mobile data disabled and the app force-stopped: Room serves the cached list and Glide serves the previously-loaded images from its disk cache, while the status icon signals the failed refresh.

### Favorites flow

| Details (not favorited) | Details (favorited) | Favorites screen |
| --- | --- | --- |
| <img src="docs/screenshots/fav_details_empty.png" alt="Dog details screen with the heart toggle in its empty state" width="240"/> | <img src="docs/screenshots/fav_details_filled.png" alt="Dog details screen with the heart toggle filled in" width="240"/> | <img src="docs/screenshots/fav_screen.png" alt="Favorites screen showing the dogs the user has hearted" width="240"/> |

## Features

- Grid of random dog images, refreshable via swipe-to-refresh
- Tap a dog to open a details screen
- Offline-first: cached list + image cache survive process death and no network
- Favorite individual dogs from the details screen and browse them on a Favorites screen
- Network layer with Retrofit + Moshi
- Local persistence with Room
- Dependency injection with Hilt
- MVVM with `ViewModel`, `LiveData`, and Data Binding
- Navigation Component with Safe Args

## Tech stack

| Area              | Library                                       |
| ----------------- | --------------------------------------------- |
| Language          | Kotlin 1.9.22 on JDK 17                       |
| Build             | Android Gradle Plugin 8.2.2, Gradle 8.5       |
| UI                | View system + Data Binding, Material 1.8.0    |
| Architecture      | MVVM (ViewModel, LiveData)                    |
| DI                | Hilt 2.50                                     |
| Persistence       | Room 2.6.1 (offline cache + favorites)        |
| Networking        | Retrofit 2.9.0, Moshi 1.15.1                  |
| Async             | Kotlin Coroutines 1.6.4                       |
| Image loading     | Glide 4.14.2                                  |
| Navigation        | AndroidX Navigation 2.5.3 (Safe Args)         |
| Refresh           | `SwipeRefreshLayout` 1.1.0                    |

## Project structure

```
app/src/main/java/com/example/android/dogsapp
├── common/di/application   # Hilt module (Retrofit, API, repository, refresh manager)
├── data
│   ├── domain              # Dog, DogsResponse
│   ├── network             # DogsApi (Retrofit interface)
│   └── repository          # DogsRepository(+Impl)
└── ui
    ├── main                # MainFragment, MainViewModel, DogsAdapter
    ├── details             # DetailsFragment
    └── utils               # RefreshManager / SwipeToRefreshManagerImpl
```

The API base URL is set in `ApplicationModule.kt:22` and the endpoint is defined in `DogsApi.kt:7`.

## Getting started

### Requirements

- Android Studio (Giraffe or newer recommended)
- JDK 17
- Android SDK with API level 34 installed

### Build & run

```bash
git clone https://github.com/Tarek-Bohdima/DogsApp.git
cd DogsApp
./gradlew assembleDebug
```

Then open the project in Android Studio and run the `app` configuration on an emulator or device (API 26+).

### Tests

```bash
./gradlew test
```

## Continuous Integration

Every pull request against `master` runs the [`Android CI`](.github/workflows/build_pull_request.yml) workflow on `ubuntu-latest`: it sets up JDK 17, caches Gradle, builds a debug APK, and runs unit tests.

## Credits

Dog images provided by the free [Dog CEO API](https://dog.ceo/dog-api/).
