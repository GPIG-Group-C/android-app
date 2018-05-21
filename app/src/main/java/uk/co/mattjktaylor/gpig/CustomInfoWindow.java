package uk.co.mattjktaylor.gpig;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        MapFragment.setCameraPosBottom(marker.getPosition());

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
            ImageView gas = (ImageView) view.findViewById(R.id.utilties_gas);
            gas.setImageResource(getUtilityImg(mapDescription.getUtilities().isGas()));
            ImageView water = (ImageView) view.findViewById(R.id.utilties_water);
            water.setImageResource(getUtilityImg(mapDescription.getUtilities().isWater()));
            ImageView electricity = (ImageView) view.findViewById(R.id.utilties_electricity);
            electricity.setImageResource(getUtilityImg(mapDescription.getUtilities().isElectricity()));
            ImageView sewage = (ImageView) view.findViewById(R.id.utilities_sewage);
            sewage.setImageResource(getUtilityImg(mapDescription.getUtilities().isSewage()));
        }

        if(mapDescription.getAreaInfo() != null)
        {
            LinearLayout buildingInfoContainer = (LinearLayout) view.findViewById(R.id.building_info_container);
            buildingInfoContainer.setVisibility(View.VISIBLE);

            TextView address = (TextView) view.findViewById(R.id.text_address);
            address.setText(mapDescription.getAreaInfo().getAddress());

            TextView buildingType = (TextView) view.findViewById(R.id.text_building_type);
            buildingType.setText(mapDescription.getAreaInfo().getType());

            TextView buildingYear = (TextView) view.findViewById(R.id.text_building_year);
            buildingYear.setText(String.format(Locale.ENGLISH, "%d", mapDescription.getAreaInfo().getYear()));
        }

        return view;
    }

    private int getUtilityImg(boolean isSet)
    {
        if(isSet)
            return R.drawable.ic_check_circle_green_24dp;
        else
            return R.drawable.ic_cancel_red_24dp;
    }
}
