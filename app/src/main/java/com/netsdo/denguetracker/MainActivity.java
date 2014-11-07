package com.netsdo.denguetracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.netsdo.denguetracker.bluetooth.BluetoothDeviceInfo;
import com.netsdo.denguetracker.bluetooth.BluetoothLeService;
import com.netsdo.denguetracker.sensorvaluealarm.MaximumTemperatureAlarmHandler;
import com.netsdo.denguetracker.sensorvaluealarm.MinimumTemperatureAlarmHandler;
import com.netsdo.gattsensor.AmbientTemperatureListener;
import com.netsdo.gattsensor.BarometerGatt;
import com.netsdo.gattsensor.BarometricPressureListener;
import com.netsdo.gattsensor.GattSensor;
import com.netsdo.gattsensor.HumidityListener;
import com.netsdo.gattsensor.HygrometerGatt;
import com.netsdo.gattsensor.OnObjectTemperatureListener;
import com.netsdo.gattsensor.SensorData;
import com.netsdo.gattsensor.ThermometerGatt;
import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.VerticalPager;
import com.netsdo.swipe4d.events.HorizontalPagerSwitchedEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity {
    private final static String TAG = "MainActivity";

    private final static int START_PAGE_INDEX = 0;
    // bluetooth start
    private static final long SENSORS_REFRESH_RATE_IN_MILLISECONDS = TimeUnit.SECONDS.toMillis(5);
    // Requests to other activities
    private static final int REQUEST_TO_ENABLE_BLUETOOTHE_LE = 0;
    private static final ThermometerGatt thermometerGatt = new ThermometerGatt();
    private static final HygrometerGatt hygrometerGatt = new HygrometerGatt();
    private static final BarometerGatt barometerGatt = new BarometerGatt();
    private static final List<GattSensor> gattSensors = new ArrayList<GattSensor>();
    static {
        gattSensors.add(barometerGatt);
        gattSensors.add(hygrometerGatt);
        gattSensors.add(thermometerGatt);
    }
    public static InfoDB mInfoDB;
    public static StringDisplay mStringDisplay;
    private final List<AmbientTemperatureListener> ambientTemperatureListeners = new ArrayList<AmbientTemperatureListener>();
    private final List<OnObjectTemperatureListener> objectTemperatureListeners = new ArrayList<OnObjectTemperatureListener>();
    private final List<HumidityListener> humidityListeners = new ArrayList<HumidityListener>();
    private final List<BarometricPressureListener> barometricPressureListeners = new ArrayList<BarometricPressureListener>();
    private VerticalPager mVerticalPager;
    private Intent bluetoothLeServiceBindingIntent;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeService bluetoothLeService;
    private BroadcastReceiver sensortagUpdateReceiver;
    private BroadcastReceiver bluetoothEventReceiver;
    private volatile BluetoothDeviceInfo connectedDeviceInfo;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize BluetoothLeService");
                finish();
            } else {
                Log.i(TAG, "BluetoothLeService connected");
                if (connectedDeviceInfo != null) {
                    bluetoothLeService.connect(connectedDeviceInfo.getBluetoothDevice() .getAddress());
                }
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeService = null;
            Log.i(TAG, "BluetoothLeService disconnected");
        }
    };
    // bluetooth end
    // Device scan callback.
    // NB! Nexus 4 and Nexus 7 (2012) only provide one scan result per scan
    private BluetoothAdapter.LeScanCallback bluetoothLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            stopScanningForSensortag();
            connectToDevice(new BluetoothDeviceInfo(device, rssi));
        }

        private void connectToDevice(BluetoothDeviceInfo deviceInfo) {
            connectedDeviceInfo = deviceInfo;

            // Register the BroadcastReceiver to handle events from BluetoothAdapter and BluetoothLeService
            IntentFilter bluetoothEventFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            bluetoothEventFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
            bluetoothEventFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);

            bluetoothEventReceiver = new BlueToothEventReceiver();
            registerReceiver(bluetoothEventReceiver, bluetoothEventFilter);

            startBluetoothLeService();
        }
    };
    private ScheduledExecutorService periodicGattSensorUpdateRequestsExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVerticalPager = (VerticalPager) findViewById(R.id.activity_main_vertical_pager);

        mInfoDB = new InfoDB(this);
        mStringDisplay = new StringDisplay();
        mStringDisplay.setLocale(Locale.CHINESE);

        initViews();

        AmbientTemperatureHistory ambientTemperatureHistory = new AmbientTemperatureHistory();
        addAmbientTemperatureListener(ambientTemperatureHistory);

        ObjectTemperatureHistory objectTemperatureHistory = new ObjectTemperatureHistory();
        addObjectTemperatureListener(objectTemperatureHistory);

        ambientTemperatureHistory.deleteAll(this);
        objectTemperatureHistory.deleteAll(this);

        MaximumTemperatureAlarmHandler maximumTemperatureAlarm = new MaximumTemperatureAlarmHandler(this);
        addObjectTemperatureListener(maximumTemperatureAlarm);

        MinimumTemperatureAlarmHandler minimumTemperatureAlarm = new MinimumTemperatureAlarmHandler(this);
        addObjectTemperatureListener(minimumTemperatureAlarm);
    }

    private void initViews() {
        /*
         * VerticalPager is not fully initialized at the moment, so we want to snap to the central page only when it
		 * layout and measure all its pages.
		 */
        mVerticalPager.getViewTreeObserver().addOnGlobalLayoutListener(new GlobalLayoutListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");

        startSensortagConnectionProcedure();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");

        stopScanningForSensortag();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");

        releaseConnectionAndResources();
    }

    @Subscribe
    public void eventPageChanged(HorizontalPagerSwitchedEvent event) {
//        Log.d(TAG, "onPageChanged, hasVerticalNeighbors:" + event.hasVerticalNeighbors());
        mVerticalPager.setPagingEnabled(event.hasVerticalNeighbors()); //allow vertical scroll only if the page is Central Page.
    }

    // bluetooth start
    public void addAmbientTemperatureListener(AmbientTemperatureListener temperatureListener) {
        this.ambientTemperatureListeners.add(temperatureListener);
    }

    public void addObjectTemperatureListener(OnObjectTemperatureListener temperatureListener) {
        this.objectTemperatureListeners.add(temperatureListener);
    }

    public void addHumidityListener(HumidityListener humidityListener) {
        this.humidityListeners.add(humidityListener);
    }

    public void addBarometricPressureListener(BarometricPressureListener barometricPressureListener) {
        this.barometricPressureListeners.add(barometricPressureListener);
    }

    private void startSensortagConnectionProcedure() {
        setupBluetooth();

        if (connectedDeviceInfo == null) {
            if (bluetoothAdapter.isEnabled()) {
                startScanningForSensortag();
            } else {
                requestUserToEnableBluetooth();
            }
        } else {
            Log.i(TAG, "Already connected to device " + connectedDeviceInfo.getBluetoothDevice());
        }
    }

    private void setupBluetooth() {
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }

        if (bluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();

            if (bluetoothAdapter == null) {
                Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void requestUserToEnableBluetooth() {
        // Request for the BlueTooth adapter to be turned on
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        // The next call will (asynchronously) call onActivityResult(...) to
        // report weather or not the user enabled BlueTooth LE
        startActivityForResult(enableIntent, REQUEST_TO_ENABLE_BLUETOOTHE_LE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_TO_ENABLE_BLUETOOTHE_LE:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.bt_on, Toast.LENGTH_SHORT).show();
                    startScanningForSensortag();
                } else {
                    // User did not enable BlueTooth or an error occurred
                    Toast.makeText(this, R.string.bt_not_on, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                Log.e(TAG, "Unknown request code");
                break;
        }
    }

    private void startScanningForSensortag() {
        boolean scanningForBlueToothLeDevices = bluetoothAdapter.startLeScan(bluetoothLeScanCallback);
        if (!scanningForBlueToothLeDevices) {
            Toast.makeText(this, "Failed to search for sensortag", Toast.LENGTH_LONG).show();
            finish();
        }
    }

        private void stopScanningForSensortag() {
        bluetoothAdapter.stopLeScan(bluetoothLeScanCallback);
    };

    private void reconnect() {
        releaseConnectionAndResources();
        startSensortagConnectionProcedure();
    }

    private void releaseConnectionAndResources() {
        stopScanningForSensortag();

        if (bluetoothLeService != null) {
            bluetoothLeService.close();
            unbindService(serviceConnection);
            bluetoothLeService = null;
        }
        if (bluetoothEventReceiver != null) {
            unregisterReceiver(bluetoothEventReceiver);
            bluetoothEventReceiver = null;
        }
        if (bluetoothLeServiceBindingIntent != null) {
            stopService(bluetoothLeServiceBindingIntent);
            bluetoothLeServiceBindingIntent = null;
        }
        if (sensortagUpdateReceiver != null) {
            unregisterReceiver(sensortagUpdateReceiver);
            sensortagUpdateReceiver = null;
        }
        if (periodicGattSensorUpdateRequestsExecutor != null) {
            periodicGattSensorUpdateRequestsExecutor.shutdown();
            try {
                periodicGattSensorUpdateRequestsExecutor.awaitTermination(5, TimeUnit.SECONDS);
                periodicGattSensorUpdateRequestsExecutor = null;
            } catch (InterruptedException e) {
                Log.e(TAG, "Periodic updater was not stopped within the timeout period");
            }
        }

        connectedDeviceInfo = null;
    }

    private void discoverServices() {
        if (BluetoothLeService.getBluetoothGatt().discoverServices()) {
            Log.i(TAG, "Start service discovery");
        } else {
            Log.e(TAG, "Service discovery start failed");
        }
    }

    private void onCharacteristicRead(String uuidStr, byte[] value, int status) {
        if (value != null) {
            if (uuidStr.equals(BarometerGatt.UUID_CALIBRATION.toString())) {
                barometerGatt.processCalibrationResults(value);
            } else {
                processChangedGattCharacteristic(uuidStr, value);
            }
        } else {
            Log.w(TAG, "Sensor value is null");
        }
    }

        public void onCharacteristicChanged(String uuidStr, byte[] value) {
        if (value != null) {
            processChangedGattCharacteristic(uuidStr, value);
        } else {
            Log.w(TAG, "Sensor value is null");
        }
    };

    private void processChangedGattCharacteristic(String uuidStr, byte[] value) {
        if (uuidStr.equals(thermometerGatt.getDataUuid().toString())) {
            SensorData sensorData = thermometerGatt.convert(value);
            for (AmbientTemperatureListener listener : ambientTemperatureListeners) {
                listener.ambientTemperatureUpdate(this, sensorData.getX());
            }
            for (OnObjectTemperatureListener listener : objectTemperatureListeners) {
                listener.onObjectTemperatureUpdate(this, sensorData.getY());
            }
        } else if (uuidStr.equals(hygrometerGatt.getDataUuid().toString())) {
            SensorData sensorData = hygrometerGatt.convert(value);
            for (HumidityListener listener : humidityListeners) {
                listener.humidityUpdate(this, sensorData.getX());
            }
        } else if (uuidStr.equals(barometerGatt.getDataUuid().toString())) {
            SensorData sensorData = barometerGatt.convert(value);
            for (BarometricPressureListener listener : barometricPressureListeners) {
                listener.barometricPressureUpdate(this, sensorData.getX());
            }
        } else {
            Log.e(TAG, "Unknown uuid: " + uuidStr);
        }
    }

    private void startBluetoothLeService() {
        bluetoothLeServiceBindingIntent = new Intent(this, BluetoothLeService.class);
        startService(bluetoothLeServiceBindingIntent);
        boolean serviceSuccessfullyBound = bindService(bluetoothLeServiceBindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (serviceSuccessfullyBound)
            Log.d(TAG, "BluetoothLeService was successfully bound");
        else {
            Toast.makeText(this, "Bind to BluetoothLeService failed", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class GlobalLayoutListener implements OnGlobalLayoutListener {
        @SuppressWarnings("deprecation")
        @Override
        public void onGlobalLayout() {
            mVerticalPager.snapToPage(START_PAGE_INDEX, VerticalPager.PAGE_SNAP_DURATION_INSTANT);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                // recommended removeOnGlobalLayoutListener method is available since API 16 only
                mVerticalPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            else
                removeGlobalOnLayoutListenerForJellyBean();
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        private void removeGlobalOnLayoutListenerForJellyBean() {
            mVerticalPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

// Listens for broadcasted events from BlueTooth adapter and BluetoothLeService
    private class BlueToothEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                bluetoothAdapterActionStateChanged();
            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                bluetoothLeServiceGattConnected(context, intent);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                bluetoothLeServiceGattDisconnected(context, intent);
            } else {
                Log.w(TAG, "Unknown action: " + action);
            }
        }

        private void bluetoothLeServiceGattDisconnected(Context context, Intent intent) {
            connectedDeviceInfo = null;
/* fragment handling, not relevant to bluetooth handling
            Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
            if (!(currentFragment instanceof SensorDataFragment)) {
                getFragmentManager().popBackStackImmediate();
            }
            currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof SensorDataFragment) {
                ((SensorDataFragment)currentFragment).clearAllSensorValues();
            }
*/
            reconnect();
        }

        private void bluetoothLeServiceGattConnected(Context context, Intent intent) {
            int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS, BluetoothGatt.GATT_FAILURE);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                sensortagUpdateReceiver = new SensortagUpdateReceiver();
                registerReceiver(sensortagUpdateReceiver, createSensorTagUpdateIntentFilter());
                discoverServices();

                Log.i(TAG, "Successfully connected to sensortag");
            } else {
                Toast.makeText(context, "Connecting to the sensortag failed. Status: " + status, Toast.LENGTH_LONG).show();
                finish();
            }
        }

        private void bluetoothAdapterActionStateChanged() {
            switch (bluetoothAdapter.getState()) {
                case BluetoothAdapter.STATE_ON:
                    startBluetoothLeService();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    finish();
                    break;
                default:
                    Log.w(TAG, "Action STATE CHANGED not processed: " + bluetoothAdapter.getState());
                    break;
            }
        }

        private IntentFilter createSensorTagUpdateIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE);
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_READ);
            return intentFilter;
        }
    }

private class SensortagUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS, BluetoothGatt.GATT_SUCCESS);

            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "Services discovered");
                    startGattSensorDataUpdates();
                } else {
                    Toast.makeText(getApplication(), "Service discovery failed", Toast.LENGTH_LONG).show();
                    return;
                }
            } else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                onCharacteristicChanged(uuidStr, value);
            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                Log.d(TAG, "onCharacteristicWrite: " + uuidStr);
            } else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                onCharacteristicRead(uuidStr, value, status);
            }

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "GATT error code: " + status);
            }
        }

        private void startGattSensorDataUpdates() {
            for (GattSensor gattSensor: gattSensors) {
                gattSensor.calibrate();
                gattSensor.enable();
            }
            periodicGattSensorUpdateRequestsExecutor = new ScheduledThreadPoolExecutor(1);
            int startDelay = 500;
            PeriodicGattSensorUpdateRequester periodicGattSensorUpdateRequester = new PeriodicGattSensorUpdateRequester(gattSensors);
            periodicGattSensorUpdateRequestsExecutor.scheduleWithFixedDelay(periodicGattSensorUpdateRequester, startDelay, SENSORS_REFRESH_RATE_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        }
    }
    // bluetooth end

}
