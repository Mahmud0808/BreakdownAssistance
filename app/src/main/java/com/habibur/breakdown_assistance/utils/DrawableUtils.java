package com.habibur.breakdown_assistance.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

public class DrawableUtils {

    public static Bitmap drawableToBitmap(Context context, int drawableId) {
        if (context == null || drawableId == 0) {
            return null;
        }

        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        if (drawable == null) {
            return null;
        }

        int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 1;
        int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap tintBitmap(Bitmap bitmap, int color) {
        Bitmap tintedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(tintedBitmap);
        Paint paint = new Paint();
        ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return tintedBitmap;
    }
}
