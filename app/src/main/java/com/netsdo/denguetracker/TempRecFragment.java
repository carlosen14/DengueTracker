package com.netsdo.denguetracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.VerticalPagerSwitchedEvent;
import com.squareup.otto.Subscribe;

public class TempRecFragment extends Fragment {
    private static String TAG = "TempRecFragment";
    private static int VPOS = 1; //VerticalPage Position, for main Fragment only, should be sync with position in activity_main.xml

    private MainActivity parentActivity;
    private InfoHandler mInfo;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_temp_rec, container, false);
        parentActivity = (MainActivity) getActivity();
        mInfo = parentActivity.mInfo;

		return fragmentView;
	}

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        EventBus.getInstance().register(this);
        onActive();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        EventBus.getInstance().unregister(this);
        onInActive();

        super.onPause();
    }

    @Subscribe
    public void evenSwitched(VerticalPagerSwitchedEvent event) {
        Log.d(TAG, "evenSwitched");
        switch (event.getEvent(VPOS)) {
            case -1:
                onInActive();
                break;
            case 0:
                break;
            case 1:
                onActive();
                break;
            default:
                break;
        }
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
