package com.netsdo.denguetracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MosEditFragment extends Fragment {
    private static String TAG = "MosEditFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_mos_edit, container, false);
		return fragmentView;
	}

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

//        onActive();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

//        onInActive();

        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHintTrue");
            onActive();
        }
        else {
            Log.d(TAG, "setUserVisibleHintFalse");
            onInActive();
        }
    }

    public void onActive() {
        Log.d(TAG, "onActive");
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");
    }
}
