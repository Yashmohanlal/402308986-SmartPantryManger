package com.example.smartpantry;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Switch;

public class SettingsActivity extends BaseActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup(R.layout.activity_settings);
        setupNavigation();
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        Switch alerts = findViewById(R.id.expiryAlerts);
        alerts.setChecked(preferences.getBoolean("expiry_alerts", true));
        // A small preference belongs in SharedPreferences; pantry records belong in SQLite.
        alerts.setOnCheckedChangeListener((button, enabled) -> preferences.edit().putBoolean("expiry_alerts", enabled).apply());
    }
}
