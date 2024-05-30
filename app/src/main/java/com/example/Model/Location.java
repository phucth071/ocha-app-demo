package com.example.Model;

public class Location {
    private int ID;
    private String Loc;

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return Loc;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLoc() {
        return Loc;
    }

    public void setLoc(String loc) {
        Loc = loc;
    }

    public Location(){}
}
