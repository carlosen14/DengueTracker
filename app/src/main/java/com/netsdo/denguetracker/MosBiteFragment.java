package com.netsdo.denguetracker;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.netsdo.bestlocation.BestLocationListener;
import com.netsdo.bestlocation.BestLocationProvider;
import com.netsdo.bestlocation.BestLocationProvider.LocationType;
import com.netsdo.swipe4d.EventBus;
import com.netsdo.swipe4d.events.VerticalPagerSwitchedEvent;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MosBiteFragment extends Fragment {
    private static String TAG = "MosBiteFragment";
    private static int VPOS = 0; //VerticalPage Position, for Fragment defined as Central Page only, should be same as the position in activity_main.xml

    private static String mihow = "MosBite";

    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;
    private MainActivity mParentActivity;
    private InfoDB mInfoDB;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View lFragmentHolder = inflater.inflate(R.layout.fragment_mos_bite, container, false);
        mParentActivity = (MainActivity) getActivity();
        mInfoDB = mParentActivity.mInfoDB;

        ImageView mheadHolder = (ImageView) lFragmentHolder.findViewById(R.id.head);
        ImageView mbodyHolder = (ImageView) lFragmentHolder.findViewById(R.id.body);
        ImageView mrightarmHolder = (ImageView) lFragmentHolder.findViewById(R.id.rightarm);
        ImageView mrighthandHolder = (ImageView) lFragmentHolder.findViewById(R.id.righthand);
        ImageView mrightlegHolder = (ImageView) lFragmentHolder.findViewById(R.id.rightleg);
        ImageView mrightfootHolder = (ImageView) lFragmentHolder.findViewById(R.id.rightfoot);
        ImageView mleftarmHolder = (ImageView) lFragmentHolder.findViewById(R.id.leftarm);
        ImageView mlefthandHolder = (ImageView) lFragmentHolder.findViewById(R.id.lefthand);
        ImageView mleftlegHolder = (ImageView) lFragmentHolder.findViewById(R.id.leftleg);
        ImageView mleftfootHolder = (ImageView) lFragmentHolder.findViewById(R.id.leftfoot);

        ClickListener lClickListener = new ClickListener();
        mheadHolder.setOnClickListener(lClickListener);
        mbodyHolder.setOnClickListener(lClickListener);
        mrightarmHolder.setOnClickListener(lClickListener);
        mrighthandHolder.setOnClickListener(lClickListener);
        mrightlegHolder.setOnClickListener(lClickListener);
        mrightfootHolder.setOnClickListener(lClickListener);
        mleftarmHolder.setOnClickListener(lClickListener);
        mlefthandHolder.setOnClickListener(lClickListener);
        mleftlegHolder.setOnClickListener(lClickListener);
        mleftfootHolder.setOnClickListener(lClickListener);

        return lFragmentHolder;
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
            Log.d(TAG, "setUserVisibleHintTrue");
            onActive();
        } else {
            Log.d(TAG, "setUserVisibleHintFalse");
            onInActive();
        }
    }

    public void onActive() {
        Log.d(TAG, "onActive");
        initLocation();
        if (mBestLocationProvider != null) {
            mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
        }
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");
        if (mBestLocationProvider != null) {
            mBestLocationProvider.stopLocationUpdates();
        }
    }

    private void initLocation() {
        if (mBestLocationListener == null) {
            mBestLocationListener = new BestLocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d(TAG, "onStatusChanged, PROVIDER:" + provider + ", STATUS:" + String.valueOf(status));
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.d(TAG, "onProviderEnabled, PROVIDER:" + provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.d(TAG, "onProviderDisabled, PROVIDER:" + provider);
                }

                @Override
                public void onLocationUpdateTimeoutExceeded(LocationType type) {
//                    Log.d(TAG, "onLocationUpdateTimeoutExceeded, TYPE:" + type);
                }

                @Override
                public void onLocationUpdate(Location location, BestLocationProvider.LocationType type, boolean isFresh) {
                    Log.d(TAG, "onLocationUpdate, TYPE:" + type + ", LOCATION:" + mBestLocationProvider.locationToString(location));
                }
            };

            if (mBestLocationProvider == null) {
                mBestLocationProvider = new BestLocationProvider(this.getActivity(), true, true, 10000, 1000, 2, 0);
            }
        }
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String liwhat;
            switch (v.getId()) {
                case R.id.head:
                    liwhat = "Head";
                    break;
                case R.id.body:
                    liwhat = "Body";
                    break;
                case R.id.rightarm:
                    liwhat = "RightArm";
                    break;
                case R.id.righthand:
                    liwhat = "RightHand";
                    mInfoDB.truncateInfo();  // for testing purpose, to be removed before final release.
                    break;
                case R.id.rightleg:
                    liwhat = "RightLeg";
                    break;
                case R.id.rightfoot:
                    liwhat = "RightFoot";
                    break;
                case R.id.leftarm:
                    liwhat = "LeftArm";
                    break;
                case R.id.lefthand:
                    liwhat = "LeftHand";
                    String lSQL;// for testing purpose, to be removed before final release.
                    long lNoRec;
                    try {
                        lSQL = "SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info ORDER BY iwhen DESC;";
                        JSONObject lObj = new JSONObject();
                        lObj.put("sql", lSQL);
                        lNoRec = mInfoDB.openSelectInfo(lObj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "openSelectInfo wrong.");
                        lNoRec = 0;
                    }
                    if (lNoRec != 0) {
                        String lInfo = mInfoDB.selectNextInfo();
                        while (lInfo != null) {
                            Log.d(TAG, lInfo);
                            lInfo = mInfoDB.selectNextInfo();
                        }
                    } else {
                        Log.d(TAG, "no record of Info found.");
                    }
                    break;
                case R.id.leftleg:
                    liwhat = "LeftLeg";
                    break;
                case R.id.leftfoot:
                    liwhat = "LeftFoot";
                    break;
                default:
                    liwhat = "Unknown";
            }
            Toast.makeText(v.getContext(), MainActivity.mStringDisplay.getDisplay(mihow) + MainActivity.mStringDisplay.getDisplay(liwhat), Toast.LENGTH_SHORT).show();
            Log.i(TAG, liwhat + " - " + mBestLocationProvider.locationToString(mBestLocationProvider.getLocation()));

            Info lInfo = new Info();
            lInfo.setrowid(Info.ZEROLONG);
            lInfo.setiwhen(new SimpleDateFormat(getString(R.string.iso6301)).format(new Date()));
            lInfo.setiwhere(mBestLocationProvider.locationToString(mBestLocationProvider.getLocation()));
            lInfo.setihow(mihow);
            lInfo.setiwhat(liwhat);
            String lsInfo = lInfo.getInfo();
            if (lsInfo == Info.NULLSTRING) {
                Log.d(TAG, "ClickListener, Info parsing error.");

                return;
            } else {
                if (mInfoDB.insertInfo(lsInfo) == 0) {
                    Log.d(TAG, "ClickListener, no record lsInfo:" + lsInfo);
                }
                // data is inserted without error.
            }
        }
    }
}
