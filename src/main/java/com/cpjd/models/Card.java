package com.cpjd.models;

public class Card {

    public enum SUIT {
        SPADES,DIAMONDS,HEARTS,CLUBS;
    }

    public enum NUMBER {
        ACE,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING;
    }

    private SUIT suit;

    private NUMBER number;

    public Card(SUIT suit, NUMBER number) {
        this.suit = suit;
        this.number = number;
    }

    public SUIT getSuit() {
        return suit;
    }

    public void setSuit(SUIT suit) {
        this.suit = suit;
    }

    public NUMBER getNumber() {
        return number;
    }

    public void setNumber(NUMBER number) {
        this.number = number;
    }
}
