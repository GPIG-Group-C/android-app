package uk.co.mattjktaylor.gpig;

public class MapDescription {

    private Utility utilities;
    private AreaInfo areaInfo;
    private Incident incident;
    private Long dateAdded;

    public MapDescription(){ }

    public MapDescription(Incident incident,
                          Utility utilities,
                          AreaInfo areaInfo,
                          Long dateAdded)
    {
        this.incident = incident;
        this.utilities = utilities;
        this.areaInfo = areaInfo;
        this.dateAdded = dateAdded;
    }

    public void setDateAdded(Long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Incident getIncident() {
        return incident;
    }

    public Utility getUtilities() {
        return utilities;
    }

    public AreaInfo getAreaInfo() {
        return areaInfo;
    }

    public Long getDateAdded() {
        return dateAdded;
    }

    public static class Incident{
        private int status;
        private String reportBy;
        private String info;
        private boolean peopleDanger;
        private boolean medicNeeded;

        public Incident() {}

        public Incident(int status, String reportBy, String info, boolean peopleDanger, boolean medicNeeded) {
            this.status = status;
            this.reportBy = reportBy;
            this.info = info;
            this.peopleDanger = peopleDanger;
            this.medicNeeded = medicNeeded;
        }

        public int getStatus() {
            return status;
        }

        public String getReportBy() {
            return reportBy;
        }

        public String getInfo() {
            return info;
        }

        public boolean isPeopleDanger() {
            return peopleDanger;
        }

        public boolean isMedicNeeded() {
            return medicNeeded;
        }

        public void setReportBy(String reportBy) {
            this.reportBy = reportBy;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setPeopleDanger(boolean peopleDanger) {
            this.peopleDanger = peopleDanger;
        }

        public void setMedicNeeded(boolean medicNeeded) {
            this.medicNeeded = medicNeeded;
        }
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

        private int severity;
        private int numPeople;
        private String type;
        private int year;
        private String address;

        public AreaInfo() {}

        public AreaInfo(int severity, int numPeople, String address, String type, int year) {
            this.severity = severity;
            this.numPeople = numPeople;
            this.address = address;
            this.type = type;
            this.year = year;
        }

        public int getSeverity()
        {
            return severity;
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

        public int getNumPeople()
        {
            return numPeople;
        }

    }
}
