package com.netsdo.denguetracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.netsdo.Temperature;
import com.netsdo.gattsensor.OnObjectTemperatureListener;
import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.events.VerticalPagerSwitchedEvent;
import com.squareup.otto.Subscribe;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TempRecFragment extends Fragment {
    private final static String TAG = "TempRecFragment";

    private static int VPOS = 1; //VerticalPage Position, for main Fragment only, should be sync with position in activity_main.xml

    private MainActivity mParentActivity;
    private InfoDB mInfoDB;

    private TextView mtempsaveHolder;
    private SeekBar mtemppickerHolder;
    private TextView mtempHolder;
    private ToggleButton mtempalertswitchHolder;
    private SeekBar mtempalertpickerHolder;
    private TextView mtempalerttimeHolder;
    private TextView mmedisaveHolder;
    private Spinner mmedipickerHolder;
    private Spinner mmediqtypickerHolder;
    private ToggleButton mmedialertswitchHolder;
    private SeekBar mmedialertpickerHolder;
    private TextView mmedialerttimeHolder;

    private long mTemp;
    private Date mNextTempAlert;
    private Date mNextMediAlert;

    // graph
    private XYPlot plot;
    private SimpleXYSeries sensorValueHistorySeries;

    private Double maxAddedValue = null;
    private Double minAddedValue = null;

    public TempRecFragment() {
        Log.d(TAG, "Constructor");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        if (null != saved) {
            // Restore state here
        }
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View fragmentView = inflater.inflate(R.layout.fragment_temp_rec, container, false);
        mParentActivity = (MainActivity) getActivity();
        mInfoDB = mParentActivity.mInfoDB;

        configureGraph(fragmentView);
        addDataFromHistory();

        loadData(); // Info is loaded into mInfoArray

        mtempsaveHolder = (TextView) fragmentView.findViewById(R.id.temp_save);
        mtemppickerHolder = (SeekBar) fragmentView.findViewById(R.id.temp_picker);
        mtempHolder = (TextView) fragmentView.findViewById(R.id.temp);
        mtempalertswitchHolder = (ToggleButton) fragmentView.findViewById(R.id.temp_alert_switch);
        mtempalertpickerHolder = (SeekBar) fragmentView.findViewById(R.id.temp_alert_picker);
        mtempalerttimeHolder = (TextView) fragmentView.findViewById(R.id.temp_alert_time);
        mmedisaveHolder = (TextView) fragmentView.findViewById(R.id.medi_save);
        mmedipickerHolder = (Spinner) fragmentView.findViewById(R.id.medi_picker);
        mmediqtypickerHolder = (Spinner) fragmentView.findViewById(R.id.medi_qty_picker);
        mmedialertswitchHolder = (ToggleButton) fragmentView.findViewById(R.id.medi_alert_switch);
        mmedialertpickerHolder = (SeekBar) fragmentView.findViewById(R.id.medi_alert_picker);
        mmedialerttimeHolder = (TextView) fragmentView.findViewById(R.id.medi_alert_time);

        mtemppickerHolder.setOnSeekBarChangeListener(new SeekBarChangeListener());
        mtemppickerHolder.setMax((int) (Temperature.HUMAN_MAX - Temperature.HUMAN_MIN) * 10); // set value between min and max of human temperature, use 1 decimal.
        mtempsaveHolder.setOnClickListener(new ClickListener());
        mmedisaveHolder.setOnClickListener(new ClickListener());

        ((MainActivity) getActivity()).addObjectTemperatureListener(new ObjectTemperatureListener());

        return fragmentView;
    }

    @Override
    public void onViewCreated(android.view.View view, @android.support.annotation.Nullable android.os.Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onViewStateRestored(@android.support.annotation.Nullable android.os.Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        EventBus.getInstance().register(this);
        onActive();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        EventBus.getInstance().unregister(this);
        onInActive();

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged, HIDDEN:" + hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHint, True");
            onActive();
        } else {
            Log.d(TAG, "setUserVisibleHint, False");
            onInActive();
        }
    }

    @Subscribe
    public void eventSwitched(VerticalPagerSwitchedEvent event) {
        Log.d(TAG, "evenSwitched");
        switch (event.isSwitched(VPOS)) {
            case VerticalPagerSwitchedEvent.INACTIVE:
                onInActive();
                break;
            case VerticalPagerSwitchedEvent.NOCHANGE:
                break;
            case VerticalPagerSwitchedEvent.ACTIVE:
                onActive();
                break;
            default:
                break;
        }
    }

    public void onActive() {
        Log.d(TAG, "onActive");
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");

        if (loadData()) {
            showData();
        }
    }

    public boolean loadData() {
        return true;
    }

    public void showData() {
        return;
    }

    private void configureGraph(View fragmentView) {
        plot = (XYPlot) fragmentView.findViewById(R.id.temp_plot);
        plot.setDomainLabel("");
        plot.setRangeLabel("");

        plot.setDomainValueFormat(new TimeLabelFormat());

        sensorValueHistorySeries = new SimpleXYSeries("SensorValue");

        LineAndPointFormatter lineAndPointFormatter = new LineAndPointFormatter(Color.BLACK, Color.TRANSPARENT, Color.TRANSPARENT, null);
        Paint paint = lineAndPointFormatter.getLinePaint();
        paint.setStrokeWidth(8);
        lineAndPointFormatter.setLinePaint(paint);
        plot.addSeries(sensorValueHistorySeries, lineAndPointFormatter);

        plot.getGraphWidget().setDomainLabelOrientation(-90);
        plot.getGraphWidget().setDomainLabelVerticalOffset(-35);
        plot.getGraphWidget().getDomainLabelPaint().setTextAlign(Paint.Align.RIGHT);

        plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);

        plot.setDomainStep(XYStepMode.SUBDIVIDE, 15);

        // Colors
        plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        // Remove legend
        plot.getLayoutManager().remove(plot.getLegendWidget());
        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
        plot.getLayoutManager().remove(plot.getRangeLabelWidget());
        plot.getLayoutManager().remove(plot.getTitleWidget());

        plot.getGraphWidget().setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));
    }

    private void addDataFromHistory() {
        List<SensorValueHistoryItem> history = getHistoryItems();
        for (SensorValueHistoryItem item : history) {
            addToGraph(item.getTimestamp(), item.getSensorValue());
        }
    }

    protected List<SensorValueHistoryItem> getHistoryItems() {
        ObjectTemperatureHistory historyStore = new ObjectTemperatureHistory();
        return historyStore.getAll(getActivity());
    }

    protected void addToGraph(long timestamp, Double value) {

        if (plot != null && value != null) {
            double roundedValue = Temperature.round(value);

            sensorValueHistorySeries.addLast(timestamp, Temperature.round(roundedValue));

            updateMinMaxValues(roundedValue);
            setBoundaries();
            setRangeScale();

            plot.redraw();
        }
    }

    private void updateMinMaxValues(double roundedValue) {
        if (maxAddedValue == null || roundedValue > maxAddedValue) {
            maxAddedValue = roundedValue;
        }
        if (minAddedValue == null || roundedValue < minAddedValue) {
            minAddedValue = roundedValue;
        }
    }

    ;

    private void setBoundaries() {
        if (minAddedValue.doubleValue() == maxAddedValue.doubleValue()) {
            plot.setRangeBoundaries(minAddedValue - 0.1, maxAddedValue + 0.1, BoundaryMode.FIXED);
        } else {
            plot.setRangeBoundaries(minAddedValue, maxAddedValue, BoundaryMode.AUTO);
        }
    }

    private void setRangeScale() {
        double difference = maxAddedValue.doubleValue() - minAddedValue.doubleValue();
        if (difference == 0 || difference <= 1.0) {
            plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.1);
        } else {
            double step = difference / 14;
            double roundedStep = new BigDecimal(step).setScale(1, RoundingMode.HALF_UP).doubleValue();
            plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, roundedStep);
        }
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String lihow;
            String liwhat;
            switch (v.getId()) {
                case R.id.temp_save:
                    lihow = "TempRec";
                    liwhat = Temperature.toString(mtemppickerHolder.getProgress() / 10 + Temperature.HUMAN_MIN);
                    break;
                case R.id.medi_save:
                    lihow = "MediTake";
                    liwhat = "MediA";
                    break;
                default:
                    lihow = "Unknown";
                    liwhat = "Unknow";
            }

            Info lInfo = new Info();
            lInfo.setrowid(Info.ZEROLONG);
            lInfo.setiwhen(new SimpleDateFormat(getString(R.string.iso6301)).format(new Date()));
            lInfo.setihow(lihow);
            lInfo.setiwhat(liwhat);
            String lsInfo = lInfo.getInfo();
            if (lsInfo == Info.NULLSTRING) {
                return;
            } else {
                if (mInfoDB.insertInfo(lsInfo) == 0) {
                    Log.d(TAG, "ClickListener, no record lsInfo:" + lsInfo);
                }
            }
            if (loadData()) {
                showData();
            }
        }
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // not handle it
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // not handle it
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mtempHolder.setText(Temperature.toString(mtemppickerHolder.getProgress() / 10 + Temperature.HUMAN_MIN));
        }
    }

    private final class TimeLabelFormat extends Format {
        private static final long serialVersionUID = 2204112458107503528L;

        private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            long timestamp = ((Number) obj).longValue();
            Date date = new Date(timestamp);
            return dateFormat.format(date, toAppendTo, pos);
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return null;
        }
    }

    public class ObjectTemperatureListener implements OnObjectTemperatureListener {
        @Override
        public void onObjectTemperatureUpdate(Context context, Double updatedTemperature) {
            addToGraph(System.currentTimeMillis(), updatedTemperature);
        }
    }
