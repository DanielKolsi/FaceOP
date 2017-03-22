package com.faceop.faceop;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.faceop.faceop.db.JSONParser;
import com.faceop.faceop.db.Meeting;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class MyMeetingsActivity extends AppCompatActivity {

    private String userName = null;
    private Meeting meeting = null;
    private EditText editText = null;
    private Button confirm = null;
    private Intent intent = null;

    private JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_meetings);

        this.intent = getIntent();
        EditText dateText = (EditText) findViewById(R.id.editText5);
        EditText timeText = (EditText) findViewById(R.id.editText6);
        this.editText = (EditText) findViewById(R.id.editText);
        this.confirm = (Button) findViewById(R.id.button11);

        this.userName = intent.getStringExtra(Resources.TAG_NAME);
        this.confirm.setTextColor(Color.DKGRAY);

        // initialize a new meeeting with intent values
        this.meeting = new Meeting();
        //this.meeting.setId(Integer.parseInt(intent.getStringExtra("m_id")));
        this.meeting.setName(intent.getStringExtra(Resources.TAG_MEETING_NAME));
        this.meeting.setLatitude(intent.getDoubleExtra(Resources.TAG_MEETING_LATITUDE, 0));
        this.meeting.setLongitude(intent.getDoubleExtra(Resources.TAG_MEETING_LONGITUDE, 0));
        this.meeting.setTime(intent.getStringExtra(Resources.TAG_MEETING_TIME));
        this.meeting.setDate(intent.getStringExtra(Resources.TAG_MEETING_DATE));
        this.meeting.setExactLocation(intent.getStringExtra(Resources.TAG_MEETING_ADDRESS));
        this.meeting.setParticipants(intent.getStringExtra(Resources.TAG_PARTICIPANTS));
        this.meeting.setOrganizer(intent.getStringExtra(Resources.TAG_ORGANIZER));
        this.meeting.setConfirmCount(intent.getIntExtra(Resources.TAG_CONFIRMCOUNT, 0));

        if (intent.getIntExtra(Resources.TAG_HASCONFIRMED, 0) != 0) {
            this.confirm.setEnabled(false);
            this.confirm.setTextColor(Color.LTGRAY);
        }

        if (this.meeting == null) {
            editText.setText(R.string.noMeetings);
        } else {
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(15);
            editText.setText(getMeetingData(0));
            editText.setFadingEdgeLength(3);
            editText.setEnabled(false);

            dateText.setTextColor(Color.BLACK);
            dateText.setTextSize(22);
            dateText.setText(meeting.getDate());
            dateText.setEnabled(false);
            timeText.setTextColor(Color.BLACK);
            timeText.setTextSize(22);
            timeText.setText(meeting.getTime());
            timeText.setEnabled(false);
        }
    }

    public void profile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        setIntents(intent);
        startActivity(intent);
    }

    public void map(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        setIntents(intent);
        startActivity(intent);
    }

    private void setIntents(Intent intent) {

        intent.putExtra(Resources.TAG_NAME, userName);
        intent.putExtra(Resources.TAG_DATE, this.intent.getStringExtra(Resources.TAG_DATE));
        intent.putExtra(Resources.TAG_TIME, this.intent.getStringExtra(Resources.TAG_TIME));
        intent.putExtra(Resources.TAG_LATITUDE, this.intent.getDoubleExtra(Resources.TAG_LATITUDE, 0));
        intent.putExtra(Resources.TAG_LONGITUDE, this.intent.getDoubleExtra(Resources.TAG_LONGITUDE, 0));
        intent.putExtra(Resources.TAG_ADDRESS, this.intent.getStringExtra(Resources.TAG_ADDRESS));
        intent.putExtra(Resources.TAG_DESC, this.intent.getStringExtra(Resources.TAG_DESC));
        intent.putExtra(Resources.TAG_HASCONFIRMED, this.intent.getIntExtra(Resources.TAG_HASCONFIRMED, 0));

        intent.putExtra(Resources.TAG_MEETING_DATE, this.meeting.getDate());
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

    /**
     * @param view view
     */
    public void confirm(View view) {
        String time = meeting.getTime();
        String date = meeting.getDate();

        confirm.setEnabled(false);
        confirm.setTextColor(Color.LTGRAY);

        if (time == null || date == null) { // #VIRTUAL meeting
            String data = "Virtual meeting name: " + meeting.getName() + "\nLocation: NO-LOCATION (#VIRTUAL) "
                    + "\nParticipants: " + meeting.getParticipants();
            editText.setText(data);
            return;
        }

        if (intent.getIntExtra(Resources.TAG_HASCONFIRMED, 0) == 0) {
            editText.setText(getMeetingData(1));
            new UpdateHasconfirmed().execute();
            new UpdateConfirmcount().execute();
        }

    }

    private String getMeetingData(int additionToConfirmCount) {
        return "Meeting name: " + meeting.getName() + "\nCity: " + meeting.getExactLocation() + "\n" +
                "\uD83D\uDCC6Date: " + meeting.getDate() + "\n\u23F0 Time: " + meeting.getTime() +
                "\nLatitude: " + meeting.getLatitude() + "\nLongitude: " + meeting.getLongitude() + "\nParticipants: " + meeting.getParticipants() + "\nConfirmed participants: " +
                (this.meeting.getConfirmCount() + additionToConfirmCount) + "\nOrganizer: " + meeting.getOrganizer();
    }


    class UpdateHasconfirmed extends AsyncTask<String, String, String> {

        final String url_update_hasconfirmed = "http://www.faceop.com/update_confirmed.php";

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(Resources.TAG_NAME, userName));
            params.add(new BasicNameValuePair(Resources.TAG_HASCONFIRMED, "" + 1));
            jsonParser.makeHttpRequest(url_update_hasconfirmed, "POST", params); //REST POST
            return null;
        }
    }

    class UpdateConfirmcount extends AsyncTask<String, String, String> {

        final String url_update_confirmcount = "http://www.faceop.com/update_confirmcount.php";

        protected String doInBackground(String... args) {

            int updateCC = (1 + intent.getIntExtra(Resources.TAG_CONFIRMCOUNT, 0));

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(Resources.TAG_MEETING_NAME, meeting.getName()));
            params.add(new BasicNameValuePair(Resources.TAG_CONFIRMCOUNT, "" + updateCC));
            jsonParser.makeHttpRequest(url_update_confirmcount,
                    "POST", params);
            return null;
        }
    }
}
