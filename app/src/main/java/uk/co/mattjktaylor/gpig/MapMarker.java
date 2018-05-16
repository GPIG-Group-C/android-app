package uk.co.mattjktaylor.gpig;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

// TODO Incomplete
public class MapMarker {

    private JSONObject json;
    private Marker m;

    public MapMarker(JSONObject json)
    {
        this.json = json;
        m.getPosition();
        m.getId();
        m.getAlpha();
        m.getTitle();
        m.getSnippet();



    }
}
