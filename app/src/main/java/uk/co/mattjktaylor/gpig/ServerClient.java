package uk.co.mattjktaylor.gpig;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ServerClient {

    private static Socket webSocket;

    public static void init(final Context context)
    {
        if(webSocket != null)
        {
            webSocket.disconnect();
            webSocket.close();
        }

        if(!Config.getIP(context).isEmpty() && !Config.getIP(context).isEmpty())
        {
            try
            {
                IO.Options op = new IO.Options();
                op.port = Integer.parseInt(Config.getPort(context));
                webSocket = IO.socket(Config.getIpUrl(context), op);
                webSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args)
                    {
                        Config.log("Connected to server");
                    }

                });
                webSocket.on("notification", NotificationSocketListener.getInstance());
                webSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args)
                    {
                        Config.log("Disconnected from server");
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

    public static void toast(String message, Context context)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void broadcastData(JSONObject json)
    {
        Config.log("Broadcasting: " + json.toString());
        if(webSocket != null)
            webSocket.emit("broadcastData", json);
    }
}
