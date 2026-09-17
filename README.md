# Aroeira Map

Offline-first Android field mapper for finding and recording aroeira trees.

## Current MVP

- Records the walk continuously in a foreground GPS service, including with the screen off.
- Draws the route and a 40 m-wide searched corridor (20 m on each side).
- Saves aroeira locations locally with one tap.
- Keeps tree markers between walks; **New walk** only clears the current trail.
- Uses no account, map provider, analytics, or internet connection.

The view is a metric field grid centered on the current position, deliberately avoiding a network map dependency. All data stays in the app's SQLite database.

## Build

Push to GitHub and download the `AroeiraMap-debug` artifact from the **Build Android APK** action, or run `gradle assembleDebug` with JDK 17 and Android SDK 35.

## Next practical additions

1. Export/import GPX or GeoJSON.
2. Notes and photographs per tree.
3. Offline OpenStreetMap tile packs.
4. Configurable search-corridor width and separate named expeditions.
