package com.netsdo.gattsensor;

import android.content.Context;

public interface BarometricPressureListener {

	void barometricPressureUpdate(Context context, Double updatedBarometricPressure);
}
