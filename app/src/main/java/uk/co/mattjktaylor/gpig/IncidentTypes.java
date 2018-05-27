package uk.co.mattjktaylor.gpig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IncidentTypes{

    final static String GAS = "gas";
    final static String FIRE = "fire";
    final static String BLOCKED_ROAD = "blocked";
    final static String MEDIC_NEEDED = "medic";
    final static String EARTHQUAKE = "earthquake";
    final static String COLLAPSE = "collapse";
    final static String WATER_LEAK = "water";
    final static String ELECTRICITY_FAULT = "electricity";
    final static String FIRE_STATION = "fire_station";

    private final static HashMap<String, IncidentType> types;
    static
    {
        types = new HashMap<String, IncidentType>();
        types.put(GAS, new IncidentType(GAS, "Gas Leak", R.drawable.ic_gas_pipe, Arrays.<String>asList("Gas Contained", "Gas Leaking"), true));
        types.put(FIRE, new IncidentType(FIRE, "Fire", R.drawable.ic_fire, Arrays.<String>asList("Fire Extinguished", "Fire Contained", "Fire Escalating"), true));
        types.put(BLOCKED_ROAD, new IncidentType(BLOCKED_ROAD, "Blocked Road", R.drawable.ic_blocked, Arrays.<String>asList("Road Cleared", "Road Blocked"), true));
        types.put(MEDIC_NEEDED, new IncidentType(MEDIC_NEEDED, "Medic Needed", R.drawable.ic_medic, Arrays.<String>asList("N/A"), true));
        types.put(EARTHQUAKE, new IncidentType(EARTHQUAKE, "Earthquake", R.drawable.ic_earthquake, Arrays.<String>asList("N/A"), false));
        types.put(COLLAPSE, new IncidentType(COLLAPSE, "Collapsed Building", R.drawable.ic_collapse, Arrays.<String>asList("N/A"), true));
        types.put(WATER_LEAK, new IncidentType(WATER_LEAK, "Water Leak", R.drawable.ic_water, Arrays.<String>asList("Water Contained", " Water Leaking"), true));
        types.put(ELECTRICITY_FAULT, new IncidentType(ELECTRICITY_FAULT, "Electricity Fault", R.drawable.ic_electricity, Arrays.<String>asList("N/A"), true));
        types.put(FIRE_STATION, new IncidentType(FIRE_STATION, "Fire Station", R.drawable.ic_fire_station, Arrays.<String>asList("N/A"), false));
    }

    public static IncidentType getIncidentType(String ID)
    {
        return types.get(ID);
    }

    public static ArrayList<String> getTypes()
    {
        return new ArrayList<String>(types.keySet());
    }

    public static IncidentType getTypeFromDesc(String desc)
    {
        for(IncidentType incidentType : types.values())
        {
            if(incidentType.getDescription().equals(desc))
                return incidentType;
        }
        return null;
    }

    public static List<String> getDescriptons()
    {
        List<String> descriptions = new ArrayList<String>();
        for(IncidentType incidentType : types.values())
        {
            if(incidentType.isClickable())
                descriptions.add(incidentType.getDescription());
        }
        return descriptions;
    }

    public static class IncidentType {

        private final String ID;
        private final String description;
        private final int icon;
        private final List<String> status;
        private final boolean clickable;

        public IncidentType(String ID, String description, int icon, List<String> status, boolean clickable)
        {
            this.ID = ID;
            this.description = description;
            this.icon = icon;
            this.status = status;
            this.clickable = clickable;
        }

        public String getID()
        {
            return ID;
        }

        public String getDescription()
        {
            return description;
        }

        public int getIcon()
        {
            return icon;
        }

        public List<String> getStatus()
        {
            return status;
        }

        public boolean isClickable()
        {
            return clickable;
        }
    }
}


