package com.xly.middlelibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class HeaderBehavior extends CoordinatorLayout.Behavior<View> {

    private int maxTranslation;
    private int headerHeight;
    private BottomSheetBehavior bottomSheetBehavior;

    public HeaderBehavior() {
        super();
    }

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        /*if (dependency.getId() == R.id.bottom_sheet) {
            bottomSheetBehavior = BottomSheetBehavior.from(dependency);
            return true;
        }*/
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        // 初始化参数
        headerHeight = child.getHeight();
        maxTranslation = -(headerHeight / 2);
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (bottomSheetBehavior == null) return false;

        // 计算滑动比例 (0-1)
        float progress = getProgress(parent,dependency);

        // 应用联动效果
        applyHeaderTransformation(child, progress);

        return true;
    }

    private float getProgress(CoordinatorLayout parent,View dependency) {
        int peekHeight = bottomSheetBehavior.getPeekHeight();
        float current = dependency.getY();
        float min = 0; // 完全展开位置
        float max = parent.getHeight() - peekHeight; // 初始位置

        // 当面板在展开和折叠之间时
        if (current >= min && current <= max) {
            return 1 - ((current - min) / (max - min));
        }

        // 超出展开位置时（向下拖拽）
        if (current < min) {
            return 1 + (min - current) / max;
        }

        // 低于折叠位置时（向上拖拽）
        if (current > max) {
            return (current - max) / max;
        }

        return 0;
    }

    private void applyHeaderTransformation(View header, float progress) {
        // 限制progress在0-1.5范围内（允许一些弹性效果）
        float clampedProgress = Math.max(0, Math.min(progress, 1.5f));

        // 1. 上移效果（非视差）
        float translationY = maxTranslation * clampedProgress;
        header.setTranslationY(translationY);

        // 2. 缩放效果（在0.7-1范围内）
        float scale = 1.0f - (0.3f * clampedProgress);
        header.setScaleX(scale);
        header.setScaleY(scale);

        // 3. 透明度效果
        float alpha = 1.0f - (0.5f * clampedProgress);
        header.setAlpha(alpha);
    }
}
