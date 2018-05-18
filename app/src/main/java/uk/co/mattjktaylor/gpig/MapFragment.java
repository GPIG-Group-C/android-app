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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MapFragment extends Fragment implements OnNotificationListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

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
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        googleMap.setOnMapLongClickListener(this);
        googleMap.setInfoWindowAdapter(new CustomInfoWindow(getActivity()));
        googleMap.setOnInfoWindowClickListener(this);
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
                MapDescription desc = new MapDescription(0, null, 0, null, null, null, "Test");
                addMarker(new MapMarker("0", 0, 37.72961, -122.4269,
                        "Title 0", desc, Calendar.getInstance().getTimeInMillis()));
                addMarker(new MapMarker("1", 1, 37.75961, -122.4269,
                        "Title 1", desc, Calendar.getInstance().getTimeInMillis()));
                addMarker(new MapMarker("2", 2, 37.76961, -122.4269,
                        "Title 2", desc, Calendar.getInstance().getTimeInMillis()));
                addMarker(new MapMarker("3", 3, 37.77961, -122.4269,
                        "Title 3", desc, Calendar.getInstance().getTimeInMillis()));
                addMarker(new MapMarker("4", 4, 37.78961, -122.4269,
                        "Title 4", desc, Calendar.getInstance().getTimeInMillis()));
                addMarker(new MapMarker("5", 5, 37.79961, -122.4269,
                        "Title 5", desc, Calendar.getInstance().getTimeInMillis()));
                addMarker(new MapMarker("6", 6, 37.74961, -122.4269,
                        "Title 6", desc, Calendar.getInstance().getTimeInMillis()));
                addMarker(new MapMarker("7", 7, 37.73961, -122.4269,
                        "Title 7", desc, Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_location:
                centerMap();
                return true;

            case R.id.action_add_heatmap:
                addHeatMap(new MapHeatMap(UUID.randomUUID().toString(), 37.74961, -122.4169, 40, 10.0, Calendar.getInstance().getTimeInMillis()));
                addHeatMap(new MapHeatMap(UUID.randomUUID().toString(), 37.76961, -122.4369, 50, 5.0, Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_add_polygon:
                /*
                ArrayList<LatLng> coords = new ArrayList<>();
                coords.add( new LatLng(37.74961, -122.4169));
                coords.add( new LatLng(37.76961, -122.4369));
                coords.add( new LatLng(37.73961, -122.4269));

                JsonArray coordJson = new JsonArray();
                Gson gson = new Gson();

                for(LatLng xy : coords)
                {
                    int i = gson.fromJson(xy, LatLng.class);
                }
                JsonElement
                coords.add(new JsonElement().);
                addPolygon(new MapPolygon("1", 1, 1));
                */
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
                        MapDescription description = new MapDescription(1, "", 1, utility, null, "First Responder", info);

                        MapMarker m = new MapMarker(UUID.randomUUID().toString(), type, latLng.latitude, latLng.longitude, "Incident", description, Calendar.getInstance().getTimeInMillis());
                        // Add to map:
                        addMarker(m);
                        // Send to server:
                        ClientUsage.sendMarker(m);
                    }
                }).setNegativeButton("Cancel", null);
        alertSearch.show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "InfoWindow clicked", Toast.LENGTH_SHORT).show();
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
                    mp.getPolygon().remove();
                }

                p.setPolygon(googleMap.addPolygon(p.getPolygonOptions()));
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
