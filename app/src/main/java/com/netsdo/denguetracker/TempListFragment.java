package com.netsdo.denguetracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TempListFragment extends Fragment {
    private static String TAG = "TempListFragment";

    private MainActivity mParentActivity;
    private InfoDB mInfoDB;

    private TempListAdapter mAdapter;
    private ArrayList<Info> mInfoArray;
    private ListView mtemplistHolder;

    /**
     * Called when the activity is first created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		View fragmentView = inflater.inflate(R.layout.fragment_temp_list, container, false);
        mParentActivity = (MainActivity) getActivity();
        mInfoDB = mParentActivity.mInfoDB;

        loadData(); // Info is loaded into mInfoArray

        mInfoArray = new ArrayList<Info>();

        mAdapter = new TempListAdapter(mInfoArray);
        mtemplistHolder = (ListView) fragmentView.findViewById(R.id.temp_list);
        mtemplistHolder.setAdapter(mAdapter);

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

}
