# Six minute demonstration guide

Record your screen and your own voice. Aim for 6–6.5 minutes, within the required 5–7 minutes. Use the points below as cues and explain the code in your own words. Do not claim that the assisted commit history represents work you personally performed over a longer period.

## Before recording

Open Android Studio, the app, your published GitHub repository and its commit history. Rehearse once. Remove sample pantry entries through the app so your demonstration starts empty. The app currently has no reset-all button. Check microphone volume and readable text. Make sure your own GitHub URL is in the report.

## 0:00–1:00 GitHub walkthrough

Show the project name, README and actual commits. Explain what changed: Java project, models and matching, SQLite, screens, input validation, tests, build fixes and evidence. Describe assistance accurately. Do not invent dates, development duration or your personal involvement.

## 1:00–3:15 Live app demonstration

1. Open pantry; show the empty list and zero suggestions.
2. Attempt an empty save to show validation.
3. Add `Eggs`, quantity `2`, unit `piece`. Show the list normalises this to `egg`.
4. Open suggestions: scrambled eggs is excluded because butter is missing.
5. Add `butter`, quantity `0.01`, unit `kg`.
6. Open suggestions: scrambled eggs now appears, because 0.01 kg = 10 g.
7. Open its detail page and show the complete ingredient list and steps.
8. Edit egg quantity to `1`; the recipe disappears. Edit back to `2`.
9. Force-stop the app in Android settings or via Android Studio, reopen it, and show both entries still exist. Do not uninstall or clear app data.
10. Delete butter with the confirmation dialog. Show suggestions disappear again.
11. Show Settings, toggle the expiry reminder, and show the clearly separate Browse all recipes mode.

## 3:15–5:30 Explain three concepts

**Lifecycle:** Point to `MainActivity.onCreate` and `onResume`. The layout and listeners are set up at creation. The pantry is re-read when the Activity becomes active again, so returning from the form shows current database contents.

**SQLite and CRUD:** Point to `DatabaseHelper.onCreate`, `saveIngredient`, `getPantry` and `deleteIngredient`. Explain the three tables, the recipe foreign key, the one-time JSON seed, and why an ID of -1 means a new item. Explain how ContentValues and selection arguments separate values from SQL.

**Strict matching:** Point to `RecipeMatcher.canMake`, `normalise` and `baseAmount`. Explain the two maps, unit dimensions, decimal quantities and early `return false`. Give the missing butter and one-egg examples from the demonstration. Explain why 100 g of milk cannot automatically satisfy 100 ml.

Alternative concepts if preferred: show how PantryAdapter recycles a row and sets two TextViews; or show how an explicit Intent carries `ingredient_id` or `recipe_id`.

## 5:30–6:10 Database justification and reflection

Explain why local SQLite suits a small offline pantry without cloud accounts. Mention the trade-off: no synchronisation across phones. Describe something you actually learned while reviewing and running the project, and one future improvement such as background database operations or a larger alias list.

## Export and submit

Export an MP4 with H.264 video and AAC speech audio. A six-minute recording at about 600 kbps video plus 64 kbps audio is approximately 30 MB; actual size varies. Prefer readable 1080p where possible and preview the encoded result. Keep the final ZIP below 50 MB.

Put the MP4 directly in the final ZIP, alongside the source folder and completed report. A link or a silent recording does not meet the brief. Exclude build caches; the debug APK is optional and not a replacement for source code.
