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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MosListFragment extends Fragment {
    private static String TAG = "MosListFragment";

    private ListView mList;
    private MainActivity mParentActivity;
    private InfoHandler mInfo;

    private ArrayList<Info> mInfoArray;

    public class InfoViewer {
        TextView rowid;
        TextView iwho;
        TextView iwhen;
        TextView iwhere;
        TextView ihow;
        TextView iwhat;
        TextView iwhy;
    }

    public InfoViewer mInfoViewer = new InfoViewer();

    public class MosListAdapter extends BaseAdapter {

        private Context mmContext;
        private ArrayList<Info> mmInfoArray;

        public MosListAdapter(Context context, ArrayList<Info> InfoArray) {
            mmContext = context;
            mmInfoArray = InfoArray;
        }

        public int getCount() {
            return mmInfoArray.size();
        }

        public Object getItem(int position) {
            return mmInfoArray.get(position);
        }

        public long getItemId(int position) {
            return mmInfoArray.get(position).getRowid();
        }

        public View getView(int position, View child, ViewGroup parent) {
            LayoutInflater lLayoutInflater;

            if (child == null) {
                lLayoutInflater = (LayoutInflater) mmContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                child = lLayoutInflater.inflate(R.layout.fragment_mos_item, null);
                mInfoViewer.rowid = (TextView) child.findViewById(R.id.rowid);
                mInfoViewer.iwho = (TextView) child.findViewById(R.id.iwho);
                mInfoViewer.iwhen = (TextView) child.findViewById(R.id.iwhen);
                mInfoViewer.iwhere = (TextView) child.findViewById(R.id.iwhere);
                mInfoViewer.ihow = (TextView) child.findViewById(R.id.ihow);
                mInfoViewer.iwhat = (TextView) child.findViewById(R.id.iwhat);
                mInfoViewer.iwhy = (TextView) child.findViewById(R.id.iwhy);
                child.setTag(mInfoViewer);
            } else {
                mInfoViewer = (InfoViewer) child.getTag();
            }
            mInfoViewer.rowid.setText(mmInfoArray.get(position).getRowid().toString());
            mInfoViewer.iwho.setText(mmInfoArray.get(position).getIwho());
            mInfoViewer.iwhen.setText(mmInfoArray.get(position).getIwhen());
            mInfoViewer.iwhere.setText(mmInfoArray.get(position).getIwhere());//todo, to display key iwhere info only.
            mInfoViewer.ihow.setText(mmInfoArray.get(position).getIhow());
            mInfoViewer.iwhat.setText(mmInfoArray.get(position).getIwhat());
            mInfoViewer.iwhy.setText(mmInfoArray.get(position).getIwhy());

            return child;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.fragment_mos_list, container, false);
        mList = (ListView) lView.findViewById(R.id.bite_list);

        mParentActivity = (MainActivity) getActivity();
        mInfo = mParentActivity.mInfo;
        mInfoArray = new ArrayList<Info>();

//        displayList();

//        MosListAdapter mAdapter = new MosListAdapter(mParentActivity, mInfoArray);
//        mList.setAdapter(mAdapter);

        AdapterView.OnItemClickListener a;
        a = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mParentActivity, "position:" + position + ", rowid:" + mInfoArray.get(position).getRowid(), Toast.LENGTH_SHORT).show();
                EventBus.getInstance().post(new MosEditEvent(mInfoArray.get(position).getRowid()));
            }
        };

        mList.setOnItemClickListener(a);

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

        displayList();
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");
    }

    private void displayList() {
        String lSQL;
        String lInfo;

        mInfoArray.clear();

        lSQL = "SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info WHERE ihow = 'MosBite' ORDER BY iwhen DESC;";

        try {
            JSONObject lObj = new JSONObject();
            lObj.put("sql", lSQL);
            lInfo = mInfo.selectInfo(lObj.toString());
            if (lInfo == null) { // no data stored
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();

            return;
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
                if (lcInfo.setInfo(lsObj.toString())) {
                    mInfoArray.add(lcInfo);
                }
            }

            MosListAdapter mAdapter = new MosListAdapter(mParentActivity, mInfoArray);
            mList.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();

            return;
        }

    }

}
