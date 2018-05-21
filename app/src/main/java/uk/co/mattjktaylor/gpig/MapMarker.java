package uk.co.mattjktaylor.gpig;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapMarker extends MapObject{

    private Double latitude;
    private Double longitude;
    private String title;

    // Do not serialise:
    private transient Marker marker;

    public static transient ArrayList<Integer> iconDictionary;
    static
    {
        iconDictionary = new ArrayList<Integer>();
        iconDictionary.add(R.drawable.ic_gas_pipe);
        iconDictionary.add(R.drawable.ic_fire);
        iconDictionary.add(R.drawable.ic_blocked);
        iconDictionary.add(R.drawable.ic_medic);
        iconDictionary.add(R.drawable.ic_earthquake);
        iconDictionary.add(R.drawable.ic_collapse);
        iconDictionary.add(R.drawable.ic_water);
        iconDictionary.add(R.drawable.ic_electricity);
    }

    // Initialised using Gson
    public MapMarker() {}

    // Constructor for adding responder markers:
    public MapMarker(String ID, int type, double lat, double lon, String title, MapDescription desc)
    {
        super(ID, type, desc);
        this.latitude = lat;
        this.longitude = lon;
        this.title = title;
    }

    public ClusteredMapMarker getMarkerOptions()
    {
        return new ClusteredMapMarker(latitude, longitude, title, null);
    }

    public void setMarker(Marker m, Context context)
    {
        this.marker = m;
        if(getType() == -1)
            m.setAlpha(0);
        else
        {
            int resourceID = iconDictionary.get(getType());
            if(resourceID != -1)
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, resourceID)));
        }
    }

    public Marker getMarker()
    {
        return marker;
    }

}
