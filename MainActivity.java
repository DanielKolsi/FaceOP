package com.faceop.faceop;

/**
 * Created by Daniel A.M. Kolsi on 03/09/16.
 * FaceOP Sign in
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FaceOP TODO:
 * 1) Unit tests
 * 2) integration tests
 * 6) OK: #tags library for matching -> restricted matching (tag codes 1-10)
 * 7) exact location test (meeting.address?)
 * 12) mock credit system implementation
 * 14) in-app alpha impl
 * 20) build.cradle -> 10.x & FireBase integration->10.01
 * ***22) Bug in CONFIRMCOUNT: you can confirm your own meeting n times after signing of
 * 23) AI Motor for matching & tags (text mining for profile field)
 * 24) More for the MAP: multianchor, places of interest, own position, more info about cities, tooltips, move anchor (d&d), all anchors <-DB to map,
 * 26) flexible meeting dates & times / no date -> future
 * 27) https://developer.android.com/guide/topics/ui/controls/button.html
 * ***28) CHANGE: ic_launcher.png to *FACEOP* official icon! (res/mipmapXXX/... .png
 * 29) Google Places API: https://developers.google.com/places/android-api/start
 * 30) name -> ID
 * <?xml version="1.0" encoding="utf-8"?>
 * <selector xmlns:android="http://schemas.android.com/apk/res/android">
 *  <item android:drawable="@drawable/button_pressed"
 * android:state_pressed="true" />
 * <item android:drawable="@drawable/button_focused"
 *  android:state_focused="true" />
 * <item android:drawable="@drawable/button_default" />
 * </selector>
 *
 * MainActivity for FaceOP (MainActivity)
 */
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInAccount googleSignInAccount = null;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    private LoginButton loginButtonFB;
    private CallbackManager callbackManager;
    private String displayName;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MultiDex.install(this);

        final Intent intent = new Intent(this, MapsActivity.class);

        if (intent.getFlags() == (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
            finish();
            return;
        }
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in); // must be before initializing the login button!
        callbackManager = CallbackManager.Factory.create();


        this.loginButtonFB = (LoginButton) findViewById(R.id.login_button);

        if (this.loginButtonFB != null) {
            this.loginButtonFB.setReadPermissions("public_profile");
        }

        loginButtonFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        //String email = user.optString("email");
                        String name = user.optString("name");
                        intent.putExtra("name", name);
                        mStatusTextView.setText(getString(R.string.signed_in_fmt, name));
                        startActivity(intent);
                    }
                }).executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("onCancel", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("onError", "onError");
            }
        });

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        try {
            findViewById(R.id.sign_in_button).setOnClickListener(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestId()
                .requestEmail()
                .requestIdToken(Resources.IDTOKEN)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .addApi(AppIndex.API).build();
    }

    public String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        String loginText = loginButtonFB.getText().toString();

        if (loginText.length() < 10) { // FIXME, fix this kludge; user is logged in but needs to be logged out!

            if (googleSignInAccount != null) {
                this.displayName = googleSignInAccount.getDisplayName();
                final Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("name", this.displayName);
                startActivity(intent);
            }
        }

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);


        if (opr.isDone()) { // cached sign-in
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }

        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * Handles sign in result (check the statuscode)
     *
     * @param result sign in result
     */
    private void handleSignInResult(GoogleSignInResult result) {

        this.googleSignInAccount = result.getSignInAccount();

        if (this.googleSignInAccount != null) {
            this.displayName = this.googleSignInAccount.getDisplayName();

            if (this.displayName != null && result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                mStatusTextView.setText(getString(R.string.signed_in_fmt, this.displayName));
                updateUI(true);
            } else {
                // Signed out, show unauthenticated UI.
                updateUI(false);
            }
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    private void updateUI(boolean signedIn) {

        if (signedIn) {

            try {
                findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            final Intent intent = new Intent(this, MapsActivity.class);

            if (googleSignInAccount != null) {
                intent.putExtra("name", this.displayName);
            }
            startActivity(intent);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            try {
                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://www.faceop.com"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}