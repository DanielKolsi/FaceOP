package com.faceop.faceop;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.faceop.faceop.db.JSONParser;
import com.faceop.faceop.db.Meeting;
import com.faceop.faceop.db.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.faceop.faceop.DatePickerFragment.format1;


public class ProfileActivity extends AppCompatActivity {

    private User user = null;
    private TimePickerFragment timePickerFragment = null;
    private DatePickerFragment datePickerFragment = null;

    private Meeting meeting = null;
    private Button matchButton = null;
    private TextView feedBackTextView = null;
    private boolean deletePassedMeeting = false; // meeting time has already passed OR is at the same date than the matching attempt
    private GoogleApiClient googleApiClient = null;

    private Intent intent = null;
    private JSONParser jsonParser = new JSONParser();
    private String match_url = null;
    private JSONParser jParser = new JSONParser();
    private List<NameValuePair> matchParams = new ArrayList<>();

    private List<User> userList = new ArrayList<>();
    private View view = null;
    private EditText editTextAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API).build();
        TextView textView;

        this.intent = getIntent();

        if (this.user == null) {
            this.user = new User();
        }


        this.user.setName(intent.getStringExtra(Resources.TAG_NAME));
        double lat = intent.getDoubleExtra(Resources.TAG_LATITUDE, 0);
        this.user.setAnchorLat(lat);
        double lo = intent.getDoubleExtra(Resources.TAG_LONGITUDE, 0);
        this.user.setAnchorLong(lo);

        this.user.setDisplayTime(intent.getStringExtra(Resources.TAG_TIME));
        String date = intent.getStringExtra(Resources.TAG_DATE);

        this.user.setDateStart(date);
        this.user.setAddress(intent.getStringExtra(Resources.TAG_ADDRESS));
        this.user.setDescription(intent.getStringExtra(Resources.TAG_DESC));
        int hasconfirmed = intent.getIntExtra(Resources.TAG_HASCONFIRMED, 0);

        this.user.setHasConfirmed(hasconfirmed);

        String m_name = intent.getStringExtra(Resources.TAG_MEETING_NAME);

        if (m_name != null) {
            this.meeting = new Meeting(); // meeting has been stored to the DB (fetched at MapsActivity)

            meeting.setName(m_name);
            meeting.setLongitude(intent.getDoubleExtra(Resources.TAG_MEETING_LONGITUDE, 0));
            meeting.setLatitude(intent.getDoubleExtra(Resources.TAG_MEETING_LATITUDE, 0));
            meeting.setTime(intent.getStringExtra(Resources.TAG_MEETING_TIME));
            meeting.setDate(intent.getStringExtra(Resources.TAG_MEETING_DATE));
            meeting.setExactLocation(intent.getStringExtra(Resources.TAG_MEETING_ADDRESS));
            meeting.setParticipants(intent.getStringExtra(Resources.TAG_PARTICIPANTS));
            meeting.setOrganizer(intent.getStringExtra(Resources.TAG_ORGANIZER));
            meeting.setConfirmCount(intent.getIntExtra(Resources.TAG_CONFIRMCOUNT, 0));
        }



        setContentView(R.layout.activity_profile);
        this.matchButton = (Button) findViewById(R.id.matchButton);

        Button myMeetingsButton = (Button) findViewById(R.id.myMeetingsButton);
        feedBackTextView = (TextView) findViewById(R.id.errorTextView);

        this.datePickerFragment = new DatePickerFragment();
        textView = (TextView) findViewById(R.id.userProfileText);


        if (textView != null) {
            textView.setText(getUserDataFromIntent());
        }
        isReadyForMatchingAMeeting();

        boolean userDatePassed;

        String desc = this.user.getDescription();

        if (desc != null && desc.equals(Meeting.VIRTUAL)) {
            this.matchButton.setEnabled(true);
            this.editTextAddress.setEnabled(true);
        }

        if (this.meeting != null) {

            Date now = new Date();
            String simpleDateUser = this.user.getDateStart();
            String simpleDateMeeting = this.meeting.getDate();

            if (simpleDateMeeting == null) return;

            Date meetingDate; // the actual date the matched and stored meeting should occur
            Date userDate; // the date user has saved for matching a meeting

            try {
                String dateString = simpleDateMeeting + " " + this.meeting.getTime() + ":00";

                meetingDate = format1.parse(dateString); // TODO meetingDate = format1.parse(dateString);
                userDate = format1.parse(simpleDateUser);

                this.deletePassedMeeting = now.after(meetingDate);
                userDatePassed = now.after(userDate); // cannot match a meeting for a date that has already passed

                if (this.deletePassedMeeting) {
                    myMeetingsButton.setEnabled(false);

                    feedBackTextView.setTextColor(Color.RED);
                    feedBackTextView.setText(R.string.noMatchedMeetings);

                    new DeleteMeeting().execute(); // delete the past meeting from the DB
                    this.user.setHasConfirmed(0);// not confirmed, because his meeting was deleted!
                    new UpdateHasconfirmed().execute();

                    if (userDatePassed) {
                        this.matchButton.setEnabled(false);
                        this.editTextAddress.setEnabled(false);
                    } else {
                        this.matchButton.setEnabled(true);
                        this.editTextAddress.setEnabled(true);
                    }
                } else {
                    myMeetingsButton.setEnabled(true);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            isReadyForMatchingAMeeting();
            myMeetingsButton.setEnabled(false);
        }
        checkProfileMeetingDate();
    }


    private void checkProfileMeetingDate() {

        Date date = new Date();
        String simpleDate = this.user.getDateStart();

        if (simpleDate == null) return;

        Date meetingDate = null;

        try {
            meetingDate = format1.parse(simpleDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean meetingDatePassed = date.after(meetingDate);

        if (meetingDatePassed) {
            this.feedBackTextView.setText(Resources.MEETING_DATE_PASSED2);
        }

    }
    /**
     * @param view View
     */
    public void myMeetings(View view) {


            Intent intent = new Intent(this, MyMeetingsActivity.class);
            intent.putExtra(Resources.TAG_MEETING_NAME, this.meeting.getName());
            intent.putExtra(Resources.TAG_MEETING_LATITUDE, this.meeting.getLatitude());

            intent.putExtra(Resources.TAG_MEETING_LONGITUDE, this.meeting.getLongitude());
            intent.putExtra(Resources.TAG_MEETING_DATE, this.meeting.getDate());
            intent.putExtra(Resources.TAG_MEETING_TIME, this.meeting.getTime());
            intent.putExtra(Resources.TAG_MEETING_ADDRESS, this.meeting.getExactLocation());
            intent.putExtra(Resources.TAG_PARTICIPANTS, this.meeting.getParticipants());
            intent.putExtra(Resources.TAG_CONFIRMCOUNT, meeting.getConfirmCount());
            intent.putExtra(Resources.TAG_ORGANIZER, meeting.getOrganizer());

            intent.putExtra(Resources.TAG_NAME, user.getName());

            intent.putExtra(Resources.TAG_LATITUDE, user.getAnchorLat());
            intent.putExtra(Resources.TAG_LONGITUDE, user.getAnchorLong());
            intent.putExtra(Resources.TAG_TIME, user.getDisplayTime());
            intent.putExtra(Resources.TAG_DATE, user.getDateStart());
            intent.putExtra(Resources.TAG_ADDRESS, user.getAddress());
            intent.putExtra(Resources.TAG_DESC, user.getDescription());
            intent.putExtra(Resources.TAG_MEETINGS, user.getMeetings());
            intent.putExtra(Resources.TAG_HASCONFIRMED, user.getHasConfirmed());

            startActivity(intent);
    }

    /**
     * @param view view
     */
    public synchronized void matchAndStoreMeeting(View view) {

        final String url_match = "http://www.faceop.com/match.php";
        final String url_match_virtual = "http://www.faceop.com/match_virtual.php";
        final String url_match_simple = "http://www.faceop.com/match_simple.php";


        this.view = view;

        if (meeting != null) return; // TODO at this point allow only match meetings when no meetings are reserved for the future

        String tag = this.user.getDescription();

        if ((user.getAddress() == null) && tag.equals(Meeting.VIRTUAL)) {
            match_url = url_match_virtual; // for testing
        } else if (tag == null || tag.length() < 2) {
            match_url = url_match_simple; // simple query is without #tag description
        } else if (tag.equals(Meeting.VIRTUAL)) {
            match_url = url_match_simple; // simple query is without #tag description
        } else if (tag.length() > 15) {
            match_url = url_match_simple; // initially support only tag lengts of 2-15
        } else {
            match_url = url_match;
        }
        matchParams.clear();
        matchParams.add(new BasicNameValuePair(Resources.TAG_DATE, user.getDateStart()));
        matchParams.add(new BasicNameValuePair(Resources.TAG_TIME, user.getDisplayTime()));
        matchParams.add(new BasicNameValuePair(Resources.TAG_DESC, user.getDescription()));
        matchParams.add(new BasicNameValuePair(Resources.TAG_ADDRESS, user.getAddress()));
        this.meeting = new Meeting(); // only confirmcount of a meeting shall be updated; create a new meeting!
        EditText editText = (EditText) findViewById(R.id.exactAddressEditText);


        if (editText != null) {
            String exactLocation = editText.getText().toString();
            int l = exactLocation.length();

            if (l > 10 && l <= 100) {
                String exactMeetingPlace = exactLocation + " (" + user.getAddress() + ")";
                this.meeting.setExactLocation(exactMeetingPlace); // add exact location
            }
        }
        new MatchAndStoreMeeting().execute();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!user.getDescription().equals(Meeting.VIRTUAL)) {
            feedBackTextView.setTextColor(Color.parseColor("#33cc33"));
            feedBackTextView.setText(Resources.NEW_MEETING_STORED);
        }
        if (userList.size() < Meeting.MIN_MATCHED_PARTICIPANTS) {
            feedBackTextView.setTextColor(Color.RED);
            feedBackTextView.setText((getString(R.string.notEnoughParticipants) + userList.size() + ")"));
        }
    }

    /**
     * @param view Android view
     */
    public void map(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(Resources.TAG_NAME, this.user.getName());
        startActivity(intent);
    }

    /**
     * @param view Android view
     */
    public void time(View view) {
        if (this.timePickerFragment == null) {
            this.timePickerFragment = new TimePickerFragment();
        }

        timePickerFragment.show(getFragmentManager(), "TimePicker");
        //isReadyForMatchingAMeeting(); // FIXME matching is done against STORED values!
    }

    /**
     * @param view Android view
     */
    public void date(View view) {

        if (this.datePickerFragment == null) {
            this.datePickerFragment = new DatePickerFragment();
        }
        this.datePickerFragment.show(getFragmentManager(), "DatePicker");
        //isReadyForMatchingAMeeting();
    }

    /**
     * Updates the already created user profile.
     *
     * @param view Android view
     */
    public void save(View view) {

        if (this.user != null) {

            double lat = intent.getDoubleExtra(Resources.TAG_LATITUDE, 0);
            double lo = intent.getDoubleExtra(Resources.TAG_LONGITUDE, 0);
            this.user.setAnchorLat(lat);
            this.user.setAnchorLong(lo);
            String city = intent.getStringExtra(Resources.TAG_ADDRESS); // anchor city
            this.user.setAddress(city);
            EditText editText = (EditText) findViewById(R.id.tag_description);
            String desc = editText.getText().toString();
            this.user.setDescription(desc);

            if (timePickerFragment != null) {
                String displayTime = this.timePickerFragment.getDisplayTime();

                if (displayTime != null) {
                    this.user.setDisplayTime(displayTime);
                }
            }

            if (datePickerFragment != null) {
                String date = datePickerFragment.getDate();

                if (date != null) {
                    this.user.setDateStart(date);
                }
            }
            new SaveUser().execute();

            feedBackTextView.setTextColor(Color.parseColor("#33cc33"));
            feedBackTextView.setText(Resources.PROFILE_SAVED);


            TextView tv = (TextView) findViewById(R.id.userProfileText);
            if (tv != null) {
                tv.setText(getUserDataAfterSave());
            }

            isReadyForMatchingAMeeting();
        }
    }

    /**
     * @return true, if ready for matching a meeting, false otherwise
     */
    private boolean isReadyForMatchingAMeeting() {

        final String desc = this.user.getDescription();

        if (desc == null) {
            this.matchButton.setEnabled(false);
            this.editTextAddress.setEnabled(false);
            return false;
        }
        if (desc.equals(Meeting.VIRTUAL)) {
            this.matchButton.setEnabled(true); // allow just the virtual meeting
            this.editTextAddress.setEnabled(true);
            this.feedBackTextView.setText("Ready for virtual matching.");
            return true;
        }
        if (this.meeting != null && !this.deletePassedMeeting) {
            this.matchButton.setEnabled(false);
            this.editTextAddress.setEnabled(false);
            return false;
        }
        if (this.user.getDateStart() == null) {
            this.matchButton.setEnabled(false);
            this.editTextAddress.setEnabled(false);
            return false;
        }
        if (this.user.getDisplayTime() == null) {
            this.matchButton.setEnabled(false);
            this.editTextAddress.setEnabled(false);
            return false;
        }

        if (this.user.getAddress() == null) {
            this.matchButton.setEnabled(false);
            this.editTextAddress.setEnabled(false);
            return false;
        }
        this.editTextAddress.setEnabled(true);
        this.feedBackTextView.setText("Ready for matching.");
        this.matchButton.setEnabled(true);
        return true;
    }

    private String getUserDataFromIntent() {

        double lat = intent.getDoubleExtra(Resources.TAG_LATITUDE, 0); // läntinen leveys
        double lo = intent.getDoubleExtra(Resources.TAG_LONGITUDE, 0);
        String address = intent.getStringExtra(Resources.TAG_ADDRESS);


        if (address == null) {
            address = "N/A";
        }
        String desc = intent.getStringExtra(Resources.TAG_DESC);


        if (desc == null || desc.trim().length() < 1) {
            desc = "#VIRTUAL";
        }

        EditText editText = (EditText) findViewById(R.id.tag_description);
        editText.setBackgroundColor(Color.parseColor("#E6E6FA"));
        editText.setText(desc);
        this.user.setDescription(desc);

        String date = intent.getStringExtra(Resources.TAG_DATE);
        if (date == null) {
            date = "N/A";
        }
        String time = intent.getStringExtra(Resources.TAG_TIME);
        if (time == null) {
            time = "N/A";
        }

        String latitude = "N/A";
        String longitude = "N/A";


        if (lat != 0) {
            latitude = "" + lat;
        }
        if (lo != 0) {
            longitude = "" + lo;
        }
        this.editTextAddress = (EditText) findViewById(R.id.exactAddressEditText);
        editTextAddress.setBackgroundColor(Color.parseColor("#E6E6FA"));


        return "\uD83D\uDC64" + " " + this.user.getName() + "\n\uD83D\uDCC3 Tag: " + desc +
                "\n\uD83D\uDCC6Meeting date: " + date + "\n\u23F0Meeting time: " + time /*+ "\nMeetings: " + user.getMeetings()*/ +
                "\nLatitude: " + latitude + "\nLongitude: " + longitude + "\n\u2693 Anchor: " + address;
    }

    private String getUserDataAfterSave() {
        return "\uD83D\uDC64" + " " + this.user.getName() + "\n\uD83D\uDCC3 Tag: " + user.getDescription() +
                "\n\uD83D\uDCC6Meeting date: " + user.getDateStart() + "\n\u23F0Meeting time: " + user.getDisplayTime() +
                "\nLatitude: " + user.getAnchorLat() + "\nLongitude: " + user.getAnchorLong() + "\n\u2693 Anchor: " + user.getAddress();
    }


    /**
     * @param view View
     */
    public void signout(View view) {

        final Intent intent = new Intent(this, MainActivity.class);
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        this.googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.googleApiClient.disconnect();
    }

    class SaveUser extends AsyncTask<String, String, String> {

        final String url_update_user = "http://www.faceop.com/update_user.php";

        /**
         * Saving user
         */
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(Resources.TAG_NAME, user.getName()));

            params.add(new BasicNameValuePair(Resources.TAG_LATITUDE, "" + user.getAnchorLat()));
            params.add(new BasicNameValuePair(Resources.TAG_LONGITUDE, "" + user.getAnchorLong()));

            params.add(new BasicNameValuePair(Resources.TAG_DESC, user.getDescription()));
            params.add(new BasicNameValuePair(Resources.TAG_DATE, user.getDateStart()));
            params.add(new BasicNameValuePair(Resources.TAG_TIME, user.getDisplayTime()));
            params.add(new BasicNameValuePair(Resources.TAG_MEETINGS, user.getMeetings()));
            params.add(new BasicNameValuePair(Resources.TAG_ADDRESS, user.getAddress()));
            params.add(new BasicNameValuePair(Resources.TAG_HASCONFIRMED, "" + user.getHasConfirmed()));

            jsonParser.makeHttpRequest(url_update_user, "POST", params);
            return null;
        }
    }


    class DeleteMeeting extends AsyncTask<String, String, String> {
        final String url_delete_meeting = "http://www.faceop.com/delete_meeting.php";

        protected String doInBackground(String... args) {

            //Log.d("DEL", "DELMEX2=" + meeting.getName());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(Resources.TAG_MEETING_NAME, meeting.getName()));
            jsonParser.makeHttpRequest(url_delete_meeting, "POST", params);
            return null;
        }
    }

    class MatchAndStoreMeeting extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            JSONObject json = jParser.makeHttpRequest(match_url, "GET", matchParams);
            final String url_create_meeting = "http://www.faceop.com/create_meeting.php"; // meeting is never updated!

            try {
                userList.clear(); // empty previous matches
                int success = json.getInt(Resources.TAG_SUCCESS);

                if (success == 1) {
                    JSONArray participants = json.getJSONArray(Resources.TAG_USERS); // TODO

                    for (int i = 0; i < participants.length(); i++) {
                        User matchUser = new User();
                        JSONObject participant = participants.getJSONObject(i);

                        try {
                            matchUser.setName(participant.getString(Resources.TAG_NAME));
                            userList.add(matchUser);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (userList.size() < Meeting.MIN_MATCHED_PARTICIPANTS) return null; // TODO

                    Iterator<User> iterator = userList.iterator();

                    String user_names = "";
                    while (iterator.hasNext()) {
                        User usr = iterator.next();
                        if (user_names.length() > 1) {
                            user_names = user_names + ", " + usr.getName();
                        } else {
                            user_names = user_names + usr.getName();
                        }
                    }

                    meeting.setName(user.getDescription() + user.getDisplayTime()); // meeting name
                    meeting.setParticipants(user_names); // John Smith#Joe Dow#Tim Lahaye
                    meeting.setDate(user.getDateStart()); // matcher's anchor date
                    meeting.setLatitude(user.getAnchorLat()); // user's anchor lat -> meeting.lat
                    meeting.setLongitude(user.getAnchorLong());// user's anchor longitude -> meeting.longitude
                    meeting.setTime(user.getDisplayTime()); // matcher's anchor time
                    meeting.setOrganizer(user.getName()); // matcher is the organizer of the meeting
                    meeting.setConfirmCount(1); // matcher always confirms his own meeting
                }

                if (!user.getDescription().equals(Meeting.VIRTUAL)) {  // don't store a virtual meeting
                    matchParams.add(new BasicNameValuePair(Resources.TAG_NAME, meeting.getName()));
                    matchParams.add(new BasicNameValuePair(Resources.TAG_LATITUDE, "" + meeting.getLatitude()));
                    matchParams.add(new BasicNameValuePair(Resources.TAG_LONGITUDE, "" + meeting.getLongitude()));
                    matchParams.add(new BasicNameValuePair(Resources.TAG_PARTICIPANTS, meeting.getParticipants()));
                    matchParams.add(new BasicNameValuePair(Resources.TAG_ORGANIZER, meeting.getOrganizer()));
                    matchParams.add(new BasicNameValuePair(Resources.TAG_DATE, meeting.getDate()));
                    matchParams.add(new BasicNameValuePair(Resources.TAG_TIME, meeting.getTime()));
                    matchParams.add(new BasicNameValuePair(Resources.TAG_ADDRESS, meeting.getExactLocation()));
                    matchParams.add(new BasicNameValuePair(Resources.TAG_CONFIRMCOUNT, "" + meeting.getConfirmCount()));
                    matchParams.add(new BasicNameValuePair(Resources.TAG_MEETING_ADDRESS, "" + meeting.getExactLocation()));

                    jsonParser.makeHttpRequest(url_create_meeting, "POST", matchParams); // store the meeting into MySQL
                    myMeetings(view);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class UpdateHasconfirmed extends AsyncTask<String, String, String> {

        final String url_update_hasconfirmed = "http://www.faceop.com/update_hasconfirmed.php";

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(Resources.TAG_NAME, user.getName()));
            params.add(new BasicNameValuePair(Resources.TAG_HASCONFIRMED, "" + user.getHasConfirmed()));
            jsonParser.makeHttpRequest(url_update_hasconfirmed, "POST", params);
            return null;
        }
    }
}
