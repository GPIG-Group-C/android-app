package uk.co.mattjktaylor.gpig;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Arrays;

public class MapTransparentPolygon extends MapPolygon {

    public MapTransparentPolygon(String ID, ArrayList<LatLng> coords)
    {
        super(ID, coords,  null);
    }

    @Override
    public PolygonOptions getPolygonOptions()
    {
        PolygonOptions po = new PolygonOptions();
        po.add(coords.toArray(new LatLng[coords.size()]));
        po.strokePattern(Arrays.<PatternItem>asList(new Dash(15), new Gap(10)));
        po.strokeWidth(4);
        po.clickable(false);
        return po;
    }
}
