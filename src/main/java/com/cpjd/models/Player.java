package com.cpjd.models;

import lombok.Data;
import net.dv8tion.jda.core.entities.User;

@Data
public class Player {

    public double bank;
    public double gameBank;

    public double wager;

    public User user;
    public boolean isFolded;
    public boolean isAllIn;

    public Player(User user, double bank) {
        this.user = user;
        this.bank = bank;
    }

    public void add(double amount) {
        this.gameBank += amount;
    }

    public boolean wager(double wager) {
        if(gameBank - wager < 0) return false;
        if(wager >= gameBank) {
            this.wager = gameBank;
            isAllIn = true;
            return true;
        }

        this.wager = wager;

        return true;
    }

    public void transfer(double amount) {
        if(amount > gameBank) {
            gameBank = 0;
            add(gameBank);
        }
        else {
            gameBank -= amount;
            add(amount);
        }
    }


    public boolean sub(double amount) {
        if(gameBank - amount < 0) return false;
        gameBank -= amount;
        return true;
    }


    public double getBank() {
        return round(bank, 2);
    }

    public double getGameBank() {
        return round(gameBank, 2);
    }

    /**
     * Rounds a decimal to the specified number of digits (right of decimal point)
     * @param value the value to round
     * @param precision the amount of digits to keep
     * @return rounded double
     */
    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
