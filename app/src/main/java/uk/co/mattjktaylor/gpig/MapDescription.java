package uk.co.mattjktaylor.gpig;

public class MapDescription {

    private int numPeople;
    private String address;
    private int status;
    private Utility utilities;
    private BuildingInfo buildingInfo;
    private String reportBy;
    private String info;

    private Long dateAdded;

    public MapDescription() {}

    public MapDescription(int numPeople,
                          String address,
                          int status,
                          Utility utilities,
                          BuildingInfo buildingInfo,
                          String reportBy,
                          String info,
                          Long dateAdded) {
        this.numPeople = numPeople;
        this.address = address;
        this.status = status;
        this.utilities = utilities;
        this.buildingInfo = buildingInfo;
        this.reportBy = reportBy;
        this.info = info;
        this.dateAdded = dateAdded;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUtilities(Utility utilities) {
        this.utilities = utilities;
    }

    public void setReportBy(String reportBy) {
        this.reportBy = reportBy;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setDateAdded(Long dateAdded) {
        this.dateAdded = dateAdded;
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

    public Long getDateAdded() {
        return dateAdded;
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
        private int year;

        public BuildingInfo() {}

        public BuildingInfo(String type, int year) {
            this.type = type;
            this.year = year;
        }

        public String getType() {
            return type;
        }

        public int getYear() {
            return year;
        }
    }
}
