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

    private final static HashMap<String, IncidentType> types;
    static
    {
        types = new HashMap<String, IncidentType>();
        types.put(GAS, new IncidentType(GAS, "Gas Leak", R.drawable.ic_gas_pipe, Arrays.<String>asList("Gas Contained", "Gas Leaking")));
        types.put(FIRE, new IncidentType(FIRE, "Fire", R.drawable.ic_fire, Arrays.<String>asList("Fire Extinguished", "Fire Contained", "Fire Escalating")));
        types.put(BLOCKED_ROAD, new IncidentType(BLOCKED_ROAD, "Blocked Road", R.drawable.ic_blocked, Arrays.<String>asList("Road Cleared", "Road Blocked")));
        types.put(MEDIC_NEEDED, new IncidentType(MEDIC_NEEDED, "Medic Needed", R.drawable.ic_medic, Arrays.<String>asList("N/A")));
        types.put(EARTHQUAKE, new IncidentType(EARTHQUAKE, "Earthquake", R.drawable.ic_earthquake, Arrays.<String>asList("N/A")));
        types.put(COLLAPSE, new IncidentType(COLLAPSE, "Collapsed Building", R.drawable.ic_collapse, Arrays.<String>asList("N/A")));
        types.put(WATER_LEAK, new IncidentType(WATER_LEAK, "Water Leak", R.drawable.ic_water, Arrays.<String>asList("Water Contained", " Water Leaking")));
        types.put(ELECTRICITY_FAULT, new IncidentType(ELECTRICITY_FAULT, "Electricity Fault", R.drawable.ic_electricity, Arrays.<String>asList("N/A")));
    }

    public static IncidentType getIncidentType(String ID)
    {
        return types.get(ID);
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
            descriptions.add(incidentType.getDescription());
        }
        return descriptions;
    }

    public static class IncidentType {

        private final String ID;
        private final String description;
        private final int icon;
        private final List<String> status;

        public IncidentType(String ID, String description, int icon, List<String> status)
        {
            this.ID = ID;
            this.description = description;
            this.icon = icon;
            this.status = status;
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
    }
}


