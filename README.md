# DogsApp

[![Android CI](https://github.com/Tarek-Bohdima/DogsApp/actions/workflows/build_pull_request.yml/badge.svg)](https://github.com/Tarek-Bohdima/DogsApp/actions/workflows/build_pull_request.yml)

DogsApp is an Android application built with Kotlin that displays a grid of random dog images fetched from [dog.ceo API](https://dog.ceo/dog-api/).

## Features

- Fetch random dog images: Loads a set of 50 random dog images from the API.
- MVVM Architecture: Clean separation of concerns using ViewModel, Repository, and Data classes.
- Navigation: Users can tap on a dog image to view details in a separate fragment.
- Swipe to Refresh: Pull-to-refresh to reload new images.
- Dependency Injection: Powered by Dagger Hilt for scalable and maintainable code.
- Data Binding & LiveData: UI updates automatically as data changes.
- Glide Integration: Efficient image loading and caching.

## Installation

1. Clone the repo:
```bash
git clone https://github.com/Tarek-Bohdima/DogsApp.git
```
2. Open in Android Studio.
3. Build and run on an emulator or Android device.

## Usage

* Launch the app to see a grid of random dog images.
* Swipe down to refresh images.
* Tap on any dog image for a detailed view.

## Technologies
* Kotlin
* Android Jetpack (ViewModel, LiveData, Navigation)
* Retrofit & Moshi (Networking & JSON parsing)
* Dagger Hilt (Dependency Injection)
* Glide (Image loading)
* Data Binding

## Testing

* Unit and instrumented tests included in /app/src/test and /app/src/androidTest.

## License
Specify your license here if applicable.
