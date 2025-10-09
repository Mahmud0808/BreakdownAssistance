package com.habibur.breakdown_assistance.utils;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewUtils {

    public static void applyWindowInsets(View view, boolean top, boolean bottom) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int paddingTop = top ? systemBars.top : 0;
            int paddingBottom = bottom ? systemBars.bottom : 0;
            v.setPadding(systemBars.left, paddingTop, systemBars.right, paddingBottom);
            return insets;
        });
    }
}
