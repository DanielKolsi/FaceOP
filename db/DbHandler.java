package com.faceop.faceop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * FaceOP database handler
 * Created by Agape on 28/08/16.
 */

public class DbHandler extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "faceop";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_MEETINGS = "meetings";
    //private static final String TABLE_TEST = "test"; // for db upgrade tests

    // Users table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description"; // user own desc
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_MEETINGS = "meetings"; // foreign key?
    private static final String KEY_LATITUDE = "latitude"; // user "anchor" lat
    private static final String KEY_LONGITUDE = "longitude"; // user "anchor" long
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_HASCONFIRMED = "confirmed"; // Has user confirmed his meeting? 0 = false, 1 = true

    // Meetings table column names
    //private static final String KEY_ORGANIZER_USER_ID = "user_id"; // id of the organizer user
    private static final String KEY_PARTICIPANTS = "participants";
    private static final String KEY_PARTICIPANT_USER_IDS = "ids";

    private static final String KEY_CONFIRMCOUNT = "confirmcount"; // How many users have confirmed their meeting?
    private static final String KEY_ORGANIZER = "organizer";
    private User user;

    /**
     * @param context db context
     */
    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //SQLiteDatabase db = this.getWritableDatabase();
        //String dbPath = db.getPath();
        //Log.d("dbpath = ", "" + dbPath); //  /data/user/0/com.faceop.faceop/databases/faceop
    }

    /**
     * DB handler constructor method
     * @param context DB contect
     * @param upgrade true, if DEBUG upgrade is done, false otherwise
     */
    public DbHandler(Context context, boolean upgrade) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


        SQLiteDatabase db = this.getWritableDatabase();
        String dbPath = db.getPath();
        Log.d("dbpath = ", "" + dbPath); // TODO

        if (upgrade) {
            int version = db.getVersion();
            onUpgrade(db, version, ++version); // TODO
            Log.d("DB Upgrade: ", "upgrading db to version " + version);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("onCreate", "creating table users & meetings");

/*
        String CREATE_TEST_TABLE = "CREATE TABLE " + TABLE_TEST + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_DATE + " TEXT," + KEY_TIME + " TEXT," +
                KEY_MEETINGS + " TEXT," + KEY_LATITUDE + " REAL," + KEY_LONGITUDE + " REAL," + KEY_ADDRESS + " REAL," + KEY_HASCONFIRMED + " INTEGER" + ")";
        db.execSQL(CREATE_TEST_TABLE);
*/

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_DATE + " TEXT," + KEY_TIME + " TEXT," +
                KEY_MEETINGS + " TEXT," + KEY_LATITUDE + " REAL," + KEY_LONGITUDE + " REAL," + KEY_ADDRESS + " REAL," + KEY_HASCONFIRMED + " INTEGER" + ")";
        db.execSQL(CREATE_USERS_TABLE);// TODO MYSQL-CREATE-TABLE


        String CREATE_MEETINGS_TABLE = "CREATE TABLE " + KEY_MEETINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PARTICIPANTS + " TEXT," + KEY_PARTICIPANT_USER_IDS + " TEXT," + KEY_LATITUDE
                + " REAL," + KEY_LONGITUDE + " REAL," + KEY_TIME + " TEXT, " + KEY_DATE + " TEXT, " + KEY_ADDRESS + " TEXT," + KEY_CONFIRMCOUNT + " INTEGER, " + KEY_ORGANIZER + " TEXT" + ")";
        // participants was BLOB type
        db.execSQL(CREATE_MEETINGS_TABLE);// TODO MYSQL-CREATE-TABLE
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("upgrading..", "tables-dropped");
        // Creating tables again
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS); // TODO MYSQL-DROP
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS);  // TODO MYSQL-DROP
        onCreate(db);
    }

    /**
     * Add FaceOP user to DB.
     * @param user DB user
     */
    public void addUserProfile(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_DESCRIPTION, user.getDescription());
        values.put(KEY_DATE, user.getDateStart());
        values.put(KEY_TIME, user.getDisplayTime());

        values.put(KEY_MEETINGS, user.getMeetings());
        values.put(KEY_LATITUDE, user.getAnchorLat());
        values.put(KEY_LONGITUDE, user.getAnchorLong());
        values.put(KEY_ADDRESS, user.getAddress());
        values.put(KEY_HASCONFIRMED, 0);
        // Inserting Row
        db.insert(TABLE_USERS, null, values); // TODO MYSQL-INSERT
        db.close(); // Closing database connection
    }

    /**
     * Add a FaceOP meeting to the DB.
     * @param meeting FaceOP meeting.
     */
    public void addMeeting(Meeting meeting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_DATE, meeting.getDate());

        values.put(KEY_PARTICIPANTS, meeting.getParticipants());
        values.put(KEY_TIME, meeting.getTime());
        values.put(KEY_NAME, meeting.getName());
        values.put(KEY_ADDRESS, meeting.getExactLocation());
        values.put(KEY_LATITUDE, meeting.getLatitude());
        values.put(KEY_LONGITUDE, meeting.getLongitude());
        values.put(KEY_CONFIRMCOUNT, meeting.getConfirmCount());
        values.put(KEY_ORGANIZER, meeting.getOrganizer());

        // Inserting Row
        db.insert(TABLE_MEETINGS, null, values); // TODO MYSQL-INSERT
        db.close(); // Closing database connection
    }

    /**
     * @param name user name
     * @return DB user by name
     */
    public User getUserByName(String name) {


        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from users where name = " + "\"" + name + "\"";

        Cursor cursor = db.rawQuery(query, null);
        int cursorCount = cursor.getCount();

        if (cursorCount == 0) {
            return null; // no user found by 'name'
        }

        cursor.moveToFirst();

        User user = new User();

        user.setId(Integer.parseInt(cursor.getString(0)));
        user.setName(cursor.getString(1)); // name
        user.setDescription(cursor.getString(2)); // desc
        user.setDateStart(cursor.getString(3)); // (meeting) date start
        user.setDisplayTime(cursor.getString(4)); // time
        //user.setMeetings(cursor.getString(5)); // meetings
        user.setAnchorLat(Double.parseDouble(cursor.getString(6))); // latitude
        user.setAnchorLong(Double.parseDouble(cursor.getString(7))); // longitude
        user.setAddress(cursor.getString(8)); // address
        cursor.close();
        return user;
    }

    /**
     * Profile match users and establish a new FaceOP meeting. Users are matched against the defined match criteria which include
     * date and time, tag description and the selected city from the Google Maps.
     * @param date selected meeting date
     * @param time selected meeting time
     * @param tag user defined meeting criterion match tag (if it's empty, no matching against it)
     * @param city user selected Google Maps anchor city for the FaceOP meeting
     * @return list of matched users for a FaceOP meeting
     */
    public List<User> matchByDateTimeTagAndCity(String date, String time, String tag, String city) {

        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query;


        final String virtualMeetingQuery = "select * from users where description = " + "\"" + Meeting.VIRTUAL + "\""; // match all users with #VIRTUAL #tag description
        final String tagQuery = "select * from users where time = " + "\"" + time + "\"" + " and date = " + "\"" + date + "\"" + " and description = " + "\"" + tag + "\"" + " and address = " + "\""
                + city
                + "\"";
        final String simpleQuery = "select * from users where time = " + "\"" + time + "\"" + " and date = " + "\"" + date + "\"" + " and address = " + "\"" + city + "\"";

        if (city == null && tag.equals(Meeting.VIRTUAL)) {
            query = virtualMeetingQuery; // virtual meetings only in alpha/beta prototypes for testing
        } else if (tag == null || tag.length() < 2) {
            query = simpleQuery; // simple query is without #tag description
        } else if (tag.equals(Meeting.VIRTUAL)) {
            query = simpleQuery;
        } else if (tag.length() > 15) {
            query = simpleQuery; // initially support only tag lengts of 2-15
        } else {
            query = tagQuery;
        }

        //Log.d("match-query", "query=" + query);

        Cursor cursor = db.rawQuery(query, null); // TODO MySQLi-QUERY

        //int count = cursor.getCount();
        // Log.d("matched n-users = ", "matchAndStoreMeeting-count = " + count);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                String name = cursor.getString(1);
                user.setName(name);
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }


    /**
     * @param desc Regexp description for profile matching users for a meeting
     * @return list of matched users
     */
   /* public List<User> matchByDesc(String desc) {

         List<User> userList = new ArrayList<User>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from users where description = " + "\"" + desc + "\"";
        Cursor cursor = db.rawQuery(query, null);

        int count = cursor.getCount();
        Log.d("matched n-users = ", "matchAndStoreMeeting-count = " + count);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                String name = cursor.getString(1);
                Log.d("user-name = ", name);
                user.setName(name);
                user.setDescription(cursor.getString(2));
                user.setTime(cursor.getString(3));
                // Adding users to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        // return user list
        cursor.close();
        return userList;
    }*/

    /**
     * @param userName name of the FaceOP user
     * @return FaceOP meeting
     */
    public Meeting getMyMeeting(String userName) {

        int cursorCount;
        Cursor cursor;

        String selectQuery = "SELECT * FROM " + TABLE_MEETINGS + " WHERE participants LIKE '%" + userName + "%'";

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null); // TODO MySQL-QUERY
            cursorCount = cursor.getCount();
        } catch (SQLiteException sqle) {
            return null; // no meetings
        } catch (Exception e) {
            return null;
        }

        //Log.d("cursorCount", "c-count = " + cursorCount + "query = " + selectQuery);

        if (cursorCount == 0) {
            return null; // no meetings found
        }

        cursor.moveToFirst();
        //+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PARTICIPANTS + " TEXT," + KEY_PARTICIPANT_USER_IDS + " TEXT," + KEY_LATITUDE
        //      + " REAL," + KEY_LONGITUDE + " REAL," + KEY_TIME + " TEXT, " + KEY_DATE + " TEXT, " + KEY_ADDRESS + " TEXT," + KEY_CONFIRMCOUNT + " INTEGER"  + KEY_ORGANIZER + " TEXT" + ")";

        Meeting meeting = new Meeting();
        meeting.setId(Integer.parseInt(cursor.getString(0)));
        meeting.setName(cursor.getString(1));
        meeting.setParticipants(cursor.getString(2));

        meeting.setLatitude(Double.parseDouble(cursor.getString(4)));
        meeting.setLongitude(Double.parseDouble(cursor.getString(5)));

        meeting.setTime(cursor.getString(6));
        meeting.setDate(cursor.getString(7));
        String addr = cursor.getString(8);

        meeting.setExactLocation(addr);
        meeting.setConfirmCount(Integer.parseInt(cursor.getString(9)));
        meeting.setOrganizer(cursor.getString(10));
        // meeting time, date & location & number of participants

        cursor.close();
        return meeting;
    }

    /**
     * @param time
     * @param date
     * @param city
     * @return
     */
    /*public List<User> matchByTimeAndDateAndCity(String time, String date, String city) {


        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from users where time = " + "\"" + time + "\" and date = " + "\"" + date + "\" and city = " + "\"" + city + "\"";

        Cursor cursor = db.rawQuery(query, null);


        int count = cursor.getCount();
        Log.d("matched n-users = ", "matchAndStoreMeeting-count = " + count);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                String name = cursor.getString(1);
                user.setName(name);
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }*/

    /**
     *
     * @param time
     * @return
     */
   /* public List<User> getUsersByMeetingTime(int time) {

        // TODO first try to get ALL users!
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor =  db.rawQuery("select * from users where time = " + time + "", null);

        int count = cursor.getCount();
        Log.d("nusers = ", "count="+count);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                String name = cursor.getString(1);
                Log.d("user-name = ", name);
                user.setName(name);
                // Adding users to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }*/

    /*
    public User getUserProfile(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID,
                KEY_NAME, KEY_DESCRIPTION, KEY_TIME}, KEY_ID + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));

        return user;
    }*/

    /**
     *
     * @return all Users
     */
    /*public List<User> getAllUsers() {

        List<User> userList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setName(cursor.getString(1));
                user.setDescription(cursor.getString(2));
                user.setDateStart(cursor.getString(3));
                user.setTime(cursor.getString(4));
                user.setMeetings(cursor.getString(5));
                user.setAnchorLat(Double.parseDouble(cursor.getString(6)));
                user.setAnchorLong(Double.parseDouble(cursor.getString(7)));
                user.setExactLocation((cursor.getString(8)));
                // Adding users to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }*/

    /**
     * Updating a user
     * @param user FaceOP user
     * @return number of db rows affected
     */
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_DESCRIPTION, user.getDescription());

        values.put(KEY_DATE, user.getDateStart());
        values.put(KEY_TIME, user.getDisplayTime());

        values.put(KEY_MEETINGS, user.getMeetings());
        values.put(KEY_LATITUDE, user.getAnchorLat());
        values.put(KEY_LONGITUDE, user.getAnchorLong());
        values.put(KEY_ADDRESS, user.getAddress());
        // Log.d("updating user", "confirmed=" + user.getHasConfirmed());
        values.put(KEY_HASCONFIRMED, user.getHasConfirmed());
        // updating row
        int userID = user.getId();
        int ra = db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(userID)}); // TODO MYSQL-UPDATE

        //Log.d("update-ra", "userId=" + userID + " date = " + user.getDateStart() + " desc = " + user.getDescription() + " rows-affected=" + ra);
        db.close();
        return ra;
    }


    /**
     * get all meetings in a city
     * @param city Meeting city
     * @param date Meeting date
     * @return List of Meeting objects
     */
  /*  public List<Meeting> getMeetings(String city, String date) {

        List<Meeting> meetingList = new ArrayList<Meeting>();

        String selectQuery = "SELECT * FROM " + TABLE_MEETINGS + " WHERE city = " + "\"" + city + "\" and date = " + "\"" + date;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Meeting meeting = new Meeting();
                meeting.setId(Integer.parseInt(cursor.getString(0)));
                meeting.setName(cursor.getString(1));
                meeting.setParticipants(cursor.getString(2));

                meeting.setExactLocation(cursor.getString(8)); // address
                meetingList.add(meeting);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return meetingList;
    }*/

    // Getting user Count
   /* public int getUsersCount() {
        String countQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }*/

    /**
     * Update confirm boolean true (1) to the DB.
     * @param id user id.
     * @return the number of rows affected
     */
    public int updateUserHasConfirmed(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_HASCONFIRMED, "1");

        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});// TODO MYSQL-UPDATE
    }

    /**
     * Update confirm boolean false (0)  to the DB.
     *
     * @param id user id.
     * @return the number of rows affected
     */
    public int updateUserHasNotConfirmed(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_HASCONFIRMED, "0");
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});// TODO MYSQL-UPDATE
    }

    /**
     * Delete a meeting.
     *
     * @param meeting Meeting to be deleted.
     */
    public void deleteMeeting(Meeting meeting) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Deleting ", " meeting with id =" + meeting.getId());
        db.delete(TABLE_MEETINGS, KEY_ID + " = ?",
                new String[]{String.valueOf(meeting.getId())});// TODO MYSQL-DELETE
        db.close();
    }

    /**
     * Updates a meeting DB table.
     * @param meeting FaceOP meeting
     * @return number of affected table rows
     */
   /* public int updateMeeting(Meeting meeting) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, meeting.getName());
        values.put(KEY_DATE, meeting.getDate());
        values.put(KEY_TIME, meeting.getTime());
        values.put(KEY_LATITUDE, meeting.getLatitude());
        values.put(KEY_LONGITUDE, meeting.getLongitude());
        values.put(KEY_ADDRESS, meeting.getExactLocation());
        values.put(KEY_CONFIRMCOUNT, meeting.getConfirmCount());
        // updating row
        int meetingID = meeting.getId();
        int ra = db.update(TABLE_MEETINGS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(meetingID)});

        Log.d("update-meetings", "meetingId=" + meetingID + " rows-affected=" + ra);
        return ra;
    }*/

    /**
     * Delete a user.
     * @param user User to be deleted.
     */
   /* public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
        db.close();
    }*/

    /**
     * Get 'hasconfirmedmeeting' value from the DB
     * @param userName User name
     * @return cursor first value or -1 if cursorCount is zero
     */
    public int getHasConfirmedMeeting(String userName) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select " + KEY_HASCONFIRMED + " from users where name = " + "\"" + userName + "\"";
        //Log.d("query", "query-user=" + query);
        Cursor cursor = db.rawQuery(query, null);// TODO MYSQL-QUERY
        int cursorCount = cursor.getCount();
        //int columntCount = cursor.getColumnCount();

        if (cursorCount > 0) { // TODO
            cursor.moveToFirst();
            //Log.d("cursor=", "ccount=" + cursorCount + " ccolumns=" + columntCount + "isFirst=" + isFirst);
            int value = Integer.parseInt(cursor.getString(0));
            cursor.close();
            return value;
        } else return 0;
    }

    /**
     *
     * @param meeting FaceOP meeting.
     * @return number of confirmed FaceOP meetings.
     */
    public int getConfirmCount(Meeting meeting) {
        SQLiteDatabase db = this.getWritableDatabase();

        int meetingID = meeting.getId();
        String query = "select " + KEY_CONFIRMCOUNT + " from meetings where id = " + meetingID;
        Cursor cursor = db.rawQuery(query, null);// TODO MYSQL-QUERY
        cursor.moveToFirst();
        int count = Integer.parseInt(cursor.getString(0));
        cursor.close();
        return count;
    }

    /**
     * @param meeting FaceOP meeting
     * @return meeting confirm count value
     */
    public int setConfirmCount(Meeting meeting, int newConfirmCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int meetingID = meeting.getId();
        values.put(KEY_CONFIRMCOUNT, newConfirmCount);
        return db.update(TABLE_MEETINGS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(meetingID)});// TODO MYSQL-UPDATE
    }

    class CreateNewUser extends AsyncTask<String, String, String> {

        // JSON Node names
        private static final String TAG_SUCCESS = "success";
        // JSON parser class
        JSONParser jsonParser = new JSONParser();
        String url_create_user = "http://www.faceop.com/create_user.php";

        /**
         * @param args
         * @return
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", user.getName()));
            params.add(new BasicNameValuePair("date", user.getDateStart()));
            params.add(new BasicNameValuePair("time", user.getDisplayTime()));
            params.add(new BasicNameValuePair("meetings", user.getMeetings()));
            params.add(new BasicNameValuePair("latitude", "" + user.getAnchorLat()));
            params.add(new BasicNameValuePair("longitude", "" + user.getAnchorLong()));
            params.add(new BasicNameValuePair("address", user.getAddress()));
            params.add(new BasicNameValuePair("hasConfirmed", "" + user.getHasConfirmed())); // TODO
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}