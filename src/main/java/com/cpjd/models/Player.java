package com.cpjd.models;

import com.cpjd.poker.HandValue;
import lombok.Data;
import net.dv8tion.jda.core.entities.Member;

@Data
public class Player implements Comparable<Player> {

    /*
     * Characteristics
     */

    private Member member; // the Discord object to identify and communicate with this player

    private double bank; // this probably won't be used, it gets saved to the file system to save progress if it's something we decide to use

    /*
     * Game only
     */

    private boolean leaveRequested; // if the player wants to leave the game
    private double gameBank; // how much money the player has
    private double wager; // total money bet throughout the current round of poker
    private double cardCycleBet; // money throughout one turn of betting
    private boolean folded; // if the player is folded

    private Card card1, card2; // the cards the player has

    private HandValue value; // a placeholder to hold hand value information for game evaluation

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

    @Override
    public int compareTo(Player o) {
        return value.compareTo(o.getValue());
    }
}
