package com.cpjd.poker;

import com.cpjd.models.Card;
import com.cpjd.models.Suit;
import com.cpjd.models.Number;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class AnalyzeHandTest {

    @Test
    public void analyzeHand() {
        ArrayList<Card> straightFlush = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.FOUR), card(Suit.SPADES, Number.FIVE), card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.SEVEN));
        ArrayList<Card> fourOfAKind = cards(card(Suit.SPADES, Number.ACE), card(Suit.SPADES, Number.ACE), card(Suit.SPADES, Number.ACE), card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.ACE));
        ArrayList<Card> fullHouse = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.TWO), card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.TWO));
        ArrayList<Card> flush = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.FOUR), card(Suit.SPADES, Number.FIVE), card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.SEVEN));
        ArrayList<Card> straight = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.FOUR), card(Suit.SPADES, Number.FIVE), card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.SEVEN));
        ArrayList<Card> threeOfAKind = cards(card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.FOUR), card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.SEVEN));
        ArrayList<Card> twoPairs = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.FIVE), card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.FIVE));
        ArrayList<Card> onePair = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.FOUR), card(Suit.SPADES, Number.FIVE), card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.SEVEN));
        ArrayList<Card> noPair = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.FOUR), card(Suit.SPADES, Number.FIVE), card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.SEVEN));

        assertEquals(new HandValue(straightFlush, HandValue.Category.STRAIGHT_FLUSH), new AnalyzeHand(null).analyzeHand(straightFlush));
        assertEquals(new HandValue(straightFlush, HandValue.Category.FOUR_OF_A_KIND), new AnalyzeHand(null).analyzeHand(fourOfAKind));
        assertEquals(new HandValue(straightFlush, HandValue.Category.FULL_HOUSE), new AnalyzeHand(null).analyzeHand(fullHouse));
        assertEquals(new HandValue(straightFlush, HandValue.Category.FLUSH), new AnalyzeHand(null).analyzeHand(flush));
        assertEquals(new HandValue(straightFlush, HandValue.Category.STRAIGHT), new AnalyzeHand(null).analyzeHand(straight));
        assertEquals(new HandValue(straightFlush, HandValue.Category.THREE_OF_A_KIND), new AnalyzeHand(null).analyzeHand(threeOfAKind));
        assertEquals(new HandValue(straightFlush, HandValue.Category.TWO_PAIR), new AnalyzeHand(null).analyzeHand(twoPairs));
        assertEquals(new HandValue(straightFlush, HandValue.Category.PAIR), new AnalyzeHand(null).analyzeHand(onePair));
        assertEquals(new HandValue(straightFlush, HandValue.Category.HIGH_CARD), new AnalyzeHand(null).analyzeHand(noPair));
    }

    @Test
    public void isStraightFlush() {
        // True cases
        ArrayList<Card> case1 = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.FOUR), card(Suit.SPADES, Number.FIVE), card(Suit.SPADES, Number.SIX));
        ArrayList<Card> case2 = cards(card(Suit.HEARTS, Number.ACE), card(Suit.HEARTS, Number.TWO), card(Suit.HEARTS, Number.FOUR), card(Suit.HEARTS, Number.THREE));
        ArrayList<Card> case3 = cards(card(Suit.SPADES, Number.JACK), card(Suit.SPADES, Number.QUEEN), card(Suit.SPADES, Number.KING), card(Suit.SPADES, Number.ACE));

        // False cases
        ArrayList<Card> case4 = cards(card(Suit.SPADES, Number.THREE), card(Suit.HEARTS, Number.FOUR), card(Suit.SPADES, Number.FIVE), card(Suit.SPADES, Number.SIX));
        ArrayList<Card> case5 = cards(card(Suit.HEARTS, Number.ACE), card(Suit.HEARTS, Number.TWO), card(Suit.HEARTS, Number.THREE), card(Suit.HEARTS, Number.FOUR));
        ArrayList<Card> case6 = cards(card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.QUEEN), card(Suit.SPADES, Number.KING), card(Suit.SPADES, Number.ACE), card(Suit.SPADES, Number.QUEEN));

        assertTrue(new AnalyzeHand(null).isStraightFlush(case1));
        assertTrue(new AnalyzeHand(null).isStraightFlush(case2));
        assertTrue(new AnalyzeHand(null).isStraightFlush(case3));

        assertFalse(new AnalyzeHand(null).isStraightFlush(case4));
        assertFalse(new AnalyzeHand(null).isStraightFlush(case5));
        assertFalse(new AnalyzeHand(null).isStraightFlush(case6));
    }

    @Test
    public void isFourOfAKind() {
        // True cases
        ArrayList<Card> case1 = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.THREE), card(Suit.HEARTS, Number.THREE), card(Suit.HEARTS, Number.QUEEN));
        ArrayList<Card> case2 = cards(card(Suit.HEARTS, Number.ACE), card(Suit.SPADES, Number.ACE), card(Suit.HEARTS, Number.FOUR), card(Suit.HEARTS, Number.ACE), card(Suit.CLUBS, Number.ACE));

        // False cases
        ArrayList<Card> case3 = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.FOUR), card(Suit.SPADES, Number.SEVEN), card(Suit.SPADES, Number.THREE), card(Suit.HEARTS, Number.THREE));
        ArrayList<Card> case4 = cards(card(Suit.HEARTS, Number.SIX), card(Suit.SPADES, Number.ACE), card(Suit.HEARTS, Number.FOUR), card(Suit.HEARTS, Number.ACE), card(Suit.CLUBS, Number.ACE));

        assertTrue(new AnalyzeHand(null).isFourOfAKind(case1));
        assertTrue(new AnalyzeHand(null).isFourOfAKind(case2));

        assertFalse(new AnalyzeHand(null).isFourOfAKind(case3));
        assertFalse(new AnalyzeHand(null).isFourOfAKind(case4));
    }

    @Test
    public void isFullHouse() {
        // True cases
        ArrayList<Card> case1 = cards(card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.THREE), card(Suit.HEARTS, Number.TEN));
        ArrayList<Card> case2 = cards(card(Suit.HEARTS, Number.ACE), card(Suit.SPADES, Number.ACE), card(Suit.HEARTS, Number.FOUR), card(Suit.HEARTS, Number.ACE), card(Suit.CLUBS, Number.FOUR));

        // False cases
        ArrayList<Card> case3 = cards(card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.THREE), card(Suit.HEARTS, Number.TEN));
        ArrayList<Card> case4 = cards(card(Suit.HEARTS, Number.TEN), card(Suit.SPADES, Number.FOUR), card(Suit.HEARTS, Number.FOUR), card(Suit.HEARTS, Number.ACE), card(Suit.CLUBS, Number.FOUR));

        assertTrue(new AnalyzeHand(null).isFullHouse(case1));
        assertTrue(new AnalyzeHand(null).isFullHouse(case2));

        assertFalse(new AnalyzeHand(null).isFullHouse(case3));
        assertFalse(new AnalyzeHand(null).isFullHouse(case4));
    }

    @Test
    public void isFlush() {
        // True cases
        ArrayList<Card> case1 = cards(card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.TEN));
        ArrayList<Card> case2 = cards(card(Suit.CLUBS, Number.ACE), card(Suit.CLUBS, Number.ACE), card(Suit.CLUBS, Number.FOUR), card(Suit.CLUBS, Number.ACE), card(Suit.CLUBS, Number.FOUR));

        // False cases
        ArrayList<Card> case3 = cards(card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.TEN), card(Suit.SPADES, Number.THREE), card(Suit.HEARTS, Number.TEN));
        ArrayList<Card> case4 = cards(card(Suit.HEARTS, Number.ACE), card(Suit.SPADES, Number.FOUR), card(Suit.HEARTS, Number.FOUR), card(Suit.HEARTS, Number.ACE), card(Suit.CLUBS, Number.FOUR));

        assertTrue(new AnalyzeHand(null).isFlush(case1));
        assertTrue(new AnalyzeHand(null).isFlush(case2));

        assertFalse(new AnalyzeHand(null).isFlush(case3));
        assertFalse(new AnalyzeHand(null).isFlush(case4));
    }

    @Test
    public void isStraight() {
        // True cases
        ArrayList<Card> case1 = cards(card(Suit.SPADES, Number.TEN), card(Suit.CLUBS, Number.NINE), card(Suit.SPADES, Number.EIGHT), card(Suit.DIAMONDS, Number.JACK), card(Suit.SPADES, Number.QUEEN));
        ArrayList<Card> case2 = cards(card(Suit.CLUBS, Number.ACE), card(Suit.DIAMONDS, Number.KING), card(Suit.CLUBS, Number.JACK), card(Suit.CLUBS, Number.QUEEN), card(Suit.CLUBS, Number.TEN));

        // False cases
        ArrayList<Card> case3 = cards(card(Suit.SPADES, Number.TEN), card(Suit.CLUBS, Number.TWO), card(Suit.SPADES, Number.EIGHT), card(Suit.DIAMONDS, Number.JACK), card(Suit.SPADES, Number.QUEEN));
        ArrayList<Card> case4 = cards(card(Suit.CLUBS, Number.ACE), card(Suit.DIAMONDS, Number.KING), card(Suit.CLUBS, Number.JACK), card(Suit.CLUBS, Number.THREE), card(Suit.CLUBS, Number.TEN));

        assertTrue(new AnalyzeHand(null).isStraight(case1));
        assertTrue(new AnalyzeHand(null).isStraight(case2));

        assertFalse(new AnalyzeHand(null).isStraight(case3));
        assertFalse(new AnalyzeHand(null).isStraight(case4));
    }

    @Test
    public void isThreeOfAKind() {
        // True cases
        ArrayList<Card> case1 = cards(card(Suit.SPADES, Number.ACE), card(Suit.CLUBS, Number.NINE), card(Suit.SPADES, Number.EIGHT), card(Suit.DIAMONDS, Number.ACE), card(Suit.SPADES, Number.ACE));
        ArrayList<Card> case2 = cards(card(Suit.CLUBS, Number.JACK), card(Suit.DIAMONDS, Number.JACK), card(Suit.CLUBS, Number.JACK), card(Suit.CLUBS, Number.KING), card(Suit.CLUBS, Number.TEN));

        // False cases
        ArrayList<Card> case3 = cards(card(Suit.SPADES, Number.JACK), card(Suit.CLUBS, Number.JACK), card(Suit.SPADES, Number.JACK), card(Suit.DIAMONDS, Number.JACK), card(Suit.SPADES, Number.JACK));
        ArrayList<Card> case4 = cards(card(Suit.CLUBS, Number.ACE), card(Suit.DIAMONDS, Number.ACE), card(Suit.CLUBS, Number.JACK), card(Suit.CLUBS, Number.THREE), card(Suit.CLUBS, Number.TEN));

        assertTrue(new AnalyzeHand(null).isThreeOfAKind(case1));
        assertTrue(new AnalyzeHand(null).isThreeOfAKind(case2));

        assertFalse(new AnalyzeHand(null).isThreeOfAKind(case3));
        assertFalse(new AnalyzeHand(null).isThreeOfAKind(case4));
    }

    @Test
    public void isTwoPair() {
        // True cases
        ArrayList<Card> case1 = cards(card(Suit.CLUBS, Number.ACE), card(Suit.DIAMONDS, Number.ACE), card(Suit.CLUBS, Number.FOUR), card(Suit.CLUBS, Number.THREE), card(Suit.CLUBS, Number.THREE));
        ArrayList<Card> case2 = cards(card(Suit.CLUBS, Number.ACE), card(Suit.DIAMONDS, Number.JACK), card(Suit.CLUBS, Number.JACK), card(Suit.CLUBS, Number.KING), card(Suit.CLUBS, Number.KING));

        // False cases
        ArrayList<Card> case3 = cards(card(Suit.SPADES, Number.JACK), card(Suit.CLUBS, Number.JACK), card(Suit.SPADES, Number.JACK), card(Suit.DIAMONDS, Number.ACE), card(Suit.SPADES, Number.TEN));
        ArrayList<Card> case4 = cards(card(Suit.CLUBS, Number.ACE), card(Suit.DIAMONDS, Number.ACE), card(Suit.CLUBS, Number.ACE), card(Suit.CLUBS, Number.SIX), card(Suit.CLUBS, Number.THREE));

        assertTrue(new AnalyzeHand(null).isTwoPair(case1));
        assertTrue(new AnalyzeHand(null).isTwoPair(case2));

        assertFalse(new AnalyzeHand(null).isTwoPair(case3));
        assertFalse(new AnalyzeHand(null).isTwoPair(case4));
    }

    @Test
    public void isPair() {
        // True cases
        ArrayList<Card> case1 = cards(card(Suit.CLUBS, Number.QUEEN), card(Suit.DIAMONDS, Number.ACE), card(Suit.CLUBS, Number.FOUR), card(Suit.CLUBS, Number.THREE), card(Suit.CLUBS, Number.THREE));
        ArrayList<Card> case2 = cards(card(Suit.CLUBS, Number.ACE), card(Suit.DIAMONDS, Number.THREE), card(Suit.CLUBS, Number.ACE), card(Suit.CLUBS, Number.KING), card(Suit.CLUBS, Number.FOUR));

        // False cases
        ArrayList<Card> case3 = cards(card(Suit.SPADES, Number.JACK), card(Suit.CLUBS, Number.TEN), card(Suit.SPADES, Number.THREE), card(Suit.DIAMONDS, Number.ACE), card(Suit.SPADES, Number.SEVEN));
        ArrayList<Card> case4 = cards(card(Suit.CLUBS, Number.ACE), card(Suit.DIAMONDS, Number.TWO), card(Suit.CLUBS, Number.EIGHT), card(Suit.CLUBS, Number.SIX), card(Suit.CLUBS, Number.THREE));
        ArrayList<Card> case5 = cards(card(Suit.HEARTS, Number.TEN), card(Suit.SPADES, Number.FOUR), card(Suit.HEARTS, Number.FOUR), card(Suit.HEARTS, Number.ACE), card(Suit.CLUBS, Number.FOUR));

        assertTrue(new AnalyzeHand(null).isPair(case1));
        assertTrue(new AnalyzeHand(null).isPair(case2));

        assertFalse(new AnalyzeHand(null).isPair(case3));
        assertFalse(new AnalyzeHand(null).isPair(case4));
        assertFalse(new AnalyzeHand(null).isPair(case5));
    }

    @Test
    public void sameSuit() {
        ArrayList<Card> same = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.KING), card(Suit.SPADES, Number.ACE));
        ArrayList<Card> diff = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.KING), card(Suit.HEARTS, Number.ACE));

        assertTrue(new AnalyzeHand(null).sameSuit(same));
        assertFalse(new AnalyzeHand(null).sameSuit(diff));
    }

    @Test
    public void ascending() {
        ArrayList<Card> case1 = cards(card(Suit.SPADES, Number.THREE), card(Suit.SPADES, Number.FOUR), card(Suit.SPADES, Number.FIVE));
        ArrayList<Card> case2 = cards(card(Suit.SPADES, Number.ACE), card(Suit.SPADES, Number.TWO), card(Suit.SPADES, Number.THREE));
        ArrayList<Card> case3 = cards(card(Suit.SPADES, Number.QUEEN), card(Suit.SPADES, Number.KING), card(Suit.SPADES, Number.ACE));
        ArrayList<Card> case4 = cards(card(Suit.SPADES, Number.ACE), card(Suit.SPADES, Number.ACE), card(Suit.SPADES, Number.JACK));
        ArrayList<Card> case5 = cards(card(Suit.SPADES, Number.SIX), card(Suit.SPADES, Number.FIVE), card(Suit.SPADES, Number.FOUR));

        assertTrue(new AnalyzeHand(null).ascending(case1));
        assertTrue(new AnalyzeHand(null).ascending(case2));
        assertTrue(new AnalyzeHand(null).ascending(case3));
        assertFalse(new AnalyzeHand(null).ascending(case4));
        assertFalse(new AnalyzeHand(null).ascending(case5));
    }

    @Test
    public void getCombinations() {

    }

    // Utils

    private Card card(Suit suit, Number number) {
        return new Card(suit, number);
    }

    private ArrayList<Card> cards(Card... cards) {
        if (cards == null) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(cards));
    }
}