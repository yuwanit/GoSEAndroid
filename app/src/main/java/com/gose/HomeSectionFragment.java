package com.gose;

import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.gose.asyncTask.GetLocation;

import java.lang.reflect.Field;

/**
 * Created by Yuwanit on 1/22/2015.
 */
public class HomeSectionFragment extends Fragment implements LocationListener {

    private static String TAG = HomeSectionFragment.class.getSimpleName();
    private static MainActivity mainActivity = new MainActivity();
    private static View view;
    private static GoogleMap googleMap = mainActivity.mMap;
    private static HomeSectionFragment instance;

    public static HomeSectionFragment getInstance() {
        if (instance == null) {
            instance = new HomeSectionFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        if(view == null) {
            view = (LinearLayout) inflater.inflate(R.layout.fragment_section_home, container, false);
            setUpMapIfNeeded();
        }

        if (googleMap == null) {
            googleMap = ((SupportMapFragment) MainActivity.fragmentManager.findFragmentById(R.id.map)).getMap();
            int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

            // Showing status
            if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

                int requestCode = 10;
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
                dialog.show();

            } else { // Google Play Services are available

                googleMap.setMyLocationEnabled(true);

                Criteria criteria = new Criteria();

                String provider = mainActivity.locationManager.getBestProvider(criteria, true);

                Location location = mainActivity.locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    onLocationChanged(location);
                }
                mainActivity.locationManager.requestLocationUpdates(provider, 20000, 0, this);
            }
            if (googleMap != null)
                setUpMap();
        }

        new GetLocation(getActivity(), inflater, googleMap).execute();

        return view;
    }

    public static void setUpMapIfNeeded() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) MainActivity.fragmentManager.findFragmentById(R.id.map)).getMap();
            if (googleMap != null)
                setUpMap();
        }
    }

    private static void setUpMap() {
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (googleMap != null) {
//            MainActivity.fragmentManager.beginTransaction().remove(MainActivity.fragmentManager.findFragmentById(R.id.map)).commit();
//            googleMap = null;
//        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
