package com.gose.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gose.R;
import com.gose.asyncTask.AddReviewLike;
import com.gose.session.UserLogin;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yuwanit on 3/4/2015.
 */
public class ReviewAdapter extends BaseAdapter {

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private UserLogin sessionUser = UserLogin.getInstance();

    private Context context;
    private ArrayList<HashMap<String, String>> arrayList;

    public ReviewAdapter(Context c, ArrayList<HashMap<String, String>> list) {
        // TODO Auto-generated method stub
        context = c;
        arrayList = list;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_review, null);
        }

        ImageView img_user = (ImageView) convertView
                .findViewById(R.id.img_user);
        TextView tv_username = (TextView) convertView
                .findViewById(R.id.tv_username);
        TextView tv_date = (TextView) convertView
                .findViewById(R.id.tv_date);
        TextView tv_show_review = (TextView) convertView
                .findViewById(R.id.tv_show_review);

        final String review_id = arrayList.get(position).get("review_id");
        final String user_id = arrayList.get(position).get("user_id");
        String user_img = arrayList.get(position).get("user_img");
        String user_name = arrayList.get(position).get("user_name");
        String review = arrayList.get(position).get("review");
        String create_date = arrayList.get(position).get("create_date");
        String evaluation = arrayList.get(position).get("evaluation");

        Picasso.with(context).load(user_img).into(img_user);
        tv_username.setText(user_name);
        tv_date.setText(create_date);
        tv_show_review.setText(review);

        LinearLayout linear_yes_layout = (LinearLayout) convertView.findViewById(R.id.linear_yes_layout);
        ImageButton button_yes = new ImageButton(context);
        button_yes.setBackgroundResource(R.drawable.agree);

        linear_yes_layout.addView(button_yes);
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddReviewLike(context, review_id, sessionUser.getUserId()).execute();
            }
        });

        return convertView;
    }
}
