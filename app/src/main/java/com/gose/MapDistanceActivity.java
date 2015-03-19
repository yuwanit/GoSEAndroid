package com.gose;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gose.route.GPSTracker;
import com.gose.route.Navigator;
import com.gose.session.GovernmentOffice;


public class MapDistanceActivity extends FragmentActivity implements LocationListener, GoogleMap.OnMapLoadedCallback {

    private static String TAG = MapDistanceActivity.class.getSimpleName();

    private GoogleMap googleMap;
    private Marker mPositionMarker;
    private GovernmentOffice governmentOffice = GovernmentOffice.getInstance();

    private static RouteSectionFragment instance;
    private AutoCompleteTextView et_destination;
    private Button btn_distance;

    // GPSTracker class
    private GPSTracker gps;
    private LatLng latLngCurrent;
    private LatLng latLngDestination;
    private String governmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_distance);

        setArguments();

        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_distance)).getMap();
        googleMap.setMyLocationEnabled(true);

        gps = new GPSTracker(this);

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
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

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

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        GPSTracker gps = new GPSTracker(this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            latLngCurrent = new LatLng(latitude, longitude);
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        if (latLngCurrent != null && latLngDestination != null) {

            googleMap.clear();

            CameraUpdate center = CameraUpdateFactory.newLatLng(latLngCurrent);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);

            mPositionMarker = googleMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.car_icon))
                    .position(latLngCurrent));

            googleMap.addMarker(new MarkerOptions()
                    .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.goverment_icon))
                    .position(latLngDestination)
                    .title(governmentName)).showInfoWindow();

            Navigator nav = new Navigator(googleMap, latLngCurrent, latLngDestination);
            nav.findDirections(true);
            nav.setPathColor(Color.parseColor("#f2a84c"), Color.parseColor("#00ff00"), Color.parseColor("#ff0080"));
            nav.setPathBorderColor(Color.WHITE);
        } else {
            Toast.makeText(this, "Can not found your place.", Toast.LENGTH_SHORT).show();
        }

        CameraUpdate center = CameraUpdateFactory.newLatLng(latLngCurrent);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
    }

    private void setArguments() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            governmentName = extras.getString("imageDesc");
            Double latitude = Double.parseDouble(extras.getString("latitude"));
            Double longitude = Double.parseDouble(extras.getString("longitude"));

            latLngDestination = new LatLng(latitude, longitude);

            extras.clear();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_distance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

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
