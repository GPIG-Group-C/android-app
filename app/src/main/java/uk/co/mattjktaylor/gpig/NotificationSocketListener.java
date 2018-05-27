package uk.co.mattjktaylor.gpig;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public final class NotificationSocketListener implements Emitter.Listener {

    private static List<OnNotificationListener> listeners = new ArrayList<OnNotificationListener>();
    private static Gson gson = new GsonBuilder().registerTypeAdapter(LatLng.class, new LatLngSerializer()).create();

    private static NotificationSocketListener instance;
    private NotificationSocketListener(){}

    public static NotificationSocketListener getInstance()
    {
        if(instance == null)
        {
            instance = new NotificationSocketListener();
        }
        return instance;
    }

    public static void addNotificationListener(OnNotificationListener listener)
    {
        listeners.add(listener);
    }

    public static void removeNotificationListener(OnNotificationListener listener)
    {
        listeners.remove(listener);
    }

    public static void notifyListUpdate()
    {
        for (OnNotificationListener l : listeners)
        {
            l.onListUpdated();
        }
    }

    // Notification from the server:
    @Override
    public void call(Object... args)
    {
        if(args.length == 0 || listeners == null)
            return;

        Object obj = args[0];
        try
        {
            if(obj instanceof JSONArray)
            {
                JSONArray jsonArray = (JSONArray) obj;
                Config.log(jsonArray.toString());
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    parseJsonRpc(jsonArray.getJSONObject(i));
                }
            }
            else if(obj instanceof JSONObject)
            {
                JSONObject jsonObj= (JSONObject) obj;
                parseJsonRpc(jsonObj);
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void parseJsonRpc(JSONObject json)
    {
        try
        {
            String method = json.getString("method");
            JSONObject params = json.getJSONObject("params");
            switch (method)
            {
                case "addMarker":
                    Config.log(params.toString());
                    MapMarker m = gson.fromJson(params.toString(), MapMarker.class);
                    if(IncidentTypes.getTypes().contains(m.getType()))
                    {
                        addMarker(m);
                    }
                    break;

                case "addCircle":
                    MapCircle c = gson.fromJson(params.toString(), MapCircle.class);
                    for (OnNotificationListener l : listeners)
                    {
                        l.addCircle(c);
                    }
                    break;

                case "addHeatmap":
                    MapHeatMap h = gson.fromJson(params.toString(), MapHeatMap.class);
                    for (OnNotificationListener l : listeners)
                    {
                        l.addHeatMap(h);
                    }
                    break;

                case "addPolygon":
                    MapPolygon p = gson.fromJson(params.toString(), MapPolygon.class);

                    // Parse coordinates seperately:
                    JSONArray coordJson = params.getJSONArray("coords");
                    Type listType = new TypeToken<ArrayList<LatLng>>(){}.getType();
                    ArrayList<LatLng> coords = gson.fromJson(coordJson.toString(), listType);
                    p.setCoords(coords);;

                    for (OnNotificationListener l : listeners)
                    {
                        l.addPolygon(p);
                    }
                    break;

                case "addTransparentPolygon":
                    MapTransparentPolygon tP = gson.fromJson(params.toString(), MapTransparentPolygon.class);
                    // Parse coordinates seperately:
                    JSONArray tCoordJson = params.getJSONArray("coords");
                    Type tListType = new TypeToken<ArrayList<LatLng>>(){}.getType();
                    ArrayList<LatLng> tCoords = gson.fromJson(tCoordJson.toString(), tListType);
                    tP.setCoords(tCoords);;

                    for (OnNotificationListener l : listeners)
                    {
                        l.addTransparentPolygon(tP);
                    }
                    break;
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void addMarker(MapMarker m)
    {
        for (OnNotificationListener l : listeners)
        {
            l.addMarker(m);
        }
    }

    public static void updateSeverity(MapMarker m)
    {
        if(m.getMarker() == null)
            return;

        for(MapObject mo : MapFragment.mapObjects)
        {
            if(mo instanceof MapPolygon && !(mo instanceof MapTransparentPolygon))
            {
                MapPolygon p = (MapPolygon) mo;
                if(PolyUtil.containsLocation(m.getMarker().getPosition(), p.getPolygon().getPoints(), true))
                {
                    Config.log("Updating polygon sev...");

                    boolean medicNeeded = m.getDescription().getIncident().isMedicNeeded();
                    boolean peopleDanger = m.getDescription().getIncident().isPeopleDanger();
                    int newSev = 1;

                    if(peopleDanger && medicNeeded)
                        newSev = 10;
                    else if(peopleDanger)
                        newSev = 8;
                    else if(medicNeeded)
                        newSev = 6;
                    else
                        newSev = 4;

                    p.getDescription().getAreaInfo().setSeverity(newSev);
                    for (OnNotificationListener l : listeners)
                    {
                        l.addPolygon(p);
                    }
                    return;
                }
            }
        }
    }

    public static class LatLngSerializer implements JsonSerializer<LatLng>, JsonDeserializer<LatLng> {
        @Override
        public JsonElement serialize(LatLng src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("lat", new JsonPrimitive(src.latitude));
            jsonObject.add("lng", new JsonPrimitive(src.longitude));
            return jsonObject;
        }

        @Override
        public LatLng deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject o = json.getAsJsonObject();
            return new LatLng(o.get("lat").getAsDouble(), o.get("lng").getAsDouble());
        }
    }
}