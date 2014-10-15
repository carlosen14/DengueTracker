package com.netsdo.denguetracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.FragmentsClassesPagerAdapter;
import com.netsdo.swipe4d.PageChangedEvent;
import com.netsdo.swipe4d.SwitchToPageEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class MosCompositeFragment extends Fragment {
    private static String TAG = "MosCompositeFragment";

    private ViewPager mHorizontalPager;
    private int mCentralPageIndex = 0;
    private class PageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected, position:" + position + ", mCentralPageIndex:" + mCentralPageIndex);
            EventBus.getInstance().post(new PageChangedEvent(mCentralPageIndex == position));
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_mos_acomp, container, false);
        mHorizontalPager = (ViewPager) fragmentView.findViewById(R.id.fragment_composite_mos_pager);

        initViews();

        return fragmentView;
    }

    private void initViews() {
        populateHorizontalPager();
        mHorizontalPager.setCurrentItem(mCentralPageIndex); //todo + modify here to record and display to last page user used if user restart the app
        mHorizontalPager.setOnPageChangeListener(new PageChangeListener());
    }

    private void populateHorizontalPager() {
        ArrayList<Class<? extends Fragment>> pages = new ArrayList<Class<? extends Fragment>>();
        pages.add(MosBiteFragment.class);
        pages.add(MosListFragment.class);
        pages.add(MosEditFragment.class);
        mCentralPageIndex = pages.indexOf(MosBiteFragment.class); //set default Central Page
        mHorizontalPager.setAdapter(new FragmentsClassesPagerAdapter(getChildFragmentManager(), getActivity(), pages));
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        EventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        EventBus.getInstance().unregister(this);

        super.onPause();
    }

    @Subscribe
    public void eventSwitchToPage(SwitchToPageEvent event) {
        mHorizontalPager.setCurrentItem(event.getPage());
    }
}
