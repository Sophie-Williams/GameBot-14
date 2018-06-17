package com.cpjd.models;

import lombok.Getter;

public enum NUMBER {
    ACE("A", 1),TWO("2", 2),THREE("3", 3),FOUR("4", 4),FIVE("5", 5),SIX("6", 6),SEVEN("7", 7),EIGHT("8", 8),NINE("9", 9),TEN("10", 10),
    JACK("J", 11),QUEEN("Q", 12),KING("K", 13);

    @Getter
    private String ID;

    private int numerical;

    NUMBER(String ID, int numerical) {
        this.ID = ID; this.numerical = numerical;
    }

    public int getNumerical(boolean aceHigh) {
        if(this == ACE) {
            return aceHigh ? 14 : 1;
        } else return numerical;
    }

    @Override
    public String toString() {
        return ID;
    }
}
