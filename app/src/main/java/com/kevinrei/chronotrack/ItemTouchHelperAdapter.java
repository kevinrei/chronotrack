package com.kevinrei.chronotrack;

/**
 * Source: https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf#.uf17llcr0
 */
public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
