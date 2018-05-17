package uk.co.mattjktaylor.gpig;

import android.content.DialogInterface;
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
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MapFragment extends Fragment implements OnNotificationListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    private static MapView mMapView;
    private static GoogleMap googleMap;

    public static ArrayList<MapMarker> markers = new ArrayList<>();
    public static ArrayList<MapCircle> circles = new ArrayList<>();
    public static ArrayList<MapHeatMap> heatmaps = new ArrayList<>();

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
        //googleMap.setOnMarkerClickListener(this);
        googleMap.setInfoWindowAdapter(new CustomInfoWindow(getActivity()));
        googleMap.setOnInfoWindowClickListener(this);
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
                addCircle(new MapCircle("1", 37.75961, -122.4269, 1000, Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_add_marker:
                addMarker(new MapMarker("1", 1, 37.75961, -122.4269,
                        "Title 1", "Sent from app 1", Calendar.getInstance().getTimeInMillis()));
                addMarker(new MapMarker("2", 1, 37.76961, -122.4269,
                        "Title 2", "Sent from app 2", Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_location:
                centerMap();
                return true;

            case R.id.action_add_heatmap:
                addHeatMap(new MapHeatMap(UUID.randomUUID().toString(), 37.74961, -122.4169, 40, 10.0, Calendar.getInstance().getTimeInMillis()));
                addHeatMap(new MapHeatMap(UUID.randomUUID().toString(), 37.76961, -122.4369, 50, 5.0, Calendar.getInstance().getTimeInMillis()));

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
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
                        EditText editDescription = (EditText) dialogView.findViewById(R.id.edit_description);
                        String desc = editDescription.getText().toString();

                        // Add new marker using form data:
                        MapMarker m = new MapMarker(UUID.randomUUID().toString(), 1, latLng.latitude, latLng.longitude, "Incident", desc, Calendar.getInstance().getTimeInMillis());
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
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(13).build();
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

                m.setMarker(googleMap.addMarker(m.getMarkerOptions()));

                if(m.getID().equals("3"))
                {
                    m.getMarker().setAlpha(0);
                }

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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                int index = heatmaps.indexOf(h);
                if(index != -1)
                {
                    MapHeatMap hh = heatmaps.get(index);
                    hh.getMapMarker().getMarker().remove();
                    hh.getHeatmap().remove();
                }

                h.setHeatmap(googleMap.addTileOverlay(h.getTileOverlayOptions()));
                addMarker(h.getMapMarker());
                heatmaps.add(h);
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
