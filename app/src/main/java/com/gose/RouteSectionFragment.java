package com.gose;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gose.asyncTask.GetGovernmentDistance;
import com.gose.route.GPSTracker;
import com.gose.session.GovernmentOffice;

/**
 * Created by Yuwanit on 1/27/2015.
 */
public class RouteSectionFragment extends Fragment implements LocationListener, GoogleMap.OnMapLoadedCallback {

    private static String TAG = RouteSectionFragment.class.getSimpleName();

    private View view;
    private GoogleMap googleMap;
    private Marker mPositionMarker;

    private GovernmentOffice governmentOffice = GovernmentOffice.getInstance();

    private static RouteSectionFragment instance;
    private AutoCompleteTextView et_destination;
    private Button btn_distance;

    // GPSTracker class
    private GPSTracker gps;
    private LatLng latLngCurrent;

    public static RouteSectionFragment getInstance() {
        if (instance == null) {
            instance = new RouteSectionFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null) {
            return view;
        }

        view = (RelativeLayout) inflater.inflate(R.layout.fragment_section_route, container, false);

        et_destination = (AutoCompleteTextView) view.findViewById(R.id.et_destination);
        ArrayAdapter adapter = new ArrayAdapter
                (getActivity(),android.R.layout.simple_list_item_1, governmentOffice.getGovernmentNameList());
        et_destination.setAdapter(adapter);

        btn_distance = (Button) view.findViewById(R.id.btn_distance);
        btn_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetGovernmentDistance(getActivity(), et_destination.getText().toString(), googleMap).execute();
            }
        });

        googleMap = ((SupportMapFragment) MainActivity.fragmentManager.findFragmentById(R.id.route_map)).getMap();
        googleMap.setMyLocationEnabled(true);

        gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            latLngCurrent = new LatLng(latitude, longitude);
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double latitude_current = location.getLatitude();

                // Getting longitude of the current location
                double longitude_current = location.getLongitude();

                // Creating a LatLng object for the current location
                latLngCurrent = new LatLng(latitude_current, longitude_current);

                // Showing the current location in Google Map
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngCurrent));

                // Zoom in the Google Map
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                if (mPositionMarker == null) {

                    mPositionMarker = googleMap.addMarker(new MarkerOptions()
                            .flat(true)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.car_icon))
                            .position(latLngCurrent));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        if (mPositionMarker == null) {

            mPositionMarker = googleMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.car_icon))
                    .position(latLngCurrent));
        }

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        CameraUpdate center = CameraUpdateFactory.newLatLng(latLngCurrent);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        if (view != null) {
            ViewGroup parentViewGroup = (ViewGroup) view.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
        super.onDestroyView();
    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude_current = location.getLatitude();

        // Getting longitude of the current location
        double longitude_current = location.getLongitude();

        // Creating a LatLng object for the current location
        latLngCurrent = new LatLng(latitude_current, longitude_current);

        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngCurrent));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        if (location == null)
            return;

        if (mPositionMarker == null) {

            mPositionMarker = googleMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.car_icon))
                    .position(latLngCurrent));
        }

        animateMarker(mPositionMarker, location); // Helper method for smooth
        // animation

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLngCurrent));
    }

    public void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
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
    public void onMapLoaded() {

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 16));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon))
                .position(latLngCurrent)
                .flat(true)
                .rotation(245));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLngCurrent)
                .zoom(16)
                .bearing(90)
                .build();

        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                1000, null);
    }
}
