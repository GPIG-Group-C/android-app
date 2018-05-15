package uk.co.mattjktaylor.gpig;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class MapFragment extends Fragment implements OnNotificationListener, GoogleMap.OnMapLongClickListener{

    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);
        setHasOptionsMenu(true);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try
        {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap)
            {
                googleMap = mMap;
                googleMap.setOnMapLongClickListener(MapFragment.this);
                centerMap();
            }
        });

        NotificationSocketListener.addNotificationListener(this);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.action_add)
            return true;

        switch (item.getItemId())
        {
            case R.id.action_add_circle:
                addCircle(null,37.75961,-122.4269, 1000, null, null);
                return true;

            case R.id.action_add_marker:
                addMarker(null,1, 37.75961,-122.4269, null, null);
                return true;

            case R.id.action_location:
                centerMap();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {
        //TODO Replace with alertDialog:
        addMarker(null,1, latLng.latitude, latLng.longitude, null, null);
        ClientUsage.sendMarker("1",1, latLng.latitude, latLng.longitude, "Send from app", Calendar.getInstance().getTimeInMillis());
    }

    private void centerMap()
    {
        LatLng location = new LatLng(37.75961,-122.4269);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void addMarker(String ID, int type, final double lat, final double lon, final String desc, Long dateRecorded)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Marker Title").snippet(desc));
            }
        });
    }

    @Override
    public void addCircle(String ID, final double lat, final double lon, final double radius, String desc, Long dateRecorded)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(lat, lon))
                        .fillColor(Color.RED)
                        .radius(radius));
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
