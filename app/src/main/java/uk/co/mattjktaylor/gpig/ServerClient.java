package uk.co.mattjktaylor.gpig;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ServerClient {

    private static Context context;
    private static Socket webSocket;

    public static void init(final Context mContext)
    {
        context = mContext;
        if(!Config.getIP(context).isEmpty())
        {
            if(webSocket != null)
            {
                webSocket.disconnect();
                webSocket.close();
            }

            try
            {
                IO.Options op = new IO.Options();
                op.port = Integer.parseInt(Config.getPort(getContext()));
                webSocket = IO.socket(Config.getIpUrl(getContext()), op);
                webSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args)
                    {
                        Config.log("Connected");
                    }

                });
                webSocket.on("notification", NotificationSocketListener.getInstance());
                webSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args)
                    {
                        Config.log("Disconnected");
                    }

                });
                webSocket.connect();
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static Context getContext()
    {
        return context;
    }

    public static void toast(String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void broadcastData(JSONObject json)
    {
        webSocket.emit("broadcastData", json);
    }
}
