package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;
import java.time.LocalDate;

public class RecipeDetailActivity extends BaseActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup(R.layout.activity_recipe_detail);
        findViewById(R.id.back).setOnClickListener(v -> finish());
    }

    @Override protected void onResume() {
        super.onResume();
        Recipe recipe = database.getRecipe(getIntent().getLongExtra("recipe_id", -1));
        if (recipe == null) { finish(); return; }
        ((TextView) findViewById(R.id.heading)).setText(recipe.name);
        boolean ready = RecipeMatcher.canMake(recipe, database.getPantry(), LocalDate.now());
        ((TextView) findViewById(R.id.status)).setText(ready ? "Ready to cook with your pantry" : "Not ready: missing ingredients or quantity");
        StringBuilder ingredients = new StringBuilder();
        for (Ingredient item : recipe.ingredients) ingredients.append("• ").append(item.name).append(" — ").append(item.amount()).append("\n");
        ((TextView) findViewById(R.id.ingredients)).setText(ingredients.toString().trim());
        ((TextView) findViewById(R.id.method)).setText(recipe.steps);
    }
}
