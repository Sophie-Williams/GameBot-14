package com.cpjd.modules;

import com.cpjd.comms.Responder;
import com.cpjd.main.RoleAuth;
import com.cpjd.models.Player;
import com.cpjd.poker.AnalyzeGame;
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

    private final String HELP_TEXT = "";

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
                if(players.size() <= 1) {
                    responder.post("At least 2 players required to start game.");
                    return;
                }

                try {
                    double deposit = Double.parseDouble(message.split("\\s+")[1]);
                    for(Player p : players) {
                        p.deposit(deposit);
                    }
                    state = STATE.IN_PROGRESS;
                    activeRound = new Round(responder, players, 0);
                    activeRound.begin();
                    activeRound.setListener(new Round.RoundEndListener() {
                        @Override
                        public void roundEnded(int newTurnLeader) {
                            // Remove bankrupt players
                            for(int i = 0; i < players.size(); i++) {
                                if(players.get(i).getGameBank() <= 0) {
                                    responder.post(players.get(i).getMember().getNickname()+" was removed because of poverty.");
                                    players.remove(i);
                                    i--;
                                }
                            }

                            if(players.size() == 1) {
                                responder.post(players.get(0).getMember().getNickname()+" is the only player left! Stopping the game...");
                                state = STATE.IDLE;
                                players.clear();
                                return;
                            }

                            activeRound = new Round(responder, players, newTurnLeader);
                            activeRound.setListener(this);
                            activeRound.begin();
                        }
                    });

                } catch(Exception e) {
                    responder.post("An error occur while trying to start the game. Please use correct syntax: start <starting-bank>.");
                }
            }
        }

        /*
         * Joining the game
         */
        if((message.equalsIgnoreCase("j") || message.equalsIgnoreCase("join")) && state == STATE.OPEN) {
            for(Player p1 : players) {
                if(p1.matchesMember(author)) return;
            }

            if(author.getNickname() == null || author.getNickname().equals("") || author.getNickname().equals("null")) {
                responder.post("You must have a nickname to play.");
                return;
            }

            Player p = save.search(author);
            players.add(p);

            //players.add(new Player(author.getGuild().getMembersByNickname("Sam", true).get(0), 0));
            //players.add(new Player(author.getGuild().getMembersByNickname("Alex", true).get(0), 0));

            responder.post(author.getNickname()+" joined the game. "+players.size()+" players in the game.");
        }

        /*
         * Turn syntax
         */
        if(activeRound != null && activeRound.getCurrentTurn().matchesMember(author) && state == STATE.IN_PROGRESS) {
            if(message.equalsIgnoreCase("fold") || message.equalsIgnoreCase("f")) activeRound.turn().fold();
            else if(message.equalsIgnoreCase("all in")) activeRound.turn().allIn();
            else if(message.equalsIgnoreCase("check") || message.equalsIgnoreCase("c")) activeRound.turn().check();
            else if(message.equalsIgnoreCase("match") || message.equalsIgnoreCase("m") || message.equalsIgnoreCase("call")) activeRound.turn().match();
            else if(message.toLowerCase().startsWith("bet")) {
                try {
                    activeRound.turn().bet(Double.parseDouble(message.split("\\s+")[1]));
                } catch(Exception e) {
                    EmbedBuilder err = new EmbedBuilder();
                    err.setColor(Color.red);
                    err.setTitle("Incorrect syntax. Please use bet <amount>.");
                    responder.getPoker().sendMessage(err.build()).queue();
                }
            } else {
                try {
                    double bet = Double.parseDouble(message);
                    activeRound.turn().bet(bet);
                } catch(Exception e) {
                    //ignore
                }
            }
        }

        if(message.equalsIgnoreCase("banks") && state == STATE.IN_PROGRESS) {
            banks();
        }

        else if(message.equalsIgnoreCase("results") && state == STATE.IN_PROGRESS) {
            responder.post( "```Markdown\n"+AnalyzeGame.lastGameResults+"```");
        }

        if(message.equalsIgnoreCase("help")) {
            StringBuilder helper = new StringBuilder("```Markdown\n" +
                    "#Commands\n");

            ArrayList<String> temp = new ArrayList<>();
            temp.add("• bet [$] - Bets the specified amount");
            temp.add("• call OR match- Auto-bets the minimum amount to stay in the round");
            temp.add("• check - Bets $0.");
            temp.add("• fold - Leaves the current betting round");
            temp.add("• all in - Bets the entire bank");
            temp.add("• banks - Outputs banking information during the game");
            temp.add("• results - Outputs information about the last poker game results");
            temp.add("• open - Opens a new poker game and allows people to join");
            temp.add("• join - Joins an open poker game");
            temp.add("• start [$] - Starts the game with the specified beginning bank for each player");
            for(String s : temp) {
                helper.append(s).append("\n");
            }
            helper.append("```");
            responder.dm(author, helper.toString());
        }
    }


    /**
     * Outputs the information about money to the channel
     */
    private void banks() {
        if(activeRound == null) return;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Texas Hold-Em Standings");
        eb.setColor(Color.BLUE);
        for(Player p : players) {
            eb.addField(p.getMember().getNickname(), "Bank: $"+(int)(p.getGameBank() - p.getWager())+(p.getWager() > 0 ? "\nWager: $"+(int)p.getWager() : ""), true);
        }
        if(activeRound.getPot() > 0) eb.addField("Pot", "$"+(int)round(activeRound.getPot()), true);

        poker.sendMessage(eb.build()).queue();
    }

    private double round(double value) {
        int scale = (int) Math.pow(10, 2);
        return (double) Math.round(value * scale) / scale;
    }

    // TODO call needs to check for insufficient funds
}
