package uk.co.mattjktaylor.gpig;

public class MapDescription {

    private int numPeople;
    private String address;
    private int status;
    private Utility utilities;
    private BuildingInfo buildingInfo;
    private String reportBy;
    private String info;

    public MapDescription() {}

    public MapDescription(int numPeople,
                          String address,
                          int status,
                          Utility utilities,
                          BuildingInfo buildingInfo,
                          String reportBy,
                          String info) {
        this.numPeople = numPeople;
        this.address = address;
        this.status = status;
        this.utilities = utilities;
        this.buildingInfo = buildingInfo;
        this.reportBy = reportBy;
        this.info = info;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public String getAddress() {
        return address;
    }

    public int getStatus() {
        return status;
    }

    public Utility getUtilities() {
        return utilities;
    }

    public BuildingInfo getBuildingInfo() {
        return buildingInfo;
    }

    public String getReportBy() {
        return reportBy;
    }

    public String getInfo() {
        return info;
    }

    public static class Utility {

        private boolean gas;
        private boolean electricity;
        private boolean water;
        private boolean sewage;

        public Utility() {}

        public Utility(boolean gas,
                       boolean electricity,
                       boolean water,
                       boolean sewage) {
            this.gas = gas;
            this.electricity = electricity;
            this.water = water;
            this.sewage = sewage;
        }

        public boolean isGas() {
            return gas;
        }

        public boolean isElectricity() {
            return electricity;
        }

        public boolean isWater() {
            return water;
        }

        public boolean isSewage() {
            return sewage;
        }
    }

    public static class BuildingInfo {

        private String type;
        private String year;

        public BuildingInfo() {}

        public BuildingInfo(String type, String year) {
            this.type = type;
            this.year = year;
        }

        public String getType() {
            return type;
        }

        public String getYear() {
            return year;
        }
    }
}
