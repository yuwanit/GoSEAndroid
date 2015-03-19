package com.gose;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.android.gms.maps.GoogleMap;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    public static FragmentManager fragmentManager;
    public static LocationManager locationManager;
    public static GoogleMap mMap;

    public static HomeSectionFragment homeSectionFragment = homeSectionFragment = HomeSectionFragment.getInstance();
    public static SearchSectionFragment searchSectionFragment = SearchSectionFragment.getInstance();
    public static RouteSectionFragment routeSectionFragment = RouteSectionFragment.getInstance();
    public static FavoriteSectionFragment favoriteSectionFragment = FavoriteSectionFragment.getInstance();

    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Map
        fragmentManager = getSupportFragmentManager();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.

        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon((int) mAppSectionsPagerAdapter.getItemId(i))
                                    //.setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return homeSectionFragment;

                case 1:
                    return searchSectionFragment;

                case 2:
                    return routeSectionFragment;

                case 3:
                    return favoriteSectionFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public long getItemId(int position) {
            int img = 0;

            if (position == 0) {
                img = R.drawable.home;
            } else if (position == 1) {
                img = R.drawable.search;
            } else if (position == 2) {
                img = R.drawable.route;
            } else if (position == 3) {
                img = R.drawable.favorites;
            }

            return img;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMap != null) {
            MainActivity.fragmentManager.beginTransaction().remove(MainActivity.fragmentManager.findFragmentById(R.id.map)).commit();
            mMap = null;
        }
    }

    @Override
    protected void onStop() {
        if (mMap != null) {
            fragmentManager.beginTransaction().remove(MainActivity.fragmentManager.findFragmentById(R.id.map)).commit();
            mMap = null;
        }
        super.onStop();
    }

//    @Override
//     public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//
//        int id = item.getItemId();
//        Locale locale;
//        Configuration config;
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//
//        switch (id) {
//            case R.id.menu_th:
//                Log.e(TAG, "language thai");
//                locale = new Locale("th");
//                Locale.setDefault(locale);
//                config = new Configuration();
//                config.locale = locale;
//                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//
//                editor.putString(pref_language, "th");
//                editor.commit();
//                return true;
//            case R.id.menu_usa:
//                Log.e(TAG, "language english");
//                locale = new Locale("en");
//                Locale.setDefault(locale);
//                config = new Configuration();
//                config.locale = locale;
//                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//
//                editor.putString(pref_language, "en");
//                editor.commit();
//                return true;
//            default:
//                locale = new Locale("en");
//                Locale.setDefault(locale);
//                config = new Configuration();
//                config.locale = locale;
//                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//
//                editor.putString(pref_language, "en");
//                editor.commit();
//                return true;
//        }
//    }

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

}