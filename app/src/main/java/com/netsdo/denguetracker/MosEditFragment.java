package com.netsdo.denguetracker;

import com.antistatic.spinnerwheel.AbstractWheel;
import com.antistatic.spinnerwheel.OnWheelScrollListener;
import com.antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;
import com.antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import com.antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.MosEditEvent;
import com.netsdo.swipe4d.SwitchToPageEvent;
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

    private DayArrayAdapter mDayAdapter;
    private Calendar mCalendar;
    private ArrayWheelAdapter<String> miwhatAdapter;

    private TextView mrowidHolder;
    private AbstractWheel mDayHolder;
    private AbstractWheel mHourHolder;
    private AbstractWheel mMinHolder;
    private AbstractWheel miwhatHolder;

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String clickOn;
            switch (v.getId()) {
                case R.id.iwhere:
                    Log.d(TAG, "ClickListener:" + mInfo.getInfo());
                    Uri geoLocation = Uri.parse(mInfo.getiwhere("1"));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(geoLocation);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    clickOn = "iwhere";
                    break;
                case R.id.delete:
                    if (mInfo.getrowid() != Info.NULLLONG) {
                        mInfoHandler.deleteInfo(mInfo.getrowid());
                    }
                    EventBus.getInstance().post(new SwitchToPageEvent(1)); // hardcode to switch back to MosList
                    clickOn = "delete";
                    break;
                default:
                    clickOn = "Unknown";
            }
            Log.d(TAG, "onClick, " + clickOn);
        }
    }

    private class WheelScrollListener implements OnWheelScrollListener {
        public void onScrollingStarted(AbstractWheel wheel) {
            // not use the event
        }

        public void onScrollingFinished(AbstractWheel wheel) {
            // 0123456789  0  12345678901234567
            // yyyy-MM-dd\'T\'HH:mm:ss.SSSZ
            String liwhen = mInfo.getiwhen();
            switch (wheel.getId()) {
                case R.id.hour:
                    Log.d(TAG, liwhen.substring(0, 11) + "T" + String.format("%02d", mHourHolder.getCurrentItem()) + ":" + liwhen.substring(13, 28));
                    mInfo.setiwhen(liwhen.substring(0, 11) + String.format("%02d", mHourHolder.getCurrentItem()) + liwhen.substring(13, 28));
                    break;
                case R.id.min:
                    Log.d(TAG, liwhen.substring(0, 14) + "T" + String.format("%02d", mMinHolder.getCurrentItem()) + ":" + liwhen.substring(16, 28));
                    mInfo.setiwhen(liwhen.substring(0, 14) + String.format("%02d", mMinHolder.getCurrentItem()) + liwhen.substring(16, 28));
                    break;
                case R.id.day:
                    Log.d(TAG, (String) mDayAdapter.getItemText(mDayHolder.getCurrentItem()));
                    mInfo.setiwhen(((String) mDayAdapter.getItemText(mDayHolder.getCurrentItem())).substring(0, 10) + liwhen.substring(10, 28));
                    break;
                case R.id.iwhat:
                    Log.d(TAG, (String) miwhatAdapter.getItemText(miwhatHolder.getCurrentItem()));
                    mInfo.setiwhat(mInfo.getlistiwhat()[miwhatHolder.getCurrentItem()]);
                    break;
                default:
                    break;
            }
            Toast.makeText(wheel.getContext(), "iwhen:" + mDayAdapter.getItemText(mDayHolder.getCurrentItem()) + String.format("%02d", mHourHolder.getCurrentItem()) + ":" + String.format("%02d", mMinHolder.getCurrentItem()) + ", iwhat:" + miwhatAdapter.getItemText(miwhatHolder.getCurrentItem()), Toast.LENGTH_SHORT).show();
            updateInfo();
        }
    }

    private class DayArrayAdapter extends AbstractWheelTextAdapter {
        //todo # simplify the dayscount configuration
        private final int daysCount = 30; // show date back to 20 days before and 10 days after

        // Calendar
        Calendar calendar;

        protected DayArrayAdapter(Context context, Calendar calendar) {
            super(context, R.layout.picker_day, NO_RESOURCE);

            this.calendar = calendar;
            setItemTextResource(R.id.time2_monthday);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            int day = -daysCount + index + 10; // set default index to last 10th position
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.roll(Calendar.DAY_OF_YEAR, day);

            View view = super.getItem(index, cachedView, parent);

            TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            monthday.setText(format.format(newCalendar.getTime()));
            if (day > 0) {
                monthday.setTextColor(0xFF0000F0); // future days
            } else {
                monthday.setTextColor(0xFF111111); // past day
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
            lcalendar.add(Calendar.DATE, -daysCount + index + 10); // adjust text index based on default index

            return new SimpleDateFormat(context.getString(R.string.iso6301)).format(lcalendar.getTime());
        }

        @Override
        protected void notifyDataChangedEvent() {
            super.notifyDataChangedEvent();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View lFragment = inflater.inflate(R.layout.fragment_mos_edit, container, false);

        mParentActivity = (MainActivity) getActivity();
        mInfoHandler = mParentActivity.mInfoHandler;
        mInfo = new Info();

        //todo * during initial loading, the edit page is not triggered to load first record in list page.
        loadInfo();

        mrowidHolder = (TextView) lFragment.findViewById(R.id.rowid);

        mCalendar = Calendar.getInstance(Locale.getDefault());
        mDayHolder = (AbstractWheel) lFragment.findViewById(R.id.day);
        mDayAdapter = new DayArrayAdapter(this.getActivity(), mCalendar);
        mDayHolder.setViewAdapter(mDayAdapter);

        mHourHolder = (AbstractWheel) lFragment.findViewById(R.id.hour);
        mHourHolder.setViewAdapter(new NumericWheelAdapter(this.getActivity(), 0, 23, "%02d"));
        mHourHolder.setCyclic(true);

        mMinHolder = (AbstractWheel) lFragment.findViewById(R.id.min);
        mMinHolder.setViewAdapter(new NumericWheelAdapter(this.getActivity(), 0, 59, "%02d"));
        mMinHolder.setCyclic(true);

        miwhatHolder = (AbstractWheel) lFragment.findViewById(R.id.iwhat);
        miwhatAdapter = new ArrayWheelAdapter<String>(this.getActivity(), mInfo.getlistiwhat("0"));
        miwhatAdapter.setItemResource(R.layout.picker_string);
        miwhatAdapter.setItemTextResource(R.id.text);
        miwhatHolder.setViewAdapter(miwhatAdapter);

        Button liwhereButton = (Button) lFragment.findViewById(R.id.iwhere);
        Button lDeleteButton = (Button) lFragment.findViewById(R.id.delete);


        liwhereButton.setOnClickListener(new ClickListener());
        lDeleteButton.setOnClickListener(new ClickListener());

        mDayHolder.addScrollingListener(new WheelScrollListener());
        mHourHolder.addScrollingListener(new WheelScrollListener());
        mMinHolder.addScrollingListener(new WheelScrollListener());
        miwhatHolder.addScrollingListener(new WheelScrollListener());

        return lFragment;
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
        mInfo.setrowid(event.getrowid());
    }

    private boolean loadInfo() {
        // return true if data is loaded
        // return false if data is not loaded
        String lInfo;

        if (mInfo.getrowid() == Info.NULLLONG) {
            return false; // no data
        }

        lInfo = mInfoHandler.selectInfo(mInfo.getrowid());
        if (lInfo == null) {
            return false; // no data
        }

        mInfo.setInfo(lInfo);

        Date lDate;
        try {
            lDate = (new SimpleDateFormat(mParentActivity.getString(R.string.iso6301))).parse(mInfo.getiwhen());
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // iwhen is wrong, no data to show
        }
        mCalendar.setTime(lDate);


        return true; // data is loaded
    }

    private boolean showInfo() {
        mrowidHolder.setText(String.format("%d", mInfo.getrowid()));
        miwhatHolder.setCurrentItem(Integer.valueOf(mInfo.getiwhat("1")));
        mDayHolder.setCurrentItem(20); // set to day of iwhen at position 20
        mDayAdapter.notifyDataChangedEvent(); // refresh mDayHolder
        // 0123456789  0  12345678901234567
        // yyyy-MM-dd\'T\'HH:mm:ss.SSSZ
        mHourHolder.setCurrentItem(Integer.valueOf(mInfo.getiwhen().substring(11, 13)));
        mMinHolder.setCurrentItem(Integer.valueOf(mInfo.getiwhen().substring(14, 16)));

        return true;
    }

    private long updateInfo() {
        return mInfoHandler.updateInfo(mInfo.getInfo());
    }
}
