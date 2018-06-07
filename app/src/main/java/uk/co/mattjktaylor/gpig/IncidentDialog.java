package uk.co.mattjktaylor.gpig;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Calendar;
import java.util.UUID;

public class IncidentDialog extends AlertDialog.Builder {

    private Activity activity;
    private LatLng coords;
    private Marker marker;

    public IncidentDialog(Activity activity, LatLng coords)
    {
        super(activity);
        this.activity = activity;
        this.coords = coords;
        markerDialog();
    }

    public IncidentDialog(Activity activity, Marker marker)
    {
        super(activity);
        this.activity = activity;
        this.marker = marker;
        updateDialog();
    }

    public static String getMarkerType(Marker marker)
    {
        for(MapObject mapObject : MapFragment.mapObjects)
        {
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon p = (MapPolygon) mapObject;
                if(p.getMarker() != null)
                {
                    if(p.getMarker().getId().equals(marker.getId()))
                    {
                        return p.getType();
                    }
                }
            }
            else if(mapObject instanceof MapMarker)
            {
                MapMarker m = (MapMarker) mapObject;
                if(m.getMarker().getId().equals(marker.getId()))
                {
                    return m.getType();
                }
            }
        }
        return null;
    }

    private void markerDialog()
    {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_incident, null);

        // Setup spinner for incident types:
        Spinner spinnerType = (Spinner) dialogView.findViewById(R.id.spinner_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spinner_item, IncidentTypes.getDescriptons());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Setup spinner for incident types:
        final Spinner spinnerStatus = (Spinner) dialogView.findViewById(R.id.spinner_status);
        String description = spinnerType.getSelectedItem().toString();
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(activity, R.layout.spinner_item, IncidentTypes.getTypeFromDesc(description).getStatus());
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapterStatus);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                String description = adapterView.getSelectedItem().toString();
                ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(activity, R.layout.spinner_item, IncidentTypes.getTypeFromDesc(description).getStatus());
                adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStatus.setAdapter(adapterStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

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
                        String type = IncidentTypes.getTypeFromDesc(typeSpinner.getSelectedItem().toString()).getID();
                        Config.log("Type: " + type);

                        Spinner statusSpinner = (Spinner) dialogView.findViewById(R.id.spinner_status);
                        int status = statusSpinner.getSelectedItemPosition();

                        Switch dangerSwitch = (Switch) dialogView.findViewById(R.id.switch_danger);
                        boolean danger = dangerSwitch.isChecked();

                        Switch medicSwitch = (Switch) dialogView.findViewById(R.id.switch_medic);
                        boolean medic = medicSwitch.isChecked();

                        // Add new marker using form data:
                        MapDescription.Incident incident = new MapDescription.Incident(status, "First Responder", info, danger, medic);
                        MapDescription description = new MapDescription(incident, null, null, Calendar.getInstance().getTimeInMillis());
                        MapMarker m = new MapMarker(UUID.randomUUID().toString(), type, coords.latitude, coords.longitude,
                                IncidentTypes.getIncidentType(type).getDescription(), description);
                        // Add to map:
                        NotificationSocketListener.addMarker(m);
                        // Send to server:
                        ClientUsage.sendMarker(m);

                    }
                }).setNegativeButton("Cancel", null);
    }

    // TODO Needs refactoring:
    private void updateDialog()
    {
        marker.hideInfoWindow();

        // Find marker:
        // TODO could probably improve:
        MapMarker mapMarker = null;
        MapPolygon mapPolygon = null;
        MapDescription mapDescription = null;
        String type = "";
        for(MapObject mapObject : MapFragment.mapObjects)
        {
            if(mapObject instanceof MapMarker)
            {
                MapMarker m = (MapMarker) mapObject;
                if(m.getMarker().getId().equals(marker.getId()))
                {
                    mapMarker = m;
                    mapDescription = m.getDescription();
                    type = m.getType();
                    break;
                }
            }
            else if(mapObject instanceof MapPolygon)
            {
                MapPolygon p = (MapPolygon) mapObject;

                if(p.getMarker() != null)
                {
                    if(p.getMarker().getId().equals(marker.getId()))
                    {
                        mapPolygon = p;
                        mapDescription = p.getDescription();
                        type = p.getType();
                        break;
                    }
                }
            }
        }

        // Get layout view for new incident:
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_incident, null);

        // Hide type:
        Spinner spinner_type = (Spinner) dialogView.findViewById(R.id.spinner_type);
        TextView text_type = (TextView) dialogView.findViewById(R.id.text_incident_type);
        spinner_type.setVisibility(View.GONE);
        text_type.setVisibility(View.GONE);

        final Spinner spinner_status= (Spinner) dialogView.findViewById(R.id.spinner_status);
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(activity, R.layout.spinner_item, IncidentTypes.getIncidentType(type).getStatus());
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_status.setAdapter(adapterStatus);
        spinner_status.setSelection(mapDescription.getIncident().getStatus());

        final EditText info = (EditText) dialogView.findViewById(R.id.edit_description);
        info.setText(mapDescription.getIncident().getInfo());

        final Switch dangerSwitch = (Switch) dialogView.findViewById(R.id.switch_danger);
        dangerSwitch.setChecked(mapDescription.getIncident().isPeopleDanger());
        final Switch medicSwitch = (Switch) dialogView.findViewById(R.id.switch_medic);
        medicSwitch.setChecked(mapDescription.getIncident().isMedicNeeded());

        /*
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
        */

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
                        mapDescription_.getIncident().setStatus(spinner_status.getSelectedItemPosition());
                        mapDescription_.getIncident().setInfo(info.getText().toString());
                        mapDescription_.setDateAdded(Calendar.getInstance().getTimeInMillis());
                        mapDescription_.getIncident().setReportBy("First Responder");
                        mapDescription_.getIncident().setMedicNeeded(medicSwitch.isChecked());
                        mapDescription_.getIncident().setPeopleDanger(dangerSwitch.isChecked());

                        /*
                        if(mapDescription_.getUtilities() != null)
                            mapDescription_.setUtilities(new MapDescription.Utility(switchGas.isChecked(), switchElec.isChecked(), switchWater.isChecked(), switchSewage.isChecked()));
                        */

                        if(mapMarker_ != null)
                            ClientUsage.sendMarker(mapMarker_);
                        else if(mapPolygon_ != null)
                            ClientUsage.sendPolygon(mapPolygon_);

                    }
                }).setNegativeButton("Cancel", null);
    }
}
