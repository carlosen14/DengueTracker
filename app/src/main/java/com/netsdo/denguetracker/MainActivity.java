package com.netsdo.denguetracker;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.PageChangedEvent;
import com.netsdo.swipe4d.VerticalPager;
import com.squareup.otto.Subscribe;

public class MainActivity extends FragmentActivity {

	private static final int START_PAGE_INDEX = 0;

	private VerticalPager mVerticalPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
	}

	private void findViews() {
		mVerticalPager = (VerticalPager) findViewById(R.id.activity_main_vertical_pager);
		initViews();
	}

	private void initViews() {
        snapPageWhenLayoutIsReady(mVerticalPager, START_PAGE_INDEX);
	}

	private void snapPageWhenLayoutIsReady(final View pageView, final int page) {
		/*
		 * VerticalPager is not fully initialized at the moment, so we want to snap to the central page only when it
		 * layout and measure all its pages.
		 */
		pageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				mVerticalPager.snapToPage(page, VerticalPager.PAGE_SNAP_DURATION_INSTANT);

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
					// recommended removeOnGlobalLayoutListener method is available since API 16 only
					pageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				else
					removeGlobalOnLayoutListenerForJellyBean(pageView);
			}

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			private void removeGlobalOnLayoutListenerForJellyBean(final View pageView) {
				pageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
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
	public void onLocationChanged(PageChangedEvent event) {
		mVerticalPager.setPagingEnabled(event.hasVerticalNeighbors());
	}

}
