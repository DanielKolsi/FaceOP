package com.faceop.faceop.db;



/**
 * FaceOP Meeting -> DB
 * Created by Agape on 30/08/16.
 */


public class Meeting {


    public static final String VIRTUAL = "#VIRTUAL";
    public static final int MIN_MATCHED_PARTICIPANTS = 4;
//    public static final int MAX_MATCHED_PARTICIPANTS = 15;

    private int id;
    private String name;
    private String organizer; // name of the meeting organizer user (i.e. matcher)
    private int confirmCount = 0; // how many has confirmed the meeting
    private double latitude;
    private double longitude;
    private String date; // meeting date
    private String time; // meeting starting time
    //private String ids; // meeting participants user ids
    private String participants; // meeting participants user participants
    private String exactLocation; // meeting exactLocation / city

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
    //private int organizer_id; // matcher user id of that meeting
    //private ArrayList<User> participants; // confirmed meeting participants
    //private String location = latitude + "#" + longitude;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExactLocation() {
        return exactLocation;
    }

    public void setExactLocation(String exactLocation) {
        this.exactLocation = exactLocation;
    }


    /*  public String getIds() {
          return ids;
      }

      public void setIds(String ids) {
          this.ids = ids;
      }
  */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return confirmed users count
     */
    public int getConfirmCount() {
        return confirmCount;
    }

    /**
     *
     * @param confirmCount confirmed users count
     */
    public void setConfirmCount(int confirmCount) {
        this.confirmCount = confirmCount;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }


    public double getLatitude() {

        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
