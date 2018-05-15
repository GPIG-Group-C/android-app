package uk.co.mattjktaylor.gpig;

import android.content.Context;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class mClient{

    private static Context context;
    private static OkHttpClient client;
    private static Socket webSocket;
    private static Timer timer;

    public static void init(final Context mContext)
    {
        context = mContext;
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        startWebSocket();
    }

    public static void startWebSocket()
    {
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

    public static void postAsynch(String json)
    {
        postAsynch(json, new HttpCallback() {

            public void onSuccess(String response)
            {
                Config.log("Success: " + response);
            }
        });
    }

    public static void postAsynch(String json, Callback callback)
    {
        postAsynch(json, callback, true);
    }

    public static void postAsynch(String json, Callback callback, boolean isCancellable)
    {
        if(Config.getHttpURL(context).isEmpty())
            return;

        json = json.replace("\'", "\"");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request;

        if(isCancellable)
        {
             request = new Request.Builder()
                    .url(Config.getHttpURL(context))
                    .post(body)
                    .build();
        }
        else
        {
             request = new Request.Builder()
                    .url(Config.getHttpURL(context))
                    .post(body).tag("ws")
                    .build();
        }

        client.newCall(request).enqueue(callback);
    }

    public static void cancelRequests()
    {
        for (Call call : client.dispatcher().queuedCalls())
        {
            if(!call.request().tag().toString().equals("ws"))
            {
                call.cancel();
            }
        }

        for (Call call : client.dispatcher().runningCalls())
        {
            if(!call.request().tag().toString().equals("ws"))
            {
                call.cancel();
            }
        }
    }
}
