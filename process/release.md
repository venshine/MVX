# Release parameters (release-seal)

The generic steps live in the global `release-seal` skill. This file only records the parameters and exceptions for this project.

## Version

- This is a set of demo apps, not a library: there is no JitPack coordinate and no `maven-publish`.
- Semantic versioning `X.Y.Z`. Bump `versionName` (and `versionCode` by 1) in `MVC/build.gradle`, `MVP/build.gradle` and `MVVM/build.gradle` in the release commit.

## Pre-release checks

```bash
./gradlew testDebugUnitTest assembleDebug lintDebug
```

Also run the three apps on a real device once: query a city (loading, then result), query with an empty city (error), turn on "Simulate failure" (error), and toggle light / dark mode on the home page.

## Tag

- Bare version tag without a `v` prefix: `2.0.0`. Annotated, message `MVX X.Y.Z`, created on the release commit and pushed to `origin`.

## GitHub Release

- Title is the version number; notes in English.
- Attachments (optional): `MVX-MVC-sample-X.Y.Z.apk`, `MVX-MVP-sample-X.Y.Z.apk`, `MVX-MVVM-sample-X.Y.Z.apk` (pattern `<Repo>-sample-X.Y.Z.apk` with the module name added), from `./gradlew assembleDebug` (`<module>/build/outputs/apk/debug/<module>-debug.apk`), signed with the local debug certificate.

## Afterwards

- No product doc or ROADMAP; the GitHub Release is the source of truth.
