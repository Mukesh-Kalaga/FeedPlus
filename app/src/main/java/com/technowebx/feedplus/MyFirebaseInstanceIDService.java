package com.technowebx.feedplus;

import android.app.Service;

/**
 * Created by WIN8 on 30-07-2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    public void sendRegistrationToServer(String token) {

        send_data sd = new send_data(token);
        sd.execute();
    }
    public static class send_data extends AsyncTask<Void,Void,Void> {
        String token;
        send_data(String token) {
            this.token = token;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestHandler sendid = new RequestHandler();
            HashMap<String,String> data = new HashMap<>();
            data.put("fcm_token",token);
            sendid.sendPostRequest("http://www.adityawebapps.com/feedback/new_user.php",data);
            return null;
        }
    }
}