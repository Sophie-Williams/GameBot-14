package com.cpjd.models;

public enum Suit {
    SPADES,DIAMONDS,HEARTS,CLUBS;

    @Override
    public String toString() {
        if(this == SPADES) return "S";
        else if(this == DIAMONDS) return "D";
        else if(this == HEARTS) return "H";
        else return "C";
    }
}
