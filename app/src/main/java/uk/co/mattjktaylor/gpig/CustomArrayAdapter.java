package uk.co.mattjktaylor.gpig;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String>{

    public CustomArrayAdapter(Context context, int textViewResourceId, String[] objects)
    {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View v = null;
        if (position == 0)
        {
            TextView tv = new TextView(getContext());
            tv.setVisibility(View.GONE);
            tv.setHeight(0);
            v = tv;
        }
        else
        {
            v = super.getDropDownView(position, null, parent);
            v.setPadding(7, 13, 7, 13);
        }
        return v;
    }
}
