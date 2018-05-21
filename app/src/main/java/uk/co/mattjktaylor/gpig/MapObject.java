package uk.co.mattjktaylor.gpig;

public class MapObject {

    private String ID;
    private String type;
    private MapDescription description;

    public MapObject(){}

    public MapObject(String ID, String type, MapDescription description)
    {
        this.ID = ID;
        this.type = type;
        this.description = description;
    }

    public String getID() {
        return ID;
    }

    public String getType() {
        return type;
    }

    public MapDescription getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MapObject))
            return false;

        MapObject mapObject = (MapObject) o;
        if(mapObject.getID().equals(ID))
            return true;
        else
            return false;
    }

}
