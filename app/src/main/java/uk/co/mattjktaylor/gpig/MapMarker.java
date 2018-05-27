package uk.co.mattjktaylor.gpig;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarker extends MapObject{

    private Double lat;
    private Double lng;
    private String title;

    // Do not serialise:
    private transient Marker marker;

    // Initialised using Gson
    public MapMarker() {}

    // Constructor for adding responder markers:
    public MapMarker(String ID, String type, double lat, double lon, String title, MapDescription desc)
    {
        super(ID, type, desc);
        this.lat = lat;
        this.lng = lon;
        this.title = title;
    }

    public MarkerOptions getMarkerOptions()
    {
        MarkerOptions mo = new MarkerOptions().position(new LatLng(lat, lng)).title(title);
        if(getDescription() != null && getDescription().getIncident() != null)
            mo.snippet(getDescription().getIncident().getInfo());
        
        return mo;
    }

    public void setMarker(Marker m, Context context)
    {
        this.marker = m;
        if(getType() == null)
            m.setAlpha(0);
        else
        {
            int resourceID = IncidentTypes.getIncidentType(getType()).getIcon();
            if(resourceID != -1)
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, resourceID)));
        }
    }

    public Marker getMarker()
    {
        return marker;
    }

}
