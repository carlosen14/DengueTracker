package com.netsdo.denguetracker.sensorvaluealarm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.netsdo.denguetracker.R;

public abstract class ValueAlarmHandler {

	private static final int ALARM_NOTIFICATION_ID = 1;
	private final String LOG_TAG = this.getClass().getSimpleName();
	private final String ALARM_NOTIFICATION_TAG = this.getClass().getSimpleName();
	private final String valueAlarmEnabledPreferenceKey;
	private final String valueAlarmValuePreferenceKey;
	private final SharedPreferences.OnSharedPreferenceChangeListener preferenceListener;
	private double DEFAULT_ALARM_VALUE = 29.0;
	private double alarmValue = DEFAULT_ALARM_VALUE;
	private boolean alarmEnabled = true;
	private NotificationCompat.Builder notificationBuilder;
	
	public ValueAlarmHandler(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		valueAlarmEnabledPreferenceKey = getValueAlarmEnabledPreferenceKey(context);
		valueAlarmValuePreferenceKey = getValueAlarmValuePreferenceKey(context);
		
		// Use instance field for listener
		// It will not be gc'd as long as this instance is kept referenced
		preferenceListener = new PreferenceListener();
		sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener);
		
		// Set initial values
		preferenceListener.onSharedPreferenceChanged(sharedPreferences, valueAlarmEnabledPreferenceKey);
		preferenceListener.onSharedPreferenceChanged(sharedPreferences, valueAlarmValuePreferenceKey);
	}

	protected abstract String getValueAlarmValuePreferenceKey(Context context);

	protected abstract String getValueAlarmEnabledPreferenceKey(Context context);
	
	protected abstract String getAlarmNotificationText(Context context, Double updatedValue, Double alarmValue);
	
	protected abstract String getValueAlarmNotificationTitle(Context context);
	
	protected abstract boolean isAlarmConditionMet(Double updatedValue, Double alarmValue);
	
	protected void valueChanged(Context context, Double updatedValue) {
		if (alarm(updatedValue)) {
			createNotification(context, updatedValue);
		}
	}

	private boolean alarm(Double updatedValue) {
//		return alarmEnabled && isAlarmConditionMet(updatedValue, alarmValue);
        return isAlarmConditionMet(updatedValue, 29.0);
	}
	
	private void createNotification(Context context, Double updatedValue) {
		String message = getAlarmNotificationText(context, updatedValue, alarmValue);
		
		Log.i(this.getClass().getSimpleName(), "Notification message: " + message);
		
		configureNotificationBuilder(context, message);
		
		// Needed for autoCancel to work...
		PendingIntent notifyPIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);     
		notificationBuilder.setContentIntent(notifyPIntent);
		
		NotificationManager notificationyMgr = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		notificationyMgr.notify(ALARM_NOTIFICATION_TAG, ALARM_NOTIFICATION_ID, notificationBuilder.build());
	}

	private void configureNotificationBuilder(Context context, String message) {
		if (notificationBuilder == null) {
			notificationBuilder = new NotificationCompat.Builder(context);
		}
		notificationBuilder
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(getValueAlarmNotificationTitle(context))
			.setContentText(message)
			.setOnlyAlertOnce(true)
			.setAutoCancel(true)
			.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
	}
		
	private void logState() {
		StringBuffer sb = new StringBuffer("Alarm enabled: ").append(alarmEnabled);
		if (alarmEnabled) {
			sb.append(" alarm value: ").append(alarmValue);
		}
		Log.i(LOG_TAG, sb.toString());
	}
	
	/**
	 * Handles changes in the alarm preferences by updating the fields
	 */
	private class PreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key.equals(valueAlarmEnabledPreferenceKey)) {
				alarmEnabled = sharedPreferences.getBoolean(key, true);
				if (!alarmEnabled) {
					alarmValue = DEFAULT_ALARM_VALUE;
				}
			} else if (key.equals(valueAlarmValuePreferenceKey)) {
				alarmValue = (double) sharedPreferences.getFloat(key, 29.0f);
			}
			logState();
		}
	}
}
