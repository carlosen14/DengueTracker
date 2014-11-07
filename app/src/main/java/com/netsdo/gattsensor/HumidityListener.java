package com.netsdo.gattsensor;

import android.content.Context;

public interface HumidityListener {

	void humidityUpdate(Context context, Double updatedHumidity);
}
