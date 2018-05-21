package uk.co.mattjktaylor.gpig;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
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
                mapFragment.addCircle(new MapCircle(UUID.randomUUID().toString(),1, 37.75961, -122.4269, 2500, Calendar.getInstance().getTimeInMillis()));
                return true;

            case R.id.action_add_marker:
                MapDescription desc = new MapDescription(50, "N/A", 0, null, null,
                        null, "Test", Calendar.getInstance().getTimeInMillis());
                mapFragment.addMarker(new MapMarker(UUID.randomUUID().toString(), 0, 37.72961, -122.4269,
                        "Title 0", desc));
                mapFragment.addMarker(new MapMarker(UUID.randomUUID().toString(), 1, 37.75961, -122.4269,
                        "Title 1", desc));
                mapFragment.addMarker(new MapMarker(UUID.randomUUID().toString(), 2, 37.76961, -122.4269,
                        "Title 2", desc));
                mapFragment.addMarker(new MapMarker(UUID.randomUUID().toString(), 3, 37.77961, -122.4269,
                        "Title 3", desc));
                mapFragment.addMarker(new MapMarker(UUID.randomUUID().toString(), 4, 37.78961, -122.4269,
                        "Title 4", desc));
                mapFragment.addMarker(new MapMarker(UUID.randomUUID().toString(), 5, 37.79961, -122.4269,
                        "Title 5", desc));
                mapFragment.addMarker(new MapMarker(UUID.randomUUID().toString(), 6, 37.74961, -122.4269,
                        "Title 6", desc));
                mapFragment.addMarker(new MapMarker(UUID.randomUUID().toString(), 7, 37.73961, -122.4269,
                        "Title 7", desc));
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
                MapDescription.BuildingInfo bInfo = new MapDescription.BuildingInfo("Apartment Block", 1995);
                MapDescription descr = new MapDescription(500, "Address", 1, util, bInfo, "First Responder","Testing", Calendar.getInstance().getTimeInMillis());
                mapFragment.addPolygon(new MapPolygon(UUID.randomUUID().toString(), 1, 0, coords, descr));

                coords.clear();
                coords.add( new LatLng(37.71961, -122.4069));
                coords.add( new LatLng(37.72961, -122.4069));
                coords.add( new LatLng(37.72961, -122.4369));
                coords.add( new LatLng(37.71961, -122.4369));
                mapFragment.addPolygon(new MapPolygon(UUID.randomUUID().toString(), 2, 1, coords, descr));

                coords.clear();
                coords.add( new LatLng(37.75961, -122.4069));
                coords.add( new LatLng(37.77961, -122.4069));
                coords.add( new LatLng(37.77961, -122.4369));
                coords.add( new LatLng(37.75961, -122.4369));
                mapFragment.addPolygon(new MapPolygon(UUID.randomUUID().toString(), 3, 2,coords, descr));

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
