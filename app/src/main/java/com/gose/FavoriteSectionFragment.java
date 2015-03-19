package com.gose;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gose.asyncTask.FavoriteList;
import com.gose.database.DatabaseHelper;
import com.gose.database.FavoriteGovernmentOffice;
import com.gose.database.FavoriteGovernmentOfficeData;
import com.gose.session.GovernmentOffice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by APATTA-PU on 11/3/2558.
 */
public class FavoriteSectionFragment extends Fragment {

    private static final String TAG = FavoriteSectionFragment.class.getSimpleName();
    private DatabaseHelper databaseHelper;
    private GovernmentOffice governmentOffice = GovernmentOffice.getInstance();
    private String language = governmentOffice.getLanguage();
    private static ListView listView_favorite;

    private  List<Integer> governmentIdList = new ArrayList<Integer>();

    private static FavoriteSectionFragment instance;

    public static FavoriteSectionFragment getInstance() {
        if (instance == null) {
            instance = new FavoriteSectionFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (LinearLayout) inflater.inflate(R.layout.fragment_section_favorite, container, false);

        databaseHelper = new DatabaseHelper(getActivity());

        listView_favorite = (ListView) view.findViewById(R.id.listView_favorite);
        listView_favorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new FavoriteList(getActivity(), governmentIdList.get(position)).execute();
            }
        });

        databaseHelper = new DatabaseHelper(getActivity());

        FavoriteGovernmentOfficeData favoriteGovernmentOfficeData = new FavoriteGovernmentOfficeData();
        try {
            List<FavoriteGovernmentOffice> favoriteList = favoriteGovernmentOfficeData.getFavoriteGovernmentOffice(databaseHelper);

            List<String> favoriteNameList = new ArrayList<String>();
            for(FavoriteGovernmentOffice favorite : favoriteList){
                if (!favoriteNameList.contains(favorite.government_name)){
                    if(language.equals("th")){
                        favoriteNameList.add(favorite.government_thai_name);
                    }else {
                        favoriteNameList.add(favorite.government_name);
                    }
                    governmentIdList.add(favorite.government_id);}
            }

            if(favoriteList.size() != 0){
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.item_favorite,
                        favoriteNameList);

                listView_favorite.setAdapter(arrayAdapter);
            }

            Log.e(TAG, favoriteList.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return view;
    }


}




