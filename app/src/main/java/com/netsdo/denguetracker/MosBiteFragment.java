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

public class MosBiteFragment extends Fragment  {

    private static String TAG = "MosBiteFragment";

    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_mos_bite, container, false);

        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String showText;
                switch (v.getId()) {
                    case R.id.hbutton:
                        showText = "Head Clicked.";
                        break;
                    case R.id.bbutton:
                        showText = "Body Clicked.";
                        break;
                    case R.id.rabutton:
                        showText = "Right Arm Clicked.";
                        break;
                    case R.id.rhbutton:
                        showText = "Right Hand Clicked.";
                        break;
                    case R.id.rlbutton:
                        showText = "Right Leg Clicked.";
                        break;
                    case R.id.rfbutton:
                        showText = "Right Foot Clicked.";
                        break;
                    case R.id.labutton:
                        showText = "Left Arm Clicked.";
                        break;
                    case R.id.lhbutton:
                        showText = "Left Hand Clicked.";
                        break;
                    case R.id.llbutton:
                        showText = "Left Leg Clicked.";
                        break;
                    case R.id.lfbutton:
                        showText = "Left Foot Clicked.";
                        break;
                    default:
                        showText = "Unknown Clicked.";
                }
//                 mTvLog.setText("\n\n" + new Date().toLocaleString() + "\nLOCATION UPDATE: isFresh:" + String.valueOf(isFresh) + "\n" + mBestLocationProvider.locationToString(location) + mTvLog.getText());
                Toast.makeText(v.getContext(), showText + " - " + mBestLocationProvider.locationToString(mBestLocationProvider.getLocation()), Toast.LENGTH_SHORT).show();
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
        initLocation();
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);

        super.onResume();
    }
    @Override
    public void onPause() {
        initLocation();
        mBestLocationProvider.stopLocationUpdates();

        super.onPause();
    }

    private void initLocation() {
        if (mBestLocationListener == null) {
            mBestLocationListener = new BestLocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i(TAG, "onStatusChanged PROVIDER:" + provider + " STATUS:" + String.valueOf(status));
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.i(TAG, "onProviderEnabled PROVIDER:" + provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.i(TAG, "onProviderDisabled PROVIDER:" + provider);
                }

                @Override
                public void onLocationUpdateTimeoutExceeded(LocationType type) {
                    Log.w(TAG, "onLocationUpdateTimeoutExceeded PROVIDER:" + type);
                }

                @Override
                public void onLocationUpdate(Location location, BestLocationProvider.LocationType type, boolean isFresh) {
                    Log.i(TAG, "onLocationUpdate TYPE:" + type + " Location:" + mBestLocationProvider.locationToString(location));
                }
            };

            if (mBestLocationProvider == null) {
                mBestLocationProvider = new BestLocationProvider(this.getActivity(), true, true, 10000, 1000, 2, 0);
            }
        }
    }
}
