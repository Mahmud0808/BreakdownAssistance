package com.habibur.breakdown_assistance.utils;

import android.text.Html;
import android.text.Spanned;

public class TextUtils {

    public static Spanned getSpannedText(String text) {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
    }
}
