package com.cpjd.utils;

import com.cpjd.models.Card;
import com.cpjd.models.Player;

import java.util.ArrayList;

public class HandEvaluator {

    private ArrayList<Player> players;
    private ArrayList<Card> drawn;
    private double pot;

    public HandEvaluator(ArrayList<Player> players, ArrayList<Card> drawn, double pot) {
        this.pot =  pot;
        this.players = players;
        this.drawn = drawn;
    }

    /**
     * Evaluates a round of poker.
     *
     * The following are handled by this class:
     * 1) Finding the winner(s) and distributing the pot
     * 2) Removing the losers from the game
     *
     * @return a list of players who won the game, usually only 1 length
     */
    public ArrayList<Player> evaluate() {
        /*
         * Remove folded players from the game
         */
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).isFolded()) {
                players.remove(i);
                i--;
            }
        }

        if(players.size() == 1) return players;

         /*
          * Okay, now start processing. Texas Hold-Em is a 5 card game, meaning
          * the user's value is determined by the best combination of 5 cards.
          * Each combination needs to be thoroughly analyzed and assigned a HandValue,
          * which is used to determine the winner.
          *
          * getCombinations() generates a list of all possible combinations of 5 cards.
          * Keep in mind, the length of combinations will depend on the amount of card drawn.
          */




        return null;
    }

    private ArrayList<ArrayList<Card>> getCombinations(Player p) {
        return null;
    }

}
