package com.HabitTracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;

public class Spotlightview extends View {

    private final Paint overlayPaint;
    private final Paint holePaint;
    private final int cx, cy, radius;

    public Spotlightview(Context context, int cx, int cy, int radius) {
        super(context);
        this.cx     = cx;
        this.cy     = cy;
        this.radius = radius;

        overlayPaint = new Paint();
        overlayPaint.setColor(Color.parseColor("#CC000000")); // 80% dark
        overlayPaint.setAntiAlias(true);

        holePaint = new Paint();
        holePaint.setColor(Color.TRANSPARENT);
        holePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        holePaint.setAntiAlias(true);

        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);
        canvas.drawCircle(cx, cy, radius, holePaint);
    }
}