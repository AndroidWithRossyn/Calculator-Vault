package com.example.vault.securitylocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.example.vault.LoginActivity;

import com.example.vault.R;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.utilities.Common;

public class ConfirmLockPatternViewLogin extends View {
    public static final int BACKGROUND_COLOR = -16760187;
    public static final long BUILD_TIMEOUT_MILLIS = 30;
    public static final float CELL_NODE_RATIO = 0.9f;
    public static final int DEATH_COLOR = -65536;
    public static final int DEFAULT_LENGTH_NODES = 3;
    public static final int DEFAULT_LENGTH_PX = 100;
    public static final int DEFAULT_PATTERN_GREEN_LINE_COLOR = NodeDrawable.white;
    public static final int DEFAULT_PATTERN_RED_LINE_COLOR = NodeDrawable.red;
    public static final int EDGE_COLOR = -4008213;
    public static final int LINE_COLOR = -10027060;
    public static final float NODE_EDGE_RATIO = 0.33f;
    public static final int PRACTICE_RESULT_DISPLAY_MILLIS = 30;
    public static final int TACTILE_FEEDBACK_DURATION = 35;
    protected static Paint mLinePaint;
    String DecoyPattern;
    public Context con;
    protected int mCellLength;
    protected List<Point> mCurrentPattern = Collections.emptyList();
    protected boolean mDisplayingPracticeResult = false;
    protected boolean mDrawTouchExtension = false;
    protected Paint mEdgePaint = new Paint();
    protected Handler mHandler = new Handler();
    protected HighlightMode mHighlightMode = new NoHighlight();
    protected int mLengthNodes = 3;
    protected int mLengthPx = 100;
    protected NodeDrawable[][] mNodeDrawables = ((NodeDrawable[][]) Array.newInstance(NodeDrawable.class, new int[]{0, 0}));
    protected HighlightMode mPracticeFailureMode = new FailureHighlight();
    protected boolean mPracticeMode;
    protected List<Point> mPracticePattern;
    protected Set<Point> mPracticePool;
    protected HighlightMode mPracticeSuccessMode = new SuccessHighlight();
    protected boolean mTactileFeedback;
    protected Point mTouchCell = new Point(-1, -1);
    protected Point mTouchPoint = new Point(-1, -1);
    protected int mTouchThreshold;
    protected Vibrator mVibrator = ((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE));

    private class CenterIterator implements Iterator<Point> {
        private Iterator<Point> nodeIterator;

        public CenterIterator(Iterator<Point> it) {
            this.nodeIterator = it;
        }

        public boolean hasNext() {
            return this.nodeIterator.hasNext();
        }

