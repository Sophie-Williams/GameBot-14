package com.cpjd.models;

import lombok.Data;
import net.dv8tion.jda.core.entities.Member;

@Data
public class Player {

    /*
     * Characteristics
     */

    private Member member;
    // Winnings (gameBank) are added to bank after "end" is executed


    private double bank;

    /*
     * Game only
     */

    private boolean leaveRequested;
    private double gameBank;
    private double wager;
    private double cardCycleBet;
    private boolean folded;

    private Card card1, card2;

    public Player(Member member, double bank) {
        this.member = member;
        this.bank = bank;
    }

    /*
     *
     * Methods
     *
     */

    /**
     * Deals the member a hand of 2 cards. Also resets the round.
     * @param card1 the player's first card
     * @param card2 the player's second card
     */
    public void dealHand(Card card1, Card card2) {
        folded = false;
        wager = 0;

        this.card1 = card1;
        this.card2 = card2;
    }

    /**
     * Deposits money into the player's game bank account
     * @param amount the money to deposit
     */
    public void deposit(double amount) {
        if(amount < 0) return;

        gameBank += amount;
    }

    /**
     * Withdraws the specified amount from the player's game account, if the member
     * doesn't have enough money to meet the withdraw, all of their money is withdrawn.
     * @param amount the amount to withdraw
     * @return the amount successfully drawn from the account
     */
    public double withdraw(double amount) {
        if(gameBank == 0) return 0;

        if(gameBank - amount < 0) amount = gameBank;

        gameBank -= amount;

        return amount;
    }

    /**
     * Wagers the specified amount, if the specified amount is greater than the game bank, the entire
     * game bank is wagered.
     * @param amount the amount to wager
     */
    public double wager(double amount) {
        if(folded) throw new RuntimeException("User attempted to wager while already folded.");

        if(wager > gameBank) {
            wager += gameBank;
            return gameBank;
        }
        else  {
            wager += amount;
            return amount;
        }
    }

    public void transfer() {
        bank += gameBank;
        gameBank = 0;
    }

    public void leave() {
        folded = true;
        leaveRequested = true;
    }

    public boolean isAllIn() {
        return wager == gameBank;
    }

    /*
     * Transfers money from the player's reserve account to their game account.
     * @param amount the amount to transfer, if more than the bank, the entire bank is then just transferred
     *
    public void transfer(double amount) {
        if(bank == 0) return;

        if(bank - amount < 0) amount = bank;

        deposit(amount);
    }*/

    public boolean matchesMember(Member member) {
        return this.member.getUser().getId().equals(member.getUser().getId());
    }

    /*
     *
     * s & s
     *
     */
    public double getBank() {
        return round(bank, 2);
    }

    public double getGameBank() {
        return round(gameBank, 2);
    }

    public double getWager() { return round(wager, 2); }

    /**
     * Rounds a decimal to the specified number of digits (right of decimal point)
     * @param value the value to round
     * @param precision the amount of digits to keep
     * @return rounded double
     */
    private double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
