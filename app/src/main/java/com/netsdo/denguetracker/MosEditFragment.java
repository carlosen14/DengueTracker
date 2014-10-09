package com.netsdo.denguetracker;

import com.antistatic.spinnerwheel.AbstractWheel;
import com.antistatic.spinnerwheel.OnWheelChangedListener;
import com.antistatic.spinnerwheel.OnWheelScrollListener;
import com.antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;
import com.antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import com.antistatic.spinnerwheel.adapters.NumericWheelAdapter;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MosEditFragment extends Fragment {
    private static String TAG = "MosEditFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_mos_edit, container, false);

        final AbstractWheel hours = (AbstractWheel) fragmentView.findViewById(R.id.hour);
        hours.setViewAdapter(new NumericWheelAdapter(this.getActivity(), 0, 23));
        hours.setCyclic(true);

        final AbstractWheel mins = (AbstractWheel) fragmentView.findViewById(R.id.mins);
        mins.setViewAdapter(new NumericWheelAdapter(this.getActivity(), 0, 59, "%02d"));
        mins.setCyclic(true);

        final AbstractWheel iwhat = (AbstractWheel) fragmentView.findViewById(R.id.iwhat);
        ArrayWheelAdapter<String> iwhatAdapter = new ArrayWheelAdapter<String>(this.getActivity(), new String[]{"Head", "Body", "RightArm", "RightHand", "RightLeg", "RightFoot", "LeftArm", "LeftHand", "LeftLeg", "LeftFoot"});
        iwhatAdapter.setItemResource(R.layout.wheel_text_centered);
        iwhatAdapter.setItemTextResource(R.id.text);
        iwhat.setViewAdapter(iwhatAdapter);

        // set current time
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE));
        iwhat.setCurrentItem(calendar.get(Calendar.AM_PM));

        final AbstractWheel day = (AbstractWheel) fragmentView.findViewById(R.id.day);
        calendar.add(Calendar.DATE, -3);
        final DayArrayAdapter dayAdapter = new DayArrayAdapter(this.getActivity(), calendar);
        day.setViewAdapter(dayAdapter);
        day.setCurrentItem(dayAdapter.getToday());

        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String biteOn;
                switch (v.getId()) {
                    case R.id.positiona:
                        Uri geoLocation = Uri.parse("geo:0,0?q=1.3569602420177331,103.88373221520939(Mosquito Bite on Right Leg)&z=20");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(geoLocation);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        biteOn = "positiona";
                        break;
                    default:
                        biteOn = "Unknown";
                }
                Log.d(TAG, "onClick, " + biteOn);
            }
        };
        Button posbutton = (Button) fragmentView.findViewById(R.id.positiona);
        posbutton.setOnClickListener(buttonListener);

        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                //
            }

            public void onScrollingFinished(AbstractWheel wheel) {
                switch (wheel.getId()){
                    case R.id.hour :
                        Toast.makeText(wheel.getContext(), "Hour:" + hours.getCurrentItem() + ":" + mins.getCurrentItem(), Toast.LENGTH_SHORT).show();
                    case R.id.mins :
                        Toast.makeText(wheel.getContext(), "min:" + hours.getCurrentItem() + ":" + mins.getCurrentItem(), Toast.LENGTH_SHORT).show();
                    case R.id.day :
                        Toast.makeText(wheel.getContext(), "day:" + dayAdapter.getItemText(day.getCurrentItem()), Toast.LENGTH_SHORT).show();
                }
            }
        };
        hours.addScrollingListener(scrollListener);
        mins.addScrollingListener(scrollListener);
        day.addScrollingListener(scrollListener);


        return fragmentView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        onActive();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

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
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");
    }

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
            String datetime = new SimpleDateFormat(context.getString(R.string.iso6301)).format(lcalendar.getTime());

            return datetime;
        }
    }

}
