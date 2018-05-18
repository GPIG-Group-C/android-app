package uk.co.mattjktaylor.gpig;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MapPolygon {

    private String ID;
    private int type;
    private int severity;
    private JsonArray coords;
    private MapDescription desc;
    private Long timeAdded;

    private transient Polygon polygon;

    public MapPolygon() {}

    public MapPolygon(String ID, int type, int severity, JsonArray coords, MapDescription desc, Long timeAdded) {
        this.ID = ID;
        this.type = type;
        this.severity = severity;
        this.coords = coords;
        this.desc = desc;
        this.timeAdded = timeAdded;
    }

    public PolygonOptions getPolygonOptions()
    {
        PolygonOptions po = new PolygonOptions();
        for(JsonElement el : coords)
        {
            JsonObject coordPair = el.getAsJsonObject();
            po.add(new LatLng(coordPair.get("lat").getAsDouble(), coordPair.get("lng").getAsDouble()));
        }
        return po;
    }

    public void setPolygon(Polygon p)
    {
        polygon = p;
    }

    public Polygon getPolygon()
    {
        return polygon;
    }

    public String getID() {
        return ID;
    }

    public int getType() {
        return type;
    }

    public int getSeverity() {
        return severity;
    }

    public MapDescription getDesc() {
        return desc;
    }

    public Long getTimeAdded() {
        return timeAdded;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MapPolygon))
            return false;

        MapPolygon p = (MapPolygon) o;
        if(p.getID().equals(ID))
            return true;
        else
            return false;
    }
}
