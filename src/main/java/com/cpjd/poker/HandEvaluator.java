package com.cpjd.poker;

import com.cpjd.models.Card;
import com.cpjd.models.Player;

import java.util.ArrayList;

public class HandEvaluator {

    private ArrayList<Card> drawn;

    public HandEvaluator(ArrayList<Card> drawn) {
        this.drawn = drawn;
    }

    /**
     * Computes the value of the players best 5 cards including the drawn cards
     * @param card1 the player's first card
     * @param card2 the player's second card
     * @return the value of the player's hand
     */
    public HandValue computePlayerValue(Card card1, Card card2) {
        ArrayList<HandValue> potentialValues = new ArrayList<>();
    }

    private HandValue computeHandValue(Card card1, Card card2) {

    }

    private ArrayList<ArrayList<Card>> getCombinations(Card card1, Card card2) {
        // Output array
        ArrayList<ArrayList<Card>> possibleHands = new ArrayList<>();

        // Create a new ArrayList of all cards
        ArrayList<Card> total = new ArrayList<>(drawn);
        total.add(card1);
        total.add(card2);

        // Only one possible array
        if(total.size() == 5) {
            possibleHands.add(total);
            return possibleHands;
        }

        // Each hand will only leave out one card, so
        // array processing is pretty easy
        else if(total.size() == 6) {

            // Skip whatever card skip is at
            for(int skip = 0; skip < total.size(); skip++) {
                ArrayList<Card> possible = new ArrayList<>();

                for(int i = 0; i < total.size(); i++) {
                    if(i == skip) continue;

                    possible.add(total.get(i));
                }

                possibleHands.add(possible);
            }
        }

        // Each hand will leave 2 cards out, these means,
        // Every card needs to be left out twice total
        else if(total.size() == 7) {
            for(int skip = 0; skip < total.size(); skip++) {
                ArrayList<Card> possible = new ArrayList<>();

                for(int i = 0; i < total.size(); i++) {
                    if(i == skip || (i == skip + 1 || (skip == total.size() && i == 0))) continue;

                    possible.add(total.get(i));
                }

                possibleHands.add(possible);
            }
        }
        return possibleHands;
    }
}
