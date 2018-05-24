package uk.co.mattjktaylor.gpig;

public interface OnNotificationListener {
    public void addCircle(MapCircle c);
    public void addMarker(MapMarker m);
    public void addHeatMap(MapHeatMap h);
    public void addPolygon(MapPolygon p);
    public void addTransparentPolygon(MapTransparentPolygon p);
    public void onListUpdated();
}
