package uk.co.mattjktaylor.gpig;

public interface OnNotificationListener {
    public void addMarker(String ID, int type, double lat, double lon, String desc, Long dateRecorded);
    public void addCircle(String ID, double lat, double lon, double radius, String desc, Long dateRecorded);
}
