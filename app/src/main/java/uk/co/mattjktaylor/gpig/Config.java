package uk.co.mattjktaylor.gpig;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Config {

    private Config(){}

    public static final String PREFS_NAME = "GpigData";

    public static boolean isConnected(Context mContext)
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getIpUrl(Context mContext)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return String.format("http://%s", getIP(mContext));
    }

    public static String getIP(Context mContext)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString("IP_ADDRESS", "");
    }

    public static String getPort(Context mContext)
    {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString("PORT", "");
    }

    public static Boolean isValidIP(String url)
    {
        return Patterns.IP_ADDRESS.matcher(url).matches();
    }

    // getDate(82233213123L, "dd/MM/yyyy hh:mm:ss")
    public static String getFormattedDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static void log(String message)
    {
        if(message != null && !message.isEmpty())
            Log.e("yt", message);
    }
}
