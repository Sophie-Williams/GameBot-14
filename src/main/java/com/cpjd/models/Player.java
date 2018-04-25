package com.cpjd.models;

public class Player {

    private String discordID;

    private Card one, two;

    private double bank;

    // stores if this player has folded yet
    private boolean folded;

    // stores the player's current bet
    private double wager;

    public Player(String discordID) {
        this.discordID = discordID;
        bank = 0;
    }

    // Resets variables for a NEW ROUND
    public void reset() {
        folded = false;
        wager = 0;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public double getWager() {
        return wager;
    }

    public void setWager(double wager) {
        this.wager = wager;
    }

    public String getDiscordID() {
        return discordID;
    }

    public void setDiscordID(String discordID) {
        this.discordID = discordID;
    }

    public Card getOne() {
        return one;
    }

    public void setOne(Card one) {
        this.one = one;
    }

    public Card getTwo() {
        return two;
    }

    public void setTwo(Card two) {
        this.two = two;
    }

    public double getBank() {
        return bank;
    }

    public void setBank(double bank) {
        this.bank = bank;
    }
}
