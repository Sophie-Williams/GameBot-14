package com.cpjd.modules;

import com.cpjd.comms.Responder;
import com.cpjd.main.RoleAuth;
import com.cpjd.models.Player;
import com.cpjd.utils.SaveFile;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;

/**
 * The game module handles the "Texas Hold-em" game and its related flow and commands.
 */
public class Game {

    private enum STATE {
        IDLE, OPEN, IN_PROGRESS
    }

    private STATE state = STATE.IDLE;

    private Responder responder;
    private ArrayList<Player> players;
    private SaveFile save;

    private Round activeRound;

    private TextChannel poker;

    public Game(TextChannel poker) {
        this.poker = poker;
        players = new ArrayList<>();
        responder = new Responder(poker, players);
    }
    public void commandReceived(Member author, String message) {
        /*
         * Handle game state change
         */
        if(RoleAuth.hasElevatedPrivileges(author)) {
            if(message.equalsIgnoreCase("open") && state == STATE.IDLE) {
                state = STATE.OPEN;
                responder.post("Game is now open. Join the game with \"join\".");
                players.clear();
                save = new SaveFile();
            }
            else if(message.equalsIgnoreCase("end") && state == STATE.IN_PROGRESS) {
                // Force quit the round
                if(activeRound != null) activeRound.end(true);
                // Transfer game banks
                for(Player p : players) {
                    p.transfer();
                }
                // Save the player's scores
                save.save(players);
                state = STATE.IDLE;
                responder.post("Game ended.");
                players.clear();
            } else if(message.startsWith("start")) {
                try {
                    double deposit = Double.parseDouble(message.split("\\s+")[1]);
                    for(Player p : players) {
                        p.deposit(deposit);
                    }
                    state = STATE.IN_PROGRESS;
                    activeRound = new Round(responder, players);
                    activeRound.begin();
                } catch(Exception e) {
                    responder.post("An error occur while trying to start the game. Please use correct syntax: start <starting-bank>.");
                }
            }
        }

        /*
         * Joining the game
         */
        if(message.equalsIgnoreCase("join") && state == STATE.OPEN) {
            Player p = save.search(author);
            players.add(p);

            responder.post(author.getNickname()+" joined the game. "+players.size()+" players in the game.");
        }

        /*
         * Turn syntax
         */
        if(activeRound != null && activeRound.getCurrentTurn().matchesMember(author) && state == STATE.IN_PROGRESS) {
            if(message.equalsIgnoreCase("fold")) activeRound.turn().fold();
            else if(message.equalsIgnoreCase("all in")) activeRound.turn().allIn();
            else if(message.equalsIgnoreCase("check")) activeRound.turn().check();
            else if(message.equalsIgnoreCase("match")) activeRound.turn().match();
            else if(message.startsWith("bet")) {
                try {
                    activeRound.turn().bet(Double.parseDouble(message.split("\\s+")[1]));
                } catch(Exception e) {
                    responder.post("Incorrect syntax. Please use bet <amount>.");
                }
            }
        }

        if(message.equalsIgnoreCase("banks") && state == STATE.IN_PROGRESS) {
            banks();
        }
    }


    /**
     * Outputs the information about money to the channel
     */
    public void banks() {
        if(activeRound == null) return;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Texas Hold-Em Standings");
        eb.setColor(Color.BLUE);
        for(Player p : players) {
            eb.addField(p.getMember().getNickname(), "Bank: $"+p.getGameBank()+(p.getWager() > 0 ? " Wager: $"+p.getWager() : ""), true);
        }
        eb.addBlankField(true);
        eb.addField("Pot", "$"+round(activeRound.getPot()), true);

        poker.sendMessage(eb.build()).queue();
    }

    private double round(double value) {
        int scale = (int) Math.pow(10, 2);
        return (double) Math.round(value * scale) / scale;
    }
}
