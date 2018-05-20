package uk.co.mattjktaylor.gpig;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Locale;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Activity activity;
    public static ArrayList<String> severities = new ArrayList<>();
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
        MapDescription mapDescription = null;
        for(MapObject mapObject : MapFragment.mapObjects)
        {
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon p = (MapPolygon) mapObject;
                if(p.getMarker().getId().equals(marker.getId()))
                {
                    mapDescription = p.getDescription();
                    break;
                }
            }
            else if(mapObject instanceof MapMarker)
            {
                MapMarker m = (MapMarker) mapObject;
                if(m.getMarker().getId().equals(marker.getId()))
                {
                    mapDescription = m.getDescription();
                    break;
                }
            }
        }
        if(mapDescription == null)
            return null;

        View view = activity.getLayoutInflater().inflate(R.layout.map_infowindow, null);

        TextView sev = (TextView) view.findViewById(R.id.text_severity);
        sev.setText(severities.get(mapDescription.getStatus()));

        TextView numPeople = (TextView) view.findViewById(R.id.text_people);
        numPeople.setText(String.format(Locale.ENGLISH, "~%d", mapDescription.getNumPeople()));

        TextView address = (TextView) view.findViewById(R.id.text_address);
        address.setText(mapDescription.getAddress());

        TextView reportedBy = (TextView) view.findViewById(R.id.text_reported);
        reportedBy.setText(mapDescription.getReportBy());

        TextView reportedAt = (TextView) view.findViewById(R.id.text_time);
        reportedAt.setText(Config.getFormattedDate(mapDescription.getDateAdded(), "dd/MM/yy HH:mm:ss"));

        TextView additionalInfo = (TextView) view.findViewById(R.id.text_additional_info);
        if(mapDescription.getInfo().isEmpty())
        {
            view.findViewById(R.id.text_additional_info_title).setVisibility(View.GONE);
            additionalInfo.setVisibility(View.GONE);
        }
        else
        {
            additionalInfo.setText(mapDescription.getInfo());
        }

        if(mapDescription.getUtilities() != null)
        {
            LinearLayout utilitiesContainer = (LinearLayout) view.findViewById(R.id.utilities_container);
            utilitiesContainer.setVisibility(View.VISIBLE);

            Switch gas = (Switch) view.findViewById(R.id.utilties_gas_switch);
            gas.setChecked(mapDescription.getUtilities().isGas());

            Switch water = (Switch) view.findViewById(R.id.utilties_water_switch);
            water.setChecked(mapDescription.getUtilities().isWater());

            Switch electricity = (Switch) view.findViewById(R.id.utilties_electricity_switch);
            electricity.setChecked(mapDescription.getUtilities().isElectricity());

            Switch sewage = (Switch) view.findViewById(R.id.utilties_sewage_switch);
            sewage.setChecked(mapDescription.getUtilities().isSewage());
        }

        if(mapDescription.getBuildingInfo() != null)
        {
            LinearLayout buildingInfoContainer = (LinearLayout) view.findViewById(R.id.building_info_container);
            buildingInfoContainer.setVisibility(View.VISIBLE);

            TextView buildingType = (TextView) view.findViewById(R.id.text_building_type);
            buildingType.setText(mapDescription.getBuildingInfo().getType());

            TextView buildingYear = (TextView) view.findViewById(R.id.text_building_year);
            buildingYear.setText(String.format(Locale.ENGLISH, "%d", mapDescription.getBuildingInfo().getYear()));
        }

        return view;
    }
}
