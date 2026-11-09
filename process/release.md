# Release parameters (release-seal)

The generic steps live in the global `release-seal` skill. This file only records the parameters and exceptions for this project.

## Version

- This is a set of demo apps, not a library: there is no JitPack coordinate and no `maven-publish`.
- Semantic versioning `X.Y.Z`. Bump `versionName` (and `versionCode` by 1) in `MVC/build.gradle`, `MVP/build.gradle` and `MVVM/build.gradle` in the release commit.

## Pre-release checks

```bash
./gradlew testDebugUnitTest assembleDebug lintDebug
```

Also run the three apps on a real device once: query a city (loading, then result) and query with an empty city (error).

## Tag

- Bare version tag without a `v` prefix: `2.0.0`. Annotated, message `MVX X.Y.Z`, created on the release commit and pushed to `origin`.

## GitHub Release

- Title is the version number; notes in English.
- Attachments (optional): `MVC-X.Y.Z.apk`, `MVP-X.Y.Z.apk`, `MVVM-X.Y.Z.apk`, from `./gradlew assembleDebug` (`<module>/build/outputs/apk/debug/<module>-debug.apk`), signed with the local debug certificate.

## Afterwards

- No product doc or ROADMAP; the GitHub Release is the source of truth.
