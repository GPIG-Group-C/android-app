package uk.co.mattjktaylor.gpig;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class MapPolygon extends MapObject{

    private int severity;
    private ArrayList<LatLng> coords;
    private MapDescription desc;

    private transient Polygon polygon;
    private transient Marker marker;

    public MapPolygon() {}

    public MapPolygon(String ID, int type, int severity, ArrayList<LatLng> coords, MapDescription desc)
    {
        super(ID, type, desc);
        this.severity = severity;
        this.coords = coords;
        this.desc = desc;
    }

    public PolygonOptions getPolygonOptions()
    {
        PolygonOptions po = new PolygonOptions();
        po.add(coords.toArray(new LatLng[coords.size()]));

        int polyColour = 0x220000FF;
        // 40% transparency = 0x64
        switch (severity)
        {
            case 0:
                // Yellow
                polyColour = 0x64FFFF00;
                break;

            case 1:
                // Orange
                polyColour = 0x64FF9900;
                break;

            case 2:
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

    public int getSeverity(){
        return severity;
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
}
