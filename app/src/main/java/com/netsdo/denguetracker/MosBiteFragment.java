package com.netsdo.denguetracker;

import java.text.ParseException;
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
import com.netsdo.swipe4d.PageChangedEvent;
import com.netsdo.swipe4d.VerticalPageInVisibleEvent;
import com.netsdo.swipe4d.VerticalPageVisibleEvent;
import com.squareup.otto.Subscribe;

public class MosBiteFragment extends Fragment {
    private static String TAG = "MosBiteFragment";
    private static int VPOS = 0; //VerticalPage Position, for main Fragment only, should be sync with position in activity_main.xml

    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;
    private MainActivity parentActivity;
    private InfoHandler mInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_mos_bite, container, false);
        parentActivity = (MainActivity) getActivity();
        mInfo = parentActivity.mInfo;

        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String biteOn;
                switch (v.getId()) {
                    case R.id.hbutton:
                        biteOn = "Head";
                        break;
                    case R.id.bbutton:
                        biteOn = "Body";
                        break;
                    case R.id.rabutton:
                        biteOn = "RightArm";
                        break;
                    case R.id.rhbutton:
                        biteOn = "RightHand";
                        break;
                    case R.id.rlbutton:
                        biteOn = "RightLeg";
                        break;
                    case R.id.rfbutton:
                        biteOn = "RightFoot";
                        break;
                    case R.id.labutton:
                        biteOn = "LeftArm";
                        break;
                    case R.id.lhbutton:
                        biteOn = "LeftHand";
                        break;
                    case R.id.llbutton:
                        biteOn = "LeftLeg";
                        break;
                    case R.id.lfbutton:
                        biteOn = "LeftFoot";
                        break;
                    default:
                        biteOn = "Unknown";
                }
                Toast.makeText(v.getContext(), "Mosquito Bite On " + biteOn , Toast.LENGTH_SHORT).show();
                Log.i(TAG, biteOn + " - " + mBestLocationProvider.locationToString(mBestLocationProvider.getLocation()));
                boolean mosBite = mInfo.insertInfo(new SimpleDateFormat(getString(R.string.iso6301)).format(new Date()), mBestLocationProvider.locationToString(mBestLocationProvider.getLocation()), "MosBite", biteOn);

                Integer norec = mInfo.openSelectInfo(null, null, null, null, null);
                if (norec != 0) {
                    String jsonInfo = mInfo.selectNextInfo();
                    while (jsonInfo != null) {
                        Log.d(TAG, jsonInfo);
                        jsonInfo = mInfo.selectNextInfo();
                    }
                } else {
                    Log.i(TAG, "no record of Info found.");
                }
            }
        };

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
        hbutton.setOnClickListener(buttonListener);
        bbutton.setOnClickListener(buttonListener);
        rabutton.setOnClickListener(buttonListener);
        rhbutton.setOnClickListener(buttonListener);
        rlbutton.setOnClickListener(buttonListener);
        rfbutton.setOnClickListener(buttonListener);
        labutton.setOnClickListener(buttonListener);
        lhbutton.setOnClickListener(buttonListener);
        llbutton.setOnClickListener(buttonListener);
        lfbutton.setOnClickListener(buttonListener);

        return fragmentView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        EventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        EventBus.getInstance().unregister(this);

        super.onPause();
    }

    @Subscribe
    public void onInVisible(VerticalPageInVisibleEvent event) {
        if (event.setInVisible(VPOS)) {
            Log.d(TAG, "onInVisible");
            onInActive();
        };
    }

    @Subscribe
    public void onVisible(VerticalPageVisibleEvent event) {
        if (event.setVisible(VPOS)) {
            Log.d(TAG, "onVisible");
            onActive();
        };
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
        initLocation();
        if (mBestLocationProvider != null) {
            mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
        }
    }

    public void onInActive() {
        Log.d(TAG, "onInActive");
//        initLocation(); //original code has this line, but don't see it is necessary.
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
                    Log.w(TAG, "onLocationUpdateTimeoutExceeded, TYPE:" + type);
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
