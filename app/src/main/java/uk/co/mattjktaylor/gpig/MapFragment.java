package uk.co.mattjktaylor.gpig;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class MapFragment extends Fragment implements OnNotificationListener, OnMapReadyCallback, GoogleMap.OnPolygonClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    private static MapView mMapView;
    private static GoogleMap googleMap;

    public static ArrayList<MapMarker> markers = new ArrayList<>();
    public static ArrayList<MapCircle> circles = new ArrayList<>();
    public static ArrayList<MapHeatMap> heatmaps = new ArrayList<>();
    public static ArrayList<MapPolygon> polygons = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);
        setHasOptionsMenu(true);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try
        {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        NotificationSocketListener.addNotificationListener(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap mMap)
    {
        googleMap = mMap;
        googleMap.setOnMapLongClickListener(this);
        googleMap.setInfoWindowAdapter(new CustomInfoWindow(getActivity()));
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnPolygonClickListener(this);

        // Set map style:
        try
        {
            boolean styleSuccess = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstyles));
            if(styleSuccess)
                Config.log("Map style applied successfully");
            else
                Config.log("Map style failed");
        }
        catch (Resources.NotFoundException e)
        {
            e.printStackTrace();
        }
        centerMap();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add)
            return true;

        switch (item.getItemId()) {
            case R.id.action_add_circle:
                addCircle(new MapCircle("1", 37.75961, -122.4269, 2500, Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_add_marker:
                MapDescription desc = new MapDescription(50, "N/A", 0, null, null,
                        null, "Test", Calendar.getInstance().getTimeInMillis());
                addMarker(new MapMarker("0", 0, 37.72961, -122.4269,
                        "Title 0", desc));
                addMarker(new MapMarker("1", 1, 37.75961, -122.4269,
                        "Title 1", desc));
                addMarker(new MapMarker("2", 2, 37.76961, -122.4269,
                        "Title 2", desc));
                addMarker(new MapMarker("3", 3, 37.77961, -122.4269,
                        "Title 3", desc));
                addMarker(new MapMarker("4", 4, 37.78961, -122.4269,
                        "Title 4", desc));
                addMarker(new MapMarker("5", 5, 37.79961, -122.4269,
                        "Title 5", desc));
                addMarker(new MapMarker("6", 6, 37.74961, -122.4269,
                        "Title 6", desc));
                addMarker(new MapMarker("7", 7, 37.73961, -122.4269,
                        "Title 7", desc));
                return true;

            case R.id.action_location:
                centerMap();
                return true;

            case R.id.action_add_heatmap:
                addHeatMap(new MapHeatMap(UUID.randomUUID().toString(), 37.74961, -122.4169, 40, 10.0, Calendar.getInstance().getTimeInMillis()));
                addHeatMap(new MapHeatMap(UUID.randomUUID().toString(), 37.76961, -122.4369, 50, 5.0, Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_add_polygon:
                ArrayList<LatLng> coords = new ArrayList<>();
                coords.add( new LatLng(37.74961, -122.4169));
                coords.add( new LatLng(37.76961, -122.4369));
                coords.add( new LatLng(37.73961, -122.4269));

                MapDescription.Utility util = new MapDescription.Utility(true, false, false, true);
                MapDescription.BuildingInfo bInfo = new MapDescription.BuildingInfo("Apartment Block", 1995);
                MapDescription descr = new MapDescription(500, "Address", 1, util, bInfo, "First Responder","Testing", Calendar.getInstance().getTimeInMillis());
                addPolygon(new MapPolygon("1", 1, 1, coords, descr));

                coords.clear();
                coords.add( new LatLng(37.71961, -122.4069));
                coords.add( new LatLng(37.72961, -122.4069));
                coords.add( new LatLng(37.72961, -122.4369));
                coords.add( new LatLng(37.71961, -122.4369));
                addPolygon(new MapPolygon("2", 1, 2, coords, descr));

                coords.clear();
                coords.add( new LatLng(37.75961, -122.4069));
                coords.add( new LatLng(37.77961, -122.4069));
                coords.add( new LatLng(37.77961, -122.4369));
                coords.add( new LatLng(37.75961, -122.4369));
                addPolygon(new MapPolygon("3", 1, 3, coords, descr));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng)
    {
        // Get layout view for new incident:
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_incident, null);
        // Setup spinner for incident types:
        Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.incident_types, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // Show dialog using created view:
        AlertDialog.Builder alertSearch = new AlertDialog.Builder(getActivity());
        alertSearch.setTitle("Add Incident At Location");
        alertSearch.setView(dialogView)
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

                        MapMarker m = new MapMarker(UUID.randomUUID().toString(), type, latLng.latitude, latLng.longitude, "Incident", description);
                        // Add to map:
                        addMarker(m);
                        // Send to server:
                        ClientUsage.sendMarker(m);

                    }
                }).setNegativeButton("Cancel", null);
        alertSearch.show();
    }

    // TODO Needs refactoring:
    @Override
    public void onInfoWindowClick(Marker marker)
    {
        marker.hideInfoWindow();

        // TODO should probably make superclass:
        // Find marker:
        MapMarker mapMarker = null;
        MapPolygon mapPolygon = null;
        MapDescription mapDescription = null;
        for(MapMarker m : MapFragment.markers)
        {
            if(m.getMarker().getId().equals(marker.getId()))
            {
                mapMarker = m;
                break;
            }
        }
        if(mapMarker == null)
        {
            for(MapPolygon p : MapFragment.polygons)
            {
                if(p.getMarker().getId().equals(marker.getId()))
                {
                    mapPolygon = p;
                    break;
                }
            }

            if(mapPolygon == null)
                return;
            else
            {
                mapDescription = mapPolygon.getDescription();
            }
        }
        else
        {
            mapDescription = mapMarker.getDescription();
        }

        // Get layout view for new incident:
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_incident_update, null);

        // Populate using existing data:
        final Spinner spinner_sev = (Spinner) dialogView.findViewById(R.id.spinner_severity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, CustomInfoWindow.severities);
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

        // TODO sort out final duplication
        final MapDescription mapDescription_ = mapDescription;
        final MapPolygon mapPolygon_ = mapPolygon;
        final MapMarker mapMarker_ = mapMarker;

        // Show dialog using created view:
        AlertDialog.Builder alertSearch = new AlertDialog.Builder(getActivity());
        alertSearch.setTitle("Update Incident:");
        alertSearch.setView(dialogView)
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
        alertSearch.show();
    }

    @Override
    public void onPolygonClick(Polygon polygon)
    {
        MapPolygon mp = null;
        for(MapPolygon p : polygons)
        {
            if(p.getPolygon().getId().equals(polygon.getId()))
            {
                mp = p;
                break;
            }
        }

        if(mp != null)
        {
            mp.getMarker().showInfoWindow();
        }

    }

    private void centerMap() {
        LatLng location = new LatLng(37.75961, -122.4269);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void addMarker(final MapMarker m) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                int index = markers.indexOf(m);
                if(index != -1)
                {
                    MapMarker mm = markers.get(index);
                    mm.getMarker().remove();
                    markers.remove(index);
                }

                m.setMarker(googleMap.addMarker(m.getMarkerOptions()), getContext());
                markers.add(m);
            }
        });
    }

    @Override
    public void addCircle(final MapCircle c) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = circles.indexOf(c);
                if(index != -1)
                {
                    MapCircle cc = circles.get(index);
                    cc.getCircle().remove();
                    circles.remove(index);
                }
                c.setCircle(googleMap.addCircle(c.getCircleOptions()));
                circles.add(c);
            }
        });
    }

    @Override
    public void addHeatMap(final MapHeatMap h) {
        // TODO correct heatmap behaviour:
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                int index = heatmaps.indexOf(h);
                if(index != -1)
                {
                    MapHeatMap hh = heatmaps.get(index);
                    //hh.getMapMarker().getMarker().remove();
                    hh.getHeatmap().remove();
                }

                h.setHeatmap(googleMap.addTileOverlay(h.getTileOverlayOptions()));
                //addMarker(h.getMapMarker());
                heatmaps.add(h);
            }
        });
    }

    @Override
    public void addPolygon(final MapPolygon p)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                int index = polygons.indexOf(p);
                if(index != -1)
                {
                    MapPolygon mp = polygons.get(index);
                    mp.getMarker().remove();
                    mp.getPolygon().remove();
                }

                p.setPolygon(googleMap.addPolygon(p.getPolygonOptions()));

                LatLng pos = p.getAverageCoords();
                p.setMarker(googleMap.addMarker(new MarkerOptions().alpha(0).position(pos)
                        .anchor((float) pos.latitude, (float) pos.longitude)
                        .infoWindowAnchor((float) pos.latitude, (float) pos.longitude)));
                polygons.add(p);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
