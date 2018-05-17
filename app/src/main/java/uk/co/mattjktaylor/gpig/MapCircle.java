package uk.co.mattjktaylor.gpig;

import android.graphics.Color;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class MapCircle {

    private String ID;
    private int type;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private Long dateTime;

    // Do not serialise:
    private transient Circle circle;

    // Initialised using Gson
    public MapCircle() {}

    // Constructor for adding responder markers:
    public MapCircle(String ID, double lat, double lon, double radius, Long dateRecorded)
    {
        this.ID = ID;
        this.type = type;
        this.latitude = lat;
        this.longitude = lon;
        this.radius = radius;
        this.dateTime = dateRecorded;
    }

    public CircleOptions getCircleOptions()
    {
        return new CircleOptions().center(new LatLng(latitude, longitude))
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(0x220000FF)
                .strokeWidth(5);
    }

    public void setCircle(Circle c)
    {
        this.circle = c;
    }

    public Circle getCircle()
    {
        return circle;
    }

    public String getID()
    {
        return ID;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MapCircle))
            return false;

        MapCircle c = (MapCircle) o;
        if(c.getID().equals(ID))
            return true;
        else
            return false;
    }
}
