package com.cpjd.game;

import com.cpjd.comms.Responder;
import com.cpjd.models.Card;
import com.cpjd.models.NUMBER;
import com.cpjd.models.Player;
import com.cpjd.models.SUIT;
import com.cpjd.utils.SaveFile;
import lombok.Data;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;

@Data
public class Game {

    public boolean isJoinable;
    public boolean started;
    private ArrayList<Player> players;
    private ArrayList<Card> cards;
    private SaveFile save;
    private Responder responder;

    private double pot;

    private int index = 0;

    public Game(Guild guild) {
        isJoinable = true;
        players = new ArrayList<>();
        save = new SaveFile();
    }

    /**
     *
     * @param poker guaranteed to be a channel called "poker"
     * @param author
     * @param message
     */
    public void messageReceived(TextChannel poker, User author, String message,/* if needed */ MessageReceivedEvent event) {
        if(author.isBot()) return;

        try {
            if(started) {
                if(getPlayer(author) == null) {
                    poker.sendMessage("You're not in the game. Begone thot!").queue();
                    return;
                }

                gameMessageReceived(poker, author, message, event);
                return;
            }

            if(!isJoinable && message.startsWith("open")) {
                // user wants to open the game, check permissions
                if(event.getMember().isOwner() || author.getName().equals("Will")) {
                    event.getMessage().delete().queue();
                    isJoinable = true;
                    poker.sendMessage("Game opened. Players may now join with `join`").queue();
                    return;
                }
            } else if(isJoinable && message.startsWith("start")) {
                if(message.split("\\s+").length < 2) {
                    poker.sendMessage("Please specify deposit amount for all players like `start 100`").queue();
                    return;
                }

                // user wants to start the game, check permissions
                if(event.getMember().isOwner() || author.getName().equals("Will")) {
                    event.getMessage().delete().queue();
                    isJoinable = false;
                    started = true;
                    // deposit cash
                    double deposit = Double.parseDouble(message.split("\\s+")[1]);
                    for(Player p : players) {
                        // transfer previous money to game money
                        p.setGameBank(0);
                        p.add(deposit);
                    }
                    poker.sendMessage("Game started.").queue();
                    round(poker, true);
                    return;
                }
            }

            if(message.startsWith("join")) {
                if(isJoinable) {
                    // Join the player
                    joinPlayer(poker, author);
                    return;
                } else {
                    event.getMessage().delete().queue();
                    poker.sendMessage("Game not joinable/active.").queue();
                    return;
                }
            }

            // command not recognized, delete
            event.getMessage().delete().queue();
        } catch(Exception e) {
            event.getMessage().delete().queue();
            poker.sendMessage("Command not recognized.").queue();
        }
    }

    private void round(TextChannel poker, boolean first) {
        index = 0;

        if(!first) {
            /*
             * Figure out who won
             */
            ArrayList<Player> candidates = new ArrayList<>();

            for(Player p : candidates) {
                if(!p.isFolded()) candidates.add(p);
            }

            Player winner = HandEvaluator.evaluate(players, cards);

            /*
             * Remove bankrupt players
             */
            for(int i = 0; i < players.size(); i++) {
                if(players.get(i).getGameBank() == 0) {
                    players.remove(i);
                    poker.sendMessage(players.get(i).getUser().getName()+" was removed from the game due to bankruptcy.").queue();
                    i--;
                }
            }

        }
        poker.sendMessage("A new round has begun!").queue();

        /*
         * calculate ante
         */


        turn(poker);

    }

    private void turn(TextChannel poker) {
        index++;
        if(players.get(index).isFolded) index++;
        if(index == players.size()) index = 0;

        poker.sendMessage("It's "+players.get(index).getUser().getName()+" turn."+(pot > 0 ?  " The pot is at $"+pot : " ")).queue();
    }

    private void gameMessageReceived(TextChannel poker, User author, String message, MessageReceivedEvent event) {
        Player p = getPlayer(author);

        if(p == null) throw new RuntimeException("Null player exception");

        if(message.startsWith("withdraw")) {
            double old = p.getGameBank();
            p.transfer(Double.parseDouble(message.split("\\s+")[1]));
            event.getMessage().delete().queue();
            poker.sendMessage(author.getName()+" transferred $"+(p.getGameBank() - old)+" from bank to active game bank.").queue();
            return;
        }

        if(message.startsWith("fold") || message.startsWith("bet") || message.startsWith("check") || message.startsWith("all in") ||
                message.startsWith("call")) {
            if(!getPlayer(author).getUser().equals(author.getId())) {
                poker.sendMessage("It's not your turn, "+author.getName()+".").queue();
                return;
            }

            if(message.startsWith("fold")) {
                p.setFolded(true);
            }
        }
    }


    private Player getPlayer(User author) {
        for(Player p : players) {
            if(p.getUser().getId().equals(author.getId())) return p;
        }
        return null;
    }

    private void joinPlayer(TextChannel poker, User author) {
        // load players old data if applicable
        players.add(save.load(author));
        // feedback
        poker.sendMessage(author.getName()+" joined the game with $"+players.get(players.size() - 1).getBank()+". There are "+players.size()+" player(s) in the game.").queue();
    }



}
