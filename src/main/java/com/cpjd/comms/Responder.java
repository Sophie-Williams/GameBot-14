package com.cpjd.comms;

import com.cpjd.models.Card;
import com.cpjd.models.Player;
import com.cpjd.poker.GameResult;
import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Responder is used for any sort of feedback to the user(s)
 *
 * @author Will Davies
 */
public class Responder {

    @Getter
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
        post("Computing...");

        String y = "Now the flop.";
        if(drawn.size() != 3) y = "Next card.";

        Message m = new MessageBuilder().append(y).build();
        poker.sendFile(Card.combine(true, drawn.toArray(new Card[0])), m).queue();
    }

    // winners array includes poorWinners
    public void postWinners(ArrayList<Player> winners, ArrayList<Player> poorWinners, double pot) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.magenta);

        // If there were poor winners
        if(poorWinners != null) {
            StringBuilder builder = new StringBuilder();
            for(Player p : poorWinners) {
                builder.append(p.getMember().getNickname()).append(" was poor, and only won $").append((int) (p.getWager() * players.size() / poorWinners.size())).append(". Hand: ").append(p.getCard1().toString()).append(", ").append(p.getCard2().toString()).append("\n");
            }
            for(Player p : winners) {
                if(!poorWinners.contains(p)) builder.append(p.getMember().getNickname()).append(" wins $").append((int) (pot / (winners.size() - poorWinners.size()))).append(". Hand: ").append(p.getCard1().toString()).append(", ").append(p.getCard2().toString()).append("\n");
            }
            embed.setTitle(builder.toString());
            poker.sendMessage(embed.build()).queue();
            return;
        }

        // One winner and folded
        if(winners.size() == 1 && winners.get(0).isFolded()) {
            embed.setTitle(winners.get(0).getMember().getNickname()+" won the pot of $"+(int)pot+"!");
            poker.sendMessage(embed.build()).queue();
            return;
        }

        // Multiple winners, not folded
        StringBuilder builder = new StringBuilder();
        for(Player p : winners) {
            builder.append(p.getMember().getNickname()).append(" wins $").append((int) (pot / winners.size())).append(". Hand: ").append(p.getCard1().toString()).append(", ").append(p.getCard2().toString()).append("\n");
        }
        embed.setTitle(builder.toString());
        poker.sendMessage(embed.build()).queue();
    }

    public void begin(double ante) {
        EmbedBuilder round = new EmbedBuilder();
        round.setColor(Color.white);
        round.setTitle("NEW ROUND");
        poker.sendMessage(round.build()).queue();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.orange);
        builder.setTitle("Everyone antes $"+(int)ante+". Pot: $"+(int)(ante * players.size())+".");
        poker.sendMessage(builder.build()).queue();
    }

    public void dmHands() {
        if(true) return;

        Player will = players.get(0);

        for(Player p : players) {
            Message message = new MessageBuilder().append(p.getMember().getNickname()).append(", your hand is: ").append(p.getCard1().toString()).append(", ").append(p.getCard2().toString()).append(".").build();

            File hand = Card.combine(false, p.getCard1(), p.getCard2());

            p.getMember().getUser().openPrivateChannel().queue((channel) ->
                    channel.sendFile(hand, message).queue());
        }
    }

    public void dm(Member member, String msg) {
        Message message = new MessageBuilder().append(msg).build();
        member.getUser().openPrivateChannel().queue((channel) ->
                channel.sendMessage(message).queue());
    }


}
