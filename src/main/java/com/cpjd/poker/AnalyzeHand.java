package com.cpjd.poker;

import com.cpjd.models.Card;
import com.cpjd.models.Player;
import com.cpjd.models.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AnalyzeHand {

    // The cards that have been dealt in the center
    private ArrayList<Card> drawn;

    public AnalyzeHand(ArrayList<Card> drawn) {
        this.drawn = drawn;
    }

    public HandValue analyzePlayer(Player player) {
        // Combinations
        ArrayList<ArrayList<Card>> handCombinations = getCombinations(player.getCard1(), player.getCard2());

        // Determine best hand
        ArrayList<HandValue> handValues = new ArrayList<>();

        for(ArrayList<Card> cards : handCombinations) handValues.add(analyzeHand(cards));

        Collections.sort(handValues);

        // Remove all worse hands (that don't match category)
        HandValue.Category category = handValues.get(handValues.size() - 1).getCategory();
        for(int i = 0; i < handValues.size(); i++) {
            if(handValues.get(i).getCategory() != category) {
                handValues.remove(i);
                i--;
            }
        }

        // Remove all worse hands (same category, but different high cards)
        handValues.sort(HandValue::distinguish);

        // Return the best hand
        return handValues.get(handValues.size() - 1);
    }

    HandValue analyzeHand(ArrayList<Card> cards) {
        // Set category
        if(isStraightFlush(cards)) return new HandValue(cards, HandValue.Category.STRAIGHT_FLUSH);
        else if(isFourOfAKind(cards)) return new HandValue(cards, HandValue.Category.FOUR_OF_A_KIND);
        else if(isFullHouse(cards)) return new HandValue(cards, HandValue.Category.FULL_HOUSE);
        else if(isFlush(cards)) return new HandValue(cards, HandValue.Category.FLUSH);
        else if(isStraight(cards)) return new HandValue(cards, HandValue.Category.STRAIGHT);
        else if(isThreeOfAKind(cards)) return new HandValue(cards, HandValue.Category.THREE_OF_A_KIND);
        else if(isTwoPair(cards)) return new HandValue(cards, HandValue.Category.TWO_PAIR);
        else if(isPair(cards)) return new HandValue(cards, HandValue.Category.PAIR);
        else return new HandValue(cards, HandValue.Category.HIGH_CARD);
    }

    /*
     * Hand evaluation methods
     */

    /**
     * Determines if the cards are a straight flush (royal flushes included)
     * @param cards The cards to check
     * @return true if straight flush, false if not
     */
    boolean isStraightFlush(ArrayList<Card> cards) {
        return sameSuit(cards) && ascending(cards);
    }

    /**
     * Checks if there are four cards of the same {@link com.cpjd.models.Number} within the provided
     * hand
     * @param cards The cards to check
     * @return true if EXACTLY four of the cards have the same {@link Number}
     */
    boolean isFourOfAKind(ArrayList<Card> cards) {
        for(int i = 0; i < cards.size(); i++) {
            int occurrences = 1;
            for(int j = 0; j < cards.size(); j++) {
                if (j == i) continue;
                if (cards.get(i).getNumber() == cards.get(j).getNumber()) occurrences++;
            }
            if (occurrences == 4) return true;
        }
        return false;
    }

    /**
     * Checks for a pair and a three of a kind within the hand
     *
     * Note: isPair() will only return true if there are EXACTLY 2 cards of the same {@link com.cpjd.models.Number},
     * that's the reason this method works
     *
     * @param cards The cards to check
     * @return true if full house, false if not
     */
    boolean isFullHouse(ArrayList<Card> cards) {
        return isThreeOfAKind(cards) && isPair(cards);
    }

    /**
     * Checks if the cards are all of the same suit, note, this method must be called after
     * the straight flush method to ensure accuracy
     * @param cards The cards to check
     * @return true if flush, false if not
     */
    boolean isFlush(ArrayList<Card> cards) {
        return sameSuit(cards);
    }

    /**
     * Checks if the cards are a straight, or ascending (regardless of suit)
     * @param cards The cards to check
     * @return true if straight, false if not
     */
    boolean isStraight(ArrayList<Card> cards) {
        return ascending(cards);
    }

    /**
     * Checks if three of a kind, must be exact (4 won't trigger it)
     * @param cards The cards to check
     * @return true if three of a kind, false if not
     */
    boolean isThreeOfAKind(ArrayList<Card> cards) {
        for(int i = 0; i < cards.size(); i++) {
            int occurrences = 1;
            for(int j = 0; j < cards.size(); j++) {
                if (j == i) continue;
                if (cards.get(i).getNumber() == cards.get(j).getNumber()) occurrences++;
            }
            if (occurrences == 3) return true;
        }
        return false;
    }

    /**
     * Checks if there are two pairs in the deck
     * @param cards The cards to check
     * @return true if two pair, false if not
     */
    boolean isTwoPair(ArrayList<Card> cards) {
        ArrayList<Integer> exclude = new ArrayList<>(); // can never be checked again

        int pairs = 0;

        for(int i = 0; i < cards.size(); i++) {
            if (exclude.contains(i)) continue;

            for(int j = 0; j < cards.size(); j++) {
                if (exclude.contains(j) || i == j) continue;

                if (cards.get(i).getNumber().getNumerical() == cards.get(j).getNumber().getNumerical()) {
                    pairs++;
                    if (pairs == 2) return true;
                    exclude.add(i);
                    exclude.add(j);
                    break;
                }
            }
        }

        return false;
    }

    /**
     * Checks if there is one pair in the deck
     * @param cards The cards to check
     * @return true if pair, false if not
     */
    // For other functions to work, this MUST mean that there is EXACTLY 1 pair
    boolean isPair(ArrayList<Card> cards) {
        for(int i = 0; i < cards.size(); i++) {

            int occurrences = 1;

            for(int j = 0; j < cards.size(); j++) {
                if(i == j) continue;

                if(cards.get(i).getNumber().getNumerical() == cards.get(j).getNumber().getNumerical()) occurrences++;
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

    /**
     * Checks if the cards are all of the same suit
     * @param cards The cards to check for equivalent suits
     * @return true if all the cards belong to the same suit
     */
    boolean sameSuit(ArrayList<Card> cards) {
        Suit suit = cards.get(0).getSuit();
        for(Card c : cards) if (c.getSuit() != suit) return false;
        return true;
    }

    /**
     * Checks if the cards' numerical values are ascending, order
     * provided does not matter, this method will sort the cards
     * @param cards The cards to check
     * @return True if the cards are ascending
     */
    boolean ascending(ArrayList<Card> cards) {
        // Sort the cards with ace as high
        cards.sort((o1, o2) -> o1.compareTo(o2, false));

        boolean ascending = true;

        for(int i = 1; i < cards.size(); i++) {
            if(cards.get(i).getNumber().getNumerical() - 1 != cards.get(i - 1).getNumber().getNumerical()) ascending = false;
        }

        if(ascending) return true;

        // Retry with ace as low
        cards.sort((o1, o2) -> o1.compareTo(o2, true));
        ascending = true;

        for(int i = 1; i < cards.size(); i++) {
            if(cards.get(i).getNumber().getNumericalAceLow() - 1 != cards.get(i - 1).getNumber().getNumericalAceLow()) ascending = false;
        }

        return ascending;
    }

    /**
     * Returns an array of all possible hand combinations of the player's cards and
     * the drawn cards. Each hand will be evaluated, and the best hand will be assigned
     * to the player's {@link HandValue}
     * @param card1 The player's first card
     * @param card2 The player's second card
     * @return An array of all possible hand combinations
     */
    ArrayList<ArrayList<Card>> getCombinations(Card card1, Card card2) {
        // Output array
        ArrayList<ArrayList<Card>> possibleHands = new ArrayList<>();

        // Create a new ArrayList of all cards
        ArrayList<Card> total = new ArrayList<>(drawn);
        total.add(card1);
        total.add(card2);

        // Only one possible array
        if (total.size() <= 5) {
            possibleHands.add(total);
            return possibleHands;
        }

        // Each hand will only leave out one card, so
        // array processing is pretty easy
        else if (total.size() == 6) {

            // Skip whatever card skip is at
            for(int skip = 0; skip < total.size(); skip++) {
                ArrayList<Card> possible = new ArrayList<>();

                for(int i = 0; i < total.size(); i++) {
                    if (i == skip) continue;

                    possible.add(total.get(i));
                }

                possibleHands.add(possible);
            }
        }

        // Each hand will leave 2 cards out, these means,
        // Every card needs to be left out twice total
        else if (total.size() == 7) {
            for(int skipStartIndex = 0; skipStartIndex < 7; skipStartIndex++) {
                ArrayList<Card> hand = new ArrayList<>();

                int firstCard = (skipStartIndex + 2) % 7;

                for(int i = 0; i < 5; i++) {
                    hand.add(total.get(firstCard));
                    firstCard++;
                    if(firstCard == 7) firstCard = 0;
                }
                possibleHands.add(hand);
            }
        }
        return possibleHands;
    }

}
