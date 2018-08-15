package com.cpjd.models;

import lombok.Getter;

public enum Number {
    ACE("A", 14),TWO("2", 2),THREE("3", 3),FOUR("4", 4),FIVE("5", 5),SIX("6", 6),SEVEN("7", 7),EIGHT("8", 8),NINE("9", 9),TEN("10", 10),
    JACK("J", 11),QUEEN("Q", 12),KING("K", 13);

    @Getter
    private String ID;

    @Getter
    private int numerical;

    Number(String ID, int numerical) {
        this.ID = ID; this.numerical = numerical;
    }

    public int getNumericalAceLow() {
        if(this == ACE) return 1;
        else return getNumerical();
    }

    @Override
    public String toString() {
        return ID;
    }
}
