package com.example.vault.tryattempt;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SimpleGestureFilter extends SimpleOnGestureListener {
    private static final int ACTION_FAKE = -13;
    public static final int MODE_DYNAMIC = 2;
    public static final int MODE_SOLID = 1;
    public static final int MODE_TRANSPARENT = 0;
    public static final int SWIPE_DOWN = 2;
    public static final int SWIPE_LEFT = 3;
    public static final int SWIPE_RIGHT = 4;
    public static final int SWIPE_UP = 1;
    private Activity context;
    private GestureDetector detector;
    private SimpleGestureListener listener;
    private int mode = 2;
    private boolean running = true;
    private int swipe_Max_Distance = 1000;
    private int swipe_Min_Distance = 10;
    private int swipe_Min_Velocity = 20;
    private boolean tapIndicator = false;

    interface SimpleGestureListener {
        void onDoubleTap();

        void onSwipe(int i);
    }

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return true;
    }

    public SimpleGestureFilter(Activity activity, SimpleGestureListener simpleGestureListener) {
        this.context = activity;
        this.detector = new GestureDetector(activity, this);
        this.listener = simpleGestureListener;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        if (this.running) {
            boolean onTouchEvent = this.detector.onTouchEvent(motionEvent);
            int i = this.mode;
            if (i == 1) {
                motionEvent.setAction(3);
            } else if (i == 2) {
                if (motionEvent.getAction() == ACTION_FAKE) {
                    motionEvent.setAction(1);
                } else if (onTouchEvent) {
                    motionEvent.setAction(3);
                } else if (this.tapIndicator) {
                    motionEvent.setAction(0);
                    this.tapIndicator = false;
                }
            }
        }
    }

    public void setMode(int i) {
        this.mode = i;
    }

    public int getMode() {
        return this.mode;
    }

    public void setEnabled(boolean z) {
        this.running = z;
    }

    public void setSwipeMaxDistance(int i) {
        this.swipe_Max_Distance = i;
    }

    public void setSwipeMinDistance(int i) {
        this.swipe_Min_Distance = i;
    }

    public void setSwipeMinVelocity(int i) {
        this.swipe_Min_Velocity = i;
    }

    public int getSwipeMaxDistance() {
        return this.swipe_Max_Distance;
    }

    public int getSwipeMinDistance() {
        return this.swipe_Min_Distance;
    }

    public int getSwipeMinVelocity() {
        return this.swipe_Min_Velocity;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        float abs = Math.abs(motionEvent.getX() - motionEvent2.getX());
        float abs2 = Math.abs(motionEvent.getY() - motionEvent2.getY());
        int i = this.swipe_Max_Distance;
        if (abs > ((float) i) || abs2 > ((float) i)) {
            return false;
        }
        float abs3 = Math.abs(f);
        float abs4 = Math.abs(f2);
        boolean z = true;
        if (abs3 <= ((float) this.swipe_Min_Velocity) || abs <= ((float) this.swipe_Min_Distance)) {
            if (abs4 <= ((float) this.swipe_Min_Velocity) || abs2 <= ((float) this.swipe_Min_Distance)) {
                z = false;
            } else if (motionEvent.getY() > motionEvent2.getY()) {
                this.listener.onSwipe(1);
            } else {
                this.listener.onSwipe(2);
            }
        } else if (motionEvent.getX() > motionEvent2.getX()) {
            this.listener.onSwipe(3);
        } else {
            this.listener.onSwipe(4);
        }
        return z;
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        this.tapIndicator = true;
        return false;
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        this.listener.onDoubleTap();
        return true;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (this.mode == 2) {
            motionEvent.setAction(ACTION_FAKE);
            this.context.dispatchTouchEvent(motionEvent);
        }
        return false;
    }
}
