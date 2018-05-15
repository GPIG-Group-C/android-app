package uk.co.mattjktaylor.gpig;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientUsage {

    public static void sendMarker(String ID, int type, double lat, double lon, String desc, Long dateRecorded)
    {
        JSONObject json = new JSONObject();
        JSONObject params = new JSONObject();
        try
        {
            params.put("ID", ID);
            params.put("type", type);
            params.put("latitude", lat);
            params.put("longitude", lon);
            params.put("desc", desc);
            params.put("dateTime", dateRecorded);

            json.put("method", "addMarker");
            json.put("params", params);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        ServerClient.broadcastData(json);
    }
}
