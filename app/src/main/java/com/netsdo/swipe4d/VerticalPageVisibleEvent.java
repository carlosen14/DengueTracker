package com.netsdo.swipe4d;

public class VerticalPageVisibleEvent {

    private int mPosition = 0;

    public VerticalPageVisibleEvent(int position) {
        mPosition = position;
    }

    public boolean setVisible(int position) {
        if (position == mPosition) {
            return true;
        } else {
            return false;
        }
    }
}
