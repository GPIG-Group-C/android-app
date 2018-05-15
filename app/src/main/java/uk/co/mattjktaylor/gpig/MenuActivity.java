package uk.co.mattjktaylor.gpig;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;

public class MenuActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private SettingsFragment settingsFragment;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if (findViewById(R.id.nav_framelayout) != null)
        {
            if (savedInstanceState != null)
                return;
            else
            {
                settingsFragment = new SettingsFragment();
                settingsFragment.setArguments(getIntent().getExtras());
                mapFragment = new MapFragment();
                mapFragment.setArguments(getIntent().getExtras());

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.nav_framelayout, mapFragment).commit();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Live Map");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ServerClient.init(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(!Config.isConnected(this))
        {
            ServerClient.toast("No Internet Connection Detected");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(getSupportActionBar().getTitle().toString());

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_map)
        {
            getSupportActionBar().setTitle("Live Map");
            transaction.replace(R.id.nav_framelayout, mapFragment);
            transaction.commit();
        }
        else if(id == R.id.nav_settings)
        {
            getSupportActionBar().setTitle("Settings");
            transaction.replace(R.id.nav_framelayout, settingsFragment);
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
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
}
