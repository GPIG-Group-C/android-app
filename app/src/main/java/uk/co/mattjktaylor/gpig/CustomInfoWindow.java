package uk.co.mattjktaylor.gpig;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Activity activity;

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

        View view = activity.getLayoutInflater().inflate(R.layout.map_infowindow, null);
        if(mapMarker != null)
        {
            TextView sev = (TextView) view.findViewById(R.id.text_severity);
            sev.setText(mapMarker.getDescription());

            TextView buildingType = (TextView) view.findViewById(R.id.text_building_type);
            buildingType.setText(mapMarker.getDescription());

            TextView buildingYear = (TextView) view.findViewById(R.id.text_building_year);
            buildingYear.setText(mapMarker.getDescription());

            TextView risk = (TextView) view.findViewById(R.id.text_risk);
            risk.setText(mapMarker.getDescription());
        }
        return view;
    }
}
