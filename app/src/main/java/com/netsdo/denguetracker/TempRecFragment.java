package com.netsdo.denguetracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.netsdo.Temperature;
import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.events.VerticalPagerSwitchedEvent;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TempRecFragment extends Fragment {
    private final static String TAG = "TempRecFragment";

    private static int VPOS = 1; //VerticalPage Position, for main Fragment only, should be sync with position in activity_main.xml

    private MainActivity mParentActivity;
    private InfoDB mInfoDB;

    private TempListAdapter mAdapter;
    private ArrayList<Info> mInfoArray;

    private ListView mtemplistHolder;
    private TextView mtempsaveHolder;
    private SeekBar mtemppickerHolder;
    private TextView mtempHolder;
    private Switch mtempalertswitchHolder;
    private SeekBar mtempalertpickerHolder;
    private TextView mtempalerttimeHolder;
    private TextView mmedisaveHolder;
    private Spinner mmedipickerHolder;
    private Spinner mmediqtypickerHolder;
    private Switch mmedialertswitchHolder;
    private SeekBar mmedialertpickerHolder;
    private TextView mmedialerttimeHolder;

    private long mTemp;
    private Date mNextTempAlert;
    private Date mNextMediAlert;

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
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        Log.d(TAG, "onSaveinstanceState");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged, HIDDEN:" + hidden);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View fragmentView = inflater.inflate(R.layout.fragment_temp_rec, container, false);
        mParentActivity = (MainActivity) getActivity();
        mInfoDB = mParentActivity.mInfoDB;
        mInfoArray = new ArrayList<Info>();

        loadData(); // Info is loaded into mInfoArray

        mAdapter = new TempListAdapter(mInfoArray);
        mtemplistHolder = (ListView) fragmentView.findViewById(R.id.temp_list);
        mtemplistHolder.setAdapter(mAdapter);

        mtempsaveHolder = (TextView) fragmentView.findViewById(R.id.tempsave);
        mtemppickerHolder = (SeekBar) fragmentView.findViewById(R.id.temppicker);
        mtempHolder = (TextView) fragmentView.findViewById(R.id.temp);
        mtempalertswitchHolder = (Switch) fragmentView.findViewById(R.id.tempalertswitch);
        mtempalertpickerHolder = (SeekBar) fragmentView.findViewById(R.id.tempalertpicker);
        mtempalerttimeHolder = (TextView) fragmentView.findViewById(R.id.tempalerttime);
        mmedisaveHolder = (TextView) fragmentView.findViewById(R.id.medisave);
        mmedipickerHolder = (Spinner) fragmentView.findViewById(R.id.medipicker);
        mmediqtypickerHolder = (Spinner) fragmentView.findViewById(R.id.mediqtypicker);
        mmedialertswitchHolder = (Switch) fragmentView.findViewById(R.id.medialertswitch);
        mmedialertpickerHolder = (SeekBar) fragmentView.findViewById(R.id.medialertpicker);
        mmedialerttimeHolder = (TextView) fragmentView.findViewById(R.id.medialerttime);

        mtemppickerHolder.setOnSeekBarChangeListener(new SeekBarChangeListener());
        mtemppickerHolder.setMax((int)(Temperature.HUMAN_MAX - Temperature.HUMAN_MIN ) * 10); // set value between min and max of human temperature, use 1 decimal.
        mtempsaveHolder.setOnClickListener(new ClickListener());
        mmedisaveHolder.setOnClickListener(new ClickListener());

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
        Log.d(TAG, "loadData");
        String lSQL;
        String lInfo;

        if (mInfoArray == null) {
            return false; // no data
        }

        mInfoArray.clear();

        try {
            lSQL = "SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info WHERE ihow = 'TempRec' OR ihow = 'MediTake' ORDER BY iwhen DESC;";
            JSONObject lObj = new JSONObject();
            lObj.put("sql", lSQL);
            lInfo = mInfoDB.selectInfo(lObj.toString());
            if (lInfo == null) {
                return false; // no data found, treat it as data is no change
            }
        } catch (JSONException e) {
            e.printStackTrace();

            return false; // error, treat it as data is no change
        }

        try {
            JSONObject lObj = new JSONObject(lInfo);
            long lNoRec = lObj.getLong("norec");
            JSONArray lInfoObj = lObj.getJSONArray("info");
            for (int i = 0; i < lNoRec; i++) {
                JSONObject lInfoRec = lInfoObj.getJSONObject(i);
                JSONArray lsInfo = new JSONArray();
                JSONObject lsObj = new JSONObject();
                lsInfo.put(lInfoRec);
                lsObj.put("info", lsInfo);
                Info lcInfo = new Info();
                if (lcInfo.setInfo(lsObj.toString())) {//reconstruct a record in JSON and assign it to array
                    mInfoArray.add(lcInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

            return false; // error, consider data is not changed.
        }

        return true;
    }

    public boolean showData() {
        Log.d(TAG, "showData");

        mAdapter.notifyDataSetChanged();
        return true;
    }

    public class TempListAdapter extends BaseAdapter {

        private ArrayList<Info> mmInfoArray;

        public TempListAdapter(ArrayList<Info> InfoArray) {
            mmInfoArray = InfoArray;
        }

        public ArrayList<Info> getData() {
            return mmInfoArray;
        }

        public int getCount() {
            if (mmInfoArray == null) {
                return 0;
            } else {
                return mmInfoArray.size();
            }
        }

        public Object getItem(int position) {
            if (mmInfoArray == null) {
                return null;
            } else {
                return mmInfoArray.get(position);
            }
        }

        public long getItemId(int position) { // key of list item
            if (mmInfoArray == null) {
                return 0;
            } else {
                return mmInfoArray.get(position).getrowid();
            }
        }

        public View getView(int position, View child, ViewGroup parent) {
            LayoutInflater lLayoutInflater;
            ViewHolder lViewHolder;

            if (child == null) {
                lLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE); // must use getActivity
                child = lLayoutInflater.inflate(R.layout.item_mos, null);
                lViewHolder = new ViewHolder();
                lViewHolder.rowid = (TextView) child.findViewById(R.id.rowid);
                lViewHolder.iwho = (TextView) child.findViewById(R.id.iwho);
                lViewHolder.iwhen = (TextView) child.findViewById(R.id.iwhen);
                lViewHolder.iwhere = (TextView) child.findViewById(R.id.iwhere);
                lViewHolder.ihow = (TextView) child.findViewById(R.id.ihow);
                lViewHolder.iwhat = (TextView) child.findViewById(R.id.iwhat);
                lViewHolder.iwhy = (TextView) child.findViewById(R.id.iwhy);
                lViewHolder.label_iwhen = (TextView) child.findViewById(R.id.label_iwhen);
                lViewHolder.label_iwhere = (TextView) child.findViewById(R.id.label_iwhere);
                child.setTag(lViewHolder);
            } else {
                lViewHolder = (ViewHolder) child.getTag();
            }

            if (mmInfoArray != null) {
                lViewHolder.rowid.setText(String.format("%d", mmInfoArray.get(position).getrowid()));
                lViewHolder.iwho.setText(""); // not display iwho
                lViewHolder.iwhen.setText(mmInfoArray.get(position).getiwhen());
                lViewHolder.iwhere.setText(""); // not display iwhere
                lViewHolder.ihow.setText(mmInfoArray.get(position).getihow("0"));
                lViewHolder.iwhat.setText(mmInfoArray.get(position).getiwhat("0"));
                lViewHolder.iwhy.setText(""); // not display iwhy
                lViewHolder.label_iwhen.setText(MainActivity.mStringDisplay.getDisplay("When")); // display when label
                lViewHolder.label_iwhere.setText(MainActivity.mStringDisplay.getDisplay("Where")); // display where label
            }

            return child;
        }

        public class ViewHolder {
            TextView rowid;
            TextView iwho;
            TextView iwhen;
            TextView iwhere;
            TextView ihow;
            TextView iwhat;
            TextView iwhy;
            TextView label_iwhen;
            TextView label_iwhere;
        }
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String lihow;
            String liwhat;
            switch (v.getId()) {
                case R.id.tempsave:
                    lihow = "TempRec";
                    liwhat = Temperature.toString(mtemppickerHolder.getProgress() / 10 + Temperature.HUMAN_MIN);
                    break;
                case R.id.medisave:
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
}
