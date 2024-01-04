package com.example.vault.securitylocks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.example.vault.R;

public class SetLockPatternView extends View {
    public static final long BUILD_TIMEOUT_MILLIS = 1000;
    public static final float CELL_NODE_RATIO = 0.9f;
    public static final int DEFAULT_LENGTH_NODES = 3;
    public static final int DEFAULT_LENGTH_PX = 50;
    public static final int DEFAULT_PATTERN_GREEN_LINE_COLOR = NodeDrawable1.green;
    public static final int DEFAULT_PATTERN_RED_LINE_COLOR = NodeDrawable1.red;
    public static final int EDGE_COLOR = -4008213;
    public static boolean IspatternContinue = false;
    protected static boolean IspatternDone = false;
    public static final float NODE_EDGE_RATIO = 0.33f;
    public static final int PRACTICE_RESULT_DISPLAY_MILLIS = 1000;
    public static final int TACTILE_FEEDBACK_DURATION = 35;
    protected static Paint mLinePaint;
    Context con;
    protected int mCellLength;
    protected List<Point> mCurrentPattern = Collections.emptyList();
    protected boolean mDisplayingPracticeResult = false;
    protected boolean mDrawTouchExtension = false;
    protected Paint mEdgePaint = new Paint();
    protected Handler mHandler = new Handler();
    protected HighlightMode mHighlightMode = new NoHighlight();
    protected int mLengthNodes = 3;
    protected int mLengthPx = 50;
    protected NodeDrawable1[][] mNodeDrawables = ((NodeDrawable1[][]) Array.newInstance(NodeDrawable1.class, new int[]{0, 0}));
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
            return SetLockPatternView.this.mNodeDrawables[point.x][point.y].getCenter();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static class FailureHighlight implements HighlightMode {
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            SetLockPatternView.mLinePaint.setColor(SetLockPatternView.DEFAULT_PATTERN_RED_LINE_COLOR);
            SetLockPatternView.mLinePaint.setAlpha(100);
            return 4;
        }
    }

    public static class FirstHighlight implements HighlightMode {
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            return i == 0 ? 2 : 1;
        }
    }

    public interface HighlightMode {
        int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5);
    }

    public static class NoHighlight implements HighlightMode {
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            return 1;
        }
    }

    public static class RainbowHighlight implements HighlightMode {
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            nodeDrawable1.setCustomColor(Color.HSVToColor(new float[]{(((float) i) / ((float) i2)) * 360.0f, 1.0f, 1.0f}));
            return 5;
        }
    }

    public static class SuccessHighlight implements HighlightMode {
        public int select(NodeDrawable1 nodeDrawable1, int i, int i2, int i3, int i4, int i5) {
            SetLockPatternView.mLinePaint.setColor(SetLockPatternView.DEFAULT_PATTERN_GREEN_LINE_COLOR);
            SetLockPatternView.mLinePaint.setAlpha(100);
            return 3;
        }
    }

    public SetLockPatternView(Context context, AttributeSet attributeSet) {
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
    }

    private void buildDrawables() {
        int i = this.mLengthNodes;
        this.mNodeDrawables = (NodeDrawable1[][]) Array.newInstance(NodeDrawable1.class, new int[]{i, i});
        this.mCellLength = this.mLengthPx / this.mLengthNodes;
        float f = ((float) this.mCellLength) * 0.9f;
        this.mEdgePaint.setStrokeWidth(0.33f * f);
        this.mTouchThreshold = (int) (f / 2.0f);
        int i2 = this.mCellLength / 2;
        long currentTimeMillis = System.currentTimeMillis();
        for (int i3 = 0; i3 < this.mLengthNodes; i3++) {
            for (int i4 = 0; i4 < this.mLengthNodes; i4++) {
                if (System.currentTimeMillis() - currentTimeMillis >= 1000) {
                    PatternActivityMethods.clearAndBail(getContext());
                }
                int i5 = this.mCellLength;
                this.mNodeDrawables[i4][i3] = new NodeDrawable1(f, new Point((i4 * i5) + i2, (i5 * i3) + i2), this.con);
            }
        }
        if (!this.mPracticeMode) {
            loadPattern(this.mCurrentPattern, this.mHighlightMode);
        }
    }


    public void clearPattern(List<Point> list) {
        for (Point point : list) {
            this.mNodeDrawables[point.x][point.y].setNodeState(0);
            mLinePaint.setColor(DEFAULT_PATTERN_GREEN_LINE_COLOR);
            mLinePaint.setAlpha(100);
        }
    }

    private void loadPattern(List<Point> list, HighlightMode highlightMode) {
        for (int i = 0; i < list.size(); i++) {
            Point point = (Point) list.get(i);
            NodeDrawable1 nodeDrawable1 = this.mNodeDrawables[point.x][point.y];
            nodeDrawable1.setNodeState(highlightMode.select(nodeDrawable1, i, list.size(), point.x, point.y, this.mLengthNodes));
            if (i < list.size() - 1) {
                Point point2 = (Point) list.get(i + 1);
                Point center = this.mNodeDrawables[point.x][point.y].getCenter();
                Point center2 = this.mNodeDrawables[point2.x][point2.y].getCenter();
                this.mNodeDrawables[point.x][point.y].setExitAngle((float) Math.atan2((double) (center.y - center2.y), (double) (center.x - center2.x)));
            }
        }
    }

    private void appendPattern(List<Point> list, Point point) {
        NodeDrawable1 nodeDrawable1 = this.mNodeDrawables[point.x][point.y];
        nodeDrawable1.setNodeState(1);
        if (list.size() > 0) {
            Point point2 = (Point) list.get(list.size() - 1);
            NodeDrawable1 nodeDrawable12 = this.mNodeDrawables[point2.x][point2.y];
            Point center = nodeDrawable12.getCenter();
            Point center2 = nodeDrawable1.getCenter();
            nodeDrawable12.setExitAngle((float) Math.atan2((double) (center.y - center2.y), (double) (center.x - center2.x)));
        }
        list.add(point);
    }

    private void testPracticePattern() {
        this.mDisplayingPracticeResult = true;
        HighlightMode highlightMode = this.mPracticeSuccessMode;
        if (this.mPracticePattern.equals(this.mCurrentPattern)) {
            highlightMode = this.mPracticeSuccessMode;
        }
        loadPattern(this.mPracticePattern, highlightMode);
        setPattern(this.mPracticePattern);
        if (!SecurityLocksCommon.IsCancel) {
            if (this.mPracticePattern.size() <= 3) {
                SetPatternActivity.txtdrawpattern.setText(R.string.f0x5389e0a);
                loadPattern(this.mPracticePattern, this.mPracticeFailureMode);
                this.mDisplayingPracticeResult = false;
            } else if (!SecurityLocksCommon.IsSiginPattern) {
                IspatternContinue = true;
                SecurityLocksCommon.IsCancel = true;
                SetPatternActivity.txtdrawpattern.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Patternrecorded);
                SetPatternActivity.ll_ContinueOrDone.setBackgroundResource(R.drawable.btn_bottom_baar_album);
                SetPatternActivity.lblContinueOrDone.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Continue);
                SetPatternActivity.ll_Cancel.setBackgroundResource(R.drawable.btn_bottom_baar_album);
                SetPatternActivity.lblCancel.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Retry);
                SecurityLocksCommon.mSiginPattern.clear();
                for (Point add : this.mPracticePattern) {
                    SecurityLocksCommon.mSiginPattern.add(add);
                }
                SecurityLocksCommon.IsSiginPattern = true;
            }
        }
        if (SecurityLocksCommon.IsSiginPatternConfirm) {
            SecurityLocksCommon.mSiginPatternConfirm = this.mPracticePattern;
            if (SecurityLocksCommon.mSiginPatternConfirm.equals(SecurityLocksCommon.mSiginPattern)) {
                HighlightMode highlightMode2 = this.mPracticeSuccessMode;
                SecurityLocksCommon.IsSiginPatternConfirm = true;
                SecurityLocksCommon.IsSiginPattern = false;
                SetPatternActivity.txtdrawpattern.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Newpatternrecorded);
                IspatternDone = true;
                SecurityLocksCommon.IsCancel = false;
                SetPatternActivity.ll_ContinueOrDone.setBackgroundResource(R.drawable.btn_bottom_baar_album);
                SetPatternActivity.lblContinueOrDone.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Confirm);
                SetPatternActivity.ll_Cancel.setBackgroundResource(R.drawable.btn_bottom_baar_album);
                SetPatternActivity.lblCancel.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Cancel);
            } else {
                SetPatternActivity.txtdrawpattern.setText(R.string.lblsetting_SecurityCredentials_Setpattern_Tryagain);
                loadPattern(this.mPracticePattern, this.mPracticeFailureMode);
                this.mDisplayingPracticeResult = false;
            }
        }
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!SetLockPatternView.this.mDisplayingPracticeResult) {
                    SetLockPatternView setLockPatternView = SetLockPatternView.this;
                    setLockPatternView.clearPattern(setLockPatternView.mCurrentPattern);
                    SetLockPatternView.this.resetPractice();
                    SetLockPatternView.this.invalidate();
                }
            }
        }, 1000);
    }

    public boolean testPattern(List<Point> list, List<Point> list2) {
        return list.equals(list2);
    }

    public void resetPractice() {
        clearPattern(this.mPracticePattern);
        clearPattern(this.mCurrentPattern);
        this.mPracticePattern.clear();
        this.mCurrentPattern.clear();
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
            mLinePaint.setStrokeWidth(30.0f);
            mLinePaint.setAlpha(100);
            mLinePaint.setStrokeWidth(9.0f);
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
        if (!IspatternDone && !IspatternContinue) {
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
            size = 50;
            setMeasuredDimension(50, 50);
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
