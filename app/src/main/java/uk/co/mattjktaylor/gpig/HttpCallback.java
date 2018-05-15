package uk.co.mattjktaylor.gpig;

import android.os.Handler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class HttpCallback implements Callback {

    public interface ConnectionTimeoutListener {
        public void onTimeout();
    }

    private static ConnectionTimeoutListener listener;
    public static void setListener(ConnectionTimeoutListener l)
    {
        listener = l;
    }

    @Override
    public void onResponse(Call call, Response response)
    {
        try
        {
            if(!response.isSuccessful())
            {
                Config.log("HttpCallback Failed: " + response.code());
                return;
            }

            final String json = response.body().string();
            Handler mainHandler = new Handler(ServerClient.getContext().getMainLooper());
            mainHandler.post(new Runnable() {

                @Override
                public void run()
                {
                    onSuccess(json);;
                }
            });
        }
        catch (IOException  e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call call, IOException e)
    {
        if(e instanceof ConnectException || e instanceof NoRouteToHostException || e instanceof SocketTimeoutException)
        {
            if(listener != null)
            {
                listener.onTimeout();
            }
        }
        else
        {
            Config.log("HttpCallback onFailure...");
            e.printStackTrace();
        }
    }

    public abstract void onSuccess(String response);
}
