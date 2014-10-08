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

public class MosListFragment extends Fragment {
    private static String TAG = "MosListFragment";

    private ListView mList;
    private MosListAdapter mAdapter;
    private MainActivity parentActivity;
    private InfoHandler mInfo;

    private ArrayList<String> rowid = new ArrayList<String>();
    private ArrayList<String> iwhen = new ArrayList<String>();
    private ArrayList<String> iwhere = new ArrayList<String>();
    private ArrayList<String> ihow = new ArrayList<String>();
    private ArrayList<String> iwhat = new ArrayList<String>();

    public class InfoHolder {
        TextView rowid;
        TextView iwhen;
        TextView iwhere;
        TextView ihow;
        TextView iwhat;
    }
	public class MosListAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<String> rowid;
        private ArrayList<String> iwhen;
        private ArrayList<String> iwhere;
        private ArrayList<String> ihow;
        private ArrayList<String> iwhat;

        public MosListAdapter(Context context, ArrayList<String> rowid, ArrayList<String> iwhen, ArrayList<String> iwhere, ArrayList<String> ihow, ArrayList<String> iwhat) {
            mContext = context;

            this.rowid = rowid;
            this.iwhen = iwhen;
            this.iwhere = iwhere;
            this.ihow = ihow;
            this.iwhat = iwhat;

        }

        public int getCount() {
            return rowid.size();
        }

        public Object getItem(int position) {
            //todo, function: Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            //todo, function: Auto-generated method stub
            return 0;
        }

        public View getView(int position, View child, ViewGroup parent) {
            InfoHolder mInfoHolder;
            LayoutInflater layoutInflater;

            if (child == null) {
                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                child = layoutInflater.inflate(R.layout.fragment_mos_item, null);
                mInfoHolder = new InfoHolder();
                mInfoHolder.rowid = (TextView) child.findViewById(R.id.rowid);
                mInfoHolder.iwhen = (TextView) child.findViewById(R.id.iwhen);
                mInfoHolder.iwhere = (TextView) child.findViewById(R.id.iwhere);
                mInfoHolder.ihow = (TextView) child.findViewById(R.id.ihow);
                mInfoHolder.iwhat = (TextView) child.findViewById(R.id.iwhat);
                child.setTag(mInfoHolder);
            } else {
                mInfoHolder = (InfoHolder) child.getTag();
            }
            mInfoHolder.rowid.setText(rowid.get(position));
            mInfoHolder.iwhen.setText(iwhen.get(position));
            mInfoHolder.iwhere.setText(iwhere.get(position));
            mInfoHolder.ihow.setText(ihow.get(position));
            mInfoHolder.iwhat.setText(iwhat.get(position));

            return child;
        }
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_mos_list, container, false);
        mList = (ListView) fragmentView.findViewById(R.id.bite_list);

        parentActivity = (MainActivity) getActivity();
        mInfo = parentActivity.mInfo;

		return fragmentView;
	}

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHintTrue");
            onActive();
        }
        else {
            Log.d(TAG, "setUserVisibleHintFalse");
            onInActive();
        }
    }

    public void onActive() {
        Log.d(TAG, "onActive");

        displayList();
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");
    }

    private void displayList() {
        String allinfo;
        rowid.clear();
        iwhen.clear();
        iwhere.clear();
        ihow.clear();
        iwhat.clear();

        try {
            String sql = "SELECT rowid, iwhen, iwhere, ihow, iwhat FROM Info ORDER BY iwhen DESC;";
            JSONObject mObj = new JSONObject();
            mObj.put("sql", sql);
            allinfo = mInfo.selectAllInfo(mObj.toString());
            if (allinfo == null) { // no data stored
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        try {
            JSONObject mObj = new JSONObject(allinfo);
            Integer mNoRec = mObj.getInt("norec");
            JSONArray mInfoObj = mObj.getJSONArray("info");
            for (int i = 0; i < mNoRec; i++) {
                JSONObject mInfoRec = mInfoObj.getJSONObject(i);
                rowid.add(mInfoRec.getString("rowid"));
                iwhen.add(mInfoRec.getString("iwhen"));
                iwhere.add(mInfoRec.getString("iwhere"));
//                ihow.add(mInfoRec.getString("ihow"));
                ihow.add(("Mosquito Bite On "));
                iwhat.add(mInfoRec.getString("iwhat"));
            }

            MosListAdapter mAdapter = new MosListAdapter(parentActivity, rowid, iwhen, iwhere, ihow, iwhat);
            mList.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
