package com.netsdo.gattsensor;

import android.content.Context;

public interface AmbientTemperatureListener {

	void ambientTemperatureUpdate(Context context, Double updatedTemperature);
}
