package com.example.androidlevel2_lesson1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ThermometerView extends View {
    private int thermometerColor = Color.WHITE;
    private int levelColor = Color.BLUE;
    private RectF levelRectangle = new RectF();
    private RectF headRectangle = new RectF();
    private Paint thermometerPaint;
    private Paint levelPaint;

    private int width = 0;
    private int height = 0;
    private int level = 40;

    private final static int padding = 10;

    public void setLevel(int themperature) {
        if (themperature>0) {
            levelColor = Color.RED;
            level = themperature;
        }
        else {
            levelColor = Color.BLUE;
            level = - themperature;
        }
        levelPaint.setColor(levelColor);
    }

    public ThermometerView(Context context) {
        super(context);
        init();
    }

    public ThermometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView, 0, 0);

        thermometerColor = typedArray.getColor(R.styleable.ThermometerView_thermometer_color, Color.WHITE);
        levelColor = typedArray.getColor(R.styleable.ThermometerView_level_color, Color.BLUE);
        level = typedArray.getInteger(R.styleable.ThermometerView_level, 40);
        typedArray.recycle();
    }

    private void init() {
        thermometerPaint = new Paint();
        thermometerPaint.setColor(thermometerColor);
        thermometerPaint.setStyle(Paint.Style.FILL);
        levelPaint = new Paint();
        levelPaint.setColor(levelColor);
        levelPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingTop() - getPaddingBottom();

        headRectangle.set(padding + width/4,
                padding,
                width - padding - width/4,
                height - padding);

        levelRectangle.set(2 * padding + width/3,
                (int) ((2 * padding + width/12) + (height - width/4 - 2 * padding)*(1 - (double)level/(double)40)),
                width - 2 * padding - width/3,
                height - width/4 - 2 * padding);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(width/2,height - width/2,(width-2*padding)/2,thermometerPaint);
        canvas.drawRoundRect(headRectangle, height / 2, height / 2, thermometerPaint);

        if (height != 0 & width != 0) {
            levelRectangle.set(2 * padding + width / 3,
                    (int) ((2 * padding + width / 12) + (height - width / 4 - 2 * padding) * (1 - (double) level / (double) 40)),
                    width - 2 * padding - width / 3,
                    height - width / 4 - 2 * padding);
        }
        canvas.drawRoundRect(levelRectangle, height / 2, height / 2, levelPaint);

    }
}
