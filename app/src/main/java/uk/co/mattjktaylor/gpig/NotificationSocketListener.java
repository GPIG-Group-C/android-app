package uk.co.mattjktaylor.gpig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public final class NotificationSocketListener implements Emitter.Listener {

    private static List<OnNotificationListener> listeners = new ArrayList<OnNotificationListener>();

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
                    for (OnNotificationListener l : listeners)
                        l.addMarker("", 1, params.getDouble("latitude"), params.getDouble("longitude"), "", null);
                    break;

                case "addCircle":
                    for (OnNotificationListener l : listeners)
                        l.addCircle("", params.getDouble("latitude"), params.getDouble("longitude"), params.getDouble("radius"), "", null);
                    break;
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
}