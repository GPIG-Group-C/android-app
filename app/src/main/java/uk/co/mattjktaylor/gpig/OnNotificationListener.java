package uk.co.mattjktaylor.gpig;

public interface OnNotificationListener {
    public void addMarker(double lat, double lon);
    public void addCircle(double lat, double lon, double radius);
}
