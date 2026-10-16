# Smart Pantry Manager

A simple Java/XML Android app for tracking leftover ingredients and finding recipes that can be made with exactly what is available.

**Student:** Yash Mohanlal — 402308986  
**Module:** Mobile App Development 700  
**Approach:** Android Views (the same Java/XML approach as Empty Views Activity), five Activities, custom ListView adapter, local SQLite.

## Open and run in Android Studio

1. Extract the ZIP. Choose **Open** in Android Studio and select the **SmartPantryManager** folder containing `settings.gradle`. Open the complete project; you do not need to create another Empty Views Activity project or paste files into one.
2. Install Android SDK Platform **36** through Tools > SDK Manager if prompted.
3. In Settings > Build, Execution, Deployment > Build Tools > Gradle, set **Gradle JDK to JDK 21** (JDK 17 also meets AGP's minimum, but this project was tested with 21). Do not select JDK 25 for this Gradle version. Download/select 21 in that menu if needed.
4. Let Gradle sync. The first sync needs internet to download build tools; the app itself works offline.
5. Choose an emulator or USB-connected Android device running **Android 8.0 / API 26 or newer**. Select the `app` configuration and press Run.

Build tools are pinned: Android Gradle Plugin 8.13.2, Gradle 8.13, Java source level 17, compile/target SDK 36. The wrapper is included. The project has no external app libraries, Firebase setup, API keys, location permissions or account requirements.

Command line (with JAVA_HOME pointing to JDK 21):

```sh
./gradlew assembleDebug lintDebug
```

Windows: `gradlew.bat assembleDebug lintDebug`.

The APK is created in `app/build/outputs/apk/debug/app-debug.apk`. A tested debug APK is also supplied separately in the delivery package. Do not commit `local.properties`, `.gradle`, `.idea` or build folders.

## Features and screens

- **My pantry:** database-backed ListView, add button, tap a row to edit/delete, optional expiry reminders.
- **Add/Edit ingredient:** name, positive quantity, unit and optional ISO date. Validation runs before saving. Delete asks for confirmation.
- **Suggested recipes:** strict matches only, refreshed on return. Explicit feedback when none match.
- **Recipe detail:** full quantities and preparation steps, plus current pantry readiness.
- **Settings:** persisted expiry-reminder preference and matching explanation.
- A separate **Browse all 20 recipes** toggle changes the screen title to Recipe collection; unavailable recipes there are explicitly labelled. This does not change the strict suggestions rule.

## Database choice and design

SQLiteOpenHelper is appropriate because this is a small, single-device pantry. Data stays on the device, works offline, and needs no server or paid service.

- `pantry(_id, name, quantity, unit, expiry)` stores user entries.
- `recipes(_id, name, steps)` stores 20 bundled recipes.
- `recipe_ingredients(_id, recipe_id, name, quantity, unit)` stores each requirement. `recipe_id` references `recipes._id`.

The JSON asset is imported in `onCreate`, only when the database is first created. Quantities are stored as decimal text and compared with BigDecimal. Form values are passed with ContentValues; record IDs use selection arguments. A future schema version needs a real migration; the app deliberately does not drop user data.

## Matching rules and limitations

1. Trim and lowercase ingredient names and map common plurals (tomatoes, potatoes, eggs, onions, carrots, bananas, apples).
2. Convert compatible units: kg/g, l/ml and standard 5 ml teaspoons / 15 ml tablespoons.
3. Exclude entries whose expiry date is before today's local date. An entry dated today still counts. Dates are optional; check food condition yourself.
4. Sum duplicate pantry entries and duplicate recipe requirements separately.
5. Reject the recipe immediately if any requirement is missing or below its required amount. BigDecimal prevents a tiny shortfall from being rounded into a match.

No ingredient is assumed, including water and oil. `piece` means one item (one slice for bread). Pieces cannot be converted to grams, and mass cannot be converted to volume without density information. Names such as `cooked rice` and `rice` are different. The small alias list is intentionally not a full language parser. Cooking does not automatically deduct inventory.

This small teaching app makes short local database calls on the main thread. For large inventories, move these operations to a background executor and add pagination. It currently supports English only. Expiry reminders are in-app labels, not push notifications.

## Code guide

- `MainActivity.java`: lifecycle refresh and intents to the form.
- `IngredientActivity.java`: input validation, insert/update and delete confirmation.
- `RecipesActivity.java`: strict filter, collection toggle and recipe-row adapter.
- `RecipeDetailActivity.java`: fetch the recipe ID from the intent and display details.
- `SettingsActivity.java`: SharedPreferences.
- `BaseActivity.java`: shared navigation and system-bar/keyboard spacing.
- `DatabaseHelper.java`: schema, seed and CRUD.
- `RecipeMatcher.java`: plain Java business logic, independently testable.
- `PantryAdapter.java`: binds each ingredient to the custom XML row.

## Matching tests

Run from the project root using a JDK:

```sh
mkdir -p build/matcher-tests
javac -d build/matcher-tests app/src/main/java/com/example/smartpantry/Ingredient.java app/src/main/java/com/example/smartpantry/Recipe.java app/src/main/java/com/example/smartpantry/RecipeMatcher.java tests/RecipeMatcherTest.java
java -cp build/matcher-tests com.example.smartpantry.RecipeMatcherTest
```

The runner checks 24 cases including missing ingredients, exact/insufficient amounts, duplicates, compatible/incompatible units, expiry, aliases and decimal arithmetic. See `TEST_RESULTS.md` for the actual local build and emulator checks.

## GitHub and submission

The included local `.git` history records genuine incremental **Codex-assisted development on 16 October 2026**. It is not evidence that the student developed the project over several days. Do not backdate commits or present this session as a different development history.

Create an empty GitHub repository named `SmartPantryManager` in your own account. Then run, replacing YOUR_USERNAME:

```sh
git remote add origin https://github.com/YOUR_USERNAME/SmartPantryManager.git
git push -u origin main
```

If a remote already exists, inspect it with `git remote -v` instead of adding a second one. Use GitHub's normal sign-in/token flow; never put a token in a file or remote URL. Add the actual accessible repository URL to the report. The assignment asks for public visibility; alternatively follow the lecturer's collaborator instructions.

Review, run and explain the app yourself. Record the required 5–7 minute video in your own voice; `VIDEO_GUIDE.md` provides a timed plan. Complete the cover date/signature and personal reflection. Include the source, report and your MP4 in one ZIP under 50 MB, named `402308986_Mohanlal_MobileAppDev700_Assignment.zip`. The supplied ZIP is a working package until those remaining items are completed.

## Assistance and references

Implementation, testing and report preparation were assisted by OpenAI Codex. Acknowledge this assistance according to your institution's rules. Do not sign a declaration that misrepresents authorship or understanding.

Google (n.d.) *SQLiteOpenHelper*. https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper (Accessed: 16 October 2026).

Google (n.d.) *The activity lifecycle*. https://developer.android.com/guide/components/activities/activity-lifecycle (Accessed: 16 October 2026).

Google (n.d.) *ArrayAdapter*. https://developer.android.com/reference/android/widget/ArrayAdapter (Accessed: 16 October 2026).

Google (n.d.) *Intents and intent filters*. https://developer.android.com/guide/components/intents-filters (Accessed: 16 October 2026).
