package com.faceop.faceop;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Agape on 14/09/16.
 * DatePickerFragment is used for picking a meeting date for a match.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    public static final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);
    public static final SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.US);

    private String date = null;
    private Date calendarTime = null;

    /**
     * @return calendar date
     */
    public String getDate() {
        return this.date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        this.calendarTime = calendar.getTime();
        this.date = format1.format(this.calendarTime);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * @param view datepickerview
     * @param year meeting year
     * @param month meeting month
     * @param day meeting day
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {


        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        this.calendarTime = calendar.getTime();
        Date now = new Date();
        TextView tv = (TextView) getActivity().findViewById(R.id.dateTextView);

        if (!now.after(this.calendarTime)) {
            this.date = format1.format(this.calendarTime);
            tv.setText(String.format("%s%s", getString(R.string.meetingDate) + " ", this.date));
        } else {
            tv.setText(R.string.futureMeeting);
        }
    }
}
