package com.gose;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.gose.asyncTask.CategorySpinnerSearch;
import com.gose.asyncTask.GetGovernmentOffice;
import com.gose.session.GovernmentOffice;

/**
 * Created by Yuwanit on 1/23/2015.
 */
public class SearchSectionFragment extends Fragment {

    private static final String TAG = SearchSectionFragment.class.getSimpleName();

    RelativeLayout layoutMenu;
//    ScrollView scrollView;
    private static ListView listView_search;
    private static Button button_search;
    private static EditText editText_search;
    private static Spinner spinner_category;
    private String category_id;

    private View view;

    private GovernmentOffice governmentOffice = GovernmentOffice.getInstance();
    private static SearchSectionFragment instance;

    public static SearchSectionFragment getInstance() {
        if (instance == null) {
            instance = new SearchSectionFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view != null) {
            return view;
        }

        view = (RelativeLayout) inflater.inflate(R.layout.fragment_section_search, container, false);

        layoutMenu = (RelativeLayout) view.findViewById(R.id.layoutMenu);

        editText_search = (EditText) view.findViewById(R.id.editText_search);
        listView_search = (ListView) view.findViewById(R.id.listView_search);

        button_search =(Button) view.findViewById(R.id.button_search);

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetGovernmentOffice(getActivity(), editText_search.getText().toString(), governmentOffice.getCategoryId(), listView_search).execute();
            }
        });

        listView_search.setOnTouchListener(new View.OnTouchListener() {
            final int DISTANCE = 3;

            float startY = 0;
            float dist = 0;
            boolean isMenuHide = false;

            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    dist = event.getY() - startY;

                    if ((pxToDp((int) dist) <= -DISTANCE) && !isMenuHide) {
                        isMenuHide = true;
                        hideMenuBar();
                    } else if ((pxToDp((int) dist) > DISTANCE) && isMenuHide) {
                        isMenuHide = false;
                        showMenuBar();
                    }

                    if ((isMenuHide && (pxToDp((int) dist) <= -DISTANCE))
                            || (!isMenuHide && (pxToDp((int) dist) > 0))) {
                        startY = event.getY();
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    startY = 0;
                }

                return false;
            }
        });

        spinner_category = (Spinner) view.findViewById(R.id.spinner_category);
        new CategorySpinnerSearch(getActivity(), spinner_category).execute();

        new GetGovernmentOffice(
                getActivity(),
                editText_search.getText().toString(),
                category_id,
                listView_search)
            .execute();

        return view;
    }

    public int pxToDp(int px) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int dp = Math.round(px / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void showMenuBar() {
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu, View.TRANSLATION_Y, 0);
        animSet.playTogether(anim1);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideMenuBar() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu, View.TRANSLATION_Y, layoutMenu.getHeight());

        animSet.playTogether(anim1);
        animSet.setDuration(300);
        animSet.start();
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
