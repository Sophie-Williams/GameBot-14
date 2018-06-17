package com.cpjd.models;

import com.cpjd.main.Bot;
import lombok.AllArgsConstructor;
import lombok.Data;
import sun.awt.geom.AreaOp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Data
@AllArgsConstructor
public class Card {

    private SUIT suit;
    private NUMBER number;

    public File toFile() {
        StringBuilder name = new StringBuilder();

        name.append(number.getID());

        if(suit == SUIT.SPADES) name.append("S");
        else if(suit == SUIT.DIAMONDS) name.append("D");
        else if(suit == SUIT.HEARTS) name.append("H");
        else if(suit == SUIT.CLUBS) name.append("C");

        return new File(Bot.CARD_DIRECTORY+File.separator+name.toString()+".png");
    }

    public static File combine(Card ... cards) {
        try {
            File combined = new File(Bot.CARD_DIRECTORY+File.separator+"combinedTemp.png");

            if(!combined.exists()) combined.createNewFile();

            BufferedImage image = new BufferedImage(691 * cards.length, 1056, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();

            int index = 0;
            for(Card c : cards) {
                BufferedImage io = ImageIO.read(c.toFile());
                g.drawImage(io, index * 691, 0, null);
                index++;
            }

            ImageIO.write(image, "png", combined);
            return combined;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
