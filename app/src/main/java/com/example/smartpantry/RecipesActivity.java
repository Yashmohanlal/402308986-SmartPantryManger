package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecipesActivity extends BaseActivity {
    private Switch showAll;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup(R.layout.activity_recipes);
        setupNavigation();
        showAll = findViewById(R.id.showAll);
        if (savedInstanceState != null) showAll.setChecked(savedInstanceState.getBoolean("show_all"));
        showAll.setOnCheckedChangeListener((button, checked) -> refresh());
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("show_all", showAll.isChecked());
        super.onSaveInstanceState(outState);
    }

    @Override protected void onResume() { super.onResume(); refresh(); }

    private void refresh() {
        List<Ingredient> pantry = database.getPantry();
        List<Recipe> visible = new ArrayList<>();
        for (Recipe recipe : database.getRecipes()) {
            if (showAll.isChecked() || RecipeMatcher.canMake(recipe, pantry, LocalDate.now())) visible.add(recipe);
        }
        ((TextView) findViewById(R.id.heading)).setText(showAll.isChecked() ? "Recipe collection" : "Suggested recipes");
        ((TextView) findViewById(R.id.recipeHelp)).setText(showAll.isChecked()
                ? "All recipes for browsing. Some need ingredients you do not have."
                : "Only recipes with every ingredient in enough quantity. Expired entries are excluded.");
        ((TextView) findViewById(R.id.summary)).setText(visible.size() + (showAll.isChecked() ? " recipes in collection" : " recipes you can make now"));
        ListView list = findViewById(R.id.list);
        list.setEmptyView(findViewById(R.id.empty));
        list.setAdapter(new ArrayAdapter<Recipe>(this, 0, visible) {
            @Override public View getView(int position, View recycled, ViewGroup parent) {
                View row = recycled == null ? LayoutInflater.from(getContext()).inflate(R.layout.row_item, parent, false) : recycled;
                Recipe recipe = getItem(position);
                ((TextView) row.findViewById(R.id.rowTitle)).setText(recipe.name);
                boolean ready = RecipeMatcher.canMake(recipe, pantry, LocalDate.now());
                ((TextView) row.findViewById(R.id.rowSubtitle)).setText(recipe.ingredients.size() + " ingredients | " + (ready ? "Ready to cook" : "Missing or insufficient ingredients"));
                return row;
            }
        });
        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", visible.get(position).id);
            startActivity(intent);
        });
    }
}
