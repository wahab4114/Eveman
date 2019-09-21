package com.smd.eveman;

import java.io.Serializable;

public class Event implements Serializable {

    public double Latitude;
    public double Longitude;
    public String Desc;
    public String Place;
    public String EventTitle;
    public boolean isApproved;

    Event(double x,double y,String desc,String place,String eventTitle)
    {
        Latitude = x;
        Longitude = y;
        Desc = desc;
        Place = place;
        EventTitle = eventTitle;
        isApproved = false;
    }

}
