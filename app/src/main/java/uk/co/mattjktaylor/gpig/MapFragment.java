package uk.co.mattjktaylor.gpig;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class MapFragment extends Fragment implements OnNotificationListener{

    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);

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
                // Zoom in on San Fran:
                LatLng location = new LatLng(37.75961,-122.4269);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(13).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        NotificationSocketListener.addNotificationListener(this);
        return rootView;
    }

    @Override
    public void addMarker(final double lat, final double lon)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Marker Title").snippet("Marker Description"));
            }
        });
    }

    @Override
    public void addCircle(final double lat, final double lon, final double radius)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(lat, lon))
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
