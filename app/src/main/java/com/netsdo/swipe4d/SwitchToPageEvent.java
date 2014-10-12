package com.netsdo.swipe4d;

public class SwitchToPageEvent {
    private static String TAG = "SwitchToPageEvent";

    private int mPosition = 0;

    public SwitchToPageEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}
