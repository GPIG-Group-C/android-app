package uk.co.mattjktaylor.gpig;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Activity activity;
    private static ArrayList<String> severities = new ArrayList<>();
    static
    {
        severities.add("Fire Extinguished");
        severities.add("Fire Contained");
        severities.add("Fire Escalating");
    }

    public CustomInfoWindow(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        MapMarker mapMarker = null;
        for(MapMarker m : MapFragment.markers)
        {
            if(m.getMarker().getId().equals(marker.getId()))
            {
                mapMarker = m;
                break;
            }
        }

        if(mapMarker == null)
            return null;

        View view = null;
        if(mapMarker.getDescription().getUtilities() != null)
        {
            view = activity.getLayoutInflater().inflate(R.layout.map_infowindow, null);
        }
        else
        {
            view = activity.getLayoutInflater().inflate(R.layout.map_infowindow, null);

            TextView sev = (TextView) view.findViewById(R.id.text_severity);
            sev.setText(severities.get(mapMarker.getDescription().getStatus()));

            if(mapMarker.getDescription().getBuildingInfo() != null)
            {
                TextView buildingType = (TextView) view.findViewById(R.id.text_building_type);
                buildingType.setText(mapMarker.getDescription().getBuildingInfo().getType());

                TextView buildingYear = (TextView) view.findViewById(R.id.text_building_year);
                buildingYear.setText(mapMarker.getDescription().getBuildingInfo().getYear());
            }
        }
        return view;
    }
}
