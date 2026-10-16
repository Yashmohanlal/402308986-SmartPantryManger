package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import java.time.LocalDate;
import java.util.List;

public class MainActivity extends BaseActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup(R.layout.activity_main);
        setupNavigation();
        findViewById(R.id.add).setOnClickListener(v -> startActivity(new Intent(this, IngredientActivity.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        // Refresh from SQLite after returning from an add, edit or delete operation.
        List<Ingredient> items = database.getPantry();
        ListView list = findViewById(R.id.list);
        list.setEmptyView(findViewById(R.id.empty));
        list.setAdapter(new PantryAdapter(this, items));
        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, IngredientActivity.class);
            intent.putExtra("ingredient_id", items.get(position).id);
            startActivity(intent);
        });
        String summary = items.size() + " pantry entries";
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("expiry_alerts", true)) {
            int soon = 0;
            for (Ingredient item : items) {
                if (!item.expiry.isEmpty() && !RecipeMatcher.isExpired(item, LocalDate.now())
                        && !LocalDate.parse(item.expiry).isAfter(LocalDate.now().plusDays(3))) soon++;
            }
            if (soon > 0) summary += " | " + soon + " expiring within 3 days";
        }
        ((TextView) findViewById(R.id.summary)).setText(summary);
    }
}
