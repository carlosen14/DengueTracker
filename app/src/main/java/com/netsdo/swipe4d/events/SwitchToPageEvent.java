package com.netsdo.swipe4d.events;

public class SwitchToPageEvent {
    private static String TAG = "SwitchToPageEvent";

    private int mPage = 0;

    public SwitchToPageEvent(int page) {
        mPage = page;
    }

    public int getPage() {
        return mPage;
    }
}
