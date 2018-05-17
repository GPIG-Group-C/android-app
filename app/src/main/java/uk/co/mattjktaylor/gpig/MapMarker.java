package uk.co.mattjktaylor.gpig;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    public void setMarker(Marker m, Context context)
    {
        this.marker = m;

        switch(type)
        {
            case -1:
                m.setAlpha(0);
                break;

            case 0:
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, R.drawable.ic_gas_pipe)));
                break;

            case 1:
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, R.drawable.ic_fire)));
                break;

            case 2:
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, R.drawable.ic_blocked)));
                break;

            case 3:
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, R.drawable.ic_medic)));
                break;

            case 4:
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, R.drawable.ic_crack)));
                break;

            case 5:
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, R.drawable.ic_collapse)));
                break;

            case 6:
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, R.drawable.ic_fire_station)));
                break;

            case 7:
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, R.drawable.ic_water)));
                break;

            case 8:
                m.setIcon(BitmapDescriptorFactory.fromBitmap(Config.getBitmapFromVectorDrawable(context, R.drawable.ic_electricity)));
                break;
        }
    }

    public String getID()
    {
        return ID;
    }

    public String getDescription()
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