/*
    // Definition of the touch states
    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;

    PointF firstFinger;
    float distBetweenFingers;
    boolean stopThread = false;

    public class TouchLIstener implements View.OnTouchListener {
        public boolean onTouch(View arg0, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: // Start gesture
                    firstFinger = new PointF(event.getX(), event.getY());
                    mode = ONE_FINGER_DRAG;
                    stopThread = true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN: // second finger
                    distBetweenFingers = spacing(event);
                    // the distance check is done to avoid false alarms
                    if (distBetweenFingers > 5f) {
                        mode = TWO_FINGERS_DRAG;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == ONE_FINGER_DRAG) {
                        PointF oldFirstFinger = firstFinger;
                        firstFinger = new PointF(event.getX(), event.getY());
                        scroll(oldFirstFinger.x - firstFinger.x);
                        mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
                                BoundaryMode.FIXED);
                        mySimpleXYPlot.redraw();

                    } else if (mode == TWO_FINGERS_DRAG) {
                        float oldDist = distBetweenFingers;
                        distBetweenFingers = spacing(event);
                        zoom(oldDist / distBetweenFingers);
                        mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
                                BoundaryMode.FIXED);
                        mySimpleXYPlot.redraw();
                    }
                    break;
            }
            return true;
        }
        private void zoom(float scale) {
            float domainSpan = maxXY.x - minXY.x;
            float domainMidPoint = maxXY.x - domainSpan / 2.0f;
            float offset = domainSpan * scale / 2.0f;

            minXY.x = domainMidPoint - offset;
            maxXY.x = domainMidPoint + offset;

            minXY.x = Math.min(minXY.x, series[3].getX(series[3].size() - 3)
                    .floatValue());
            maxXY.x = Math.max(maxXY.x, series[0].getX(1).floatValue());
            clampToDomainBounds(domainSpan);
        }

        private void scroll(float pan) {
            float domainSpan = maxXY.x - minXY.x;
            float step = domainSpan / mySimpleXYPlot.getWidth();
            float offset = pan * step;
            minXY.x = minXY.x + offset;
            maxXY.x = maxXY.x + offset;
            clampToDomainBounds(domainSpan);
        }

        private void clampToDomainBounds(float domainSpan) {
            float leftBoundary = series[0].getX(0).floatValue();
            float rightBoundary = series[3].getX(series[3].size() - 1).floatValue();
            // enforce left scroll boundary:
            if (minXY.x < leftBoundary) {
                minXY.x = leftBoundary;
                maxXY.x = leftBoundary + domainSpan;
            } else if (maxXY.x > series[3].getX(series[3].size() - 1).floatValue()) {
                maxXY.x = rightBoundary;
                minXY.x = rightBoundary - domainSpan;
            }
        }

        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return FloatMath.sqrt(x * x + y * y);
        }
    }
    */
}
