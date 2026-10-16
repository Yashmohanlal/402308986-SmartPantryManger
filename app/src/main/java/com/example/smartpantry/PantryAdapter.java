package com.example.smartpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.time.LocalDate;
import java.util.List;

public class PantryAdapter extends ArrayAdapter<Ingredient> {
    public PantryAdapter(Context context, List<Ingredient> items) {
        super(context, 0, items);
    }

    @Override public View getView(int position, View recycledView, ViewGroup parent) {
        // Reuse row views instead of inflating a new layout whenever the user scrolls.
        View row = recycledView;
        if (row == null) row = LayoutInflater.from(getContext()).inflate(R.layout.row_item, parent, false);
        Ingredient item = getItem(position);
        ((TextView) row.findViewById(R.id.rowTitle)).setText(item.name);
        String detail = item.amount();
        if (!item.expiry.isEmpty()) detail += " | Expires " + item.expiry;
        if (RecipeMatcher.isExpired(item, LocalDate.now())) detail += " | EXPIRED";
        ((TextView) row.findViewById(R.id.rowSubtitle)).setText(detail + "\nTap to edit or delete");
        return row;
    }
}
