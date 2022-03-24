package com.example.wakeapp.ui.clock;
/**
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Path;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
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

        mIsInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mIsInit){
            init();
        }

        drawHands(canvas);

        postInvalidateDelayed(500);
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



}*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.example.wakeapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;

/**
 * This widget display an analog clock with two hands for hours and minutes.
 */
public class AnalogClock extends FrameLayout {

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                final String tz = intent.getStringExtra("time-zone");
                mTime = Calendar.getInstance(TimeZone.getTimeZone(tz));
            }
            onTimeChanged();
        }
    };

    private final Runnable mClockTick = new Runnable() {
        @Override
        public void run() {
            onTimeChanged();

            if (mEnableSeconds) {
                final long now = System.currentTimeMillis();
                final long delay = SECOND_IN_MILLIS - now % SECOND_IN_MILLIS;
                postDelayed(this, delay);
            }
        }
    };

    private final ImageView mHourHand;
    private final ImageView mMinuteHand;
    private final ImageView mSecondHand;

    private Calendar mTime;
    private String mDescFormat;
    private TimeZone mTimeZone;
    private boolean mEnableSeconds = true;

    public AnalogClock(Context context) {
        this(context, null /* attrs */);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /* defStyleAttr */);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTime = Calendar.getInstance();
        mDescFormat = ((SimpleDateFormat) DateFormat.getTimeFormat(context)).toLocalizedPattern();

        // Must call mutate on these instances, otherwise the drawables will blur, because they're
        // sharing their size characteristics with the (smaller) world cities analog clocks.
        final ImageView dial = new AppCompatImageView(context);
        dial.setImageResource(R.drawable.clock);
        dial.getDrawable().mutate();
        addView(dial);

        mHourHand = new AppCompatImageView(context);
        mHourHand.setImageResource(R.drawable.hourhand);
        mHourHand.getDrawable().mutate();
        addView(mHourHand);

        mMinuteHand = new AppCompatImageView(context);
        mMinuteHand.setImageResource(R.drawable.minutehand);
        mMinuteHand.getDrawable().mutate();
        addView(mMinuteHand);

        mSecondHand = new AppCompatImageView(context);
        mSecondHand.setImageResource(R.drawable.secondhand);
        mSecondHand.getDrawable().mutate();
        addView(mSecondHand);
        /**
        if (context.getClass().getSimpleName().equalsIgnoreCase(ScreensaverActivity.class.getSimpleName())){
            dial.setColorFilter(Color.WHITE);
            mHourHand.setColorFilter(Color.WHITE);
            mMinuteHand.setColorFilter(Color.WHITE);
        }
        */
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, filter);

        // Refresh the calendar instance since the time zone may have changed while the receiver
        // wasn't registered.
        mTime = Calendar.getInstance(mTimeZone != null ? mTimeZone : TimeZone.getDefault());
        onTimeChanged();

        // Tick every second.
        if (mEnableSeconds) {
            mClockTick.run();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        getContext().unregisterReceiver(mIntentReceiver);
        removeCallbacks(mClockTick);
    }

    private void onTimeChanged() {
        mTime.setTimeInMillis(System.currentTimeMillis());
        final float hourAngle = mTime.get(Calendar.HOUR) * 30f;
        mHourHand.setRotation(hourAngle);
        final float minuteAngle = mTime.get(Calendar.MINUTE) * 6f;
        mMinuteHand.setRotation(minuteAngle);
        if (mEnableSeconds) {
            final float secondAngle = mTime.get(Calendar.SECOND) * 6f;
            mSecondHand.setRotation(secondAngle);
        }
        setContentDescription(DateFormat.format(mDescFormat, mTime));
        invalidate();
    }

    /** Permetter di impostare il fuso orario (per ora inutilizzato)*/
    public void setTimeZone(String id) {
        mTimeZone = TimeZone.getTimeZone(id);
        mTime.setTimeZone(mTimeZone);
        onTimeChanged();
    }

    /** Permetter la visualizzazione dei secondi (per ora inutilizzata)*/
    public void enableSeconds(boolean enable) {
        mEnableSeconds = enable;
        if (mEnableSeconds) {
            mSecondHand.setVisibility(VISIBLE);
            mClockTick.run();
        } else {
            mSecondHand.setVisibility(GONE);
        }
    }
}
