# Verification results

Date: 16 October 2026. Checks were performed with Codex assistance on the supplied source.

## Build and static checks

- Android Gradle Plugin 8.13.2; Gradle 8.13; JDK 21.0.11; SDK Platform 36.
- `assembleDebug`: passed; debug APK generated and installed.
- `lintDebug`: passed, **0 errors and 14 warnings**. Remaining warnings concern localisation of Java display strings, newer tool/target versions, navigation button styling, form labels and the backup declaration. No lint baseline or blanket suppression was used to hide errors.
- Initial JDK 25 incompatibility was resolved by using JDK 21. An API 27-only theme item was moved into values-v27 to retain minSdk 26.

## Database inspection

The running app database was copied read-only using adb run-as after the workflow. It contained 20 recipes, 56 recipe-ingredient records and one remaining pantry record (egg, 2 piece). PRAGMA integrity_check returned ok, and PRAGMA foreign_key_check returned no violations. This also verified that repeated launches had not duplicated recipe seeds.

## Business logic

All **24** standalone Java checks passed. Raw output: `evidence/matcher-tests.txt`.

Cases cover empty pantry; missing ingredients; exact, surplus and insufficient quantities; case/whitespace/plural normalisation; g/kg and ml/l/tsp/tbsp conversion; incompatible dimensions; duplicate pantry entries; duplicate recipe requirements; no fuzzy substitutions; expiry yesterday/today; decimal sums; 0.001 shortfall; no assumed water; and empty recipe rejection.

## Emulator workflow

Device: locally installed Medium_Phone Android emulator (API 37 development system image), 1080 × 2400. Runtime checks on other Android versions and physical phones have not been performed.

- Empty pantry and zero-match feedback displayed.
- Empty ingredient name rejected with a visible field error.
- Zero quantity and the impossible date 2026-02-30 were rejected; neither attempt created a pantry record.
- Added Eggs, 2 piece; database list displayed normalised `egg`.
- Eggs alone did not suggest scrambled eggs.
- Added butter, 0.01 kg; scrambled eggs appeared as the sole strict match.
- Recipe details showed egg 2 piece, butter 10 g and preparation steps.
- Editing eggs to 1 removed the suggestion; editing back to 2 restored stock.
- Force-stopping and reopening retained both pantry records.
- Toggling expiry reminders off, force-stopping and reopening retained the off setting.
- Deleting butter through the confirmation dialog removed the row and returned suggestions to zero.
- Collection mode displayed all 20 recipes, clearly labelled separately from suggestions.

The initial automation harness needed corrections for emulator UI delays, hidden keyboard behaviour and the uppercase DELETE dialog label. These were harness issues; assertions and screenshots were reviewed against actual running screens. The emulator was slow under host memory pressure; idle build daemons were stopped to improve responsiveness.

## Scope and limitations

These checks verify the demonstrated workflow, not every possible device or accessibility setting. The app uses short synchronous SQLite calls suitable for this small teaching dataset. In a larger production app, database work should run off the main thread. English-only strings, explicit aliases and limited compatible-unit conversions are intentional simplifications.
