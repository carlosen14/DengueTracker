package com.netsdo.denguetracker.sensorvaluealarm;

import android.content.Context;

import com.netsdo.Temperature;
import com.netsdo.denguetracker.MainActivity;
import com.netsdo.denguetracker.R;
import com.netsdo.gattsensor.OnObjectTemperatureListener;

/**
 * Issues a notification when the temperature exceeds the user defined value
 */
public class MaximumTemperatureAlarmHandler extends ValueAlarmHandler implements OnObjectTemperatureListener {

	public MaximumTemperatureAlarmHandler(Context context) {
		super(context);
	}
	
	@Override
	public void onObjectTemperatureUpdate(Context context, Double updatedValue) {
		super.valueChanged(context, updatedValue);
	}
	
	@Override
	protected boolean isAlarmConditionMet(Double updatedValue, Double alarmValue) {
		return updatedValue != null && Temperature.round(updatedValue) > Temperature.round(alarmValue);
	}
	
	@Override
	protected String getValueAlarmEnabledPreferenceKey(Context context) {
		return context.getString(R.string.preference_alarm_maximum_temperature_enabled_key);
	}
	
	@Override
	protected String getValueAlarmValuePreferenceKey(Context context) {
		return context.getString(R.string.preference_alarm_maximum_temperature_value_key);
	}
	
	@Override
	protected String getAlarmNotificationText(Context context, Double updatedValue, Double alarmValue) {
		return context.getString(R.string.maximum_temperature_alarm_notification_text,
                Temperature.toString(updatedValue.longValue()),
                MainActivity.mStringDisplay.getDisplay("TempUnit"),
                Temperature.toString(alarmValue.longValue()),
                MainActivity.mStringDisplay.getDisplay("TempUnit"));
	}

	@Override
	protected String getValueAlarmNotificationTitle(Context context) {
		return context.getString(R.string.maximum_temperature_alarm_notification_title);
	}
}
