package uk.co.mattjktaylor.gpig;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarker {

    private String ID;
    private int type;
    private Double latitude;
    private Double longitude;
    private String title;
    private String desc;
    private Long dateTime;

    // Do not serialise:
    private transient Marker marker;

    // Initialised using Gson
    public MapMarker() {}

    // Constructor for adding responder markers:
    public MapMarker(String ID, int type, double lat, double lon, String title, String desc, Long dateRecorded)
    {
        this.ID = ID;
        this.type = type;
        this.latitude = lat;
        this.longitude = lon;
        this.title = title;
        this.desc = desc;
        this.dateTime = dateRecorded;
    }

    public MarkerOptions getMarkerOptions()
    {
        return new MarkerOptions().position(new LatLng(latitude, longitude)).title(title).snippet(desc);
    }

    public void setMarker(Marker m)
    {
        this.marker = m;
    }

    public String getID()
    {
        return ID;
    }

    public Marker getMarker()
    {
        return marker;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MapMarker))
            return false;

        MapMarker m = (MapMarker) o;
        if(m.getID().equals(ID))
            return true;
        else
            return false;
    }
}
