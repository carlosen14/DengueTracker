package com.netsdo.denguetracker;

import com.netsdo.gattsensor.GattSensor;

import java.util.List;

/**
 * Runnable that initiates a read on all known gatt sensors
 */
public class PeriodicGattSensorUpdateRequester implements Runnable {

	private final List<GattSensor> gattSensors;

	public PeriodicGattSensorUpdateRequester(List<GattSensor> gattSensors) {
		this.gattSensors = gattSensors;
	}
	
	@Override
	public void run() {
		for (final GattSensor gattSensor: gattSensors) {
			gattSensor.read();	
		}
	}
}
