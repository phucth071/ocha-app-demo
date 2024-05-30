package com.example.Model;

public class Price {
    private int ID;
    private String Value;
    public Price(){}

    @Override
    public String toString() {
        return Value;
    }

    public int getID() {
        return ID;
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
}
