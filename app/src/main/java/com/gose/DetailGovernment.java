package com.gose;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.SQLException;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.gose.asyncTask.AddEvaluation;
import com.gose.asyncTask.AddReview;
import com.gose.asyncTask.ImageLoadTaskWithProgressbar;
import com.gose.asyncTask.ShowEvaluation;
import com.gose.asyncTask.ShowReview;
import com.gose.asyncTask.UpdateEvaluation;
import com.gose.database.DatabaseHelper;
import com.gose.database.FavoriteGovernmentOffice;
import com.gose.database.FavoriteGovernmentOfficeData;
import com.gose.session.UserLogin;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailGovernment extends FragmentActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = DetailGovernment.class.getSimpleName();

    private String governmentName, governmentNameThai, imgGovernment, location, headAgency,
            headAgencyThai, officesHoursStart, officesHoursEnd, latitude, longitude,
            categoryName, categoryNameThai, tel, fax, imgCategory, governmentId;

    private TextView tv_tel, tv_fax, tv_government, tv_location, tv_head_agency, tv_offices_hours, tv_total_people, tv_evaluation_rate,
            tv_no_review;
    private ImageView img_category, img_government, img_edit_evaluation, img_route;
    private ProgressBar progressBar_img_category, progressBar_img_government, progressBar_reviews;
    private SignInButton button_sign_in_google;
    private LoginButton button_connect_facebook;
    private RatingBar ratingBar;
    private UiLifecycleHelper uiHelper;
    private UserLogin sessionUser = UserLogin.getInstance();

    private static final int RC_SIGN_IN = 0;

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private static Dialog dialog = null;
    private EditText et_write_review;
    private Button button_post_review, button_write_review, button_evaluation, button_cancel_evaluation;

    private LinearLayout layout_login, layout_evaluation;
    private RelativeLayout layout_show_evaluation;
    private ListView listView_reviews;

    private float rating_evaluation;
    private boolean status_evaluation = false;
    private ImageView imageView_favorite;
    private boolean status_favorite = false;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_government);

        setArguments();
        databaseHelper = new DatabaseHelper(getApplicationContext());

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        progressBar_reviews = (ProgressBar) findViewById(R.id.progressBar_reviews);

        listView_reviews = (ListView) findViewById(R.id.listView_reviews);
        layout_login = (LinearLayout) findViewById(R.id.layout_login);
        layout_evaluation = (LinearLayout) findViewById(R.id.layout_evaluation);
        layout_show_evaluation = (RelativeLayout) findViewById(R.id.layout_show_evaluation);
        tv_government = (TextView) findViewById(R.id.tv_government);
        tv_tel = (TextView) findViewById(R.id.tv_tel);
        tv_fax = (TextView) findViewById(R.id.tv_fax);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_head_agency = (TextView) findViewById(R.id.tv_head_agency);
        tv_offices_hours = (TextView) findViewById(R.id.tv_offices_hours);
        tv_evaluation_rate = (TextView) findViewById(R.id.tv_evaluation_rate);
        tv_total_people = (TextView) findViewById(R.id.tv_total_people);
        tv_no_review = (TextView) findViewById(R.id.tv_no_review);
        img_category = (ImageView) findViewById(R.id.img_category);
        img_government = (ImageView) findViewById(R.id.img_government);
        progressBar_img_category = (ProgressBar) findViewById(R.id.progressBar_img_category);
        progressBar_img_government = (ProgressBar) findViewById(R.id.progressBar_img_government);
        button_write_review = (Button) findViewById(R.id.button_write_review);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        img_edit_evaluation = (ImageView) findViewById(R.id.img_edit_evaluation);
        img_route = (ImageView) findViewById(R.id.img_route);
        button_evaluation = (Button) findViewById(R.id.button_evaluation);
        button_cancel_evaluation = (Button) findViewById(R.id.button_cancel_evaluation);
        button_sign_in_google = (SignInButton) findViewById(R.id.button_sign_in_google);
        button_sign_in_google.setOnClickListener(this);
        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, null)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        button_connect_facebook = (LoginButton) findViewById(R.id.button_connect_facebook);
        button_connect_facebook.setReadPermissions(Arrays.asList("email", "public_profile"));

        tv_government.setText(governmentName);
        tv_tel.setText(tel);
        tv_fax.setText(fax);
        tv_location.setText(location);
        tv_head_agency.setText(headAgency);

        officesHoursStart = officesHoursStart.substring(0, officesHoursStart.length() - 3);
        officesHoursEnd = officesHoursEnd.substring(0, officesHoursEnd.length() - 3);
        tv_offices_hours.setText(officesHoursStart + " - " + officesHoursEnd);

        tv_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + Uri.encode(tel.trim())));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

        new ImageLoadTaskWithProgressbar(imgCategory, img_category, progressBar_img_category).execute();
        new ImageLoadTaskWithProgressbar(imgGovernment, img_government, progressBar_img_government).execute();

        if (sessionUser.getUserId() != null) {
            button_write_review.setVisibility(View.VISIBLE);
        }
        button_write_review.setOnClickListener(this);

        checkStatusLogin();

        new ShowReview(this, governmentId, listView_reviews, progressBar_reviews, tv_no_review).execute();

        new ShowEvaluation(this, governmentId, ratingBar, button_evaluation, img_edit_evaluation, tv_evaluation_rate, tv_total_people,
                layout_show_evaluation, layout_evaluation).execute();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_evaluation = rating;
            }
        });

        img_edit_evaluation.setOnClickListener(this);
        button_evaluation.setOnClickListener(this);
        button_cancel_evaluation.setOnClickListener(this);

        imageView_favorite = (ImageView) findViewById(R.id.imageView_favorite);

        FavoriteGovernmentOfficeData favoriteGovernmentOfficeData = new FavoriteGovernmentOfficeData();
        try {
            List<FavoriteGovernmentOffice> favoriteList = favoriteGovernmentOfficeData.getFavoriteGovernmentOffice(databaseHelper);

            List<String> favoriteNameList = new ArrayList<String>();
            for (FavoriteGovernmentOffice favorite : favoriteList) {
                favoriteNameList.add(favorite.government_name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        checkFavorite();

        imageView_favorite.setOnClickListener(this);
        img_route.setOnClickListener(this);

    }

    private void checkFavorite() {
        FavoriteGovernmentOfficeData favoriteGovernmentOfficeData = new FavoriteGovernmentOfficeData();
        try {
            List<FavoriteGovernmentOffice> favoriteList = favoriteGovernmentOfficeData.getFavoriteGovernmentOfficeByGovernmentId(databaseHelper, governmentId);

            Log.e(TAG, "favoriteList >>> " + favoriteList + " | size >>> " + favoriteList.size());

            if (favoriteList.size() != 0) {
                status_favorite = true;
                imageView_favorite.setImageResource(R.drawable.favorite_icon);
            } else {
                status_favorite = false;
                imageView_favorite.setImageResource(R.drawable.favorite_icon_off);
            }

            Log.e(TAG, favoriteList.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void createWriteReviewDialog() {
        if (dialog == null) {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_write_review);

            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());

            WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            lp.width = size.x;
            lp.height = size.y;
            // This makes the dialog take up the full width
            window.setAttributes(lp);
        }

        et_write_review = (EditText) dialog.findViewById(R.id.et_write_review);
        button_post_review = (Button) dialog.findViewById(R.id.button_post_review);
        button_post_review.setOnClickListener(this);

        ImageView btnCancel = (ImageView) dialog.findViewById(R.id.icon_close);
        // if button is clicked, close the custom dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                Log.d(TAG, "Facebook session opened.");
            } else if (state.isClosed()) {
                Log.d(TAG, "Facebook session closed.");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        Session session = Session.getActiveSession();

        if (sessionUser.getUserId() == null) {
            if (session != null &&
                    (session.isOpened() || session.isClosed())) {
                onSessionStateChange(session, session.getState(), null);
            }
            uiHelper.onResume();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        stopAsyTask();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");

            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {

                    sessionUser.setUserImage("http://graph.facebook.com/" + user.getId() + "/picture?type=small");
                    sessionUser.setUserId("F" + user.getId());
                    sessionUser.setUserName(user.getName());
                    Log.e(TAG, sessionUser.getUserName());
                    Log.e(TAG, sessionUser.getUserImage().toString());

                    checkStatusLogin();

                }
            });
            request.executeAsync();

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personID = currentPerson.getId();
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Id: " + personID + ", Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                sessionUser.setUserId("G" + personID);
                sessionUser.setUserName(personName);
                sessionUser.setUserImage(personPhotoUrl);

                checkStatusLogin();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setArguments() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            governmentId = extras.getString("government_id");
            governmentName = extras.getString("imageDesc");
            governmentNameThai = extras.getString("thai_name");
            imgGovernment = extras.getString("ImagePath").replace(" ", "%20");
            location = extras.getString("location");
            headAgency = extras.getString("head_agency");
            headAgencyThai = extras.getString("thai_head_agency");
            officesHoursStart = extras.getString("offices_hours_start");
            officesHoursEnd = extras.getString("offices_hours_end");
            latitude = extras.getString("latitude");
            longitude = extras.getString("longitude");
            categoryName = extras.getString("category_name");
            categoryNameThai = extras.getString("thai_category_name");
            tel = extras.getString("tel");
            fax = extras.getString("fax");
            imgCategory = extras.getString("category_image").replace(" ", "%20");

            extras.clear();
        }
    }

    private void checkStatusLogin() {
        if (sessionUser.getUserId() != null) {
            layout_login.setVisibility(View.INVISIBLE);
            button_write_review.setVisibility(View.VISIBLE);
        } else {
            layout_login.setVisibility(View.VISIBLE);
            button_write_review.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();

        // Update the UI after signin
        updateUI(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        updateUI(false);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_sign_in_google:
                // Signin button clicked
                signInWithGplus();
                break;
            case R.id.button_write_review:
                createWriteReviewDialog();
                dialog.show();
                break;
            case R.id.button_post_review:
                if (et_write_review.getText().length() != 0) {
                    String review = et_write_review.getText().toString();
                    new AddReview(this, governmentId, sessionUser.getUserId(), review).execute();

                    dialog.dismiss();
                } else {
                    et_write_review.setError("Please input your review.");
                }
                break;
            case R.id.img_edit_evaluation:
                status_evaluation = true;
                ratingBar.setClickable(true);
                button_evaluation.setVisibility(View.VISIBLE);
                button_cancel_evaluation.setVisibility(View.VISIBLE);
                img_edit_evaluation.setVisibility(View.GONE);
                break;
            case R.id.button_evaluation:
                if (status_evaluation == false) {
                    new AddEvaluation(DetailGovernment.this, governmentId, sessionUser.getUserId(), rating_evaluation).execute();
                } else {
                    new UpdateEvaluation(DetailGovernment.this, governmentId, sessionUser.getUserId(), rating_evaluation).execute();
                }
                ratingBar.setClickable(false);
                button_evaluation.setVisibility(View.GONE);
                button_cancel_evaluation.setVisibility(View.GONE);
                img_edit_evaluation.setVisibility(View.VISIBLE);
                break;
            case R.id.button_cancel_evaluation:
                ratingBar.setClickable(false);
                button_evaluation.setVisibility(View.GONE);
                button_cancel_evaluation.setVisibility(View.GONE);
                img_edit_evaluation.setVisibility(View.VISIBLE);
                break;
            case R.id.imageView_favorite:
                if (status_favorite == true) {
                    imageView_favorite.setImageResource(R.drawable.favorite_icon_off);
                    status_favorite = false;

                    RuntimeExceptionDao<FavoriteGovernmentOffice, Integer> canteenDao = databaseHelper.getfavoriteGovernmentofficesDao();
                    DeleteBuilder<FavoriteGovernmentOffice, Integer> deleteBuilder = canteenDao.deleteBuilder();
                    try {
                        deleteBuilder.where().eq(FavoriteGovernmentOffice.COLUMN_NAME_GOVERNMENT_ID, governmentId);
                        canteenDao.delete(deleteBuilder.prepare());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (java.sql.SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    imageView_favorite.setImageResource(R.drawable.favorite_icon);
                    status_favorite = true;

                    RuntimeExceptionDao<FavoriteGovernmentOffice, Integer> favoriteGovernmentofficesDao = databaseHelper.getfavoriteGovernmentofficesDao();
                    FavoriteGovernmentOffice favoriteGovernmentoffices = new FavoriteGovernmentOffice(Integer.parseInt(governmentId), governmentName, governmentNameThai);
                    favoriteGovernmentofficesDao.create(favoriteGovernmentoffices);
                }
                break;
            case R.id.img_route:
                Intent intent = new Intent(this,
                        MapDistanceActivity.class);

                intent.putExtra("government_id", governmentId);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude",longitude);

                startActivity(intent);
                break;

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        stopAsyTask();
        stopAsyTask();
    }

    private void stopAsyTask() {
        ShowReview showReview = new ShowReview();
        showReview.cancel(true);
        ImageLoadTaskWithProgressbar imageLoadTaskWithProgressbar = new ImageLoadTaskWithProgressbar();
        imageLoadTaskWithProgressbar.cancel(true);
        finish();
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            button_sign_in_google.setVisibility(View.GONE);
        } else {
            button_sign_in_google.setVisibility(View.VISIBLE);
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

}
