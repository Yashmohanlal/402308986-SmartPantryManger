package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.graphics.Insets;

public class BaseActivity extends Activity {
    protected DatabaseHelper database;

    protected void setup(int layout) {
        setContentView(layout);
        database = new DatabaseHelper(this);
        View root = findViewById(R.id.root);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        // Android 15+ uses edge-to-edge: keep controls clear of system bars and the keyboard.
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets bars = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.ime());
                v.setPadding(padding + bars.left, padding + bars.top, padding + bars.right, padding + bars.bottom);
                return insets;
            });
            root.requestApplyInsets();
        }
    }

    protected void setupNavigation() {
        findViewById(R.id.navPantry).setOnClickListener(v -> navigate(MainActivity.class));
        findViewById(R.id.navRecipes).setOnClickListener(v -> navigate(RecipesActivity.class));
        findViewById(R.id.navSettings).setOnClickListener(v -> navigate(SettingsActivity.class));
    }

    private void navigate(Class<?> screen) {
        if (getClass() == screen) return;
        Intent intent = new Intent(this, screen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        if (!(this instanceof MainActivity)) finish();
    }

    @Override protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
