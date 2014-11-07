package com.netsdo.denguetracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netsdo.gattsensor.OnObjectTemperatureListener;

import java.util.List;

/**
 * <pre>
 * Fragment containing a graph that shows the sensor values over time.
 * "Domain" value is timestamp at which the sample has been taken
 * "Range" value is the sensor value
 * </pre>
 */
public class ObjectTemperatureHistoryFragment extends TemperatureHistoryFragment implements OnObjectTemperatureListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
        // Register as a temperature listener
        ((MainActivity)getActivity()).addObjectTemperatureListener(this);
        return view;
	}
	
	@Override
	public void onObjectTemperatureUpdate(Context context, Double updatedTemperature) {
		addToGraph(System.currentTimeMillis(), updatedTemperature);
	}
	
	@Override
	protected List<SensorValueHistoryItem> getHistoryItems() {
		ObjectTemperatureHistory historyStore = new ObjectTemperatureHistory();
		return historyStore.getAll(getActivity());
	}
}
