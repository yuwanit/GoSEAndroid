package com.gose;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.gose.route.Navigator;

/**
 * Created by Yuwanit on 1/27/2015.
 */
public class RouteSectionFragment extends Fragment {

    private View view;
    private GoogleMap googleMap;
    private Navigator nav;

    private static RouteSectionFragment instance;

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
        googleMap = ((SupportMapFragment) MainActivity.fragmentManager.findFragmentById(R.id.route_map)).getMap();

        nav = new Navigator(googleMap, new LatLng(7.894654, 98.352212), new LatLng(7.902497, 98.343586));
        nav.findDirections(true);
        nav.setPathColor(Color.parseColor("#f94a32"), Color.parseColor("#ff8259"), Color.parseColor("#ffac88"));
        nav.setPathBorderColor(Color.parseColor("#ff0000"), Color.parseColor("#ff0000"), Color.parseColor("#ff0000"));

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
                7.894654, 98.352212));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);

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
}
