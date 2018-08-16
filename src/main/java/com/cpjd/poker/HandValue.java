package com.cpjd.poker;

import com.cpjd.models.Card;
import lombok.Data;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * HandValue represents the value of the player's 5 best cards
 *
 * Note:
 * -Royal flushes aren't a category because the {@link HandValue#distinguish(HandValue, HandValue)} method will choose a royal flush
 * over a straight flush.
 */
@Data
public class HandValue implements Comparable<HandValue> {

    public enum Category {
        STRAIGHT_FLUSH(8),
        FOUR_OF_A_KIND(7),
        FULL_HOUSE(6),
        FLUSH(5),
        STRAIGHT(4),
        THREE_OF_A_KIND(3),
        TWO_PAIR(2),
        PAIR(1),
        HIGH_CARD(0);

        @Getter
        private int value;

        Category(int value) {
            this.value = value;
        }
    }

    private ArrayList<Card> cards;
    private Category category;

    /**
     * Instantiates a HandValue object representing the value of the player's 5 best cards
     * @param cards The player's 5 best cards
     * @param category The category value of the player, note, if category==folded, cards will be null as they aren't important
     */
    public HandValue(ArrayList<Card> cards, Category category) {
        this.cards = cards;
        this.category = category;
    }

    @Override
    public int compareTo(@Nonnull HandValue o) {
        return Integer.compare(category.getValue(), o.getCategory().getValue());
    }

    public boolean equals(HandValue value) {
        return value.getCategory().equals(category);
    }

    /**
     * Distinguishes between two hands of the same category to determine
     * which hand is better
     * @param value1 The first hand
     * @param value2 The second hand
     * @return value < 0 if value1 is better, value > 0 if value 2 is better, and 0 if they're the same hand
     */
    public static int distinguish(HandValue value1, HandValue value2) {
        value1.getCards().sort((o1, o2) -> o1.compareTo(o2, false));
        value2.getCards().sort((o1, o2) -> o1.compareTo(o2, false));

        for(int i = value1.getCards().size() - 1; i >= 0; i--) {
            int result =  Integer.compare(value1.getCards().get(i).getNumber().getNumerical(),
                    value2.getCards().get(i).getNumber().getNumerical());
            if(result != 0) return result;
        }

        return 0;
    }

    // required to make the test work
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandValue handValue = (HandValue) o;
        return Objects.equals(cards, handValue.cards) &&
                category == handValue.category;
    }

}
