package uk.co.mattjktaylor.gpig;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class MapPolygon {

    private String ID;
    private int type;
    private int severity;
    private ArrayList<LatLng> coords;
    private MapDescription desc;
    private Long timeAdded;

    private transient Polygon polygon;
    private transient Marker marker;

    public MapPolygon() {}

    public MapPolygon(String ID, int type, int severity, ArrayList<LatLng> coords, MapDescription desc, Long timeAdded) {
        this.ID = ID;
        this.type = type;
        this.severity = severity;
        this.coords = coords;
        this.desc = desc;
        this.timeAdded = timeAdded;
    }

    public PolygonOptions getPolygonOptions()
    {
        PolygonOptions po = new PolygonOptions();
        po.add(coords.toArray(new LatLng[coords.size()]));

        int polyColour = 0x220000FF;
        // 40% transparency = 0x64
        switch (severity)
        {
            case 1:
                // Yellow
                polyColour = 0x64FFFF00;
                break;

            case 2:
                // Orange
                polyColour = 0x64FF9900;
                break;

            case 3:
                // Red
                polyColour = 0x64FF0000;
                break;
        }

        po.fillColor(polyColour);
        po.strokeWidth(2);
        po.clickable(true);

        return po;
    }

    public void setPolygon(Polygon p)
    {
        polygon = p;
    }

    public void setMarker(Marker m)
    {
        marker = m;
    }

    public Marker getMarker()
    {
        return marker;
    }

    public LatLng getAverageCoords()
    {
        double latSum = 0.0;
        double lonSum = 0.0;
        int size = polygon.getPoints().size();

        for(LatLng xy : polygon.getPoints())
        {
            latSum += xy.latitude;
            lonSum += xy.longitude;
        }

        return new LatLng(latSum/size, lonSum/size);
    }

    public Polygon getPolygon()
    {
        return polygon;
    }

    public String getID() {
        return ID;
    }

    public int getType() {
        return type;
    }

    public int getSeverity() {
        return severity;
    }

    public MapDescription getDescription() {
        return desc;
    }

    public Long getTimeAdded() {
        return timeAdded;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MapPolygon))
            return false;

        MapPolygon p = (MapPolygon) o;
        if(p.getID().equals(ID))
            return true;
        else
            return false;
    }
}
