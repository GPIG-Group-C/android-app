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
import java.util.UUID;

public class MapFragment extends Fragment implements OnNotificationListener, GoogleMap.OnMapLongClickListener{

    private static MapView mMapView;
    private static GoogleMap googleMap;

    //private static ArrayList<Circle>

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
                addCircle(UUID.randomUUID().toString(),37.75961,-122.4269, 1000,Calendar.getInstance().getTimeInMillis());
                return true;

            case R.id.action_add_marker:
                addMarker(UUID.randomUUID().toString(),1, 37.75961,-122.4269, "Title", "Desc", Calendar.getInstance().getTimeInMillis());
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
        String ID = UUID.randomUUID().toString();
        addMarker(ID,1, latLng.latitude, latLng.longitude, "Title", "Send from app", Calendar.getInstance().getTimeInMillis());
        ClientUsage.sendMarker(ID, 1, latLng.latitude, latLng.longitude, "Title","Send from app", Calendar.getInstance().getTimeInMillis());
    }

    private void centerMap()
    {
        LatLng location = new LatLng(37.75961,-122.4269);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void addMarker(String ID, int type, final double lat, final double lon, final String title, final String desc, Long dateRecorded)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(title).snippet(desc));
                googleMap.add
            }
        });
    }

    @Override
    public void addCircle(String ID, final double lat, final double lon, final double radius, Long dateRecorded)
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
