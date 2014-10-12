package com.netsdo.denguetracker;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.PageChangedEvent;
import com.netsdo.swipe4d.VerticalPager;
import com.squareup.otto.Subscribe;

public class MainActivity extends FragmentActivity {
    private static String TAG = "MainActivity";

    private static final int START_PAGE_INDEX = 0;

    public InfoHandler mInfoHandler;

    private VerticalPager mVerticalPager;

    OnGlobalLayoutListener mLayoutListener = new OnGlobalLayoutListener() {
        @SuppressWarnings("deprecation")
        @Override
        public void onGlobalLayout() {
            mVerticalPager.snapToPage(START_PAGE_INDEX, VerticalPager.PAGE_SNAP_DURATION_INSTANT);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                // recommended removeOnGlobalLayoutListener method is available since API 16 only
                mVerticalPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            else
                removeGlobalOnLayoutListenerForJellyBean();
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        private void removeGlobalOnLayoutListenerForJellyBean() {
            mVerticalPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVerticalPager = (VerticalPager) findViewById(R.id.activity_main_vertical_pager);

        mInfoHandler = new InfoHandler(this);

        initViews();
    }

    private void initViews() {
        /*
         * VerticalPager is not fully initialized at the moment, so we want to snap to the central page only when it
		 * layout and measure all its pages.
		 */
        mVerticalPager.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getInstance().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void eventPageChanged(PageChangedEvent event) {
//        Log.d(TAG, "onPageChanged, hasVerticalNeighbors:" + event.hasVerticalNeighbors());
        mVerticalPager.setPagingEnabled(event.hasVerticalNeighbors()); //allow vertical scroll only if the page is Central Page.
    }
}
