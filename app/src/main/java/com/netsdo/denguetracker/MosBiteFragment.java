package com.netsdo.denguetracker;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.netsdo.swipe4d.VerticalPagerSwitchedEvent;
import com.squareup.otto.Subscribe;

public class MosBiteFragment extends Fragment {
    private static String TAG = "MosBiteFragment";
    private static int VPOS = 0; //VerticalPage Position, for Fragment defined as Central Page only, should be same as the position in activity_main.xml

    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;
    private MainActivity mParentActivity;
    private InfoHandler mInfoHandler;

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String lBiteOn;
            switch (v.getId()) {
                case R.id.hbutton:
                    lBiteOn = "Head";
                    break;
                case R.id.bbutton:
                    lBiteOn = "Body";
                    break;
                case R.id.rabutton:
                    lBiteOn = "RightArm";
                    break;
                case R.id.rhbutton:
                    lBiteOn = "RightHand";
                    mInfoHandler.truncateInfo();  // for testing purpose, to be removed before final release.
                    break;
                case R.id.rlbutton:
                    lBiteOn = "RightLeg";
                    break;
                case R.id.rfbutton:
                    lBiteOn = "RightFoot";
                    break;
                case R.id.labutton:
                    lBiteOn = "LeftArm";
                    break;
                case R.id.lhbutton:
                    lBiteOn = "LeftHand";
                    Integer norec = mInfoHandler.openSelectInfo(null, null, null, null, null, null, null); // for testing purpose, to be removed before final release.
                    if (norec != 0) {
                        String lInfo = mInfoHandler.selectNextInfo();
                        while (lInfo != null) {
                            Log.d(TAG, lInfo);
                            lInfo = mInfoHandler.selectNextInfo();
                        }
                    } else {
                        Log.i(TAG, "no record of Info found.");
                    }
                    break;
                case R.id.llbutton:
                    lBiteOn = "LeftLeg";
                    break;
                case R.id.lfbutton:
                    lBiteOn = "LeftFoot";
                    break;
                default:
                    lBiteOn = "Unknown";
            }
            Toast.makeText(v.getContext(), "Mosquito Bite On " + lBiteOn, Toast.LENGTH_SHORT).show();
            Log.i(TAG, lBiteOn + " - " + mBestLocationProvider.locationToString(mBestLocationProvider.getLocation()));
            long mosBite = mInfoHandler.insertInfo(
                    null, //iwho
                    new SimpleDateFormat(getString(R.string.iso6301)).format(new Date()), // iwhen
                    mBestLocationProvider.locationToString(mBestLocationProvider.getLocation()), //iwhere
                    "MosBite", //ihow
                    lBiteOn, //iwhat
                    null); // iwhy

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_mos_bite, container, false);
        mParentActivity = (MainActivity) getActivity();
        mInfoHandler = mParentActivity.mInfoHandler;

        ImageView hbutton = (ImageView) fragmentView.findViewById(R.id.hbutton);
        ImageView bbutton = (ImageView) fragmentView.findViewById(R.id.bbutton);
        ImageView rabutton = (ImageView) fragmentView.findViewById(R.id.rabutton);
        ImageView rhbutton = (ImageView) fragmentView.findViewById(R.id.rhbutton);
        ImageView rlbutton = (ImageView) fragmentView.findViewById(R.id.rlbutton);
        ImageView rfbutton = (ImageView) fragmentView.findViewById(R.id.rfbutton);
        ImageView labutton = (ImageView) fragmentView.findViewById(R.id.labutton);
        ImageView lhbutton = (ImageView) fragmentView.findViewById(R.id.lhbutton);
        ImageView llbutton = (ImageView) fragmentView.findViewById(R.id.llbutton);
        ImageView lfbutton = (ImageView) fragmentView.findViewById(R.id.lfbutton);
        hbutton.setOnClickListener(mClickListener);
        bbutton.setOnClickListener(mClickListener);
        rabutton.setOnClickListener(mClickListener);
        rhbutton.setOnClickListener(mClickListener);
        rlbutton.setOnClickListener(mClickListener);
        rfbutton.setOnClickListener(mClickListener);
        labutton.setOnClickListener(mClickListener);
        lhbutton.setOnClickListener(mClickListener);
        llbutton.setOnClickListener(mClickListener);
        lfbutton.setOnClickListener(mClickListener);

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
            case -1:
                onInActive();
                break;
            case 0:
                break;
            case 1:
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
                    Log.i(TAG, "onStatusChanged, PROVIDER:" + provider + ", STATUS:" + String.valueOf(status));
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.i(TAG, "onProviderEnabled, PROVIDER:" + provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.i(TAG, "onProviderDisabled, PROVIDER:" + provider);
                }

                @Override
                public void onLocationUpdateTimeoutExceeded(LocationType type) {
//todo                    Log.w(TAG, "onLocationUpdateTimeoutExceeded, TYPE:" + type);
                }

                @Override
                public void onLocationUpdate(Location location, BestLocationProvider.LocationType type, boolean isFresh) {
                    Log.i(TAG, "onLocationUpdate, TYPE:" + type + ", LOCATION:" + mBestLocationProvider.locationToString(location));
                }
            };

            if (mBestLocationProvider == null) {
                mBestLocationProvider = new BestLocationProvider(this.getActivity(), true, true, 10000, 1000, 2, 0);
            }
        }
    }
}
