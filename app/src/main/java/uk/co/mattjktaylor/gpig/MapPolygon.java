package uk.co.mattjktaylor.gpig;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class MapPolygon extends MapObject{

    protected ArrayList<LatLng> coords;
    private MapDescription desc;

    private transient Polygon polygon;
    private transient Marker marker;

    public MapPolygon() {}

    public MapPolygon(String ID, ArrayList<LatLng> coords, MapDescription desc)
    {
        super(ID, null, desc);
        this.coords = coords;
        this.desc = desc;
    }

    public PolygonOptions getPolygonOptions()
    {
        PolygonOptions po = new PolygonOptions();
        po.add(coords.toArray(new LatLng[coords.size()]));

        // 40% transparency = 0x64
        int polyColour = 0x220000FF;
        if(desc != null)
        {
            polyColour = blendColors(Color.rgb(255,0,0), Color.rgb(255,255,0), (desc.getAreaInfo().getSeverity()/10f), 0x64);
        }

        po.fillColor(polyColour);
        po.strokeWidth(2);
        po.clickable(true);

        return po;
    }

    private static int blendColors(int color1, int color2, float ratio, int alpha)
    {
        final float inverseRation = 1.0f - ratio;

        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.argb(alpha, (int) r, (int) g, (int) b);
    }

    public void setCoords(ArrayList<LatLng> coords)
    {
        this.coords = coords;
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
}
