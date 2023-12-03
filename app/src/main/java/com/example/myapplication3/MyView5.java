package com.example.myapplication3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import java.util.concurrent.Semaphore;
import java.util.Vector;
/**
 * TODO: document your custom view class.
 */
public class MyView5 extends View implements  View.OnTouchListener {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private boolean mWatchForFingerUp;
    private Paint mLinePaint;
    private Vector<MotionEvent.PointerCoords> mPointStack;
    private Semaphore mAllowAccessToStack;
    public MyView5(Context context) {
        super(context);
        init(null, 0);
    }

    public MyView5(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);}

    public MyView5(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MyView5, defStyle, 0);

        this.mLinePaint = new Paint();
        this.mLinePaint.setColor(0x80ff0000);
        this.mLinePaint.setStrokeWidth(10);
        this.setOnTouchListener(this);
        this.mPointStack = new Vector<MotionEvent.PointerCoords>();
        this.mAllowAccessToStack = new Semaphore(1,true);

        mExampleString = a.getString(
                R.styleable.MyView5_exampleString);
        mExampleColor = a.getColor(
                R.styleable.MyView5_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.MyView5_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.MyView5_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.MyView5_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.mWatchForFingerUp = true;
            return true;
        }

        if ((event.getAction() == MotionEvent.ACTION_UP) && (this.mWatchForFingerUp)){
            MotionEvent.PointerCoords temp = new MotionEvent.PointerCoords();
            event.getPointerCoords(event.getPointerCount() - 1,temp);
            try {
                this.mAllowAccessToStack.acquire();
            }catch(Exception e) {
                return false;
            }
            this.mPointStack.add(temp);
            this.mAllowAccessToStack.release();
            this.mWatchForFingerUp = false;
            if (this.mPointStack.size() > 1)
                this.invalidate();
            return true;
        }
        return false;
    }

    @Override
    public void onDraw(@NonNull Canvas c)
    {

        if (mPointStack.size() < 2) {
            super.onDraw(c);
            return;
        }

        MotionEvent.PointerCoords sourcePt;
        MotionEvent.PointerCoords destinationPt;

        try {
            this.mAllowAccessToStack.acquire();
        }catch(Exception e) {
            super.onDraw(c);
            return;
        }
        for(int i=0; (i+2) <= this.mPointStack.size(); i++)
        {
            sourcePt = this.mPointStack.get(i);
            destinationPt = this.mPointStack.get(i+1);
            c.drawLine(sourcePt.x,sourcePt.y,destinationPt.x,destinationPt.y,this.mLinePaint);
        }
        this.mAllowAccessToStack.release();
        super.onDraw(c);
    }

    @Override
    protected void finalize()
    {
        this.mPointStack.clear();
        this.mLinePaint = null;
    }

}