package uk.co.mattjktaylor.gpig;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapFragment extends Fragment implements OnNotificationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener {

    private static MapView mMapView;
    private static GoogleMap googleMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay heatMap;

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
        googleMap.setOnMarkerClickListener(this);
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
        loadFireStations();
    }

    private void loadFireStations()
    {
        String json = null;
        try
        {
            InputStream is = getResources().openRawResource(R.raw.initialmapdata);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            if(json != null)
            {
                try
                {
                    JSONArray stations = new JSONArray(json);
                    for (int i = 0; i < stations.length(); i++)
                    {
                        JSONObject station = stations.getJSONObject(i);
                        JSONObject params = station.getJSONObject("params");
                        if(station.get("method").equals("addMarker"))
                        {
                            MapMarker m = NotificationSocketListener.gson.fromJson(params.toString(), MapMarker.class);
                            addMarker(m);
                        }
                        else if(station.get("method").equals("addTransparentPolygon"))
                        {
                            Config.log(params.toString());
                            MapTransparentPolygon p = NotificationSocketListener.gson.fromJson(params.toString(), MapTransparentPolygon.class);
                            JSONArray coords = params.getJSONArray("coords");
                            ArrayList<LatLng> coordList = NotificationSocketListener.gson.fromJson(coords.toString(), NotificationSocketListener.coordListType);
                            p.setCoords(coordList);
                            addTransparentPolygon(p);
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng)
    {
        if(!Config.isFirstResponder(getContext()))
        {
            Toast.makeText(getContext(), "You are not a first responder...", Toast.LENGTH_LONG).show();
            return;
        }

        IncidentDialog dialog = new IncidentDialog(getActivity(), this, latLng);
        dialog.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        setCameraPosBottom(marker.getPosition());
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        for(MapObject mapObject : mapObjects)
        {
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon p = (MapPolygon) mapObject;
                if(p.getMarker() != null)
                {
                    if(p.getMarker().getId().equals(marker.getId()))
                    {
                        marker.hideInfoWindow();
                        return;
                    }
                }
            }
        }

        if(!Config.isFirstResponder(getContext()))
        {
            Toast.makeText(getContext(), "You are not a first responder...", Toast.LENGTH_LONG).show();
            return;
        }

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
        LatLng location = new LatLng(37.75765, -122.43999);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(13).build();
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
                    mapObjects.remove(index);
                }
                mapObjects.add(h);

                ArrayList<WeightedLatLng> heatMapLst = new ArrayList<>();
                for (MapObject i : mapObjects){
                    if (i instanceof MapHeatMap){
                        heatMapLst.add(((MapHeatMap) i).getWeightedLatLng());
                    }

                }

                // Create a heat map tile provider, passing it the latlngs of the police stations.
                mProvider = new HeatmapTileProvider.Builder().weightedData(heatMapLst).build();
                mProvider.setRadius(25);

                // Create the gradient.
                int[] colors = {
                        Color.rgb(255, 255, 0), // yellow
                        Color.rgb(255, 100, 0), // orange
                        Color.rgb(255, 0, 0), // red
                };

                float[] startPoints = {
                        0.2f, 0.7f, 1.0f
                };

                Gradient gradient = new Gradient(colors, startPoints);
                mProvider.setGradient(gradient);

                if (! (heatMap == null)) {
                    heatMap.remove();
                }

                mProvider.setWeightedData(heatMapLst);
                heatMap = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                //addMarker(h.getMapMarker());

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
                    mapObjects.remove(index);
                }

                Config.log("Add polygon...");
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
    public void addTransparentPolygon(final MapTransparentPolygon p)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                int index = mapObjects.indexOf(p);
                if(index != -1)
                {
                    MapTransparentPolygon mp = (MapTransparentPolygon) mapObjects.get(index);
                    mp.getPolygon().remove();
                    mapObjects.remove(index);
                }
                p.setPolygon(googleMap.addPolygon(p.getPolygonOptions()));
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
