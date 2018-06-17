package com.cpjd.modules;

import com.cpjd.models.Card;
import lombok.Data;
import lombok.Getter;

/**
 * HandValue represents the value of the player's 5 best cards
 */
@Data
public class HandValue {

    public enum CATEGORY {
        ROYAl_FLUSH(9),
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

    private CATEGORY category;
    /*
     * Rates the quality of the above category
     */
    private int quality;
    private Card highestCard;

    public int compareTo(HandValue value) {
        if(this != value) {
            return Integer.compare(category.getValue(), value.getCategory().getValue());
        } else return Integer.compare(quality, value.getQuality());
    }
}
