package uk.co.mattjktaylor.gpig;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapMarker {

    private String ID;
    private int type;
    private Double latitude;
    private Double longitude;
    private String title;
    private MapDescription desc;
    private Long dateTime;

    // Do not serialise:
    private transient Marker marker;

    private static transient ArrayList<Integer> iconDictionary;
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
    public MapMarker(String ID, int type, double lat, double lon, String title, MapDescription desc, Long dateRecorded)
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
        return new MarkerOptions().position(new LatLng(latitude, longitude)).title(title).snippet(desc.getInfo());
    }

    public void setMarker(Marker m, Context context)
    {
        this.marker = m;
        if(type == -1)
            m.setAlpha(0);
        else
        {
            int resourceID = iconDictionary.get(type);
            if(resourceID != -1)
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, resourceID)));
        }
    }

    public String getID()
    {
        return ID;
    }

    public int getType()
    {
        return type;
    }

    public MapDescription getDescription()
    {
        return desc;
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
