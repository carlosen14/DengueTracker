package com.netsdo.swipe4d.events;

/**
 * Dispatched when the current selected page of the application navigation changed. E.g. user swipes from the center
 * page to the left page.
 */
public class HorizontalPagerSwitchedEvent {
    private final static String TAG = "HorizontalPagerSwitchedEvent";

	// -----------------------------------------------------------------------
	//
	// Constructors
	//
	// -----------------------------------------------------------------------
	// -----------------------------------------------------------------------
	//
	// Fields
	//
	// -----------------------------------------------------------------------
	private boolean mHasVerticalNeighbors = true;

	/**
	 * @param hasVerticalNeighbors
	 *            true if the current selected page has vertical (below and/or above) neighbor pages, false - if not.
	 */
	public HorizontalPagerSwitchedEvent(boolean hasVerticalNeighbors) {
		mHasVerticalNeighbors = hasVerticalNeighbors;
	}

	// -----------------------------------------------------------------------
	//
	// Methods
	//
	// -----------------------------------------------------------------------

	/**
	 * @return true if the page has vertical (below and/or above) neighbor pages, false - if not.
	 */
	public boolean hasVerticalNeighbors() {
		return mHasVerticalNeighbors;
	}

}
