package com.young.timber.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

public class MusicVisualizer extends View {
    Random random = new Random();
    Paint paint = new Paint();
    private Runnable animateView = new Runnable() {
        @Override
        public void run() {
            postDelayed(this, 120);
            invalidate();
        }
    };

    public MusicVisualizer(Context context) {
        super(context);
        new MusicVisualizer(context, null);
    }

    public MusicVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        removeCallbacks(animateView);
        post(animateView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(getDimensionInPixel(0), getHeight() - (40 + random.nextInt((int) (getHeight() / 1.5f) - 25)), getDimensionInPixel(7), getHeight() - 15, paint);
        canvas.drawRect(getDimensionInPixel(10), getHeight() - (40 + random.nextInt((int) (getHeight() / 1.5f) - 25)), getDimensionInPixel(17), getHeight() - 15, paint);
        canvas.drawRect(getDimensionInPixel(20), getHeight() - (40 + random.nextInt((int) (getHeight() / 1.5f) - 25)), getDimensionInPixel(27), getHeight() - 15, paint);

    }

    private int getDimensionInPixel(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.VISIBLE == visibility) {
            removeCallbacks(animateView);
            post(animateView);
        } else if (View.GONE == visibility) {
            removeCallbacks(animateView);
        }
    }
}
