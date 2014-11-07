package com.netsdo.denguetracker;

import android.content.Context;

import com.netsdo.gattsensor.OnObjectTemperatureListener;

import java.util.List;

/**
 * Maintains the object temperature history
 */
public class ObjectTemperatureHistory implements OnObjectTemperatureListener {

    private static final String DB_SENSOR_NAME = "object_temperature";

    @Override
    public void onObjectTemperatureUpdate(Context context, Double updatedTemperature) {
        SensorValueHistoryDatabase.getInstance(context).addSensorValue(DB_SENSOR_NAME, updatedTemperature);
    }

    public void deleteAll(Context context) {
        SensorValueHistoryDatabase.getInstance(context).deleteAll(DB_SENSOR_NAME);
    }

    public List<SensorValueHistoryItem> getAll(Context context) {
        return SensorValueHistoryDatabase.getInstance(context).getAllHistory(DB_SENSOR_NAME);
    }
}
