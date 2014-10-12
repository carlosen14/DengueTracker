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
import android.widget.Toast;

import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.MosEditEvent;
import com.netsdo.swipe4d.PageChangedEvent;
import com.netsdo.swipe4d.SwitchToPageEvent;

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

    AdapterView.OnItemClickListener mListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //todo, highlight the item after it is clicked.
            Toast.makeText(mParentActivity, "position:" + position + ", id:" + id + ", rowid:" + mInfoArray.get(position).getrowid(), Toast.LENGTH_SHORT).show();
            EventBus.getInstance().post(new MosEditEvent(id));
            EventBus.getInstance().post(new SwitchToPageEvent(2)); // hardcode to switch to MosEdit
        }
    };

    public class MosListAdapter extends BaseAdapter {

        public class ViewHolder {
            TextView rowid;
            TextView iwho;
            TextView iwhen;
            TextView iwhere;
            TextView ihow;
            TextView iwhat;
            TextView iwhy;
        }

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
                child = lLayoutInflater.inflate(R.layout.fragment_mos_item, null);
                lViewHolder = new ViewHolder();
                lViewHolder.rowid = (TextView) child.findViewById(R.id.rowid);
                lViewHolder.iwho = (TextView) child.findViewById(R.id.iwho);
                lViewHolder.iwhen = (TextView) child.findViewById(R.id.iwhen);
                lViewHolder.iwhere = (TextView) child.findViewById(R.id.iwhere);
                lViewHolder.ihow = (TextView) child.findViewById(R.id.ihow);
                lViewHolder.iwhat = (TextView) child.findViewById(R.id.iwhat);
                lViewHolder.iwhy = (TextView) child.findViewById(R.id.iwhy);
                child.setTag(lViewHolder);
            } else {
                lViewHolder = (ViewHolder) child.getTag();
            }

            if (mmInfoArray != null) {
                lViewHolder.rowid.setText(mmInfoArray.get(position).getrowid().toString());
                lViewHolder.iwho.setText(""); // not display iwho
                lViewHolder.iwhen.setText(mmInfoArray.get(position).getiwhen());
                lViewHolder.iwhere.setText(mmInfoArray.get(position).getiwhere(1)); // display simple format
                lViewHolder.ihow.setText("Mosquito Bite On"); // fixed value
                lViewHolder.iwhat.setText(mmInfoArray.get(position).getiwhat());
                lViewHolder.iwhy.setText(""); // not display iwhy
            }

            return child;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.fragment_mos_list, container, false);
        ListView lList = (ListView) lView.findViewById(R.id.bite_list);

        mParentActivity = (MainActivity) getActivity();
        mInfoHandler = mParentActivity.mInfoHandler;
        mInfoArray = new ArrayList<Info>();

        loadInfo(); // Info is loaded into mInfoArray

        mAdapter = new MosListAdapter(mInfoArray);
        lList.setAdapter(mAdapter);

        lList.setOnItemClickListener(mListListener);

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

        EventBus.getInstance().post(new PageChangedEvent(false)); // shortcut to prevent vertical scroll if the fragment is not main page and active at beginning. todo,
        EventBus.getInstance().post(new MosEditEvent(mInfoArray.get(0).getrowid())); // hardcode to pass first record to edit page to load as default, todo

        if (loadInfo()) {
            mAdapter.notifyDataSetChanged(); // refresh screen after new data is loaded.
        }
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");
    }

    private boolean loadInfo() {
        // return true if data is changes
        // return false if data is not changed.
        //todo, to do proper check and return true if data is really changed for performance purpose.
        String lSQL;
        String lInfo;

        if (mInfoArray == null) {
            return false; // no data
        }

        mInfoArray.clear();

        lSQL = "SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info WHERE ihow = 'MosBite' ORDER BY iwhen DESC;";

        try {
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
            Integer lNoRec = lObj.getInt("norec");
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
}
