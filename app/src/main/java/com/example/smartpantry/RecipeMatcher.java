package com.example.smartpantry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {
    public static final String[] UNITS = {"piece", "g", "kg", "ml", "l", "tsp", "tbsp"};

    public static String normalise(String name) {
        String n = name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        // Explicit aliases avoid corrupting words such as rice and couscous.
        switch (n) {
            case "tomatoes": return "tomato";
            case "potatoes": return "potato";
            case "eggs": return "egg";
            case "onions": return "onion";
            case "carrots": return "carrot";
            case "bananas": return "banana";
            case "apples": return "apple";
            case "peas": case "pea": return "peas";
            case "oats": case "oat": return "oats";
            case "bread slices": case "slice of bread": return "bread";
            default: return n;
        }
    }

    private static String dimension(String unit) {
        switch (unit) {
            case "g": case "kg": return "mass";
            case "ml": case "l": case "tsp": case "tbsp": return "volume";
            default: return unit;
        }
    }

    private static BigDecimal baseAmount(Ingredient item) {
        switch (item.unit) {
            case "kg": case "l": return item.quantity.multiply(new BigDecimal("1000"));
            case "tsp": return item.quantity.multiply(new BigDecimal("5"));
            case "tbsp": return item.quantity.multiply(new BigDecimal("15"));
            default: return item.quantity;
        }
    }

    private static String key(Ingredient item) {
        return normalise(item.name) + "|" + dimension(item.unit);
    }

    public static boolean isExpired(Ingredient item, LocalDate today) {
        return !item.expiry.isEmpty() && LocalDate.parse(item.expiry).isBefore(today);
    }

    public static boolean canMake(Recipe recipe, List<Ingredient> pantry, LocalDate today) {
        Map<String, BigDecimal> available = new HashMap<>();
        for (Ingredient item : pantry) {
            if (!isExpired(item, today)) {
                available.merge(key(item), baseAmount(item), BigDecimal::add);
            }
        }
        // Aggregate both sides: duplicate rows must not reuse the same stock twice.
        Map<String, BigDecimal> required = new HashMap<>();
        for (Ingredient item : recipe.ingredients) {
            required.merge(key(item), baseAmount(item), BigDecimal::add);
        }
        if (required.isEmpty()) return false;
        for (String key : required.keySet()) {
            if (available.getOrDefault(key, BigDecimal.ZERO).compareTo(required.get(key)) < 0) {
                return false; // One missing or insufficient ingredient rejects the whole recipe.
            }
        }
        return true;
    }
}
