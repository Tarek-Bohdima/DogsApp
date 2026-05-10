# Contributing to DogsApp

Practical guide for working in this repo. For internal architecture see [ARCHITECTURE.md](ARCHITECTURE.md); for agent context see [CLAUDE.md](CLAUDE.md).

## Branch and PR workflow

- Branch off `master`. Pattern: short kebab-case (`offline-first-room`, `favorites`, `upgrade-java-17`, `modernize-stack`).
- One PR per issue. Use `Closes #N` in the PR body so the issue auto-closes on merge.
- **Squash-merge** to `master`, delete the branch on merge. This keeps history one-line-per-feature; the PR description is the source of truth for the change.
- Don't force-push to `master`.
- CI runs on push to `master` and on PRs (`.github/workflows/build_pull_request.yml`). It must be green before merging.

## Commit messages

Imperative, terse subject (< 70 chars) plus body for non-obvious "why". Example pattern from the repo:

```
Cache dogs list with Room so the app works offline

Closes #12

The repository now exposes the dogs list as LiveData backed by Room.
Refresh hits the network and replaces the cached rows in a single
transaction; on network failure, previously-cached dogs stay visible
and only the status flips to ERROR.
```

Subject says **what** changed, body explains **why** or names the load-bearing tradeoffs.

## Staging hygiene

Stage files explicitly (`git add <paths>`). Never `git add -A` or `git add .`. The repo has personal Claude Code settings in `.claude/settings.local.json` (gitignored) — explicit staging is the belt-and-suspenders default for not leaking anything.

## How to add a feature

For anything that touches both data and UI (a new screen, a new field, a new persistence concern), step through the layers in this order:

1. **Domain model.** Add or extend the `data/domain/` types. Pure data, no Android types.
2. **Persistence.** New entity + DAO in `data/local/`. Add a `Migration(N, N+1)` and bump `@Database(version = ...)` — never `fallbackToDestructiveMigration`.
3. **Repository.** Extend the `DogsRepository` interface and its impl. Return `Flow` from observation methods, `suspend` from one-shot operations. The repo is the only place that touches both the network and Room.
4. **DI.** Wire any new DAO or service in `ApplicationModule`. New abstractions get an interface and a `@Provides` that returns the interface.
5. **ViewModel.** New `@HiltViewModel`. Expose state as `StateFlow` via `stateIn(viewModelScope, WhileSubscribed(5_000), initial)`. Use `SavedStateHandle` for nav args.
6. **Fragment.** View Binding (`_binding`/nullable getter pattern, null in `onDestroyView`). Collect StateFlows under `repeatOnLifecycle(STARTED)`. Never import `coil3.*` directly — use `ImageLoader`.
7. **Navigation.** Update `res/navigation/navgraph.xml`. New nav args go in there with their argType. If args are Parcelable, the domain class needs `@Parcelize`.
8. **Tests.** Cover the new ViewModel and repository code (see [Testing](#testing) below). Update existing tests if the fakes' surface changed.

## Conventions worth knowing

- **Code to abstraction.** Depend on `DogsRepository`, `ImageLoader`, `RefreshManager` (interfaces) — never the `*Impl` classes. The DI module is the only place that names concrete implementations.
- **Single Hilt module.** All `@Provides` live in `ApplicationModule`. No new modules unless we genuinely split into Gradle modules.
- **Pure helpers as top-level extensions.** `Dog.displayBreedName()` lives next to the `Dog` data class. Keeps view code clean and makes the logic unit-testable without instrumentation.
- **No DataBinding XML expressions.** If you'd reach for `@{}` or a `<data>` block, write the binding logic inline in the fragment or extract a tiny pure helper instead.
- **No Kapt.** If a library only ships a Kapt processor, find a KSP fork or pick something else.

## Testing

Run all unit tests:

```bash
./gradlew test
```

Run a single class or case:

```bash
./gradlew :app:testDebugUnitTest --tests "com.example.android.dogsapp.ui.main.MainViewModelTest"
./gradlew :app:testDebugUnitTest --tests "*.MainViewModelTest.dogs flow mirrors the repository"
```

What's in `app/src/test/`:

- `MainDispatcherRule` — sets `Dispatchers.setMain(UnconfinedTestDispatcher())`. Pair it with `runTest { … }` for any test that touches `viewModelScope`.
- `fakes/` — hand-rolled fakes for every DI surface. Reuse and extend; **do not** add a mocking framework.
- `data/repository/DogsRepositoryImplTest` — example pattern for testing flows with both `.first()` (current value) and Turbine `.test { awaitItem() }` (emission sequences).
- ViewModel tests use `viewModel.someStateFlow.value` for one-shot assertions and Turbine for emission sequences. Because `StateFlow` starts at its initial value and may emit upstream once subscribed, tests sometimes guard with `if (first.isEmpty()) awaitItem() else first` — that's intentional and not a bug.

Every PR should include tests for the new logic. The repo treats "this PR adds tests" as a hard expectation, not a stretch goal.

## Manual smoke testing

Build and install on a connected emulator/device:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.android.dogsapp/.ui.MainActivity
```

For UI changes, do at minimum:

- Cold start with network → list loads.
- Pull to refresh → list updates, no crash.
- Favorite a dog from details, navigate away, return → heart stays filled.
- Open the favorites screen → favorited dog is there.
- Airplane mode + relaunch → cached list and cached images render (the offline-first contract). To toggle network: `adb shell svc wifi disable && adb shell svc data disable`.

## PR checklist

- [ ] `./gradlew assembleDebug` passes locally
- [ ] `./gradlew test` passes locally
- [ ] Tests added/updated for new logic
- [ ] Manual smoke on the affected flows (UI changes only)
- [ ] README/screenshots updated if anything user-visible shifted
- [ ] GitHub repo description and topics updated if the stack shifted (use `gh repo edit --description ...` / `--add-topic ...`)

## Issue and PR templates

There's an issue template at `.github/ISSUE_TEMPLATE/`. There's no PR template — the convention is a `## Summary` section with bullets, an optional `### Things worth a second look` section for non-obvious tradeoffs, a `## Tests` section, and a `## Test plan` checklist that closes with whatever manual verification still needs to happen.
