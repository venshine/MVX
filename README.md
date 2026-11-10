# MVX

[中文](README-CN.md)

One screen, six architectures, **one app**. The home page lists MVC, MVP, MVVM, MVI, UDF and MV; each opens the **same business and the same look** (a simulated weather query: type a city, tap Query, see loading, then a result or an error), written six ways so you can read them side by side. There is also a Compare page. It is a demo app, not a library.

- Version 2.1.0, minSdk 23, targetSdk 36, Kotlin, coroutines.
- `mvc`, `mvp`, `mvvm`, `mvi` use Views (AppCompat DayNight, ViewBinding where noted). `udf` and `mv` use Jetpack Compose built on `ui` / `foundation` / `runtime` only (BOM `2026.06.01`, Compose 1.11.4); **no Material Components and no Material 3** anywhere: the Compose modules draw the same cards, buttons, switch and input with `foundation`, using the same colors, fonts and dark mode.
- One APK, package `com.wx.mvx`. Each architecture lives in its own library module with its own package: `com.wx.mvc`, `com.wx.mvp`, `com.wx.mvvm`, `com.wx.mvi`, `com.wx.udf`, `com.wx.mv`.
- Data is **simulated** with a coroutine `delay` (2 s). An empty city fails (or turn on the "Simulate failure" switch), anything else returns fixed data. There is no real network call.

