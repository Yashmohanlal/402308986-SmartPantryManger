package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, "pantry.db", null, 1);
        this.context = context.getApplicationContext();
    }

    @Override public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        // Quantities are decimal text so the matcher can compare them without rounding errors.
        db.execSQL("CREATE TABLE pantry (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity TEXT NOT NULL, unit TEXT NOT NULL, expiry TEXT NOT NULL DEFAULT '')");
        db.execSQL("CREATE TABLE recipes (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (_id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL REFERENCES recipes(_id) ON DELETE CASCADE, name TEXT NOT NULL, quantity TEXT NOT NULL, unit TEXT NOT NULL)");
        seedRecipes(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        // SQLiteOpenHelper calls onCreate only once, inside its creation transaction.
        try (InputStream input = context.getAssets().open("recipes.json");
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            JSONArray recipes = new JSONArray(output.toString(StandardCharsets.UTF_8.name()));
            for (int i = 0; i < recipes.length(); i++) {
                JSONObject recipe = recipes.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("name", recipe.getString("name"));
                values.put("steps", recipe.getString("steps"));
                long id = db.insertOrThrow("recipes", null, values);
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                for (int j = 0; j < ingredients.length(); j++) {
                    JSONObject item = ingredients.getJSONObject(j);
                    values = new ContentValues();
                    values.put("recipe_id", id);
                    values.put("name", item.getString("name"));
                    values.put("quantity", item.getString("quantity"));
                    values.put("unit", item.getString("unit"));
                    db.insertOrThrow("recipe_ingredients", null, values);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load bundled recipes", e);
        }
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 has no migration yet. Future upgrades must preserve pantry records.
        throw new IllegalStateException("Add a migration before changing the database version");
    }

    public long saveIngredient(Ingredient item) {
        ContentValues values = new ContentValues();
        values.put("name", RecipeMatcher.normalise(item.name));
        values.put("quantity", item.quantity.toPlainString());
        values.put("unit", item.unit);
        values.put("expiry", item.expiry);
        if (item.id == -1) return getWritableDatabase().insertOrThrow("pantry", null, values);
        return getWritableDatabase().update("pantry", values, "_id = ?", new String[]{String.valueOf(item.id)}) == 1 ? item.id : -1;
    }

    public void deleteIngredient(long id) {
        getWritableDatabase().delete("pantry", "_id = ?", new String[]{String.valueOf(id)});
    }

    public List<Ingredient> getPantry() {
        List<Ingredient> items = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT _id,name,quantity,unit,expiry FROM pantry ORDER BY name COLLATE NOCASE, _id", null)) {
            while (c.moveToNext()) items.add(new Ingredient(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)));
        }
        return items;
    }

    public Ingredient getIngredient(long id) {
        for (Ingredient item : getPantry()) if (item.id == id) return item;
        return null;
    }

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT _id,name,steps FROM recipes ORDER BY name", null)) {
            while (c.moveToNext()) {
                Recipe r = new Recipe(c.getLong(0), c.getString(1), c.getString(2));
                try (Cursor rows = db.rawQuery("SELECT _id,name,quantity,unit FROM recipe_ingredients WHERE recipe_id = ? ORDER BY _id", new String[]{String.valueOf(r.id)})) {
                    while (rows.moveToNext()) r.ingredients.add(new Ingredient(rows.getLong(0), rows.getString(1), rows.getString(2), rows.getString(3), ""));
                }
                recipes.add(r);
            }
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        for (Recipe recipe : getRecipes()) if (recipe.id == id) return recipe;
        return null;
    }
}
