package uk.co.mattjktaylor.gpig;

import android.content.res.Resources;
import android.graphics.Point;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnNotificationListener, OnMapReadyCallback, GoogleMap.OnPolygonClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    private static MapView mMapView;
    private static GoogleMap googleMap;

    public static ArrayList<MapObject> mapObjects = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);

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
    public void onMapLongClick(final LatLng latLng)
    {
        IncidentDialog dialog = new IncidentDialog(getActivity(), this, latLng);
        dialog.show();
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        IncidentDialog dialog = new IncidentDialog(getActivity(), marker);
        dialog.show();
    }

    @Override
    public void onPolygonClick(Polygon polygon)
    {
        MapPolygon mp = null;
        for(MapObject mapObject : mapObjects)
        {
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon p = (MapPolygon) mapObject;
                if(p.getPolygon().getId().equals(polygon.getId()))
                {
                    mp = p;
                    break;
                }
            }
        }
        if(mp != null)
        {
            mp.getMarker().showInfoWindow();
        }
    }

    public void centerMap() {
        LatLng location = new LatLng(37.75961, -122.4269);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static void setCameraPosBottom(LatLng pos) {
        Projection proj = googleMap.getProjection();
        Point markerPoint = proj.toScreenLocation(pos);
        Point targetPoint = new Point(markerPoint.x, markerPoint.y - mMapView.getHeight() / 2);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(proj.fromScreenLocation(targetPoint)));
    }

    @Override
    public void addMarker(final MapMarker m) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                int index = mapObjects.indexOf(m);
                if(index != -1)
                {
                    MapMarker mm = (MapMarker) mapObjects.get(index);
                    mm.getMarker().remove();
                    mapObjects.remove(index);
                    Config.log("Success...");
                }

                m.setMarker(googleMap.addMarker(m.getMarkerOptions()), getContext());
                mapObjects.add(m);
                NotificationSocketListener.notifyListUpdate();
            }
        });
    }

    @Override
    public void addCircle(final MapCircle c) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = mapObjects.indexOf(c);
                if(index != -1)
                {
                    MapCircle cc = (MapCircle) mapObjects.get(index);
                    cc.getCircle().remove();
                    mapObjects.remove(index);
                }
                c.setCircle(googleMap.addCircle(c.getCircleOptions()));
                mapObjects.add(c);
                NotificationSocketListener.notifyListUpdate();
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
                int index = mapObjects.indexOf(h);
                if(index != -1)
                {
                    MapHeatMap hh = (MapHeatMap) mapObjects.get(index);
                    //hh.getMapMarker().getMarker().remove();
                    hh.getHeatmap().remove();
                }

                h.setHeatmap(googleMap.addTileOverlay(h.getTileOverlayOptions()));
                //addMarker(h.getMapMarker());
                mapObjects.add(h);
                NotificationSocketListener.notifyListUpdate();
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
                int index = mapObjects.indexOf(p);
                if(index != -1)
                {
                    MapPolygon mp = (MapPolygon) mapObjects.get(index);
                    mp.getMarker().remove();
                    mp.getPolygon().remove();
                }

                p.setPolygon(googleMap.addPolygon(p.getPolygonOptions()));

                LatLng pos = p.getAverageCoords();
                p.setMarker(googleMap.addMarker(new MarkerOptions().alpha(0).position(pos)
                        .anchor((float) pos.latitude, (float) pos.longitude)
                        .infoWindowAnchor((float) pos.latitude, (float) pos.longitude)));
                mapObjects.add(p);
                NotificationSocketListener.notifyListUpdate();
            }
        });
    }

    @Override
    public void onListUpdated() {
        Config.log("Map updated...");
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
