package com.example.smartpantry;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class IngredientActivity extends BaseActivity {
    private EditText name, quantity, expiry;
    private Spinner unit;
    private long ingredientId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup(R.layout.activity_ingredient);
        name = findViewById(R.id.name);
        quantity = findViewById(R.id.quantity);
        expiry = findViewById(R.id.expiry);
        unit = findViewById(R.id.unit);
        ArrayAdapter<String> units = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, RecipeMatcher.UNITS);
        units.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unit.setAdapter(units);
        ingredientId = getIntent().getLongExtra("ingredient_id", -1);
        findViewById(R.id.delete).setVisibility(ingredientId == -1 ? View.GONE : View.VISIBLE);
        if (ingredientId != -1) {
            ((TextView) findViewById(R.id.heading)).setText("Edit ingredient");
            Ingredient item = database.getIngredient(ingredientId);
            if (item == null) { finish(); return; }
            // Android restores unsaved form fields itself after rotation.
            if (savedInstanceState == null) {
                name.setText(item.name);
                quantity.setText(item.quantity.toPlainString());
                expiry.setText(item.expiry);
                unit.setSelection(Arrays.asList(RecipeMatcher.UNITS).indexOf(item.unit));
            }
        }
        findViewById(R.id.save).setOnClickListener(v -> save());
        findViewById(R.id.cancel).setOnClickListener(v -> finish());
        findViewById(R.id.delete).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("This entry will be removed from your pantry.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    database.deleteIngredient(ingredientId);
                    Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }).show());
    }

    private void save() {
        String itemName = name.getText().toString().trim();
        String amount = quantity.getText().toString().trim();
        String date = expiry.getText().toString().trim();
        if (itemName.isEmpty() || !itemName.matches(".*[\\p{L}].*")) {
            name.setError("Enter an ingredient name containing letters");
            name.requestFocus();
            return;
        }
        // Reject empty, zero, negative and malformed amounts before touching the database.
        try {
            if (!amount.matches("[0-9]+(\\.[0-9]{1,3})?")
                    || new BigDecimal(amount).signum() <= 0
                    || new BigDecimal(amount).compareTo(new BigDecimal("1000000")) > 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            quantity.setError("Use 0.001 to 1000000, with at most 3 decimal places");
            quantity.requestFocus();
            return;
        }
        if (!date.isEmpty()) {
            try {
                if (!date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) throw new DateTimeParseException("Format", date, 0);
                LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                expiry.setError("Enter a real date as YYYY-MM-DD, or leave blank");
                expiry.requestFocus();
                return;
            }
        }
        Ingredient item = new Ingredient(ingredientId, itemName, amount, unit.getSelectedItem().toString(), date);
        if (database.saveIngredient(item) == -1) {
            Toast.makeText(this, "Entry no longer exists. Return to the pantry.", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
