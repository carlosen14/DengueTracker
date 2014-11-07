package com.netsdo.denguetracker;

import android.content.Context;

import com.netsdo.gattsensor.AmbientTemperatureListener;

import java.util.List;


/**
 * Maintains the ambient temperature history
 */
public class AmbientTemperatureHistory implements AmbientTemperatureListener {

	private static final String DB_SENSOR_NAME = "ambient_temperature";
	
	@Override
	public void ambientTemperatureUpdate(Context context, Double updatedTemperature) {
		SensorValueHistoryDatabase.getInstance(context).addSensorValue(DB_SENSOR_NAME, updatedTemperature);
	}

	public void deleteAll(Context context) {
		SensorValueHistoryDatabase.getInstance(context).deleteAll(DB_SENSOR_NAME);
	}
	
	public List<SensorValueHistoryItem> getAll(Context context) {
		return SensorValueHistoryDatabase.getInstance(context).getAllHistory(DB_SENSOR_NAME);
	}
}
