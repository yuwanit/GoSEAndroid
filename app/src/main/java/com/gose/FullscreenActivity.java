package com.gose;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.gose.session.UserLogin;

public class FullscreenActivity extends Activity {

    private static final int STOPSPLASH = 0;
    // time duration in millisecond for which your splash screen should visible
    // to
    // user. here i have taken half second
    private static final long SPLASHTIME = 2500;

    private static UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private static String TAG = MainActivity.class.getSimpleName();

    private static UserLogin sessionUser = UserLogin.getInstance();

    // handler for splash screen
    private Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOPSPLASH:
                    // Generating and Starting new intent on splash time out
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    FullscreenActivity.this.finish(); // Updated (Thanks to Jerimiah)
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fullscreen);

        uiHelper = new UiLifecycleHelper(FullscreenActivity.this, callback);
        uiHelper.onCreate(savedInstanceState);

        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {

                    sessionUser.setUserImage("http://graph.facebook.com/" + user.getId() + "/picture?type=small");
                    sessionUser.setUserId("F" + user.getId());
                    sessionUser.setUserName(user.getName());
                }
            });
            request.executeAsync();

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }

    }

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

}