[**Download the sample APK**](https://github.com/venshine/MVX/releases/latest) (`MVX-sample-X.Y.Z.apk`, one file for all six)

## Project structure and how to read the code

```
app/          com.wx.mvx    the only installable module: home page, Compare page, registry of the six
core/         shared (Views)  weather data source, dark/light theme, colors, fonts, icons, shared layouts and strings
core-compose/ shared (Compose) the same look drawn with foundation, stateless WeatherContent
mvc/ mvp/ mvvm/ mvi/    View-based architecture libraries (depend on core only)
udf/ mv/                Compose-based architecture libraries (depend on core-compose only)
```

- The six architecture modules **do not depend on each other**: Gradle enforces it, so you can read one without the others. Each exposes a single entry `MainActivity`; the weather page is `internal`.
- To compare: open the same file in two modules (for example `mvvm/.../viewmodel/WeatherViewModel.kt` and `udf/.../viewmodel/WeatherViewModel.kt`), or use the Compare page in the app.
- The shared Model and look live in `core` / `core-compose` (one copy, not six). Architecture-specific strings are prefixed with the module name (`mvc_title`).

## ScreenShot

Taken on a Xiaomi 2509FPN0BC (Android 17) with the merged app (2.1.0).

<img src="screenshot/demo.gif" width="300" alt="MVX demo" />

| Home | Home (dark) | Compare |
|:---:|:---:|:---:|
| <img src="screenshot/home.png" width="200" /> | <img src="screenshot/home_dark.png" width="200" /> | <img src="screenshot/compare.png" width="200" /> |

| MVC | MVP | MVVM |
|:---:|:---:|:---:|
| <img src="screenshot/mvc_weather.png" width="200" /> | <img src="screenshot/mvp_weather.png" width="200" /> | <img src="screenshot/mvvm_weather.png" width="200" /> |

| MVI | UDF | MV |
|:---:|:---:|:---:|
| <img src="screenshot/mvi_weather.png" width="200" /> | <img src="screenshot/udf_weather.png" width="200" /> | <img src="screenshot/mv_weather.png" width="200" /> |

| MVI failure | UDF dark |
|:---:|:---:|
| <img src="screenshot/mvi_weather_error.png" width="200" /> | <img src="screenshot/udf_weather_dark.png" width="200" /> |

## The same business in six shapes

| | MVC | MVP | MVVM | MVI | UDF (Compose) | MV (Compose) |
|---|---|---|---|---|---|---|
| View | layout XML + Activity | `WeatherActivity` implements `WeatherView` | `WeatherActivity`, renders `WeatherUiState` | `WeatherActivity`, renders `WeatherState`, shows `WeatherEffect` | composables: `WeatherScreen` collects, `WeatherContent` is stateless | composables: `WeatherScreen` reads the holder, `WeatherContent` is stateless |
| Who handles the click | Activity (also the Controller) | Activity forwards to `Presenter.getWeather()` | Activity forwards to `ViewModel.query()` | Activity sends `WeatherIntent.Query` to `ViewModel.dispatch()` | `WeatherContent` calls `onQuery`, hoisted to `ViewModel.query()` | `WeatherContent` calls `onQuery`, hoisted to `WeatherHolder.query()` |
| Who calls the Model | Activity, in `lifecycleScope` | `WeatherPresenter` | `WeatherViewModel`, in `viewModelScope` | `WeatherViewModel`, in `viewModelScope` | `WeatherViewModel`, in `viewModelScope` | `WeatherHolder`, in the composition's coroutine scope |
| Who produces the next state | Activity, directly on Views | Presenter calls `WeatherView.showXxx()` | ViewModel assigns a new `WeatherUiState` | pure `WeatherReducer.reduce(state, change)` | ViewModel assigns a new `WeatherUiState` | holder assigns a new `WeatherUiState` |
| Where the state lives | in the Views | in the Presenter and the Views | `StateFlow` in the ViewModel | one immutable `WeatherState` in the ViewModel's `StateFlow` | `StateFlow<WeatherUiState>` in the ViewModel | `mutableStateOf` in a plain class kept by `rememberSaveable` |
| How the UI gets it | reads Views | Presenter pushes | `repeatOnLifecycle(STARTED)` + `collect` | `repeatOnLifecycle(STARTED)` + `collect` | `collectAsStateWithLifecycle()` | reads the `State` while composing |
| One-off events | Toast in Activity | Toast via View method | not used (status line instead) | `Effect` through a `Channel` (Toast) | not used (status line instead) | not used |
| Survives rotation | no | no | yes (ViewModel) | yes (ViewModel) | yes (ViewModel) | finished result yes (Bundle); query in flight no |
| How you test it | Robolectric | plain JVM: fake View + fake Model | plain JVM: fake Model | plain JVM: Reducer without coroutines; ViewModel with a fake Model | plain JVM: fake Model | plain JVM: holder with a test scope |
| Fits | tiny screen or prototype | testable logic without Jetpack, legacy MVP code | default for View-based apps | complex screens with many events and one state to audit | default for new Compose code (official shape) | small, self-contained composable screens |

Data flow:

```
MVC   click -> Activity -> Model            Model -> Activity -> Views
MVP   click -> Activity -> Presenter -> Model        Model -> Presenter -> View interface -> Activity
MVVM  click -> Activity -> ViewModel -> Model        Model -> ViewModel -> StateFlow(UiState) -> Activity renders
MVI   click -> Intent -> ViewModel -> Change -> Reducer -> new State -> Activity renders     (+ Effect -> Toast)
UDF   click -> onQuery -> ViewModel.query() -> Model        Model -> StateFlow(UiState) -> collectAsStateWithLifecycle -> recompose
MV    click -> onQuery -> WeatherHolder.query() -> Model    Model -> mutableStateOf -> recompose
```

Shared model layer (one copy in `core`, used by all six modules): `WeatherModel` (a `suspend` interface returning `Result<WeatherInfo>`), `WeatherModelImpl(delayMillis = 2000)` (the simulated source; inject a different delay or a fake in tests) and the `WeatherInfo` data class. The look is shared too: the same `colors.xml` (light and dark), fonts, vector icons and strings; the Compose apps read them through `colorResource`, `painterResource` and `stringResource`. Compose has no ripple here (no Material), so pressed states are not animated.

### MVI, UDF and MV in this repository

- **MVI** (`mvi/`, View-based): one immutable `WeatherState`; the only way in is `dispatch(WeatherIntent)`; the ViewModel turns an Intent into a `WeatherChange` and `WeatherReducer` (a pure function, tested without coroutines) computes the next state; a failure also sends a one-shot `WeatherEffect.ShowFailure` through a `Channel`, shown as a Toast.
- **UDF** (`udf/`, Compose): the shape the official guide describes for Compose. `WeatherViewModel` exposes `uiState: StateFlow<WeatherUiState>`, the screen collects it with `collectAsStateWithLifecycle()`, and the only event is the method call `query(city, simulateFailure)`. The text field and the switch are UI element state, kept in the composable with `rememberSaveable`.
- **MV** (`mv/`, Compose, no `ViewModel`): the state sits in a plain class `WeatherHolder` (`mutableStateOf`), created by `rememberWeatherHolder()` with `rememberSaveable`. Details and limits are in the section below.

### About the names MVI and MV

MVC, MVP, MVVM, MVI and MV are community names for ways of arranging UI code. I found no mention of **MVI** or **MV** in the official pages listed in the next section (and none of them uses MVC / MVP / MVVM as a pattern name either). So "MVI" here means the community pattern (Model-View-Intent: a single state, intents in, a reducer) and "MV" means "state lives next to the view, without a ViewModel"; neither is an Android recommendation. What the official pages define are *unidirectional data flow*, *UI state*, *state holders* (a `ViewModel` or a plain class), and *events*.

### When to use which

- **MVC**: a tiny screen or a throwaway prototype. Cheap, but the Activity grows fast and is hard to test.
- **MVP**: you want the logic testable on a plain JVM without moving to Jetpack, or you maintain an existing MVP code base. You must handle attach/detach and state across rotation yourself.
- **MVVM** (`ViewModel` + `StateFlow`, Views): the default for View-based code. State survives configuration changes and the UI is a function of one state object.
- **MVI**: the same as MVVM, plus a stricter discipline (one state, one entry point, a pure reducer). Worth it when a screen has many events and you want every transition in one testable function. It adds ceremony (Intent, Change, Reducer) that a small screen does not need.
- **UDF in Compose**: the default for new Compose code, and what the official guide recommends: a `ViewModel` at screen level, `StateFlow` UI state, events as method calls, lifecycle-aware collection.
- **MV (no ViewModel)**: small, self-contained screens or reusable pieces of UI whose state does not need to outlive the screen and has no long-running work to protect. Not the official recommendation for screen-level state (see below).

## Relation to the officially recommended architecture

The sources below were read on 2026-11-10:

- [Guide to app architecture](https://developer.android.com/topic/architecture): separation of concerns, driving the UI from data models, a single source of truth and **unidirectional data flow** (state flows down, events flow up); a UI layer of UI elements plus state holders such as `ViewModel`, and a data layer.
- [UI layer](https://developer.android.com/topic/architecture/ui-layer): immutable UI state; "The `ViewModel` type is the recommended implementation for the management of screen-level UI state with access to the data layer", while "a simple class might suffice" in some cases; expose UI state in an observable holder such as `StateFlow`; consume it with `collectAsStateWithLifecycle`.
- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations): "Follow Unidirectional Data Flow" and "Use AAC ViewModels if their benefits apply to your app" are *strongly recommended*; "Use ViewModels at screen level", "Use plain state holder classes in reusable UI components" and lifecycle-aware collection are *strongly recommended*; "Do not send events from the ViewModel to the UI" is *strongly recommended*; "Expose a UI state" (single `uiState`, `StateFlow`) is *recommended*.
- [State holders and UI State](https://developer.android.com/topic/architecture/ui-layer/stateholders): UI-logic state holders are plain classes that "do not survive `Activity` recreation" by themselves and delegate to `rememberSaveable`; business-logic state holders are normally a `ViewModel`, which retains state through activity recreation.
- [State and Jetpack Compose](https://developer.android.com/develop/ui/compose/state) and [Architecting your Compose UI](https://developer.android.com/develop/ui/compose/architecture): `remember` does not survive configuration changes, `rememberSaveable` does (saved in a `Bundle`); unidirectional data flow fits Compose.
- [UI events](https://developer.android.com/topic/architecture/ui-layer/events): "ViewModel events should always result in a UI state update"; exposing ViewModel events through Kotlin `Channel`s is described as something that does not guarantee delivery when the producer outlives the consumer.

How each app relates to that:

- **UDF** is the closest to the recommendation (screen-level `ViewModel`, `StateFlow<UiState>`, method-call events, `collectAsStateWithLifecycle`, stateless content composable below the screen).
- **MVVM** is the same idea for Views (collect with `repeatOnLifecycle(STARTED)`).
- **MVI** keeps the one-shot `Effect` because that is part of the MVI pattern, but this is **against** the official "Do not send events from the ViewModel to the UI" recommendation. The official alternative is a field in the UI state that the UI clears after showing it (this repo's MVVM / UDF apps avoid the problem by showing failures in the status line instead of a Toast). Treat the Channel as a demonstration of the pattern, not as a recommended default.
- **MV** departs from the recommendation for screen-level state: the guide recommends a `ViewModel` there. It is within the guide's wording only where a plain state holder "might suffice" (short-lived UI state and UI logic, reusable components). Behaviour on this demo's single screen, checked by the Robolectric tests `recreateKeepsAFinishedResult` and `recreateWhileLoadingDropsTheQuery`:
  - Activity recreation (rotation): a finished result and the typed city are restored from the `Bundle`.
  - A query **in flight** is lost on rotation: the coroutine scope belongs to the composition and is cancelled, and the state saved while loading is restored as idle. A `ViewModel` keeps the running job.
  - Process death: only what `rememberSaveable` saved (finished result, typed city) comes back; there is no ongoing work to resume.
  - Process death also drops the in-memory state of the ViewModel apps unless they use `SavedStateHandle`, which none of these demos do.
- What this demo omits: a repository/data layer and dependency injection (Hilt is recommended by the guide); the Model here is a single fake data source passed by constructor. Navigation is plain Activities rather than Navigation 3, to keep the six implementations comparable.

## Why ViewBinding instead of DataBinding in MVVM

The 1.x MVVM module used DataBinding (XML expressions plus `BaseObservable`). 2.0 uses ViewBinding:

- The `ViewModel` + `StateFlow` pattern does not need layout variables or expressions; the Activity renders one state object. ViewBinding "doesn't support layout variables or layout expressions" and two-way binding, which this demo doesn't use. [View binding](https://developer.android.com/topic/libraries/view-binding)
- ViewBinding has faster compilation (no annotation processing) and is type- and null-safe compared with `findViewById` (same page). The page says data binding still has a place for advanced layouts, so this is a choice for this demo, not a statement that DataBinding is removed.
- Keeping logic out of XML makes the MVC / MVP / MVVM layouts identical, which is the point of the comparison.

## Build and test

```bash
./gradlew testDebugUnitTest assembleDebug lintDebug
```

- Plain JVM tests: MVP `WeatherPresenterTest`; MVVM, UDF and MVI `WeatherViewModelTest` (state sequences with `kotlinx-coroutines-test`; MVI also checks the Effect is delivered once); MVI `WeatherReducerTest` (pure function, no coroutines); MV `WeatherHolderTest` (state sequences and the saver).
- Each architecture module has Robolectric smoke tests (entry and weather page in light and dark mode, theme toggle, loading, success, empty-city and simulated failure). The `app` module walks the whole path: home page, into each of the six, query success and failure, light and dark, and the Compare page. The Compose apps use `ui-test-junit4` with Robolectric and find nodes by `testTag` (same names as the View ids). UDF and MV also recreate the Activity while idle or loading; MVI checks the Toast.
- Robolectric is pinned to SDK 35 (see `robolectric.properties` in each module).
- Compose 1.12 and later need `compileSdk 37` ([Compose compiler setup](https://developer.android.com/develop/ui/compose/compiler)); this repo stays on `compileSdk 36`, so it uses BOM `2026.06.01` (Compose 1.11.4). The Compose compiler plugin `org.jetbrains.kotlin.plugin.compose` is pinned to the Kotlin version built into AGP 9.4.1 (2.2.10), see [built-in Kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin).

## Migration

From 2.0 to 2.1: the three standalone apps (`MVC`, `MVP`, `MVVM`) became library modules and three more (`mvi`, `udf`, `mv`) were added; everything now ships as **one app** (`com.wx.mvx`, `versionCode` 3) with a home page and a Compare page. The old package names `com.wx.mvc` / `com.wx.mvp` / `com.wx.mvvm` are no longer installable applications, so there is no upgrade path from the 2.0.0 APKs: uninstall them first. The weather data source, theme, colors and fonts moved into the shared modules.

From 1.x:
- Java replaced by Kotlin; callbacks (`OnWeatherListener`) replaced by `suspend` + `Result`; `Handler.postDelayed` replaced by `delay`.
- MVVM: DataBinding removed, `BaseObservable` entity replaced by an immutable `WeatherInfo` and a `StateFlow`.
- Toasts for success / failure are now an on-screen status line (easier to test and no one-shot-event problem); only the MVI app still shows a Toast, on purpose.
- `settings.gradle` no longer references the non-existent `:app` module; the per-module README files were removed in favour of this one.

## Maintenance

Maintained as a small teaching demo. The standalone MVC / MVP / MVVM apps were run on a real device (Xiaomi 2509FPN0BC, Android 17) for 2.0.0: home page, light / dark toggle, query success, "Simulate failure" and the pages above were checked by hand. The merged app (2.1.0) was run on the same device: the home page, the Compare page, the six entry pages and the six weather pages in light and dark, query success and "Simulate failure" were checked by hand. Rotation was tried on the same device (portrait to landscape and back, 4 s after tapping Query): the MV page was back at idle ("Ready, tap Query") with the in-flight query gone, while the UDF page showed the finished result ("Loaded", 18-25, partly cloudy). That matches the table above. Only this one run on one device was observed. The empty-city error has **not** been checked on a real device yet.

## Credits

The app bundles these fonts, both licensed under the [SIL Open Font License 1.1](https://openfontlicense.org) (license texts in [`core/licenses/`](core/licenses)):
* [Space Grotesk](https://github.com/floriankarsten/space-grotesk): Copyright 2020 The Space Grotesk Project Authors
* [JetBrains Mono](https://github.com/JetBrains/JetBrainsMono): Copyright 2020 The JetBrains Mono Project Authors

All icons are hand-drawn vectors; no bitmap assets are used.

## License

Apache License 2.0, see [LICENSE](LICENSE).
