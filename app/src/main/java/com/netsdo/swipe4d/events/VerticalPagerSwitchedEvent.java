package com.netsdo.swipe4d.events;

import android.util.Log;

public class VerticalPagerSwitchedEvent {
    private final static String TAG = "VerticalPagerSwitchedEvent";

    private final static int INACTIVE = -1;
    private final static int NOCHANGE = 0;
    private final static int ACTIVE = 1;

    private int mOldPage = 0;
    private int mNewPage = 0;

    public VerticalPagerSwitchedEvent(int oldPage, int newPage) {
        mOldPage = oldPage;
        mNewPage = newPage;
    }

    public int isSwitched(int myPage) {
        Log.d(TAG, "getEvent, myPage:" + myPage + ", oldPage:" + mOldPage + ", newPage:" + mNewPage);
        if (mOldPage == mNewPage) {
            return NOCHANGE;
        }
        if (myPage == mOldPage) {
            return INACTIVE;
        }
        if (myPage == mNewPage) {
            return ACTIVE;
        }
        return NOCHANGE;
    }
}
