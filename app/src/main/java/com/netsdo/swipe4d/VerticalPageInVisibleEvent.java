package com.netsdo.swipe4d;

import android.util.Log;

public class VerticalPageInVisibleEvent {
    private static String TAG = "VerticalPageInVisibleEvent";

    private int mPosition = 0;

    public VerticalPageInVisibleEvent(int position) {
        mPosition = position;
    }

    public boolean setInVisible(int position) {
        if (position == mPosition) {
            return true;
        } else {
            return false;
        }
    }
}
