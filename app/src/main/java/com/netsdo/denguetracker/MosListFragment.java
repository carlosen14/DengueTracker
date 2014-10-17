package com.netsdo.denguetracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.events.HorizontalPagerSwitchedEvent;
import com.netsdo.swipe4d.events.MosEditEvent;
import com.netsdo.swipe4d.events.SwitchToPageEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MosListFragment extends Fragment {
    private static String TAG = "MosListFragment";

    private MainActivity mParentActivity;
    private InfoHandler mInfoHandler;

    private MosListAdapter mAdapter;
    private ArrayList<Info> mInfoArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.fragment_mos_list, container, false);
        ListView lList = (ListView) lView.findViewById(R.id.bite_list);

        mParentActivity = (MainActivity) getActivity();
        mInfoHandler = MainActivity.mInfoHandler;
        mInfoArray = new ArrayList<Info>();

        loadInfo(); // Info is loaded into mInfoArray

        mAdapter = new MosListAdapter(mInfoArray);
        lList.setAdapter(mAdapter);

        lList.setOnItemClickListener(new ItemClickListener());

        return lView;
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

        if (loadInfo()) {
            showInfo();
        }
        EventBus.getInstance().post(new HorizontalPagerSwitchedEvent(false)); // shortcut to prevent vertical scroll if the fragment is not main page and active at beginning (onPageSelected is not called at this scenario). todo,
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");
    }

    private boolean loadInfo() {
        // return true if data is changes
        // return false if data is not changed.
        String lSQL;
        String lInfo;

        if (mInfoArray == null) {
            return false; // no data
        }

        mInfoArray.clear();

        try {
            lSQL = "SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info WHERE ihow = 'MosBite' ORDER BY iwhen DESC;";
            JSONObject lObj = new JSONObject();
            lObj.put("sql", lSQL);
            lInfo = mInfoHandler.selectInfo(lObj.toString());
            if (lInfo == null) {
                return true; // no data found, data is changed
            }
        } catch (JSONException e) {
            e.printStackTrace();

            return false; // error, consider data is not changed
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

        return true; // data is changed.
    }

    private boolean showInfo() {
        mAdapter.notifyDataSetChanged(); // refresh screen after new data is loaded.
        EventBus.getInstance().post(new MosEditEvent(mInfoArray.get(0).getrowid())); // hardcode to pass first record to edit page to load as default, todo

        return true;
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setSelected(true);
            Log.d(TAG, "onItemClick, POSITION:" + position + ", ID:" + id + ", rowid:" + mInfoArray.get(position).getrowid());
            EventBus.getInstance().post(new MosEditEvent(id)); // id is item key set in getItemId
            EventBus.getInstance().post(new SwitchToPageEvent(2)); // hardcode to switch to MosEdit
        }
    }

    public class MosListAdapter extends BaseAdapter {

        private ArrayList<Info> mmInfoArray;

        public MosListAdapter(ArrayList<Info> InfoArray) {
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
                lViewHolder.iwhere.setText(mmInfoArray.get(position).getiwhere("3")); // display simple format
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
