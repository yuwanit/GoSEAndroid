package com.gose;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Yuwanit on 1/27/2015.
 */
public class FavoriteSectionFragment extends Fragment {

    private View view;

    private static FavoriteSectionFragment instance;

    public  static FavoriteSectionFragment getInstance() {
        if (instance == null) {
            instance = new FavoriteSectionFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null) {
            return view;
        }
        view = (LinearLayout) inflater.inflate(R.layout.fragment_section_favorite, container, false);
        return view;
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
