package uk.co.mattjktaylor.gpig;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId)
    {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void log(String message)
    {
        if(message != null && !message.isEmpty())
            Log.e("yt", message);
    }
}
