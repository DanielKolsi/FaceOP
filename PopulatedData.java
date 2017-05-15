package com.faceop.faceop.db;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Agape on 15/09/16.
 */
public class PopulatedData extends AppCompatActivity {

    public void saveGeneratedUsers(DbHandler db) {

        User usA1 = new User("John Smith");
        usA1.setDisplayTime("20:30");
        usA1.setAnchorLat(60);
        usA1.setAnchorLong(60);
        usA1.setDateStart("2016-Nov-30"); // meeting time
        usA1.setAddress("Mariehamn");
        usA1.setDescription("#dogs");

        User usB1 = new User("Tiina Thunder");
        usB1.setAnchorLat(60);
        usB1.setAnchorLong(60);
        usB1.setDisplayTime("20:30");
        usB1.setDescription("#dogs");
        usB1.setDateStart("2016-Nov-30"); // meeting time
        usB1.setAddress("Mariehamn");

        User usA2 = new User("Judith Aalto");
        usA2.setAnchorLat(50);
        usA2.setAnchorLong(50);
        usA2.setDisplayTime("23:00");
        usA2.setDescription("#cat");
        usA2.setDateStart("2016-Dec-30"); // meeting time
        usA2.setAddress("Mariehamn");

        User usB2 = new User("Hillary Trump");
        usB2.setAnchorLat(60);
        usB2.setAnchorLong(60);
        usB2.setDisplayTime("20:30");
        usB2.setDescription("#cats");
        usB2.setDateStart("2016-Nov-30"); // meeting time
        usB2.setAddress("Washington DC");

        User usA3 = new User("Börje Svensson");
        usA3.setAnchorLat(50);
        usA3.setAnchorLong(50);
        usA3.setDisplayTime("20:30");
        usA3.setDescription("#dogs");
        usA3.setDateStart("2016-Nov-30"); // meeting time
        usA3.setAddress("Mariehamn");

        User usB3 = new User("David Doe");
        usB3.setAnchorLat(60);
        usB3.setAnchorLong(60);
        usB3.setDisplayTime("20:30");
        usB3.setDescription("#cat");
        usB3.setDateStart("2016-Dec-30"); // meeting time
        usB3.setAddress("Mariehamn");


        if (db != null) { // -> MySQLi
            db.addUserProfile(usA1);
            db.addUserProfile(usA2);
            db.addUserProfile(usA3);
            db.addUserProfile(usB1);
            db.addUserProfile(usB2);
            db.addUserProfile(usB3);
        } else {
            Log.d("db-null", "db-was-null");
        }

    }

}
