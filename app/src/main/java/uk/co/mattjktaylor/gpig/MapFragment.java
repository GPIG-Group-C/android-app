package uk.co.mattjktaylor.gpig;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MapFragment extends Fragment implements OnNotificationListener, GoogleMap.OnMapLongClickListener {

    private static MapView mMapView;
    private static GoogleMap googleMap;

    private static ArrayList<MapMarker> markers = new ArrayList<>();
    private static ArrayList<MapCircle> circles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);
        setHasOptionsMenu(true);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setOnMapLongClickListener(MapFragment.this);
                centerMap();
            }
        });

        NotificationSocketListener.addNotificationListener(this);
        return rootView;
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
                addCircle(new MapCircle(UUID.randomUUID().toString(), 37.75961, -122.4269, 1000, Calendar.getInstance().getTimeInMillis()));
                //addMarker(new MapMarker("1",1, 37.75961,-122.4269,
                //        "Title 2", "Send from app", Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_add_marker:
                addMarker(new MapMarker("1", 1, 37.75961, -122.4269,
                        "Title", "Send from app", Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_location:
                centerMap();
                return true;

            case R.id.action_add_heatmap:
                addHeatMap(new MapHeatMap(UUID.randomUUID().toString(), 37.74961, -122.4169, 10.0, Calendar.getInstance().getTimeInMillis()));
                addHeatMap(new MapHeatMap(UUID.randomUUID().toString(), 37.76961, -122.4369, 10.0, Calendar.getInstance().getTimeInMillis()));

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //TODO Replace with alertDialog:
        MapMarker m = new MapMarker(UUID.randomUUID().toString(), 1, latLng.latitude, latLng.longitude, "Title", "Send from app", Calendar.getInstance().getTimeInMillis());
        addMarker(m);

        // Sends marker to server:
        ClientUsage.sendMarker(m);
    }

    private void centerMap() {
        LatLng location = new LatLng(37.75961, -122.4269);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void addMarker(final MapMarker m) {
        int index = markers.indexOf(m);
        if (index != -1) {
            //MapMarker n = markers.get(index);
            //n.getMarker().setTitle(m.getMarker().getTitle());
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m.setMarker(googleMap.addMarker(m.getMarkerOptions()));
                    markers.add(m);
                }
            });
        }
    }

    @Override
    public void addCircle(final MapCircle c) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                c.setCircle(googleMap.addCircle(c.getCircleOptions()));
                circles.add(c);
            }
        });
    }

    @Override
    public void addHeatMap(final MapHeatMap h) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                h.setHeatmap(googleMap.addTileOverlay(h.getTileOverlayOptions()));
//                circles.add(h);
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
