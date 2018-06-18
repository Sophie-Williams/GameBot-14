package com.cpjd.poker;

import com.cpjd.models.Card;
import com.cpjd.models.Player;

import java.util.ArrayList;
import java.util.Collections;

public class HandValueDistinguish {

    // goal is to remove all hand values but the winner(s)
    public static void determineBest(ArrayList<HandValue> values) {
        System.out.println("Input: "+values.size());

        if(values.size() == 1) return;

        Collections.sort(values);
        Collections.reverse(values); // high categories first

        // Then, if a hand doesn't match the category above it, remove it
        for(int i = values.size() - 1; i > 0; i--) {
            if(values.get(i).compareTo(values.get(i - 1)) != 0) {
                values.remove(i);
            }
        }

        // Next, we have a list of hands that match in category, a number analysis now
        // needs to be performed
        for(int i = values.size() - 1; i > 0; i--) {
            if(findWinner(values.get(i), values.get(i - 1)) == 2) {
                values.remove(i);
            }
        }

        System.out.println("Output: "+values.size());
    }

    // 0 for tie, 1 for value1 win, 2 for value2 win // todo improve
    private static int findWinner(HandValue value1, HandValue value2) {
        if(match(value1, value2)) { // if hands match, it's a winner
            return 0;
        }

        // otherwise, return the highest card
        for(int i = 4; i >= 0; i--) {
            if(getCard(value1.getCards(), i) > getCard(value2.getCards(), i)) return 1;
            else if(getCard(value1.getCards(), i) < getCard(value2.getCards(), i)) return 2;
        }

        return 2;
    }

    private static boolean match(HandValue value1, HandValue value2) {
        Collections.sort(value1.getCards());
        Collections.sort(value2.getCards());

        for(int i = 0; i < 5; i++) {
            if(value1.getCards().get(i).getNumber().getNumerical() != value2.getCards().get(i).getNumber().getNumerical()) return false;
        }

        return value1.getCategory() == value2.getCategory();
    }

    private static int getCard(ArrayList<Card> cards, int displacement) {
        Collections.sort(cards);

        return cards.get(displacement).getNumber().getNumerical();
    }

}
