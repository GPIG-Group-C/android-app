package uk.co.mattjktaylor.gpig;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientUsage {

    private static Gson gson = new Gson();

    public static void sendMarker(MapMarker m)
    {
        try
        {
            JSONObject json = new JSONObject();
            JSONObject params = new JSONObject(gson.toJson(m));
            json.put("method", "addMarker");
            json.put("params", params);

            ServerClient.broadcastData(json);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void sendCircle(MapCircle c)
    {
        try
        {
            JSONObject json = new JSONObject();
            JSONObject params = new JSONObject(gson.toJson(c));
            json.put("method", "addCircle");
            json.put("params", params);

            ServerClient.broadcastData(json);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void sendHeatmap(MapHeatMap h)
    {
        try
        {
            JSONObject json = new JSONObject();
            JSONObject params = new JSONObject(gson.toJson(h));
            json.put("method", "addHeatmap");
            json.put("params", params);

            ServerClient.broadcastData(json);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
}
