package uk.co.mattjktaylor.gpig;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;

public class MapHeatMap {

    private String ID;
    private int type;
    private Double latitude;
    private Double longitude;
    private int radius;
    private Double intensity;
    private Long dateTime;

    // Do not serialise:
    private transient TileOverlay heatmap;
    private transient MapMarker mapMarker;

    // Initialised using Gson
    public MapHeatMap() {}

    public MapHeatMap(String ID, Double lat, Double lon, int radius, Double intensity, Long dateRecorded)
    {
        this.ID = ID;
        this.latitude = lat;
        this.longitude = lon;
        this.radius = radius;
        this.intensity = intensity;
        this.dateTime = dateRecorded;

        mapMarker = new MapMarker(ID, -1, lat, lon, "heatmap title", "heatmap description", dateRecorded);
    }

    public TileOverlayOptions getTileOverlayOptions() {
        ArrayList<WeightedLatLng> list = new ArrayList<WeightedLatLng>();
        list.add(new WeightedLatLng(new LatLng(this.latitude, this.longitude), this.intensity));

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(list)
                .build();
        mProvider.setRadius(this.radius);

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

        return new TileOverlayOptions().tileProvider(mProvider);
    }

    public String getID()
    {
        return ID;
    }

    public void setHeatmap(TileOverlay h) {
        this.heatmap = heatmap;
    }

    public MapMarker getMapMarker(){
        return mapMarker;
    }

    public TileOverlay getHeatmap() {
        return heatmap;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MapHeatMap))
            return false;

        MapHeatMap h = (MapHeatMap) o;
        if(h.getID().equals(ID))
            return true;
        else
            return false;
    }
}
