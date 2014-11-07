package com.netsdo.gattsensor;

import android.content.Context;

public interface OnObjectTemperatureListener {

	void onObjectTemperatureUpdate(Context context, Double updatedTemperature);
}
