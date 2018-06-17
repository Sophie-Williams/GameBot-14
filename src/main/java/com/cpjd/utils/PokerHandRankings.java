package com.cpjd.utils;

import com.cpjd.models.Card;
import com.cpjd.models.HandValue;
import com.cpjd.models.NUMBER;
import com.cpjd.models.SUIT;
import org.omg.CORBA.ARG_IN;

import java.util.ArrayList;

// Assumed ALL are 5 in length
// An integer is returned representing the SCORE within the category,
// if there are no differences int the category, then 0 is returned, this
// still means TRUE!
public class PokerHandRankings {

    /*
     * Returns -1 if it's not a royal flush, otherwise, a score regarding the quality
     */
    public static int isRoyalFlush(ArrayList<Card> cards) {
        if(!sameSuit(cards)) return -1;
        // Must contain King and Ace to be royal flush
        if(containsNumber(cards, NUMBER.ACE) && containsNumber(cards, NUMBER.KING)) {
            return 0; // no sub score
        } else return -1;
    }

    public static int isStraightFlush(ArrayList<Card> cards) {
        if(!sameSuit(cards)) return -1;
        if(isAscending(cards)) return getHighest(cards);
        return -1;
    }

    public static int isFourOfAKing(ArrayList<Card> cards) {
        for(Card c : cards) {
            if(numCardsSameRank(cards, c.getNumber()) == 4) return c.getNumber().getNumerical(true);
        }

        return -1;
    }

    public static int isFullHouse(ArrayList<Card> cards) {
        return 0;
    }



    /*
     *
     * HELPER methods
     *
     */

    private static int getHighest(ArrayList<Card> cards) {
        int highest = cards.get(0).getNumber().getNumerical(true);

        for(Card c : cards) {
            if(c.getNumber().getNumerical(true) > highest) highest = c.getNumber().getNumerical(true);
        }

        return highest;
    }

    // returns the number of cards with the same NUMBER
    private static int numCardsSameRank(ArrayList<Card> cards, NUMBER number) {
        int count = 0;
        for(Card c : cards) {
            if(c.getNumber() == number) {
                count++;
            }
        }

        return count;
    }

    private static boolean containsSuit(ArrayList<Card> cards, SUIT suit) {
        for(Card c : cards) {
            if(c.getSuit() == suit) return true;
        }
        return false;
    }

    private static boolean containsNumber(ArrayList<Card> cards, NUMBER number) {
        for(Card c : cards) {
            if(c.getNumber() == number) return true;
        }
        return false;
    }

    private static boolean sameSuit(ArrayList<Card> cards) {
        SUIT start = cards.get(0).getSuit();
        for(Card c : cards) if(c.getSuit() != start) return false;
        return true;
    }

    private static boolean isAscending(ArrayList<Card> cards) {
        return isAscendingAceHigh(cards, true);
    }

    private static boolean isAscendingAceHigh(ArrayList<Card> cards, boolean aceHigh) {
        // Find lowest number (assume aces are 1)
        int lowest = cards.get(0).getNumber().getNumerical(aceHigh);
        for(Card c : cards) {
            if(c.getNumber().getNumerical(aceHigh) < lowest) lowest = c.getNumber().getNumerical(aceHigh);
        }

        // Now, cards must contain increasing levels
        boolean ascending = true;
        for(int i = 0, search = lowest; i < cards.size(); i++) {
            if(cards.get(i).getNumber().getNumerical(aceHigh) == search) {
                // Found a card, increment
                search++;
            }

            if(i == cards.size() && search != lowest + 4) {
                ascending = false;
            }
        }

        if(!aceHigh) return ascending; // prevent stack overflow

        return ascending || isAscendingAceHigh(cards, false);
    }




}
