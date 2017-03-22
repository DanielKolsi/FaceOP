package com.faceop.faceop;

/**
 * Created by Agape on 14/09/16.
 * FaceOP Sign in
 * TimePickerFragment is for selecting the meeting time for a match.
 */


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    private String displayTime = null;
    private String time = null;
    //private final String meetingTime = "Meeting time: ";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


    public String getDisplayTime() {
        return displayTime;
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget
        TextView tv = (TextView) getActivity().findViewById(R.id.timeTextView);
        //Set a message for user

        //Display the user changed time on TextView
        tv.setText(tv.getText() + " hour: " + String.valueOf(hourOfDay)
                + "  minute: " + String.valueOf(minute));
        final String AM = " (a.m.) ";
        final String PM = " (p.m.) ";

        if (minute < 10) {
            time = hourOfDay + ":0" + minute;
        } else {
            time = hourOfDay + ":" + minute;
        }

        this.displayTime = time;
        if (hourOfDay < 12) {
            this.displayTime = this.displayTime + AM;
        } else if (hourOfDay == 12) {
            this.displayTime = this.displayTime + PM;
        }
        tv.setText(this.displayTime);
    }
}