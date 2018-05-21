package uk.co.mattjktaylor.gpig;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class IncidentDialog extends AlertDialog.Builder {

    private Activity activity;
    private LatLng coords;
    private MapFragment map;
    private Marker marker;

    public IncidentDialog(Activity activity, MapFragment map, LatLng coords)
    {
        super(activity);
        this.activity = activity;
        this.map = map;
        this.coords = coords;
        updateDialog();
    }

    public IncidentDialog(Activity activity, Marker marker)
    {
        super(activity);
        this.activity = activity;
        this.marker = marker;
        markerDialog();
    }

    private void updateDialog()
    {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_incident, null);
        // Setup spinner for incident types:
        Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.incident_types, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Show dialog using created view:
        setTitle("Add Incident At Location");
        setView(dialogView)
                // Add action buttons
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        EditText infoDescription = (EditText) dialogView.findViewById(R.id.edit_description);
                        String info = infoDescription.getText().toString();

                        Spinner typeSpinner = (Spinner) dialogView.findViewById(R.id.spinner_type);
                        int type = typeSpinner.getSelectedItemPosition();

                        // Add new marker using form data:
                        MapDescription.Utility utility = new MapDescription.Utility();
                        MapDescription description = new MapDescription(1, "", 1, utility, null,
                                "First Responder", info, Calendar.getInstance().getTimeInMillis());

                        MapMarker m = new MapMarker(UUID.randomUUID().toString(), type, coords.latitude, coords.longitude, "Incident", description);
                        // Add to map:
                        map.addMarker(m);
                        // Send to server:
                        ClientUsage.sendMarker(m);

                    }
                }).setNegativeButton("Cancel", null);
    }

    // TODO Needs refactoring:
    private void markerDialog()
    {
        marker.hideInfoWindow();

        // Find marker:
        // TODO could probably improve:
        MapMarker mapMarker = null;
        MapPolygon mapPolygon = null;
        MapDescription mapDescription = null;
        for(MapObject mapObject : MapFragment.mapObjects)
        {
            if(mapObject instanceof MapMarker)
            {
                MapMarker m = (MapMarker) mapObject;
                if(m.getMarker().getId().equals(marker.getId()))
                {
                    mapMarker = m;
                    mapDescription = m.getDescription();
                    break;
                }
            }
            else if(mapObject instanceof MapPolygon)
            {
                MapPolygon p = (MapPolygon) mapObject;
                if(p.getMarker().getId().equals(marker.getId()))
                {
                    mapPolygon = p;
                    mapDescription = p.getDescription();
                    break;
                }
            }
        }

        // Get layout view for new incident:
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_incident_update, null);

        // Populate using existing data:
        final Spinner spinner_sev = (Spinner) dialogView.findViewById(R.id.spinner_severity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spinner_item, CustomInfoWindow.severities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sev.setAdapter(adapter);
        spinner_sev.setSelection(mapDescription.getStatus());

        final EditText people = (EditText) dialogView.findViewById(R.id.text_people);
        people.setText(String.format(Locale.ENGLISH, "%d", mapDescription.getNumPeople()));
        final EditText info = (EditText) dialogView.findViewById(R.id.text_additional_info);
        info.setText(mapDescription.getInfo());

        final Switch switchGas = (Switch) dialogView.findViewById(R.id.utilties_gas_switch);
        final Switch switchElec = (Switch) dialogView.findViewById(R.id.utilties_electricity_switch);
        final Switch switchWater = (Switch) dialogView.findViewById(R.id.utilties_water_switch);
        final Switch switchSewage = (Switch) dialogView.findViewById(R.id.utilties_sewage_switch);

        if(mapDescription.getUtilities() != null)
        {
            dialogView.findViewById(R.id.utilities_container).setVisibility(View.VISIBLE);
            switchGas.setChecked(mapDescription.getUtilities().isGas());
            switchElec.setChecked(mapDescription.getUtilities().isElectricity());
            switchWater.setChecked(mapDescription.getUtilities().isWater());
            switchSewage.setChecked(mapDescription.getUtilities().isSewage());
        }

        // TODO refactor:
        final MapDescription mapDescription_ = mapDescription;
        final MapPolygon mapPolygon_ = mapPolygon;
        final MapMarker mapMarker_ = mapMarker;

        // Show dialog using created view:
        setTitle("Update Incident:");
        setView(dialogView)
                // Add action buttons
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mapDescription_.setStatus(spinner_sev.getSelectedItemPosition());
                        mapDescription_.setInfo(info.getText().toString());
                        mapDescription_.setDateAdded(Calendar.getInstance().getTimeInMillis());
                        mapDescription_.setNumPeople(Integer.parseInt(people.getText().toString()));
                        mapDescription_.setReportBy("First Responder");

                        if(mapDescription_.getUtilities() != null)
                            mapDescription_.setUtilities(new MapDescription.Utility(switchGas.isChecked(), switchElec.isChecked(), switchWater.isChecked(), switchSewage.isChecked()));

                        if(mapMarker_ != null)
                            ClientUsage.sendMarker(mapMarker_);
                        else if(mapPolygon_ != null)
                            ClientUsage.sendPolygon(mapPolygon_);

                    }
                }).setNegativeButton("Cancel", null);
    }
}
