package uk.co.mattjktaylor.gpig;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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

        JSONObject json = (JSONObject)args[0];
        try
        {
            String method = json.getString("method");
            JSONObject params = json.getJSONObject("params");
            switch (method)
            {
                case "addMarker":
                    Config.log(params.toString());
                    MapMarker m = gson.fromJson(params.toString(), MapMarker.class);
                    for (OnNotificationListener l : listeners)
                    {
                        l.addMarker(m);
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
                    for (OnNotificationListener l : listeners)
                    {
                        l.addPolygon(p);
                    }
                    break;
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static class LatLngSerializer implements JsonSerializer<LatLng> {

        @Override
        public JsonElement serialize(LatLng src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jsonObject = new JsonObject();
            Config.log("Serialise...");
            jsonObject.add("lat", new JsonPrimitive(src.latitude));
            jsonObject.add("lng", new JsonPrimitive(src.longitude));
            return jsonObject;
        }
    }
}