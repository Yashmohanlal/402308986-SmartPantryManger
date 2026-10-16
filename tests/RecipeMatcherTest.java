package com.example.smartpantry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Plain Java test runner: no emulator or extra testing libraries required.
public class RecipeMatcherTest {
    private static int checks;
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 16);
    private static Ingredient item(String name, String qty, String unit) {
        return new Ingredient(-1, name, qty, unit, "");
    }
    private static Recipe recipe(Ingredient... items) {
        Recipe r = new Recipe(1, "Test", "Test steps");
        r.ingredients.addAll(Arrays.asList(items));
        return r;
    }
    private static void check(String label, boolean expected, Recipe recipe, Ingredient... pantry) {
        boolean actual = RecipeMatcher.canMake(recipe, Arrays.asList(pantry), TODAY);
        if (actual != expected) throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        checks++;
        System.out.println("PASS " + label);
    }
    public static void main(String[] args) {
        Recipe eggs = recipe(item("egg", "2", "piece"), item("butter", "10", "g"));
        check("Empty pantry", false, eggs);
        check("One missing ingredient", false, eggs, item("egg", "2", "piece"));
        check("Insufficient quantity", false, eggs, item("egg", "1", "piece"), item("butter", "10", "g"));
        check("Exact quantities", true, eggs, item("egg", "2", "piece"), item("butter", "10", "g"));
        check("Surplus allowed", true, eggs, item("egg", "3", "piece"), item("butter", "20", "g"));
        check("Case and plural", true, eggs, item(" EGGS ", "2", "piece"), item("Butter", "10", "g"));
        check("Kilograms convert", true, eggs, item("egg", "2", "piece"), item("butter", "0.01", "kg"));
        check("Duplicate stock sums", true, eggs, item("egg", "1", "piece"), item("egg", "1", "piece"), item("butter", "10", "g"));
        check("Mixed units sum", true, recipe(item("rice", "150", "g")), item("rice", "0.1", "kg"), item("rice", "50", "g"));
        check("Mass cannot satisfy volume", false, recipe(item("milk", "100", "ml")), item("milk", "100", "g"));
        check("Pieces cannot satisfy grams", false, recipe(item("tomato", "100", "g")), item("tomatoes", "100", "piece"));
        check("Litres convert", true, recipe(item("milk", "200", "ml")), item("milk", "0.2", "l"));
        check("Tablespoons convert", true, recipe(item("oil", "15", "ml")), item("oil", "1", "tbsp"));
        check("Teaspoons convert", true, recipe(item("oil", "15", "ml")), item("oil", "3", "tsp"));
        check("Tomatoes alias", true, recipe(item("tomato", "2", "piece")), item("Tomatoes", "2", "piece"));
        check("Potatoes alias", true, recipe(item("potato", "2", "piece")), item("potatoes", "2", "piece"));
        check("No fuzzy substitute", false, recipe(item("butter", "10", "g")), item("peanut butter", "100", "g"));
        check("Expired stock excluded", false, recipe(item("egg", "2", "piece")), new Ingredient(-1,"egg","2","piece","2026-10-15"));
        check("Expiry today accepted", true, recipe(item("egg", "2", "piece")), new Ingredient(-1,"egg","2","piece","2026-10-16"));
        check("No repeated demand double counting", false, recipe(item("egg","2","piece"),item("eggs","2","piece")),item("egg","3","piece"));
        check("Decimal sum exact", true, recipe(item("milk","0.3","ml")),item("milk","0.1","ml"),item("milk","0.2","ml"));
        check("Tiny shortfall rejected", false, recipe(item("milk","100","ml")),item("milk","99.999","ml"));
        check("No implicit water", false, recipe(item("rice","100","g"),item("water","250","ml")),item("rice","100","g"));
        check("Empty recipe rejected", false, recipe());
        System.out.println(checks + " checks passed.");
    }
}
