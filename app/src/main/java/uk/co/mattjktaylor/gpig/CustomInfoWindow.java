package uk.co.mattjktaylor.gpig;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Activity activity;
    public static ArrayList<String> mStatus = new ArrayList<>();
    static
    {
        mStatus.add("Fire Extinguished");
        mStatus.add("Fire Contained");
        mStatus.add("Fire Escalating");
    }

    public static ArrayList<String> mSeverity = new ArrayList<>();
    static
    {
        mSeverity.add("Low");
        mSeverity.add("Medium");
        mSeverity.add("High");
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

        boolean isPolygon = false;
        String type = "";
        MapDescription mapDescription = null;
        for(MapObject mapObject : MapFragment.mapObjects)
        {
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon p = (MapPolygon) mapObject;
                if(p.getMarker().getId().equals(marker.getId()))
                {
                    isPolygon = true;
                    type = p.getType();
                    mapDescription = p.getDescription();
                    break;
                }
            }
            else if(mapObject instanceof MapMarker)
            {
                MapMarker m = (MapMarker) mapObject;
                if(m.getMarker().getId().equals(marker.getId()))
                {
                    type = m.getType();
                    mapDescription = m.getDescription();
                    break;
                }
            }
        }
        if(mapDescription == null)
            return null;

        if(isPolygon)
            return getAreaView(mapDescription);
        else
            return getIncidentView(type, mapDescription);
    }

    private View getAreaView(MapDescription mapDescription)
    {
        if(mapDescription.getAreaInfo() == null)
            return null;

        View view = activity.getLayoutInflater().inflate(R.layout.infowindow_area, null);

        TextView sev = (TextView) view.findViewById(R.id.text_severity);
        sev.setText(mSeverity.get(mapDescription.getAreaInfo().getSeverity()));

        TextView numPeople = (TextView) view.findViewById(R.id.text_people);
        numPeople.setText(String.format(Locale.ENGLISH, "~%d", mapDescription.getAreaInfo().getNumPeople()));

        TextView updatedAt = (TextView) view.findViewById(R.id.text_time);
        updatedAt.setText(Config.getFormattedDate(mapDescription.getDateAdded(), "dd/MM/yy HH:mm:ss"));

        TextView address = (TextView) view.findViewById(R.id.text_address);
        address.setText(mapDescription.getAreaInfo().getAddress());

        TextView buildingType = (TextView) view.findViewById(R.id.text_building_type);
        buildingType.setText(mapDescription.getAreaInfo().getType());

        TextView buildingYear = (TextView) view.findViewById(R.id.text_building_year);
        buildingYear.setText(String.format(Locale.ENGLISH, "%d", mapDescription.getAreaInfo().getYear()));

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

        return view;
    }

    private View getIncidentView(String type, MapDescription mapDescription)
    {
        if(mapDescription.getIncident() == null)
            return null;

        View view = activity.getLayoutInflater().inflate(R.layout.infowindow_incident, null);

        TextView typeText = (TextView) view.findViewById(R.id.text_type);
        typeText.setText(IncidentDialog.incidentDesc.get(type));

        TextView status = (TextView) view.findViewById(R.id.text_status);
        status.setText(mStatus.get(mapDescription.getIncident().getStatus()));

        TextView reportedBy = (TextView) view.findViewById(R.id.text_reported);
        reportedBy.setText(mapDescription.getIncident().getReportBy());

        TextView updatedAt = (TextView) view.findViewById(R.id.text_time);
        updatedAt.setText(Config.getFormattedDate(mapDescription.getDateAdded(), "dd/MM/yy HH:mm:ss"));

        TextView address = (TextView) view.findViewById(R.id.text_address);
        address.setText("N/A");

        TextView info = (TextView) view.findViewById(R.id.text_additional_info);
        info.setText(mapDescription.getIncident().getInfo());

        ImageView medicIcon = (ImageView) view.findViewById(R.id.medic_icon);
        medicIcon.setImageResource(getUtilityImg(mapDescription.getIncident().isMedicNeeded()));

        ImageView dangerIcon = (ImageView) view.findViewById(R.id.danger_icon);
        dangerIcon.setImageResource(getUtilityImg(mapDescription.getIncident().isPeopleDanger()));

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
