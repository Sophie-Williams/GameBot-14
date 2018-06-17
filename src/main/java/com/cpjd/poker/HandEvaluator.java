package com.cpjd.poker;

import com.cpjd.models.Card;
import com.cpjd.models.NUMBER;
import com.cpjd.models.Player;
import com.cpjd.models.SUIT;

import java.lang.reflect.Array;
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

        ArrayList<ArrayList<Card>> combos = getCombinations(card1, card2);
    }

    private HandValue computeHandValue(ArrayList<Card> cards) {

    }

    /*
     * Quality - these assume types
     */
    public int getStraightFlushQuality(ArrayList<Card> cards) {
        return getHighest(cards);
    }

    public int getFourOfAKindQuality(ArrayList<Card> cards) {
        return getHighest(cards);
    }

    /*
     * Types
     */
    private boolean isStraightFlush(ArrayList<Card> cards) {
        return sameSuit(cards) && ascending(cards);
    }

    private boolean isFourOfAKind(ArrayList<Card> cards) {
        Card c = cards.get(0);
        int number = 0;
        for(Card s : cards) {
            if(c.getSuit().equals(s.getSuit())) number++;
        }
        if(number == 4) return true;
        number = 0;
        c = cards.get(1);
        for(Card s : cards) {
            if(c.getSuit().equals(s.getSuit())) number++;
        }
        if(number == 4) return true;
        return false;
    }

    private boolean sameSuit(ArrayList<Card> cards) {
        SUIT suit = cards.get(0).getSuit();
        for(Card c : cards) if(c.getSuit() != suit) return false;
        return true;
    }

    private static int getHighest(ArrayList<Card> cards) {
        int highest = cards.get(0).getNumber().getNumerical(true);

        for(Card c : cards) {
            if(c.getNumber().getNumerical(true) > highest) highest = c.getNumber().getNumerical(true);
        }

        return highest;
    }

    private static boolean ascending(ArrayList<Card> cards) {
        return isAscendingAceHigh(cards, true);
    }

    private static boolean isAscendingAceHigh(ArrayList<Card> cards, boolean aceHigh) {
        // Find lowest number (assume aces are 1)
        int lowest = cards.get(0).getNumber().getNumerical(aceHigh);
        for (Card c : cards) {
            if (c.getNumber().getNumerical(aceHigh) < lowest) lowest = c.getNumber().getNumerical(aceHigh);
        }

        // Now, cards must contain increasing levels
        boolean ascending = true;
        for (int i = 0, search = lowest; i < cards.size(); i++) {
            if (cards.get(i).getNumber().getNumerical(aceHigh) == search) {
                // Found a card, increment
                search++;
            }

            if (i == cards.size() && search != lowest + 4) {
                ascending = false;
            }
        }

        if(!aceHigh) return ascending; // prevent stack overflow

        return ascending || isAscendingAceHigh(cards, false);
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
