package com.example.wakeapp.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Path;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class AnalogClockView extends View {

    private int mHeight;
    private int mWidth;
    private int mRadius;
    private double mAngle;
    private int mCentreX;
    private int mCentreY;
    private int mPadding;
    private boolean mIsInit;

    private Rect mRect;
    private Paint mPaint;
    private Path mPath;

    private int[] mNumbers;
    private int mMinimum;
    private float mHour;
    private float mMinute;
    private float mSecond;
    private int mHourHandSize;
    private int mHandSize;

    public float mFontSize = 40;

    public AnalogClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(){

        mHeight = getHeight();
        mWidth = getWidth();
        mPadding = 200;

        mCentreX = mWidth/2;
        mCentreY = mHeight/2;

        mMinimum = Math.min(mHeight, mWidth);
        mRadius = mMinimum/2 - mPadding;

        mAngle = (float) ((Math.PI/30) - (Math.PI/2));

        mPaint = new Paint();
        mPath = new Path();
        mRect = new Rect();

        mHourHandSize = mRadius - mRadius/2;
        mHandSize = mRadius - mRadius/4;

        mNumbers = new int[] {1, 2, 3,  4, 5, 6, 7, 8, 9, 10, 11, 12};

        mIsInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mIsInit){
            init();
        }

        drawCircle(canvas);
        drawHands(canvas);
        drawNumerals(canvas);

        postInvalidateDelayed(500);
    }

    private void drawCircle(@NotNull Canvas canvas){
        mPaint.reset();
        setPaintAttributes(Color.WHITE, Paint.Style.STROKE, 10);
        canvas.drawCircle(mCentreX, mCentreY, mRadius, mPaint);
    }

    private void setPaintAttributes(int color, Paint.Style stroke, int strokeWidth){
        mPaint.reset();
        mPaint.setColor(color);
        mPaint.setStyle(stroke);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
    }

    private void drawHands(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();

        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mHour = mHour > 12 ? mHour - 12 : mHour;

        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);

        drawHourHand(canvas, (float) ((mHour + mMinute / 60.0) * 5f));
        drawMinuteHand(canvas, mMinute);
        drawSecondsHand(canvas, mSecond);

    }

    private void drawHourHand(Canvas canvas, float location){
        mPaint.reset();
        setPaintAttributes(Color.DKGRAY, Paint.Style.STROKE, 10);

        mAngle = Math.PI * location / 30 - Math.PI / 2;
        canvas.drawLine(mCentreX, mCentreY,
                (float)(mCentreX + Math.cos(mAngle)*mHourHandSize),
                (float)(mCentreY + Math.sin(mAngle)*mHourHandSize),
                mPaint);
    }

    private void drawMinuteHand(Canvas canvas, float location){
        mPaint.reset();
        setPaintAttributes(Color.LTGRAY, Paint.Style.STROKE, 8);

        mAngle = Math.PI * location / 30 - Math.PI / 2;
        canvas.drawLine(mCentreX, mCentreY,
                (float)(mCentreX + Math.cos(mAngle)*mHandSize),
                (float)(mCentreY + Math.sin(mAngle)*mHourHandSize),
                mPaint);
    }

    private void drawSecondsHand(Canvas canvas, float location){
        mPaint.reset();
        setPaintAttributes(Color.MAGENTA, Paint.Style.STROKE, 8);

        mAngle = Math.PI * location / 30 - Math.PI / 2;
        canvas.drawLine(mCentreX, mCentreY,
                (float)(mCentreX + Math.cos(mAngle)*mHandSize),
                (float)(mCentreY + Math.sin(mAngle)*mHourHandSize),
                mPaint);
    }

    private void drawNumerals(Canvas canvas){
        mPaint.setTextSize(mFontSize);

        for (int number : mNumbers){
            String num = String.valueOf(number);
            mPaint.getTextBounds(num, 0, num.length(), mRect);

            double angle = Math.PI / 6 * (number - 3);
            int x = (int) (mCentreX + Math.cos(angle) * mRadius - mRect.width() / 2);
            int y = (int) (mCentreY + Math.sin(angle) * mRadius - mRect.height() / 2);

            canvas.drawText(num, x, y, mPaint);
        }
    }
}