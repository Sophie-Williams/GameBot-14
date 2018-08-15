package com.cpjd.models;

import com.cpjd.main.Bot;
import com.cpjd.utils.RandomORG;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Data
@AllArgsConstructor
public class Card implements Comparable<Card> {

    private Suit suit;
    private Number number;

    /**
     * Returns the file path to the png image representing this card
     * @return File path corresponding to the card
     */
    public File toFile() {
        StringBuilder name = new StringBuilder();

        name.append(number.getID());

        if(suit == Suit.SPADES) name.append("S");
        else if(suit == Suit.DIAMONDS) name.append("D");
        else if(suit == Suit.HEARTS) name.append("H");
        else if(suit == Suit.CLUBS) name.append("C");

        File card = new File(Bot.CARD_DIRECTORY + File.separator + name.toString() + ".png");

        if(!card.exists()) throw new RuntimeException("Card "+card.getAbsolutePath()+" does not exist.");

        return card;
    }

    public String toString() {
        return number.toString() + suit.toString();
    }

    /**
     * Returns a deck of cards, shuffled randomly with truly random numbers.
     * @return A deck of 52 cards, shuffled 7 times according to atmospheric noise
     */
    public static ArrayList<Card> deck() {
        ArrayList<Card> deck = new ArrayList<>();

        for(Number number : Number.values()) {
            deck.add(new Card(Suit.CLUBS, number));
            deck.add(new Card(Suit.HEARTS, number));
            deck.add(new Card(Suit.SPADES, number));
            deck.add(new Card(Suit.DIAMONDS, number));
        }


        try {
            int[] nums = RandomORG.doRequest();
            for(int i = 0; i < 7; i++) {
                Collections.shuffle(deck, new Random(nums[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to shuffle good.");
            Collections.shuffle(deck);
        }

        return deck;
    }

    private static int count = 0;

    /**
     * Combines several cards into one png image
     * @param cards a variable-length argument of pngs to add to the image
     * @return a file path to an image containing multiple card pngs
     */
    public static File combine(boolean full, Card ... cards) {
        try {
            count++;

            File combined = new File(Bot.CARD_DIRECTORY+File.separator+count+"temp.png");

            if(!combined.exists()) {
                if(combined.createNewFile()) System.out.println("Creating temporary combined image file.");
            }


            BufferedImage image = new BufferedImage(691 * (full ? 5 : cards.length), 1056, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();

            int index = 0;
            for(Card c : cards) {
                BufferedImage io = ImageIO.read(c.toFile());
                g.drawImage(io, index * 691, 0, null);
                index++;
            }

            if(cards.length == 3) {
                File back = new File(Bot.CARD_DIRECTORY+File.separator+"back.png");

                BufferedImage io = ImageIO.read(back);
                g.drawImage(io, 3 * 691, 0, null);

                io = ImageIO.read(back);
                g.drawImage(io, 4 * 691, 0, null);
            }

            if(cards.length == 4) {
                File back = new File(Bot.CARD_DIRECTORY+File.separator+"back.png");

                BufferedImage io = ImageIO.read(back);
                g.drawImage(io, 4 * 691, 0, null);
            }

            ImageIO.write(image, "png", combined);
            return combined;
        } catch(Exception e) {
            throw new RuntimeException("Failed to create multi-image card.");
        }
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(number.getNumerical(), o.getNumber().getNumerical());
    }
}
