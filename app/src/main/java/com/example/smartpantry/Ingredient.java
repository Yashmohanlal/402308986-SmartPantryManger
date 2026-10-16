package com.example.smartpantry;

import java.math.BigDecimal;

// Used for both pantry entries and recipe requirements.
public class Ingredient {
    public long id;
    public String name, unit, expiry;
    public BigDecimal quantity;

    public Ingredient(long id, String name, String quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = new BigDecimal(quantity);
        this.unit = unit;
        this.expiry = expiry == null ? "" : expiry;
    }

    public String amount() {
        return quantity.stripTrailingZeros().toPlainString() + " " + unit;
    }
}
