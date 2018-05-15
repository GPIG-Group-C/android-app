package uk.co.mattjktaylor.gpig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

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

    @Override
    public void call(Object... args)
    {
        if(args.length == 0 || listeners == null)
            return;

        JSONObject obj = (JSONObject)args[0];
        Config.log("*** RECEIVING: ***");
        Config.log(obj.toString());

        try
        {
            for (OnNotificationListener l : listeners)
                l.onAddMarker(obj.getDouble("lat"), obj.getDouble("lng"));
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        /*
            String method = message.getString("method");
            Config.log("Method: " + method);
            switch(method)
            {
                case "Player.OnPlay":
                    for(OnNotificationListener l : listeners)
                        l.onPlayerPlay();
                    break;

                case "Playlist.OnRemove":
                    mClient.toast("Video removed...");
                    break;

                default:
                    break;
            }
            */
    }
}