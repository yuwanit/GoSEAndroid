package com.gose.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gose.R;
import com.gose.session.GovernmentOffice;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yuwanit on 2/26/2015.
 */
public class ImageAdapterForSearch extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> arrayList;
    private GovernmentOffice governmentOffice = GovernmentOffice.getInstance();
    private String language = governmentOffice.getLanguage();

    public ImageAdapterForSearch(Context c, ArrayList<HashMap<String, String>> list) {
        // TODO Auto-generated method stub
        context = c;
        arrayList = list;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return arrayList.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_government, null);
        }

        // ColImage
        ImageView imageView = (ImageView) convertView
                .findViewById(R.id.imgGovernment);

        Picasso.with(context).load(arrayList.get(position).get("ImagePath").replace(" ", "%20")).into(imageView);

        TextView txtPicName = (TextView) convertView
                .findViewById(R.id.government_name);
        txtPicName.setPadding(50, 0, 0, 0);

        if(language.equals("th")){
            txtPicName.setText(arrayList.get(position).get("thai_name"));
        }else {
            txtPicName.setText(arrayList.get(position).get("ImageDesc"));
        }

        return convertView;

    }

}
