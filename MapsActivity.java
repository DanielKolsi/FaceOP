package com.faceop.faceop;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder10;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder11;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder12;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder2;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder3;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder4;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder5;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder6;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder7;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder8;
import com.faceop.faceop.com.faceop.faceop.markers.MarkerAdder9;
import com.faceop.faceop.db.JSONParser;
import com.faceop.faceop.db.Meeting;
import com.faceop.faceop.db.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.faceop.faceop.Resources.TAG_SUCCESS;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnMarkerClickListener, OnConnectionFailedListener, GoogleMap.OnPoiClickListener, OnMapReadyCallback {


    private static final String anchorTitleStart = "Anchor: ";

    private GoogleApiClient googleApiClient = null;
    private String oldMarkerTitle = null;
    private String oldMarkerSnippet = null;
    private String userName = null;
    private double lat = 0;
    private double lo = 0;
    private String anchorCity = null;
    private Button saveAnchorButton = null;
    private Button clearAnchorButton = null;
    private String anchorId = "N/A";
    private Marker marker = null;
    private User user; // signed-in user
    private BitmapDescriptor anchor;
    private GoogleMap googleMap;
    private float zoomLevel;
    private MarkerAdder markerAdder;
    private MarkerAdder2 markerAdder2;
    private MarkerAdder3 markerAdder3;
    private MarkerAdder4 markerAdder4;
    private MarkerAdder5 markerAdder5;
    private MarkerAdder6 markerAdder6;
    private MarkerAdder7 markerAdder7;
    private MarkerAdder8 markerAdder8;
    private MarkerAdder9 markerAdder9;
    private MarkerAdder10 markerAdder10;
    private MarkerAdder11 markerAdder11;
    private MarkerAdder12 markerAdder12;

    private JSONParser jsonParser = new JSONParser();

    private boolean newUser = false;
    private Meeting meeting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage
                (this, this).build();

        Intent intent = getIntent();
        this.userName = intent.getStringExtra(Resources.TAG_NAME);
        this.user = new User();

        new GetUser().execute();
        new GetMyMeeting().execute();

        if (userName != null) {
            user.setName(userName);
        }

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.setHasOptionsMenu(true);
        mapFragment.setMenuVisibility(true);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onStart() {

        super.onStart();
        Button profileButton;

        this.saveAnchorButton = (Button) findViewById(R.id.saveAnchorButton);
        this.clearAnchorButton = (Button) findViewById(R.id.clearAnchorButton);
        this.saveAnchorButton.setEnabled(false);
        this.clearAnchorButton.setEnabled(false);
        profileButton = (Button) findViewById(R.id.profileButton);
        profileButton.setTextColor(Color.parseColor("#ffffff"));

        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        this.zoomLevel = this.googleMap.getCameraPosition().zoom;
        this.marker = marker;

        if (!user.isAnchorSet()) {

            this.lat = marker.getPosition().latitude;
            this.lo = marker.getPosition().longitude;
            this.anchorCity = marker.getTitle();

            marker.setTitle(anchorTitleStart + this.anchorCity);
            marker.setSnippet("Lat:" + lat + " Long:" + lo);
            marker.setIcon(anchor);
            marker.setAlpha(0.85F);

            marker.setVisible(true);
            this.oldMarkerTitle = marker.getTitle();
            this.oldMarkerSnippet = marker.getSnippet();

            user.setName(this.userName);
            user.setAnchorLat(lat);
            user.setAnchorLong(lo);
            user.setAddress(this.anchorCity);
            user.setAnchorSet(true);
            anchorId = marker.getId();
            this.saveAnchorButton.setEnabled(true);
            this.clearAnchorButton.setEnabled(true);
            this.googleMap.moveCamera(CameraUpdateFactory.zoomIn());

        } else if (anchorId.equals(marker.getId())) {
            unSetAnchor();
        }
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason != GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {

            if (!user.isAnchorSet()) {
                this.googleMap.clear();
            }
        }
    }

    @Override
    public void onCameraIdle() {


        if (!user.isAnchorSet()) {
            this.googleMap.clear();
        }
        this.zoomLevel = this.googleMap.getCameraPosition().zoom;

        markerAdder.addMarkers(googleMap, this.zoomLevel);
        markerAdder2.addMarkers(googleMap, this.zoomLevel, markerAdder2.getMarkerOptions());
        markerAdder3.addMarkers(googleMap, this.zoomLevel, markerAdder3.getMarkerOptions());
        markerAdder4.addMarkers(googleMap, this.zoomLevel, markerAdder4.getMarkerOptions());
        markerAdder5.addMarkers(googleMap, this.zoomLevel, markerAdder5.getMarkerOptions());
        markerAdder6.addMarkers(googleMap, this.zoomLevel, markerAdder6.getMarkerOptions());
        markerAdder7.addMarkers(googleMap, this.zoomLevel, markerAdder7.getMarkerOptions());
        markerAdder8.addMarkers(googleMap, this.zoomLevel, markerAdder8.getMarkerOptions());
        markerAdder9.addMarkers(googleMap, this.zoomLevel, markerAdder9.getMarkerOptions());
        markerAdder10.addMarkers(googleMap, this.zoomLevel, markerAdder10.getMarkerOptions());
        markerAdder11.addMarkers(googleMap, this.zoomLevel, markerAdder11.getMarkerOptions());
        markerAdder12.addMarkers(googleMap, this.zoomLevel, markerAdder12.getMarkerOptions());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.zoomLevel = googleMap.getCameraPosition().zoom;
        GoogleMapOptions options = new GoogleMapOptions();

        options.mapToolbarEnabled(true);
        options.rotateGesturesEnabled(true);
        options.tiltGesturesEnabled(true);
        options.ambientEnabled(true);
        options.compassEnabled(true);

        this.markerAdder = new MarkerAdder();
        this.markerAdder.addMarkerOptions();
        this.markerAdder2 = new MarkerAdder2();
        this.markerAdder2.addMarkerOptions(zoomLevel);
        this.markerAdder3 = new MarkerAdder3();
        this.markerAdder3.addMarkerOptions();
        this.markerAdder4 = new MarkerAdder4();
        this.markerAdder4.addMarkerOptions();
        this.markerAdder5 = new MarkerAdder5();
        this.markerAdder5.addMarkerOptions();
        this.markerAdder6 = new MarkerAdder6();
        this.markerAdder6.addMarkerOptions();
        this.markerAdder7 = new MarkerAdder7();
        this.markerAdder7.addMarkerOptions();
        this.markerAdder8 = new MarkerAdder8();
        this.markerAdder8.addMarkerOptions();
        this.markerAdder9 = new MarkerAdder9();
        this.markerAdder9.addMarkerOptions();
        this.markerAdder10 = new MarkerAdder10();
        this.markerAdder10.addMarkerOptions();
        this.markerAdder11 = new MarkerAdder11();
        this.markerAdder11.addMarkerOptions();
        this.markerAdder12 = new MarkerAdder12();
        this.markerAdder12.addMarkerOptions();

        this.googleMap = googleMap;
        googleMap.clear();

        this.anchor = BitmapDescriptorFactory.fromResource(R.drawable.anchor);

        googleMap.setTrafficEnabled(true);
        googleMap.setBuildingsEnabled(true);

        googleMap.setIndoorEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnPoiClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android
                .Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true); // TODO, check Google permissions, https://play.google.com/about/privacy-security/personal-sensitive/
    }


    public void profile(View view) {

        if (newUser) {
            new CreateNewUser().execute();
            newUser = false;
        } else {
            new UpdateUser().execute();
        }

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(Resources.TAG_NAME, this.userName);
        intent.putExtra(Resources.TAG_LATITUDE, user.getAnchorLat());
        intent.putExtra(Resources.TAG_LONGITUDE, user.getAnchorLong());
        intent.putExtra(Resources.TAG_TIME, user.getDisplayTime());
        intent.putExtra(Resources.TAG_DATE, user.getDateStart());
        intent.putExtra(Resources.TAG_ADDRESS, this.anchorCity);
        intent.putExtra(Resources.TAG_DESC, user.getDescription());
        intent.putExtra(Resources.TAG_MEETINGS, user.getMeetings());
        intent.putExtra(Resources.TAG_HASCONFIRMED, user.getHasConfirmed());

        // intent for meeting
        if (this.meeting != null) {
            intent.putExtra(Resources.TAG_NAME, this.userName);
            intent.putExtra(Resources.TAG_MEETING_NAME, this.meeting.getName());
            intent.putExtra(Resources.TAG_MEETING_LATITUDE, this.meeting.getLatitude());
            intent.putExtra(Resources.TAG_MEETING_LONGITUDE, this.meeting.getLongitude());
            intent.putExtra(Resources.TAG_MEETING_DATE, this.meeting.getDate());
            intent.putExtra(Resources.TAG_MEETING_TIME, this.meeting.getTime());
            intent.putExtra(Resources.TAG_MEETING_ADDRESS, this.meeting.getExactLocation());
            intent.putExtra(Resources.TAG_PARTICIPANTS, this.meeting.getParticipants());

            intent.putExtra(Resources.TAG_CONFIRMCOUNT, meeting.getConfirmCount());
            intent.putExtra(Resources.TAG_ORGANIZER, meeting.getOrganizer());
        }
        startActivity(intent);
    }

    /**
     * Saves select map anchor city and its values to the database.
     *
     * @param view View
     */
    public void saveAnchor(View view) {


        if (this.newUser) { // store a new user to the DB
            setUserAchorData();
            new CreateNewUser().execute();
            this.newUser = false;
        } else {
            setUserAchorData();
            new UpdateUser().execute();
        }
        this.saveAnchorButton.setEnabled(false);
    }

    private void unSetAnchor() {

        if (this.marker == null) return;

        this.anchorId = "N/A";
        this.saveAnchorButton.setEnabled(false);

        marker.setTitle(this.oldMarkerTitle);
        marker.setSnippet(this.oldMarkerSnippet);
        marker.setVisible(false);
        this.oldMarkerTitle = null;
        this.oldMarkerSnippet = null;

        user.setAnchorSet(false); // restore to normal marker
        user.setAnchorLat(lat);
        user.setAnchorLong(lo);
        this.anchorCity = "-";
        lat = 0;
        lo = 0;
        this.clearAnchorButton.setEnabled(false);
    }

    private void setUserAchorData() {
        this.user.setAddress(this.anchorCity);
        this.user.setAnchorLong(this.lo);
        this.user.setAnchorLat(this.lat);
    }

    /**
     * @param view view
     */
    public void clearAnchor(View view) {
        this.googleMap.clear();
        unSetAnchor();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {

        this.googleMap.clear();
        this.user.setAnchorSet(false);
        MarkerOptions options = new MarkerOptions()
                .position(pointOfInterest.latLng).icon(anchor).title(pointOfInterest.name); //.visible(true);

        this.saveAnchorButton.setEnabled(false);
        this.clearAnchorButton.setEnabled(false);

        this.googleMap.addMarker(options).setVisible(true);

        this.googleMap.setContentDescription(pointOfInterest.name);
        this.lat = pointOfInterest.latLng.latitude;
        this.lo = pointOfInterest.latLng.longitude;
        this.anchorCity = pointOfInterest.name;

        user.setName(this.userName);
        user.setAnchorLat(this.lat);
        user.setAnchorLong(this.lo);
        user.setAddress(this.anchorCity);
        user.setAnchorSet(true);
        this.saveAnchorButton.setEnabled(true);
        this.clearAnchorButton.setEnabled(true);
    }


    class GetMyMeeting extends AsyncTask<String, String, String> {

        final String url_get_my_meeting = "http://www.faceop.com/get_meeting.php";

        /**
         * Saving user
         */
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(Resources.TAG_NAME, user.getName()));
            JSONObject json = jsonParser.makeHttpRequest(url_get_my_meeting, "GET", params);

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    JSONArray userObjArray = json.getJSONArray(Resources.TAG_MEETINGS);
                    JSONObject userObj = userObjArray.getJSONObject(0);

                    if (userObj != null) {
                        meeting = new Meeting();
                        String name = userObj.getString(Resources.TAG_MEETING_NAME);

                        meeting.setName(name);
                        String participants = userObj.getString(Resources.TAG_PARTICIPANTS);
                        meeting.setParticipants(participants);
                        String latitude = userObj.getString(Resources.TAG_MEETING_LATITUDE);
                        meeting.setLatitude(Double.parseDouble(latitude));
                        String longitude = userObj.getString(Resources.TAG_MEETING_LONGITUDE);
                        meeting.setLongitude(Double.parseDouble(longitude));
                        String date = userObj.getString(Resources.TAG_MEETING_DATE);
                        meeting.setDate(date);
                        String time = userObj.getString(Resources.TAG_MEETING_TIME);
                        meeting.setTime(time);
                        String address = userObj.getString(Resources.TAG_MEETING_ADDRESS);
                        meeting.setExactLocation(address);
                        String confirmcount = userObj.getString(Resources.TAG_CONFIRMCOUNT);
                        meeting.setConfirmCount(Integer.parseInt(confirmcount));
                        String organizer = userObj.getString(Resources.TAG_ORGANIZER);
                        meeting.setOrganizer(organizer);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class GetUser extends AsyncTask<String, String, String> {

        final String url_get_user = "http://www.faceop.com/get_user.php";

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", userName));
            JSONObject json = jsonParser.makeHttpRequest(url_get_user, "GET", params);

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    JSONArray userObjArray = json.getJSONArray(Resources.TAG_USER);
                    JSONObject userObj = userObjArray.getJSONObject(0);

                    if (userObj != null) {

                        try {
                            userName = userObj.getString(Resources.TAG_NAME);
                            user.setName(userName);

                            String desc = userObj.getString(Resources.TAG_DESC);

                            user.setDescription(desc);
                            String date = userObj.getString(Resources.TAG_DATE);
                            user.setDateStart(date);
                            String time = userObj.getString(Resources.TAG_TIME);
                            user.setDisplayTime(time);
                            String meetings = userObj.getString(Resources.TAG_MEETINGS);
                            user.setMeetings(meetings); // TODO, redundant?

                            String latitude = userObj.getString(Resources.TAG_LATITUDE);

                            if (latitude != null) {
                                user.setAnchorLat(Double.parseDouble(latitude));
                            }

                            String longitude = userObj.getString(Resources.TAG_LONGITUDE);
                            if (longitude != null) {
                                user.setAnchorLong(Double.parseDouble(longitude));
                            }

                            anchorCity = userObj.getString(Resources.TAG_ADDRESS);
                            user.setAddress(anchorCity);
                            String hasconfirmed = userObj.getString(Resources.TAG_HASCONFIRMED);

                            if (hasconfirmed != null) {
                                user.setHasConfirmed(Integer.parseInt(hasconfirmed));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    newUser = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class CreateNewUser extends AsyncTask<String, String, String> {

        final String url_create_user = "http://www.faceop.com/create_user.php";


        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair(Resources.TAG_NAME, user.getName()));
            params.add(new BasicNameValuePair(Resources.TAG_LATITUDE, "" + user.getAnchorLat()));
            params.add(new BasicNameValuePair(Resources.TAG_LONGITUDE, "" + user.getAnchorLong()));
            params.add(new BasicNameValuePair(Resources.TAG_ADDRESS, user.getAddress()));

            jsonParser.makeHttpRequest(url_create_user,
                    "POST", params);
            return null;
        }
    }

    class UpdateUser extends AsyncTask<String, String, String> {

        String url_update_user_map = "http://www.faceop.com/update_user_map.php";

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair(Resources.TAG_NAME, user.getName()));
            params.add(new BasicNameValuePair(Resources.TAG_LATITUDE, "" + user.getAnchorLat()));
            params.add(new BasicNameValuePair(Resources.TAG_LONGITUDE, "" + user.getAnchorLong()));
            params.add(new BasicNameValuePair(Resources.TAG_ADDRESS, user.getAddress()));

            jsonParser.makeHttpRequest(url_update_user_map, "POST", params);
            return null;
        }
    }
}