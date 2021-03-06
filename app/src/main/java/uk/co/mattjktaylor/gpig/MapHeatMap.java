package uk.co.mattjktaylor.gpig;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;

public class MapHeatMap extends MapObject{

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
        super(ID, null, null);
        this.latitude = lat;
        this.longitude = lon;
        this.radius = radius;
        this.intensity = intensity;
        this.dateTime = dateRecorded;

        // TODO sort out marker situation with heatmaps:
        //mapMarker = new MapMarker(ID, -1, lat, lon, "heatmap title", null, dateRecorded);
    }

//    public TileOverlayOptions getTileOverlayOptions() {
//        ArrayList<WeightedLatLng> list = new ArrayList<WeightedLatLng>();
//        list.add(new WeightedLatLng(new LatLng(this.latitude, this.longitude), this.intensity));
//
//        return new TileOverlayOptions().tileProvider(mProvider);
//    }

    public void setHeatmap(TileOverlay h) {
        this.heatmap = heatmap;
    }

    public MapMarker getMapMarker(){
        return mapMarker;
    }

    public TileOverlay getHeatmap() {
        return heatmap;
    }

    public WeightedLatLng getWeightedLatLng(){
        return  new WeightedLatLng(new LatLng(latitude, longitude), intensity);
    }

}
