package com.netsdo.denguetracker;

import com.antistatic.spinnerwheel.AbstractWheel;
import com.antistatic.spinnerwheel.OnWheelScrollListener;
import com.antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;
import com.antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import com.antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.MosEditEvent;
import com.squareup.otto.Subscribe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MosEditFragment extends Fragment {
    private static String TAG = "MosEditFragment";

    private MainActivity mParentActivity;
    private InfoHandler mInfoHandler;

    private Info mInfo;

    private Long mrowid;

    private DayArrayAdapter mDayAdapter;
    private Calendar mCalendar;
    private ArrayWheelAdapter<String> miwhatAdapter;

    private TextView mrowidHolder;
    private AbstractWheel mDayHolder;
    private AbstractWheel mHourHolder;
    private AbstractWheel mMinHolder;
    private AbstractWheel miwhatHolder;

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String clickOn;
            switch (v.getId()) {
                case R.id.iwhere:
//                    Uri geoLocation = Uri.parse("geo:0,0?q=1.3569602420177331,103.88373221520939(Mosquito Bite on Right Leg)&z=20");
                    Uri geoLocation = Uri.parse(mInfo.getiwhere(4));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(geoLocation);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    clickOn = "iwhere";
                    break;
                default:
                    clickOn = "Unknown";
            }
            Log.d(TAG, "onClick, " + clickOn);
        }
    };

    OnWheelScrollListener mScrollListener = new OnWheelScrollListener() {
        public void onScrollingStarted(AbstractWheel wheel) {
            // not handle the event
        }

        public void onScrollingFinished(AbstractWheel wheel) {
            switch (wheel.getId()) {
                case R.id.hour:
                    Toast.makeText(wheel.getContext(), "hour:" + mHourHolder.getCurrentItem() + ":" + mMinHolder.getCurrentItem(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.min:
                    Toast.makeText(wheel.getContext(), "mMinHolder:" + mHourHolder.getCurrentItem() + ":" + mMinHolder.getCurrentItem(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.day:
                    Toast.makeText(wheel.getContext(), "mDayHolder:" + mDayAdapter.getItemText(mDayHolder.getCurrentItem()), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iwhat:
                    Toast.makeText(wheel.getContext(), "iwhat:" + miwhatAdapter.getItemText(miwhatHolder.getCurrentItem()), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private class DayArrayAdapter extends AbstractWheelTextAdapter {
        private final int daysCount = 20; // show date back to 3 weeks before

        // Calendar
        Calendar calendar;

        protected DayArrayAdapter(Context context, Calendar calendar) {
            super(context, R.layout.time_picker_custom_day, NO_RESOURCE);

            this.calendar = calendar;
            setItemTextResource(R.id.time2_monthday);
        }

        public int getToday() {
            return daysCount;
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            int day = -daysCount + index; // set default item to last item
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.roll(Calendar.DAY_OF_YEAR, day);

            View view = super.getItem(index, cachedView, parent);

            TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            monthday.setText(format.format(newCalendar.getTime()));
            if (day == 0) {
                monthday.setTextColor(0xFF0000F0);
            } else {
                monthday.setTextColor(0xFF111111);
            }
            return view;
        }

        @Override
        public int getItemsCount() {
            return daysCount + 1;
        }

        @Override
        protected CharSequence getItemText(int index) {
            Calendar lcalendar = (Calendar) calendar.clone();
            lcalendar.add(Calendar.DATE, -daysCount + index);

            return new SimpleDateFormat(context.getString(R.string.iso6301)).format(lcalendar.getTime());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_mos_edit, container, false);

        mParentActivity = (MainActivity) getActivity();
        mInfoHandler = mParentActivity.mInfoHandler;
        mInfo = new Info();

        mHourHolder = (AbstractWheel) fragmentView.findViewById(R.id.hour);
        mHourHolder.setViewAdapter(new NumericWheelAdapter(this.getActivity(), 0, 23, "%02d"));
        mHourHolder.setCyclic(true);

        mMinHolder = (AbstractWheel) fragmentView.findViewById(R.id.min);
        mMinHolder.setViewAdapter(new NumericWheelAdapter(this.getActivity(), 0, 59, "%02d"));
        mMinHolder.setCyclic(true);

        miwhatHolder = (AbstractWheel) fragmentView.findViewById(R.id.iwhat);
        miwhatAdapter = new ArrayWheelAdapter<String>(this.getActivity(), new String[]{"Head", "Body", "RightArm", "RightHand", "RightLeg", "RightFoot", "LeftArm", "LeftHand", "LeftLeg", "LeftFoot"});
        miwhatAdapter.setItemResource(R.layout.wheel_text_centered);
        miwhatAdapter.setItemTextResource(R.id.text);
        miwhatHolder.setViewAdapter(miwhatAdapter);

        // set current time
        mCalendar = Calendar.getInstance(Locale.getDefault());

        mDayHolder = (AbstractWheel) fragmentView.findViewById(R.id.day);
        mDayAdapter = new DayArrayAdapter(this.getActivity(), mCalendar);
        mDayHolder.setViewAdapter(mDayAdapter);

        mrowidHolder = (TextView) fragmentView.findViewById(R.id.rowid);

        Button liwhereButton = (Button) fragmentView.findViewById(R.id.iwhere);
        Button lDeleteButton = (Button) fragmentView.findViewById(R.id.delete);

        liwhereButton.setOnClickListener(mClickListener);
        lDeleteButton.setOnClickListener(mClickListener);

        mHourHolder.addScrollingListener(mScrollListener);
        mMinHolder.addScrollingListener(mScrollListener);
        mDayHolder.addScrollingListener(mScrollListener);
        miwhatHolder.addScrollingListener(mScrollListener);

        return fragmentView;
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHintTrue");
            onActive();
        } else {
            Log.d(TAG, "setUserVisibleHintFalse");
            onInActive();
        }
    }

    public void onActive() {
        Log.d(TAG, "onActive");

        if (loadInfo()) {
            showInfo();
        }
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");
    }

    @Subscribe
    public void eventMosEdit(MosEditEvent event) {
        Log.d(TAG, "eventMosEdit, event:" + event.getrowid());
        mrowid = event.getrowid();
    }

    private boolean loadInfo() {
        // return true if data is loaded
        // return false if data is not loaded
        String lInfo;

        if (mrowid == null) {
            return false; // no data
        }

        lInfo = mInfoHandler.selectInfo(mrowid);
        if (lInfo == null) {
            return false; // no data
        }

        mInfo.setInfo(lInfo);

        SimpleDateFormat lFormat = new SimpleDateFormat(mParentActivity.getString(R.string.iso6301));
        Date lDate;
        try {
            lDate = lFormat.parse(mInfo.getiwhen());
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // iwhen is wrong, no data to show
        }
        mCalendar.setTime(lDate);


        return true; // data is loaded
    }

    private boolean showInfo() {
        // 0123456789  0  1234567890123
        // yyyy-MM-dd\'T\'HH:mm:ss.SSSZ
        mrowidHolder.setText(mInfo.getrowid().toString());
        miwhatHolder.setCurrentItem(2); // todo, to map string into number
        mDayAdapter.notifyDataChangedEvent();
        mDayHolder.setCurrentItem(20); // hardcode, todo
        mHourHolder.setCurrentItem(Integer.parseInt(mInfo.getiwhen().substring(11, 13)));
        mMinHolder.setCurrentItem(Integer.parseInt(mInfo.getiwhen().substring(14, 16)));

        return true;
    }
}
