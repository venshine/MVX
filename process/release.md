# Release parameters (release-seal)

The generic steps live in the global `release-seal` skill. This file only records the parameters and exceptions for this project.

## Version

- This is one demo app (six architectures in one APK), not a library: there is no JitPack coordinate and no `maven-publish`.
- Semantic versioning `X.Y.Z`. Bump `versionName` (and `versionCode` by 1) in `app/build.gradle` in the release commit. It is the only place the version lives; the library modules have none and the home pages read it at runtime.

## Pre-release checks

```bash
./gradlew testDebugUnitTest assembleDebug lintDebug
```

Also run the app on a real device once and check each of the six architectures from the home page: query a city (loading, then result), query with an empty city (error), turn on "Simulate failure" (error), and toggle light / dark mode on the home page.

## Tag

- Bare version tag without a `v` prefix: `2.1.0`. Annotated, message `MVX X.Y.Z`, created on the release commit and pushed to `origin`.

## GitHub Release

- Title is the version number; notes in English.
- Attachment (optional): `MVX-sample-X.Y.Z.apk` (pattern `<Repo>-sample-X.Y.Z.apk`), from `./gradlew :app:assembleDebug` (`app/build/outputs/apk/debug/app-debug.apk`), signed with the local debug certificate.

## Afterwards

- No product doc or ROADMAP; the GitHub Release is the source of truth.
