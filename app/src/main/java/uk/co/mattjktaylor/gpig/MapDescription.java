package uk.co.mattjktaylor.gpig;

public class MapDescription {

    private int numPeople;
    private int status;
    private Utility utilities;
    private AreaInfo areaInfo;
    private String reportBy;
    private String info;

    private Long dateAdded;

    public MapDescription() {}

    public MapDescription(int numPeople,
                          int status,
                          Utility utilities,
                          AreaInfo areaInfo,
                          String reportBy,
                          String info,
                          Long dateAdded) {
        this.numPeople = numPeople;
        this.status = status;
        this.utilities = utilities;
        this.areaInfo = areaInfo;
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

    public int getStatus() {
        return status;
    }

    public Utility getUtilities() {
        return utilities;
    }

    public AreaInfo getAreaInfo() {
        return areaInfo;
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

    public static class AreaInfo {

        private String type;
        private int year;
        private String address;

        public AreaInfo() {}

        public AreaInfo(String address, String type, int year) {
            this.address = address;
            this.type = type;
            this.year = year;
        }

        public String getType() {
            return type;
        }

        public int getYear() {
            return year;
        }

        public String getAddress(){
            return address;
        }
    }
}