        public Point next() {
            Point point = (Point) this.nodeIterator.next();
            return ConfirmLockPatternViewLogin.this.mNodeDrawables[point.x][point.y].getCenter();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static class FailureHighlight implements HighlightMode {
        public int select(NodeDrawable nodeDrawable, int i, int i2, int i3, int i4, int i5) {
            ConfirmLockPatternViewLogin.mLinePaint.setColor(ConfirmLockPatternViewLogin.DEFAULT_PATTERN_RED_LINE_COLOR);
            ConfirmLockPatternViewLogin.mLinePaint.setAlpha(100);
            return 4;
        }
    }

    public static class FirstHighlight implements HighlightMode {
        public int select(NodeDrawable nodeDrawable, int i, int i2, int i3, int i4, int i5) {
            return i == 0 ? 2 : 1;
        }
    }

    public interface HighlightMode {
        int select(NodeDrawable nodeDrawable, int i, int i2, int i3, int i4, int i5);
    }

    public static class NoHighlight implements HighlightMode {
        public int select(NodeDrawable nodeDrawable, int i, int i2, int i3, int i4, int i5) {
            return 1;
        }
    }

    public static class RainbowHighlight implements HighlightMode {
        public int select(NodeDrawable nodeDrawable, int i, int i2, int i3, int i4, int i5) {
            nodeDrawable.setCustomColor(Color.HSVToColor(new float[]{(((float) i) / ((float) i2)) * 360.0f, 1.0f, 1.0f}));
            return 5;
        }
    }

    public static class SuccessHighlight implements HighlightMode {
        public int select(NodeDrawable nodeDrawable, int i, int i2, int i3, int i4, int i5) {
            ConfirmLockPatternViewLogin.mLinePaint.setColor(ConfirmLockPatternViewLogin.DEFAULT_PATTERN_GREEN_LINE_COLOR);
            ConfirmLockPatternViewLogin.mLinePaint.setAlpha(100);
            return 3;
        }
    }

    public ConfirmLockPatternViewLogin(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.con = context;
        this.mEdgePaint.setColor(DEFAULT_PATTERN_GREEN_LINE_COLOR);
        this.mEdgePaint.setStrokeCap(Cap.ROUND);
        this.mEdgePaint.setFlags(1);
        mLinePaint = new Paint();
        mLinePaint.setColor(DEFAULT_PATTERN_GREEN_LINE_COLOR);
        mLinePaint.setStrokeCap(Cap.ROUND);
        mLinePaint.setAlpha(100);
        mLinePaint.setFlags(1);
        this.DecoyPattern = SecurityLocksSharedPreferences.GetObject(this.con).GetDecoySecurityCredential();
    }

    private void buildDrawables() {
        int i = this.mLengthNodes;
        this.mNodeDrawables = (NodeDrawable[][]) Array.newInstance(NodeDrawable.class, new int[]{i, i});
        this.mCellLength = this.mLengthPx / this.mLengthNodes;
        float f = ((float) this.mCellLength) * 0.9f;
        this.mEdgePaint.setStrokeWidth(0.33f * f);
        this.mTouchThreshold = (int) (f / 2.0f);
        int i2 = this.mCellLength / 2;
        long currentTimeMillis = System.currentTimeMillis();
        for (int i3 = 0; i3 < this.mLengthNodes; i3++) {
            for (int i4 = 0; i4 < this.mLengthNodes; i4++) {
                if (System.currentTimeMillis() - currentTimeMillis >= 30) {
                    PatternActivityMethods.clearAndBail(getContext());
                }
                int i5 = this.mCellLength;
                this.mNodeDrawables[i4][i3] = new NodeDrawable(f, new Point((i4 * i5) + i2, (i5 * i3) + i2), this.con);
            }
        }
        if (!this.mPracticeMode) {
            loadPattern(this.mCurrentPattern, this.mHighlightMode);
        }
    }

    private void clearPattern(List<Point> list) {
        for (Point point : list) {
            this.mNodeDrawables[point.x][point.y].setNodeState(0);
            mLinePaint.setColor(DEFAULT_PATTERN_GREEN_LINE_COLOR);
            mLinePaint.setAlpha(100);
        }
    }

    private void loadPattern(List<Point> list, HighlightMode highlightMode) {
        for (int i = 0; i < list.size(); i++) {
            Point point = (Point) list.get(i);
            NodeDrawable nodeDrawable = this.mNodeDrawables[point.x][point.y];
            nodeDrawable.setNodeState(highlightMode.select(nodeDrawable, i, list.size(), point.x, point.y, this.mLengthNodes));
            if (i < list.size() - 1) {
                Point point2 = (Point) list.get(i + 1);
                Point center = this.mNodeDrawables[point.x][point.y].getCenter();
                Point center2 = this.mNodeDrawables[point2.x][point2.y].getCenter();
                this.mNodeDrawables[point.x][point.y].setExitAngle((float) Math.atan2((double) (center.y - center2.y), (double) (center.x - center2.x)));
            }
        }
    }

    private void appendPattern(List<Point> list, Point point) {
        NodeDrawable nodeDrawable = this.mNodeDrawables[point.x][point.y];
        nodeDrawable.setNodeState(1);
        if (list.size() > 0) {
            Point point2 = (Point) list.get(list.size() - 1);
            NodeDrawable nodeDrawable2 = this.mNodeDrawables[point2.x][point2.y];
            Point center = nodeDrawable2.getCenter();
            Point center2 = nodeDrawable.getCenter();
            nodeDrawable2.setExitAngle((float) Math.atan2((double) (center.y - center2.y), (double) (center.x - center2.x)));
        }
        list.add(point);
    }

    private void testPracticePattern() {
        this.mDisplayingPracticeResult = true;
        HighlightMode highlightMode = this.mPracticeFailureMode;
        if (PatternActivityMethods.ConvertPatternToNo(this.mPracticePattern).equals(SecurityLocksCommon.PatternPassword)) {
            highlightMode = this.mPracticeSuccessMode;
            Log.v(" Success", "Great ");
        } else if (PatternActivityMethods.ConvertPatternToNo(this.mPracticePattern).equals(this.DecoyPattern) && !SecurityLocksCommon.IsConfirmPatternActivity) {
            highlightMode = this.mPracticeSuccessMode;
            Log.v(" Success", "Great ");
        }
        loadPattern(this.mPracticePattern, highlightMode);
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if (ConfirmLockPatternViewLogin.this.mDisplayingPracticeResult) {
                    ConfirmLockPatternViewLogin confirmLockPatternViewLogin = ConfirmLockPatternViewLogin.this;
                    confirmLockPatternViewLogin.testPasswordPattern(PatternActivityMethods.ConvertPatternToNo(confirmLockPatternViewLogin.mPracticePattern));
                    ConfirmLockPatternViewLogin.this.resetPractice();
                    ConfirmLockPatternViewLogin.this.invalidate();
                }
            }
        }, 30);
    }

    public void testPattern(List<Point> list, List<Point> list2) {
        if (list.equals(list2)) {
            if (SecurityLocksCommon.IsConfirmPatternActivity) {
                ((Activity) getContext()).finish();
            }
            Log.v(" Success", "Great ");
        } else {
            Log.v(" Fail", "Fail ");
        }
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                ConfirmLockPatternViewLogin.this.resetPractice();
                ConfirmLockPatternViewLogin.this.invalidate();
            }
        }, 30);
    }

    public void testPasswordPattern(String str) {
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(this.con);
        if (str.equals(SecurityLocksCommon.PatternPassword)) {
            SecurityLocksCommon.IsFakeAccount = 0;
            if (SecurityLocksCommon.IsConfirmPatternActivity) {
                SecurityLocksCommon.IsAppDeactive = false;
                SecurityLocksCommon.isBackupPattern = false;
                SecurityLocksCommon.isSettingDecoy = false;
                SecurityLocksCommon.IsConfirmPatternActivity = false;
                ((Activity) getContext()).finish();
            } else if (SecurityLocksCommon.isSettingDecoy) {
                SecurityLocksCommon.IsAppDeactive = false;
                SecurityLocksCommon.isSettingDecoy = false;
                SecurityLocksCommon.IsConfirmPatternActivity = false;
                SecurityLocksCommon.isBackupPattern = false;
                Intent intent = new Intent(this.con, SetPatternActivity.class);
                intent.putExtra("isSettingDecoy", true);
                this.con.startActivity(intent);
                ((Activity) getContext()).finish();
            } else if (!SecurityLocksCommon.isBackupPattern) {
                Common.loginCount = GetObject.GetRateCount();
                Common.loginCount++;
                GetObject.SetRateCount(Common.loginCount);
                SecurityLocksCommon.IsFakeAccount = 0;
                SecurityLocksCommon.IsnewloginforAd = true;
                SecurityLocksCommon.IsAppDeactive = false;
                if (!SecurityLocksCommon.IsAppDeactive || SecurityLocksCommon.CurrentActivity == null) {
                    SecurityLocksCommon.IsAppDeactive = false;
                    this.con.startActivity(new Intent(this.con, MainiFeaturesActivity.class));
                    ((Activity) getContext()).finish();
                } else {
                    this.con.startActivity(new Intent(this.con, SecurityLocksCommon.CurrentActivity.getClass()));
                    ((Activity) getContext()).finish();
                }
            }
            Log.v(" Success", "Great ");
        } else if (!str.equals(this.DecoyPattern)) {
            if (SecurityLocksCommon.IsConfirmPatternActivity) {
                loadPattern(this.mPracticePattern, this.mPracticeFailureMode);
                ConfirmPatternActivity.lblConfirmpattern.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Tryagain);
            } else {
                loadPattern(this.mPracticePattern, this.mPracticeFailureMode);
                if (!SecurityLocksCommon.IsStealthModeOn) {
                    LoginActivity loginActivity = new LoginActivity();
                    loginActivity.getClass();
                    loginActivity.TryAttempt(this.con);
                    LoginActivity.wrongPassword = str;
                    LoginActivity.txt_wrong_pttern.setVisibility(View.VISIBLE);
                    LoginActivity.txt_wrong_pttern.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Tryagain);
                }
            }
            Log.v(" Fail", "Fail ");
        } else if (SecurityLocksCommon.IsConfirmPatternActivity) {
            if (SecurityLocksCommon.IsConfirmPatternActivity) {
                loadPattern(this.mPracticePattern, this.mPracticeFailureMode);
                ConfirmPatternActivity.lblConfirmpattern.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Tryagain);
            }
        } else if (!this.DecoyPattern.trim().isEmpty() && this.DecoyPattern.trim().length() != 0) {
            SecurityLocksCommon.IsAppDeactive = false;
            SecurityLocksCommon.IsFakeAccount = 1;
            this.con.startActivity(new Intent(this.con, MainiFeaturesActivity.class));
            ((Activity) getContext()).finish();
        }
    }

    public void resetPractice() {
        clearPattern(this.mPracticePattern);
        this.mPracticePattern.clear();
        this.mPracticePool.clear();
        this.mDisplayingPracticeResult = false;
    }


    public void onDraw(Canvas canvas) {
        List<Point> list = this.mCurrentPattern;
        if (this.mPracticeMode) {
            list = this.mPracticePattern;
        }
        CenterIterator centerIterator = new CenterIterator(list.iterator());
        if (centerIterator.hasNext()) {
            mLinePaint.setStrokeWidth(9.0f);
            mLinePaint.setAlpha(100);
            Point next = centerIterator.next();
            while (centerIterator.hasNext()) {
                Point next2 = centerIterator.next();
                canvas.drawLine((float) next.x, (float) next.y, (float) next2.x, (float) next2.y, mLinePaint);
                next = next2;
            }
            if (this.mDrawTouchExtension) {
                canvas.drawLine((float) next.x, (float) next.y, (float) this.mTouchPoint.x, (float) this.mTouchPoint.y, mLinePaint);
            }
        }
        for (int i = 0; i < this.mLengthNodes; i++) {
            for (int i2 = 0; i2 < this.mLengthNodes; i2++) {
                this.mNodeDrawables[i2][i].draw(canvas);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mPracticeMode) {
            return super.onTouchEvent(motionEvent);
        }
        switch (motionEvent.getAction()) {
            case 0:
                if (this.mDisplayingPracticeResult) {
                    resetPractice();
                }
                this.mDrawTouchExtension = true;
                break;
            case 1:
                this.mDrawTouchExtension = false;
                testPracticePattern();
                break;
            case 2:
                break;
            default:
                return super.onTouchEvent(motionEvent);
        }
        if (!SecurityLocksCommon.IsStealthModeOn) {
            try {
                LoginActivity.txt_wrong_pttern.setVisibility(View.INVISIBLE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Point point = this.mTouchPoint;
        int i = (int) x;
        point.x = i;
        int i2 = (int) y;
        point.y = i2;
        Point point2 = this.mTouchCell;
        int i3 = this.mCellLength;
        point2.x = i / i3;
        point2.y = i2 / i3;
        if (point2.x >= 0 && this.mTouchCell.x < this.mLengthNodes && this.mTouchCell.y >= 0 && this.mTouchCell.y < this.mLengthNodes) {
            Point center = this.mNodeDrawables[this.mTouchCell.x][this.mTouchCell.y].getCenter();
            if (((int) Math.sqrt(Math.pow((double) (x - ((float) center.x)), 2.0d) + Math.pow((double) (y - ((float) center.y)), 2.0d))) < this.mTouchThreshold && !this.mPracticePool.contains(this.mTouchCell)) {
                if (this.mTactileFeedback) {
                    this.mVibrator.vibrate(35);
                }
                Point point3 = new Point(this.mTouchCell);
                appendPattern(this.mPracticePattern, point3);
                this.mPracticePool.add(point3);
            }
        }
        invalidate();
        return true;
    }


    public void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        int mode = MeasureSpec.getMode(i);
        int size2 = MeasureSpec.getSize(i2);
        int mode2 = MeasureSpec.getMode(i2);
        if (mode == 0 && mode2 == 0) {
            size = 100;
            setMeasuredDimension(100, 100);
        } else if (mode == 0) {
            size = size2;
        } else if (mode2 != 0) {
            size = Math.min(size, size2);
        }
        setMeasuredDimension(size, size);
    }


    public void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mLengthPx = Math.min(i, i2);
        buildDrawables();
        if (!this.mPracticeMode) {
            loadPattern(this.mCurrentPattern, this.mHighlightMode);
        }
    }

    public void setPattern(List<Point> list) {
        clearPattern(this.mCurrentPattern);
        loadPattern(list, this.mHighlightMode);
        this.mCurrentPattern = list;
    }

    public List<Point> getPattern() {
        return this.mCurrentPattern;
    }

    public void setGridLength(int i) {
        this.mLengthNodes = i;
        this.mCurrentPattern = Collections.emptyList();
        buildDrawables();
    }

    public int getGridLength() {
        return this.mLengthNodes;
    }

    public void setHighlightMode(HighlightMode highlightMode) {
        setHighlightMode(highlightMode, this.mPracticeMode);
    }

    public void setHighlightMode(HighlightMode highlightMode, boolean z) {
        this.mHighlightMode = highlightMode;
        if (!z) {
            loadPattern(this.mCurrentPattern, this.mHighlightMode);
        }
    }

    public HighlightMode getHighlightMode() {
        return this.mHighlightMode;
    }

    public void setPracticeMode(boolean z) {
        this.mDisplayingPracticeResult = false;
        this.mPracticeMode = z;
        if (z) {
            this.mPracticePattern = new ArrayList();
            this.mPracticePool = new HashSet();
            clearPattern(this.mCurrentPattern);
            return;
        }
        clearPattern(this.mPracticePattern);
        loadPattern(this.mCurrentPattern, this.mHighlightMode);
    }

    public boolean getPracticeMode() {
        return this.mPracticeMode;
    }

    public void setTactileFeedbackEnabled(boolean z) {
        this.mTactileFeedback = z;
    }

    public boolean getTactileFeedbackEnabled() {
        return this.mTactileFeedback;
    }
}
