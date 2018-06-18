package com.cpjd.poker;

import com.cpjd.models.Card;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;

/**
 * HandValue represents the value of the player's 5 best cards
 */
@Data
public class HandValue implements Comparable<HandValue> {

    @Override
    public int compareTo(HandValue o) {
        return Integer.compare(category.getValue(), o.getCategory().getValue());
    }

    public enum CATEGORY {
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

        CATEGORY(int value) {
            this.value = value;
        }
    }

    private Member ID;
    private ArrayList<Card> cards;
    private CATEGORY category;

    public HandValue(ArrayList<Card> cards) {
        this.cards = cards;
    }
}
