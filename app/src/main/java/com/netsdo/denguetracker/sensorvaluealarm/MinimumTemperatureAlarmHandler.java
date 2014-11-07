package com.netsdo.denguetracker.sensorvaluealarm;

import android.content.Context;

import com.netsdo.Temperature;
import com.netsdo.denguetracker.MainActivity;
import com.netsdo.denguetracker.R;
import com.netsdo.gattsensor.OnObjectTemperatureListener;


/**
 * Issues a notification when the temperature get's lower than the user defined value
 */
public class MinimumTemperatureAlarmHandler extends ValueAlarmHandler implements OnObjectTemperatureListener {

	public MinimumTemperatureAlarmHandler(Context context) {
		super(context);
	}
	
	@Override
	public void onObjectTemperatureUpdate(Context context, Double updatedValue) {
		super.valueChanged(context, updatedValue);
	}
	
	@Override
	protected boolean isAlarmConditionMet(Double updatedValue, Double alarmValue) {
		return updatedValue != null && Temperature.round(updatedValue) < Temperature.round(alarmValue);
	}
	
	@Override
	protected String getValueAlarmEnabledPreferenceKey(Context context) {
		return context.getString(R.string.preference_alarm_minimum_temperature_enabled_key);
	}
	
	@Override
	protected String getValueAlarmValuePreferenceKey(Context context) {
		return context.getString(R.string.preference_alarm_minimum_temperature_value_key);
	}
	
	@Override
	protected String getAlarmNotificationText(Context context, Double updatedValue, Double alarmValue) {
		return context.getString(R.string.minimum_temperature_alarm_notification_text,
				Temperature.toString(updatedValue.longValue()),
                MainActivity.mStringDisplay.getDisplay("TempUnit"),
                Temperature.toString(alarmValue.longValue()),
                MainActivity.mStringDisplay.getDisplay("TempUnit"));
	}

	@Override
	protected String getValueAlarmNotificationTitle(Context context) {
		return context.getString(R.string.minimum_temperature_alarm_notification_title);
	}
}
