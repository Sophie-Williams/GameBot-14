package com.cpjd.comms;

import com.cpjd.models.Card;
import com.cpjd.models.Player;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.*;
import java.util.ArrayList;

/**
 * Responder is used for any sort of feedback to the user(s)
 *
 * @author Will Davies
 */
public class Responder {

    private TextChannel poker;
    private ArrayList<Player> players;

    public Responder(TextChannel poker, ArrayList<Player> players) {
        this.poker = poker;
        this.players = players;
    }

    public void post(String message) {
        poker.sendMessage(message).queue();
    }

    public void postDrawn(ArrayList<Card> drawn) {
        Message m = new MessageBuilder().append("Now the flop.").build();
        poker.sendFile(Card.combine(drawn.toArray(new Card[0])), m).queue();
    }

    public void dmHands() {
        for(Player p : players) {
            System.out.println(p.getCard1().toString());
            System.out.println(p.getCard2().toString());

            Message message = new MessageBuilder().append("Your hand is: ").build();

            File hand = Card.combine(p.getCard1(), p.getCard2());

            p.getMember().getUser().openPrivateChannel().queue((channel) ->
                    channel.sendFile(hand, message).queue());
        }
    }

}
