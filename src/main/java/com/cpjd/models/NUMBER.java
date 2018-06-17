package com.cpjd.models;

import lombok.Getter;

public enum NUMBER {
    ACE("A"),TWO("2"),THREE("3"),FOUR("4"),FIVE("5"),SIX("6"),SEVEN("7"),EIGHT("8"),NINE("9"),TEN("10"),JACK("J"),QUEEN("Q"),KING("K");

    @Getter
    private String ID;

    NUMBER(String ID) {
        this.ID = ID;
    }
}
