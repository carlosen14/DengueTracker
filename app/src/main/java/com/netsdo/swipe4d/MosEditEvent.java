package com.netsdo.swipe4d;

import android.util.Log;

public class MosEditEvent {
    private static String TAG = "MosEditEvent";

    private Long mRowID = Long.valueOf(0);

    public MosEditEvent(Long rowid) {
        mRowID = rowid;
    }

    public long getrowid() {
        return mRowID;
    }
}
