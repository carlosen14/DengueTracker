package com.netsdo.swipe4d;

import android.util.Log;

public class MosEditEvent {
    private static String TAG = "MosEditEvent";

    private Long mrowid = Long.valueOf(0);

    public MosEditEvent(Long rowid) {
        mrowid = rowid;
    }

    public void setrowid(Long rowid) {
        mrowid = rowid;
    }

    public Long getrowid() {
        return mrowid;
    }
}
