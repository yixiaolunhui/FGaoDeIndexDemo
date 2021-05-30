//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zwl.mybehaviordemo.gaode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.annotation.VisibleForTesting;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;
import androidx.customview.widget.ViewDragHelper;
import androidx.customview.widget.ViewDragHelper.Callback;

import com.zwl.mybehaviordemo.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 高德首页滑动效果
 *
 * @author yixiaolunhui
 */
public class GaoDeBottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_COLLAPSED = 4;
    public static final int STATE_HIDDEN = 5;
    public static final int STATE_HALF_EXPANDED = 6;
    public static final int PEEK_HEIGHT_AUTO = -1;
    private static final float HIDE_THRESHOLD = 0.5F;
    private static final float HIDE_FRICTION = 0.1F;
    public static final int MIDDLE_HEIGHT_AUTO = -1;
    private boolean fitToContents = true;
    private float maximumVelocity;
    private int peekHeight;
    private boolean peekHeightAuto;
    private int peekHeightMin;
    private int lastPeekHeight;
    int fitToContentsOffset;
    int halfExpandedOffset;
    int collapsedOffset;
    boolean hideable;
    private boolean skipCollapsed;
    int state = STATE_COLLAPSED;
    ViewDragHelper viewDragHelper;
    private boolean ignoreEvents;
    private int lastNestedScrollDy;
    private boolean nestedScrolled;
    int parentHeight;
    WeakReference<V> viewRef;
    WeakReference<View> nestedScrollingChildRef;
    private GaoDeBottomSheetBehavior.BottomSheetCallback callback;
    private VelocityTracker velocityTracker;
    int activePointerId;
    private int initialY;
    boolean touchingScrollingChild;
    private Map<View, Integer> importantForAccessibilityMap;
    private final Callback dragCallback;


    private int mMiddleHeight;
    private boolean mMiddleHeightAuto;

    public GaoDeBottomSheetBehavior() {
        this.dragCallback = new NamelessClass_1();
    }

    public GaoDeBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.dragCallback = new NamelessClass_1();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomSheetBehavior_Layout);
        TypedValue value = a.peekValue(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight);
        if (value != null && value.data == -1) {
            this.setPeekHeight(value.data);
        } else {
            this.setPeekHeight(a.getDimensionPixelSize(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, -1));
        }

        this.setHideable(a.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_hideAble, false));
        this.setFitToContents(a.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_fitToContents, true));
        this.setSkipCollapsed(a.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapse, false));
        setMiddleHeight(a.getDimensionPixelSize(R.styleable.BottomSheetBehavior_Layout_behavior_middleHeight, MIDDLE_HEIGHT_AUTO));

        a.recycle();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.maximumVelocity = (float) configuration.getScaledMaximumFlingVelocity();
    }

    class NamelessClass_1 extends Callback {
        NamelessClass_1() {
        }

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            if (GaoDeBottomSheetBehavior.this.state == STATE_DRAGGING) {
                return false;
            } else if (GaoDeBottomSheetBehavior.this.touchingScrollingChild) {
                return false;
            } else {
                if (GaoDeBottomSheetBehavior.this.state == 3 && GaoDeBottomSheetBehavior.this.activePointerId == pointerId) {
                    View scroll = (View) GaoDeBottomSheetBehavior.this.nestedScrollingChildRef.get();
                    if (scroll != null && scroll.canScrollVertically(-1)) {
                        return false;
                    }
                }

                return GaoDeBottomSheetBehavior.this.viewRef != null && GaoDeBottomSheetBehavior.this.viewRef.get() == child;
            }
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            GaoDeBottomSheetBehavior.this.dispatchOnSlide(top);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == 1) {
                GaoDeBottomSheetBehavior.this.setStateInternal(STATE_DRAGGING);
            }

        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int top;
            byte targetState;
            int currentTop;
            if (yvel < 0.0F) {
                if (GaoDeBottomSheetBehavior.this.fitToContents) {
                    currentTop = releasedChild.getTop();

                    if (currentTop < (collapsedOffset + HIDE_THRESHOLD) && currentTop >= halfExpandedOffset) {
                        top = GaoDeBottomSheetBehavior.this.halfExpandedOffset;
                        targetState = STATE_HALF_EXPANDED;
                    } else {
                        top = GaoDeBottomSheetBehavior.this.fitToContentsOffset;
                        targetState = STATE_EXPANDED;
                    }

                } else {
                    currentTop = releasedChild.getTop();
                    if (currentTop > GaoDeBottomSheetBehavior.this.halfExpandedOffset) {
                        top = GaoDeBottomSheetBehavior.this.halfExpandedOffset;
                        targetState = STATE_HALF_EXPANDED;
                    } else {
                        top = 0;
                        targetState = STATE_EXPANDED;
                    }
                }
            } else if (!GaoDeBottomSheetBehavior.this.hideable || !GaoDeBottomSheetBehavior.this.shouldHide(releasedChild, yvel) || releasedChild.getTop() <= GaoDeBottomSheetBehavior.this.collapsedOffset && Math.abs(xvel) >= Math.abs(yvel)) {
                if (yvel != 0.0F && Math.abs(xvel) <= Math.abs(yvel)) {
                    currentTop = releasedChild.getTop();
                    if (currentTop < halfExpandedOffset) {
                        top = GaoDeBottomSheetBehavior.this.halfExpandedOffset;
                        targetState = STATE_HALF_EXPANDED;
                    } else {
                        top = GaoDeBottomSheetBehavior.this.collapsedOffset;
                        targetState = STATE_COLLAPSED;
                    }

                } else {
                    currentTop = releasedChild.getTop();
                    if (GaoDeBottomSheetBehavior.this.fitToContents) {
                        if (Math.abs(currentTop - GaoDeBottomSheetBehavior.this.fitToContentsOffset) < Math.abs(currentTop - GaoDeBottomSheetBehavior.this.collapsedOffset)) {
                            top = GaoDeBottomSheetBehavior.this.fitToContentsOffset;
                            targetState = STATE_EXPANDED;
                        } else {
                            top = GaoDeBottomSheetBehavior.this.collapsedOffset;
                            targetState = STATE_COLLAPSED;
                        }
                    } else if (currentTop < GaoDeBottomSheetBehavior.this.halfExpandedOffset) {
                        if (currentTop < Math.abs(currentTop - GaoDeBottomSheetBehavior.this.collapsedOffset)) {
                            top = 0;
                            targetState = STATE_EXPANDED;
                        } else {
                            top = GaoDeBottomSheetBehavior.this.halfExpandedOffset;
                            targetState = STATE_HALF_EXPANDED;
                        }
                    } else if (Math.abs(currentTop - GaoDeBottomSheetBehavior.this.halfExpandedOffset) < Math.abs(currentTop - GaoDeBottomSheetBehavior.this.collapsedOffset)) {
                        top = GaoDeBottomSheetBehavior.this.halfExpandedOffset;
                        targetState = STATE_HALF_EXPANDED;
                    } else {
                        top = GaoDeBottomSheetBehavior.this.collapsedOffset;
                        targetState = STATE_COLLAPSED;
                    }
                }
            } else {
                top = GaoDeBottomSheetBehavior.this.parentHeight;
                targetState = STATE_HIDDEN;
            }

            if (GaoDeBottomSheetBehavior.this.viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                GaoDeBottomSheetBehavior.this.setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(releasedChild, GaoDeBottomSheetBehavior.this.new SettleRunnable(releasedChild, targetState));
            } else {
                GaoDeBottomSheetBehavior.this.setStateInternal(targetState);
            }

        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return MathUtils.clamp(top, GaoDeBottomSheetBehavior.this.getExpandedOffset(), GaoDeBottomSheetBehavior.this.hideable ? GaoDeBottomSheetBehavior.this.parentHeight : GaoDeBottomSheetBehavior.this.collapsedOffset);
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return child.getLeft();
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return GaoDeBottomSheetBehavior.this.hideable ? GaoDeBottomSheetBehavior.this.parentHeight : GaoDeBottomSheetBehavior.this.collapsedOffset;
        }
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return new GaoDeBottomSheetBehavior.SavedState(super.onSaveInstanceState(parent, child), this.state);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        GaoDeBottomSheetBehavior.SavedState ss = (GaoDeBottomSheetBehavior.SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        if (ss.state != STATE_DRAGGING && ss.state != STATE_SETTLING) {
            this.state = ss.state;
        } else {
            this.state = STATE_COLLAPSED;
        }

    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            child.setFitsSystemWindows(true);
        }

        int savedTop = child.getTop();
        parent.onLayoutChild(child, layoutDirection);
        this.parentHeight = parent.getHeight();
        if (this.peekHeightAuto) {
            if (this.peekHeightMin == 0) {
                this.peekHeightMin = parent.getResources().getDimensionPixelSize(R.dimen.design_bottom_sheet_peek_height_min);
            }

            this.lastPeekHeight = Math.max(this.peekHeightMin, this.parentHeight - parent.getWidth() * 9 / 16);
        } else {
            this.lastPeekHeight = this.peekHeight;
        }
        if (mMiddleHeightAuto) {
            mMiddleHeight = this.parentHeight;
        }
        this.fitToContentsOffset = Math.max(0, this.parentHeight - child.getHeight());
        this.halfExpandedOffset = this.parentHeight - mMiddleHeight;
        this.calculateCollapsedOffset();
        if (this.state == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, this.getExpandedOffset());
        } else if (this.state == STATE_HALF_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, this.halfExpandedOffset);
        } else if (this.hideable && this.state == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, this.parentHeight);
        } else if (this.state == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, this.collapsedOffset);
        } else if (this.state == STATE_DRAGGING || this.state == STATE_SETTLING) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
        }

        if (this.viewDragHelper == null) {
            this.viewDragHelper = ViewDragHelper.create(parent, this.dragCallback);
        }

        this.viewRef = new WeakReference(child);
        this.nestedScrollingChildRef = new WeakReference(this.findScrollingChild(child));
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            this.ignoreEvents = true;
            return false;
        } else {
            int action = event.getActionMasked();
            if (action == 0) {
                this.reset();
            }

            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }

            this.velocityTracker.addMovement(event);
            switch (action) {
                case 0:
                    int initialX = (int) event.getX();
                    this.initialY = (int) event.getY();
                    View scroll = this.nestedScrollingChildRef != null ? (View) this.nestedScrollingChildRef.get() : null;
                    if (scroll != null && parent.isPointInChildBounds(scroll, initialX, this.initialY)) {
                        this.activePointerId = event.getPointerId(event.getActionIndex());
                        this.touchingScrollingChild = true;
                    }

                    this.ignoreEvents = this.activePointerId == -1 && !parent.isPointInChildBounds(child, initialX, this.initialY);
                    break;
                case 1:
                case 3:
                    this.touchingScrollingChild = false;
                    this.activePointerId = -1;
                    if (this.ignoreEvents) {
                        this.ignoreEvents = false;
                        return false;
                    }
                case 2:
            }

            if (!this.ignoreEvents && this.viewDragHelper != null && this.viewDragHelper.shouldInterceptTouchEvent(event)) {
                return true;
            } else {
                View scroll = this.nestedScrollingChildRef != null ? (View) this.nestedScrollingChildRef.get() : null;
                return action == 2 && scroll != null && !this.ignoreEvents && this.state != 1 && !parent.isPointInChildBounds(scroll, (int) event.getX(), (int) event.getY()) && this.viewDragHelper != null && Math.abs((float) this.initialY - event.getY()) > (float) this.viewDragHelper.getTouchSlop();
            }
        }
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        } else {
            int action = event.getActionMasked();
            if (this.state == STATE_DRAGGING && action == 0) {
                return true;
            } else {
                if (this.viewDragHelper != null) {
                    this.viewDragHelper.processTouchEvent(event);
                }

                if (action == 0) {
                    this.reset();
                }

                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }

                this.velocityTracker.addMovement(event);
                if (action == 2 && !this.ignoreEvents && Math.abs((float) this.initialY - event.getY()) > (float) this.viewDragHelper.getTouchSlop()) {
                    this.viewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
                }

                return !this.ignoreEvents;
            }
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        this.lastNestedScrollDy = 0;
        this.nestedScrolled = false;
        return (axes & 2) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (type != 1) {
            View scrollingChild = (View) this.nestedScrollingChildRef.get();
            if (target == scrollingChild) {
                int currentTop = child.getTop();
                int newTop = currentTop - dy;
                if (dy > 0) {
                    if (newTop < this.getExpandedOffset()) {
                        consumed[1] = currentTop - this.getExpandedOffset();
                        ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                        this.setStateInternal(STATE_EXPANDED);
                    } else {
                        consumed[1] = dy;
                        ViewCompat.offsetTopAndBottom(child, -dy);
                        this.setStateInternal(STATE_DRAGGING);
                    }
                } else if (dy < 0 && !target.canScrollVertically(-1)) {
                    if (newTop > this.collapsedOffset && !this.hideable) {
                        consumed[1] = currentTop - this.collapsedOffset;
                        ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                        this.setStateInternal(STATE_COLLAPSED);
                    } else {
                        consumed[1] = dy;
                        ViewCompat.offsetTopAndBottom(child, -dy);
                        this.setStateInternal(STATE_DRAGGING);
                    }
                }

                this.dispatchOnSlide(child.getTop());
                this.lastNestedScrollDy = dy;
                this.nestedScrolled = true;
            }
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int type) {
        if (child.getTop() == this.getExpandedOffset()) {
            this.setStateInternal(STATE_EXPANDED);
        } else if (target == this.nestedScrollingChildRef.get() && this.nestedScrolled) {
            int top;
            byte targetState;
            if (this.lastNestedScrollDy > 0) {
                int currentTop = child.getTop();
                if (currentTop <= collapsedOffset - HIDE_THRESHOLD && currentTop >= halfExpandedOffset) {
                    top = this.halfExpandedOffset;
                    targetState = STATE_HALF_EXPANDED;

                } else {
                    top = this.getExpandedOffset();
                    targetState = STATE_EXPANDED;
                }

            } else if (this.hideable && this.shouldHide(child, this.getYVelocity())) {
                top = this.parentHeight;
                targetState = STATE_HIDDEN;
            } else if (this.lastNestedScrollDy == 0) {
                int currentTop = child.getTop();
                if (this.fitToContents) {
                    if (Math.abs(currentTop - this.fitToContentsOffset) < Math.abs(currentTop - this.collapsedOffset)) {
                        top = this.fitToContentsOffset;
                        targetState = STATE_EXPANDED;
                    } else {
                        top = this.collapsedOffset;
                        targetState = STATE_COLLAPSED;
                    }
                } else if (currentTop < this.halfExpandedOffset) {
                    if (currentTop < Math.abs(currentTop - this.collapsedOffset)) {
                        top = 0;
                        targetState = STATE_EXPANDED;
                    } else {
                        top = this.halfExpandedOffset;
                        targetState = STATE_HALF_EXPANDED;
                    }
                } else if (Math.abs(currentTop - this.halfExpandedOffset) < Math.abs(currentTop - this.collapsedOffset)) {
                    top = this.halfExpandedOffset;
                    targetState = STATE_HALF_EXPANDED;
                } else {
                    top = this.collapsedOffset;
                    targetState = STATE_COLLAPSED;
                }
            } else {
                int currentTop = child.getTop();
                if (currentTop <= halfExpandedOffset + HIDE_THRESHOLD && currentTop > HIDE_THRESHOLD) {
                    top = this.halfExpandedOffset;
                    targetState = STATE_HALF_EXPANDED;
                } else {
                    top = this.collapsedOffset;
                    targetState = STATE_COLLAPSED;
                }
            }

            if (this.viewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                this.setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(child, new GaoDeBottomSheetBehavior.SettleRunnable(child, targetState));
            } else {
                this.setStateInternal(targetState);
            }

            this.nestedScrolled = false;
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, float velocityX, float velocityY) {
        return target == this.nestedScrollingChildRef.get() && (this.state != STATE_EXPANDED || super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY));
    }

    public boolean isFitToContents() {
        return this.fitToContents;
    }

    public void setFitToContents(boolean fitToContents) {
        if (this.fitToContents != fitToContents) {
            this.fitToContents = fitToContents;
            if (this.viewRef != null) {
                this.calculateCollapsedOffset();
            }

            this.setStateInternal(this.fitToContents && this.state == STATE_HALF_EXPANDED ? STATE_HALF_EXPANDED : this.state);
        }
    }

    public final void setPeekHeight(int peekHeight) {
        boolean layout = false;
        if (peekHeight == -1) {
            if (!this.peekHeightAuto) {
                this.peekHeightAuto = true;
                layout = true;
            }
        } else if (this.peekHeightAuto || this.peekHeight != peekHeight) {
            this.peekHeightAuto = false;
            this.peekHeight = Math.max(0, peekHeight);
            this.collapsedOffset = this.parentHeight - peekHeight;
            layout = true;
        }

        if (layout && this.state == STATE_COLLAPSED && this.viewRef != null) {
            V view = (V) this.viewRef.get();
            if (view != null) {
                view.requestLayout();
            }
        }

    }

    public final void setMiddleHeight(int middleHeight) {
        boolean layout = false;
        if (middleHeight == PEEK_HEIGHT_AUTO) {
            if (!mMiddleHeightAuto) {
                mMiddleHeightAuto = true;
                layout = true;
            }
        } else if (mMiddleHeightAuto || mMiddleHeight != middleHeight) {
            mMiddleHeightAuto = false;
            mMiddleHeight = Math.max(0, middleHeight);
            layout = true;
        }
        if (layout && this.state == STATE_COLLAPSED && viewRef != null) {
            V view = viewRef.get();
            if (view != null) {
                view.requestLayout();
            }
        }
    }

    public final int getPeekHeight() {
        return this.peekHeightAuto ? -1 : this.peekHeight;
    }

    public final int getMiddleHeight() {
        return this.mMiddleHeightAuto ? -1 : this.mMiddleHeight;
    }

    public final int getParentHeight() {
        return this.parentHeight;
    }

    public void setHideable(boolean hideable) {
        this.hideable = hideable;
    }

    public boolean isHideable() {
        return this.hideable;
    }

    public void setSkipCollapsed(boolean skipCollapsed) {
        this.skipCollapsed = skipCollapsed;
    }

    public boolean getSkipCollapsed() {
        return this.skipCollapsed;
    }

    public void setBottomSheetCallback(GaoDeBottomSheetBehavior.BottomSheetCallback callback) {
        this.callback = callback;
    }

    public final void setState(final int state) {
        if (state != this.state) {
            if (this.viewRef == null) {
                if (state == STATE_COLLAPSED || state == STATE_EXPANDED || state == STATE_HALF_EXPANDED || this.hideable && state == STATE_HIDDEN) {
                    this.state = state;
                }

            } else {
                final V child = (V) this.viewRef.get();
                if (child != null) {
                    ViewParent parent = child.getParent();
                    if (parent != null && parent.isLayoutRequested() && ViewCompat.isAttachedToWindow(child)) {
                        child.post(new Runnable() {
                            @Override
                            public void run() {
                                GaoDeBottomSheetBehavior.this.startSettlingAnimation(child, state);
                            }
                        });
                    } else {
                        this.startSettlingAnimation(child, state);
                    }

                }
            }
        }
    }

    public final int getState() {
        return this.state;
    }

    void setStateInternal(int state) {
        if (this.state != state) {
            this.state = state;
            if (state != STATE_HALF_EXPANDED && state != STATE_EXPANDED) {
                if (state == STATE_HIDDEN || state == STATE_COLLAPSED) {
                    this.updateImportantForAccessibility(false);
                }
            } else {
                this.updateImportantForAccessibility(true);
            }

            View bottomSheet = (View) this.viewRef.get();
            if (bottomSheet != null && this.callback != null) {
                this.callback.onStateChanged(bottomSheet, state);
            }

        }
    }

    private void calculateCollapsedOffset() {
        if (this.fitToContents) {
            this.collapsedOffset = Math.max(this.parentHeight - this.lastPeekHeight, this.fitToContentsOffset);
        } else {
            this.collapsedOffset = this.parentHeight - this.lastPeekHeight;
        }

    }

    private void reset() {
        this.activePointerId = -1;
        if (this.velocityTracker != null) {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
        }

    }

    boolean shouldHide(View child, float yvel) {
        if (this.skipCollapsed) {
            return true;
        } else if (child.getTop() < this.collapsedOffset) {
            return false;
        } else {
            float newTop = (float) child.getTop() + yvel * HIDE_FRICTION;
            return Math.abs(newTop - (float) this.collapsedOffset) / (float) this.peekHeight > HIDE_THRESHOLD;
        }
    }

    @VisibleForTesting
    View findScrollingChild(View view) {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        } else {
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                int i = 0;

                for (int count = group.getChildCount(); i < count; ++i) {
                    View scrollingChild = this.findScrollingChild(group.getChildAt(i));
                    if (scrollingChild != null) {
                        return scrollingChild;
                    }
                }
            }

            return null;
        }
    }

    private float getYVelocity() {
        if (this.velocityTracker == null) {
            return 0.0F;
        } else {
            this.velocityTracker.computeCurrentVelocity(1000, this.maximumVelocity);
            return this.velocityTracker.getYVelocity(this.activePointerId);
        }
    }

    private int getExpandedOffset() {
        return this.fitToContents ? this.fitToContentsOffset : 0;
    }

    void startSettlingAnimation(View child, int state) {
        int top;
        if (state == STATE_COLLAPSED) {
            top = this.collapsedOffset;
        } else if (state == STATE_HALF_EXPANDED) {
            top = this.halfExpandedOffset;
        } else if (state == STATE_EXPANDED) {
            top = this.getExpandedOffset();
        } else {
            if (!this.hideable || state != STATE_HIDDEN) {
                throw new IllegalArgumentException("Illegal state argument: " + state);
            }

            top = this.parentHeight;
        }

        if (this.viewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            this.setStateInternal(STATE_SETTLING);
            ViewCompat.postOnAnimation(child, new GaoDeBottomSheetBehavior.SettleRunnable(child, state));
        } else {
            this.setStateInternal(state);
        }

    }

    void dispatchOnSlide(int top) {
        View bottomSheet = (View) this.viewRef.get();
        if (bottomSheet != null && this.callback != null) {
            if (top > this.collapsedOffset) {
                this.callback.onSlide(bottomSheet, (float) (this.collapsedOffset - top) / (float) (this.parentHeight - this.collapsedOffset));
            } else {
                this.callback.onSlide(bottomSheet, (float) (this.collapsedOffset - top) / (float) (this.collapsedOffset - this.getExpandedOffset()));
            }
        }

    }

    @VisibleForTesting
    int getPeekHeightMin() {
        return this.peekHeightMin;
    }

    public static <V extends View> GaoDeBottomSheetBehavior<V> from(V view) {
        LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        } else {
            Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
            if (!(behavior instanceof GaoDeBottomSheetBehavior)) {
                throw new IllegalArgumentException("The view is not associated with BottomSheetBehavior");
            } else {
                return (GaoDeBottomSheetBehavior) behavior;
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void updateImportantForAccessibility(boolean expanded) {
        if (this.viewRef != null) {
            ViewParent viewParent = ((View) this.viewRef.get()).getParent();
            if (viewParent instanceof CoordinatorLayout) {
                CoordinatorLayout parent = (CoordinatorLayout) viewParent;
                int childCount = parent.getChildCount();
                if (VERSION.SDK_INT >= 16 && expanded) {
                    if (this.importantForAccessibilityMap != null) {
                        return;
                    }

                    this.importantForAccessibilityMap = new HashMap(childCount);
                }

                for (int i = 0; i < childCount; ++i) {
                    View child = parent.getChildAt(i);
                    if (child != this.viewRef.get()) {
                        if (!expanded) {
                            if (this.importantForAccessibilityMap != null && this.importantForAccessibilityMap.containsKey(child)) {
                                ViewCompat.setImportantForAccessibility(child, (Integer) this.importantForAccessibilityMap.get(child));
                            }
                        } else {
                            if (VERSION.SDK_INT >= 16) {
                                this.importantForAccessibilityMap.put(child, child.getImportantForAccessibility());
                            }

                            ViewCompat.setImportantForAccessibility(child, 4);
                        }
                    }
                }

                if (!expanded) {
                    this.importantForAccessibilityMap = null;
                }

            }
        }
    }

    protected static class SavedState extends AbsSavedState {
        final int state;
        public static final Creator<GaoDeBottomSheetBehavior.SavedState> CREATOR = new ClassLoaderCreator<GaoDeBottomSheetBehavior.SavedState>() {
            @Override
            public GaoDeBottomSheetBehavior.SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new GaoDeBottomSheetBehavior.SavedState(in, loader);
            }

            @Override
            public GaoDeBottomSheetBehavior.SavedState createFromParcel(Parcel in) {
                return new GaoDeBottomSheetBehavior.SavedState(in, (ClassLoader) null);
            }

            @Override
            public GaoDeBottomSheetBehavior.SavedState[] newArray(int size) {
                return new GaoDeBottomSheetBehavior.SavedState[size];
            }
        };

        public SavedState(Parcel source) {
            this(source, (ClassLoader) null);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.state = source.readInt();
        }

        public SavedState(Parcelable superState, int state) {
            super(superState);
            this.state = state;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.state);
        }
    }

    private class SettleRunnable implements Runnable {
        private final View view;
        private final int targetState;

        SettleRunnable(View view, int targetState) {
            this.view = view;
            this.targetState = targetState;
        }

        @Override
        public void run() {
            if (GaoDeBottomSheetBehavior.this.viewDragHelper != null && GaoDeBottomSheetBehavior.this.viewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.view, this);
            } else {
                GaoDeBottomSheetBehavior.this.setStateInternal(this.targetState);
            }

        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({Scope.LIBRARY_GROUP})
    public @interface State {
    }

    public abstract static class BottomSheetCallback {
        public BottomSheetCallback() {
        }

        public abstract void onStateChanged(@NonNull View var1, int var2);

        public abstract void onSlide(@NonNull View var1, float var2);
    }
}
