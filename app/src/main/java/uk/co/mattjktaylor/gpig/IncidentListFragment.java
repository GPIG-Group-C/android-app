package uk.co.mattjktaylor.gpig;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IncidentListFragment extends ListFragment implements OnNotificationListener{

    private IncidentAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new IncidentAdapter(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        MenuActivity.panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        MapObject mapObject = IncidentAdapter.getMapObjects().get(position);
        if(mapObject instanceof MapPolygon)
        {
            MapPolygon p = (MapPolygon) mapObject;
            p.getMarker().showInfoWindow();
        }
        else if(mapObject instanceof MapMarker)
        {
            MapMarker m = (MapMarker) mapObject;
            m.getMarker().showInfoWindow();
        }
        else
            Toast.makeText(getActivity(), "Item clicked...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        NotificationSocketListener.addNotificationListener(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        NotificationSocketListener.removeNotificationListener(this);
    }

    @Override
    public void addCircle(MapCircle c) { }

    @Override
    public void addMarker(MapMarker m) { }

    @Override
    public void addHeatMap(MapHeatMap h) { }

    @Override
    public void addPolygon(MapPolygon p) { }

    @Override
    public void addTransparentPolygon(MapTransparentPolygon p) { }

    @Override
    public void onListUpdated() {
        updateList();
    }

    private void updateList()
    {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static class IncidentAdapter extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater;

        public IncidentAdapter(Context context)
        {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        public static List<MapObject> getMapObjects()
        {
            ArrayList<MapObject> nonPolygons = new ArrayList<>();
            for(MapObject o : MapFragment.mapObjects)
            {
                if(o instanceof MapMarker)
                {
                    if (IncidentTypes.getIncidentType(o.getType()).isClickable())
                        nonPolygons.add(o);
                }
            }
            return nonPolygons;
        }

        @Override
        public int getCount() {
            return getMapObjects().size();
        }

        @Override
        public MapObject getItem(int i) {
            return getMapObjects().get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if(view == null)
            {
                view = inflater.inflate(R.layout.incident_list_item, viewGroup, false);
            }

            MapObject mapObject = getMapObjects().get(i);
            if(!IncidentTypes.getTypes().contains(mapObject.getType()))
                return null;

            ImageView icon = (ImageView) view.findViewById(R.id.incident_icon);
            icon.setImageResource(IncidentTypes.getIncidentType(mapObject.getType()).getIcon());

            TextView title = (TextView) view.findViewById(R.id.incident_title);
            title.setText(IncidentTypes.getIncidentType(mapObject.getType()).getDescription());

            if(mapObject.getDescription() != null && mapObject.getDescription().getIncident() != null)
            {
                TextView reportedBy = (TextView) view.findViewById(R.id.incident_reportedBy);
                reportedBy.setText(mapObject.getDescription().getIncident().getReportBy());
                TextView incidentTime = (TextView) view.findViewById(R.id.incident_time);
                incidentTime.setText(Config.getFormattedDate(mapObject.getDescription().getDateAdded(), "dd/MM/yy HH:mm:ss"));

                TextView incidentSeverity = (TextView) view.findViewById(R.id.incident_severity);
                String status = IncidentTypes.getIncidentType(mapObject.getType()).getStatus().get(mapObject.getDescription().getIncident().getStatus());
                incidentSeverity.setText(String.format(Locale.ENGLISH, "Status: %s", status));
            }
            return view;
        }
    }
}
