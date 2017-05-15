package com.faceop.faceop.db;

/**
 * Created by Agape on 28/08/16.
 * Class defining FaceOP user
 */

public class User {

    private boolean anchorSet = false;
    private int id = 1;

//    private int meetingMinSize = 2;
//    private int meetingMaxSize = 50;

    private String name = null;
    private String address = null; // initially anchor city
    private String dateStart = null; // e.g. 1572016 (ddmmyyyy)
    //private String dateEnd = null;
    private String displayTime = null; // e.g. 17:30 (hh:mm)
    //private String email = null;
    private String meetings = null;
    private double anchorLat = 0; // latitude of user "anchor" position
    private double anchorLong = 0;
    private String description = "#VIRTUAL"; // user description of himself in his own words
    private int hasConfirmed = 0; // has user confirmed his meeting
    //private String radiousKm = null;
    //private String kmHoursDistance = null;

    public User() {
    }

    /**
     * @param name user name
     */
    User(String name) {
        this.name = name;
    }


    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public int getHasConfirmed() {
        return hasConfirmed;
    }

    public void setHasConfirmed(int hasConfirmed) {
        this.hasConfirmed = hasConfirmed;
    }

    public boolean isAnchorSet() {
        return anchorSet;
    }

    public void setAnchorSet(boolean anchorSet) {
        this.anchorSet = anchorSet;
    }

    /* public int getMeetingMinSize() {
         return meetingMinSize;
     }

     public void setMeetingMinSize(int meetingMinSize) {
         this.meetingMinSize = meetingMinSize;
     }
 */
    public String getAddress() {
        return address;
    }

    /**
     * Anchor city name.
     *
     * @param address anchor city
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /*  public String getEmail() {
          return email;
      }

      public void setEmail(String email) {
          this.email = email;
      }
  */
    public String getMeetings() {
        return meetings;
    }

    public void setMeetings(String meetings) {
        this.meetings = meetings;
    }

    public double getAnchorLat() {
        return anchorLat;
    }

    public void setAnchorLat(double anchorLat) {
        this.anchorLat = anchorLat;
    }

    public double getAnchorLong() {
        return anchorLong;
    }

    public void setAnchorLong(double anchorLong) {
        this.anchorLong = anchorLong;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    /*   public String getDateEnd() {
           return dateEnd;
       }

       public void setDateEnd(String dateEnd) {
           this.dateEnd = dateEnd;
       }


       public String getRadiousKm() {
           return radiousKm;
       }

       public void setRadiousKm(String radiousKm) {
           this.radiousKm = radiousKm;
       }

       public String getKmHoursDistance() {
           return kmHoursDistance;
       }

       public void setKmHoursDistance(String kmHoursDistance) {
           this.kmHoursDistance = kmHoursDistance;
       }
   */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


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
}
