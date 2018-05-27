package uk.co.mattjktaylor.gpig;

import android.graphics.Color;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Arrays;

public class MapTransparentPolygon extends MapPolygon {

    private String colour;

    public MapTransparentPolygon(String ID, String colour, ArrayList<LatLng> coords)
    {
        super(ID, coords,  null);
        this.colour = colour;
    }

    @Override
    public PolygonOptions getPolygonOptions()
    {
        PolygonOptions po = new PolygonOptions();
        po.add(coords.toArray(new LatLng[coords.size()]));
        po.strokePattern(Arrays.<PatternItem>asList(new Dash(15), new Gap(10)));
        po.strokeColor(Color.parseColor(colour));
        po.strokeWidth(4);
        po.clickable(false);
        return po;
    }
}
