package com.cpjd.poker;

import com.cpjd.models.Card;
import com.cpjd.models.SUIT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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

        for(ArrayList<Card> card : combos) {
            System.out.println("Hand value:"+computeHandValue(card).toString());

            potentialValues.add(computeHandValue(card));
        }

        // Select the best hand value out of the bunch
        HandValueDistinguish.determineBest(potentialValues);

        HandValue skeet = potentialValues.get(new Random().nextInt(potentialValues.size()));

        System.out.println("Player category: "+skeet.getCategory().toString());

        return skeet;
    }

    private HandValue computeHandValue(ArrayList<Card> cards) {
        HandValue value = new HandValue(cards);

        // Set category
        if(isStraightFlush(cards)) value.setCategory(HandValue.CATEGORY.STRAIGHT_FLUSH);
        else if(isFourOfAKind(cards)) value.setCategory(HandValue.CATEGORY.FOUR_OF_A_KIND);
        else if(isFullHouse(cards)) value.setCategory(HandValue.CATEGORY.FULL_HOUSE);
        else if(isFlush(cards)) value.setCategory(HandValue.CATEGORY.FLUSH);
        else if(isStraight(cards)) value.setCategory(HandValue.CATEGORY.STRAIGHT);
        else if(isThreeOfAKind(cards)) value.setCategory(HandValue.CATEGORY.THREE_OF_A_KIND);
        else if(isTwoPair(cards)) value.setCategory(HandValue.CATEGORY.TWO_PAIR);
        else if(isPair(cards)) value.setCategory(HandValue.CATEGORY.PAIR);
        else value.setCategory(HandValue.CATEGORY.HIGH_CARD);

        return value;
    }

    /*
     * Category functions
     */

    private boolean isStraightFlush(ArrayList<Card> cards) {
        return sameSuit(cards) && ascending(cards);
    }

    private boolean isFourOfAKind(ArrayList<Card> cards) {
        for(int i = 0; i < cards.size(); i++) {
            int occurrences = 1;
            for(int j = 0; j < cards.size(); j++) {
                if(j == i) continue;
                if(cards.get(i).getNumber() == cards.get(j).getNumber()) occurrences++;
            }
            if(occurrences == 4) return true;
        }
        return false;
    }

    private boolean isFullHouse(ArrayList<Card> cards) {
        return isThreeOfAKind(cards) && isPair(cards);
    }

    private boolean isFlush(ArrayList<Card> cards) {
        return sameSuit(cards);
    }

    private boolean isStraight(ArrayList<Card> cards) {
        return ascending(cards);
    }

    private boolean isThreeOfAKind(ArrayList<Card> cards) {
        for(int i = 0; i < cards.size(); i++) {
            int occurrences = 1;
            for(int j = 0; j < cards.size(); j++) {
                if(j == i) continue;
                if(cards.get(i).getNumber() == cards.get(j).getNumber()) occurrences++;
            }
            if(occurrences == 3) return true;
        }
        return false;
    }

    private boolean isTwoPair(ArrayList<Card> cards) {
        ArrayList<Integer> exclude = new ArrayList<>(); // can never be checked again

        int pairs = 0;

        for(int i = 0; i < cards.size(); i++) {
            if(exclude.contains(i)) continue;

            for(int j = 0;j < cards.size(); j++) {
                if(exclude.contains(j) || i == j) continue;

                if (cards.get(i).getNumber().getNumerical() == cards.get(j).getNumber().getNumerical()) {
                    pairs++;
                    if(pairs == 2) return true;
                    exclude.add(i);
                    exclude.add(j);
                    break;
                }
            }
        }

        return false;
    }

    // For other functions to work, this MUST mean that there are EXACTLY 2 pairs
    private boolean isPair(ArrayList<Card> cards) {
        ArrayList<Integer> exclude = new ArrayList<>();

        for(int i = 0; i < cards.size(); i++) {
            if(exclude.contains(i)) continue;

            int occurrences = 1;

            for(int j = 0; j < cards.size(); j++) {
                if(j == i || exclude.contains(j)) continue;

                if(cards.get(i).getNumber().getNumerical() == cards.get(j).getNumber().getNumerical()) {
                    occurrences++;
                    exclude.add(i);
                    exclude.add(j);
                    break;
                }
            }

            if(occurrences == 2) return true;
        }

        return false;
    }

    /*
     *
     * Helper functions
     *
     */
    private boolean sameSuit(ArrayList<Card> cards) {
        SUIT suit = cards.get(0).getSuit();
        for(Card c : cards) if(c.getSuit() != suit) return false;
        return true;
    }

    private boolean ascending(ArrayList<Card> cards) {
        Collections.sort(cards); // sort them lowest to highest
        for(int i = 1; i < cards.size(); i++) {
            if(cards.get(i).getNumber().getNumerical() - 1 != cards.get(i - 1).getNumber().getNumerical()) return false;
        }

        for(int i = 1; i < cards.size(); i++) {
            if(cards.get(i).getNumber().getNumericalAceLow() - 1 != cards.get(i - 1).getNumber().getNumericalAceLow()) return false;
        }

        return true;
    } // account for low ace here also

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
