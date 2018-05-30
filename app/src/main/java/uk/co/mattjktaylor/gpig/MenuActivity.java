package uk.co.mattjktaylor.gpig;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MenuActivity extends AppCompatActivity {

    private static MapFragment mapFragment;
    public static SlidingUpPanelLayout panel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if (mapFragment == null)
        {
            mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
            panel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GPIG Group C");

        ServerClient.init(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(!Config.isConnected(this))
        {
            ServerClient.toast("No Internet Connection Detected", this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add)
            return true;

        switch (item.getItemId()) {
            case R.id.action_add_circle:
                mapFragment.addCircle(new MapCircle(UUID.randomUUID().toString(),"fire", 37.75961, -122.4269, 2500, Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_add_marker:
                MapDescription desc = new MapDescription(new MapDescription.Incident(1, "First Responder", "...", false, false)
                        , null, null, Calendar.getInstance().getTimeInMillis());
                mapFragment.addMarker(new MapMarker(UUID.randomUUID().toString(), "gas", 37.72961, -122.4269,
                        "Title 0", desc));
                return true;

            case R.id.action_location:
                mapFragment.centerMap();
                return true;

            case R.id.action_add_heatmap:
                for (int i=0; i<10; i++){
                    double random = - 0.05 + Math.random() * (0.05 - (- 0.05));
                    double intensity = 2 + Math.random() * (10 - 1);
                    mapFragment.addHeatMap(new MapHeatMap(UUID.randomUUID().toString(), 37.74961 + random, -122.4169 +random, 40, intensity, Calendar.getInstance().getTimeInMillis()));
                }
                return true;

            case R.id.action_add_polygon:
                ArrayList<LatLng> coords = new ArrayList<>();
                coords.add( new LatLng(37.74961, -122.4169));
                coords.add( new LatLng(37.76961, -122.4369));
                coords.add( new LatLng(37.73961, -122.4269));

                MapDescription.Utility util = new MapDescription.Utility(true, false, false, true);
                MapDescription.AreaInfo bInfo = new MapDescription.AreaInfo(10, 100,"Address", "Apartment Block", 1995);
                MapDescription.Incident incident = new MapDescription.Incident(0, "First Responder", "...", false, false);

                MapDescription descr = new MapDescription(incident, util, bInfo, Calendar.getInstance().getTimeInMillis());
                mapFragment.addPolygon(new MapPolygon(UUID.randomUUID().toString(), coords, descr));

                coords.clear();
                coords.add( new LatLng(37.71961, -122.4069));
                coords.add( new LatLng(37.72961, -122.4069));
                coords.add( new LatLng(37.72961, -122.4369));
                coords.add( new LatLng(37.71961, -122.4369));
                mapFragment.addTransparentPolygon(new MapTransparentPolygon(UUID.randomUUID().toString(), "#b0279c", coords));

                coords.clear();
                coords.add( new LatLng(37.75961, -122.4069));
                coords.add( new LatLng(37.77961, -122.4069));
                coords.add( new LatLng(37.77961, -122.4369));
                coords.add( new LatLng(37.75961, -122.4369));

                MapPolygon p = new MapPolygon(UUID.randomUUID().toString(), coords, descr);
                mapFragment.addPolygon(p);
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0)
        {
            String title = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
            getSupportActionBar().setTitle(title);
            fm.popBackStack();
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Exit application")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            MenuActivity.super.onBackPressed();
                        }
                    }).create().show();
        }
    }
}
