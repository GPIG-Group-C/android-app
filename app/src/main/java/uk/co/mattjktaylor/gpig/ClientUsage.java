package uk.co.mattjktaylor.gpig;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClientUsage {

    public static void getPlaylistItems(final OnKodiResponse<String> listener)
    {
        // {"jsonrpc": "2.0", "method": "Playlist.GetItems", "params": { "properties": [ "file"], "playlistid": 1}, "id": 1}
        mClient.postAsynch("{'jsonrpc':'2.0', 'id':1, 'method': 'Playlist.GetItems', 'params':{'properties': ['file'], 'playlistid':1}}",  new HttpCallback(){

            @Override
            public void onSuccess(String response)
            {
                try
                {
                    JSONObject json = new JSONObject(response);
                    if(!json.isNull("result"))
                    {
                        List<String> videoIDs = new ArrayList<String>();
                        JSONObject result = json.getJSONObject("result");
                        if(!result.isNull("items"))
                        {
                            JSONArray items = result.getJSONArray("items");
                            for(int i = 0; i < items.length(); i++)
                            {
                                JSONObject o = items.getJSONObject(i);
                                Uri pluginUrl = Uri.parse(o.getString("file"));

                                String video_id = pluginUrl.getQueryParameter("video_id");
                                if(video_id == null)
                                    video_id = pluginUrl.getQueryParameter("videoid");

                                videoIDs.add(video_id);
                            }
                            listener.onSuccess(videoIDs.toArray(new String[videoIDs.size()]));
                        }
                        else
                        {
                            listener.onSuccess();
                        }
                    }
                    else
                        Config.log("No result response...");
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    // ========== CONTROLS =====================

    public static void shutdown()
    {
        // {'jsonrpc':'2.0','method':'System.Shutdown','id':1}
        mClient.postAsynch("{'jsonrpc':'2.0','method':'System.Shutdown','id':1}");
    }

    public static void changeVolume(boolean increase)
    {
        String direction = "decrement";
        if(increase)
            direction = "increment";

        mClient.postAsynch(String.format("{'jsonrpc':'2.0','method':'Application.SetVolume','params':{'volume':'%s'},'id':1}", direction));
    }

    public static void setMute(boolean mute)
    {
        mClient.postAsynch(String.format("{'jsonrpc':'2.0','method':'Application.SetMute','params':[%b],'id':1}", mute));
    }
}
