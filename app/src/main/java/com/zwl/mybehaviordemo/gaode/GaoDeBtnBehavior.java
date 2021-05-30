package com.zwl.mybehaviordemo.gaode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.zwl.mybehaviordemo.R;

/**
 * 高德首页按钮处理
 *
 * @author yixiaolunhui
 */
public class GaoDeBtnBehavior extends CoordinatorLayout.Behavior {

    private View rightActions;
    private View topActions;

    public GaoDeBtnBehavior() {
    }

    public GaoDeBtnBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            child.setFitsSystemWindows(true);
        }
        if (rightActions == null) {
            rightActions = parent.findViewById(R.id.rightActions);
        }
        if (topActions == null) {
            topActions = parent.findViewById(R.id.topActions);
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return dependency instanceof LinearLayoutCompat || super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        //判断当前dependency 是内容布局
        if (dependency instanceof LinearLayoutCompat && dependency.getId() == R.id.bottom_sheet) {
            if (rightActions != null) {
                GaoDeBottomSheetBehavior behavior = GaoDeBottomSheetBehavior.from(dependency);
                int middleHeight = behavior.getParentHeight() - behavior.getMiddleHeight() - rightActions.getMeasuredHeight();
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) rightActions.getLayoutParams();
                int newY = dependency.getTop() - rightActions.getHeight() - layoutParams.bottomMargin;
                if (newY >= middleHeight) {
                    rightActions.setTranslationY(newY - layoutParams.bottomMargin);
                } else {
                    rightActions.setTranslationY(middleHeight);
                }
                int offset = behavior.getParentHeight() - behavior.getMiddleHeight() - layoutParams.bottomMargin - dependency.getTop();
                float alpha = 1f - offset * 1.0f / rightActions.getHeight();

                rightActions.setAlpha(alpha);
                if (topActions != null) {
                    topActions.setAlpha(alpha);
                }
            }
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }

}
