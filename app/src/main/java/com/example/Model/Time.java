package com.example.Model;

public class Time {
    private int ID;

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return Value;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    private String Value;
    public Time(){}
}
