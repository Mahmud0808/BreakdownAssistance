package com.habibur.breakdown_assistance.utils;

import android.content.Context;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class ViewUtils {

    public static void applyWindowInsets(View view, boolean top, boolean bottom) {
        int originalPaddingTop = view.getPaddingTop();
        int originalPaddingBottom = view.getPaddingBottom();
        int originalPaddingLeft = view.getPaddingLeft();
        int originalPaddingRight = view.getPaddingRight();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            int paddingTop = top ? systemBars.top + originalPaddingTop : originalPaddingTop;
            int paddingBottom = bottom ? systemBars.bottom + originalPaddingBottom : originalPaddingBottom;
            int paddingLeft = systemBars.left + originalPaddingLeft;
            int paddingRight = systemBars.right + originalPaddingRight;

            v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            return insets;
        });
    }

    public static void setToolbarTitle(Context context, MaterialToolbar toolbar, @StringRes int title, boolean showBackButton) {
        AppCompatActivity activity = (AppCompatActivity) context;
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();

        if (actionBar != null) {
            ((AppCompatActivity) context).getSupportActionBar().setTitle(title);
            ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
            ((AppCompatActivity) context).getSupportActionBar().setDisplayShowHomeEnabled(showBackButton);

            if (showBackButton) {
                toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
            }
        }
    }
}
