package com.kevinrei.chronotrack;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Spacing for RecyclerView
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int horizontalMargin;
    private int verticalMargin;

    public SpacesItemDecoration(int horizontalMargin, int verticalMargin) {
        this.horizontalMargin = horizontalMargin;
        this.verticalMargin = verticalMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = horizontalMargin;
        outRect.right = horizontalMargin;
        outRect.bottom = verticalMargin;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = verticalMargin - 48;
    }
}
