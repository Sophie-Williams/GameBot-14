package com.cpjd.modules;

import com.cpjd.comms.Responder;
import com.cpjd.main.RoleAuth;
import com.cpjd.models.Card;
import com.cpjd.models.Player;
import com.cpjd.utils.SaveFile;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * The game module handles the "Texas Hold-em" game and its related flow and commands.
 */
public class Game extends Module {

    private enum STATE {
        IDLE, OPEN, IN_PROGRESS
    }

    private STATE state = STATE.IDLE;

    private Responder responder;
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Card> deck;
    private ArrayList<Card> drawn = new ArrayList<>();
    private int firstTurnIndex = 0;
    private int turn = 0;
    private double pot;
    private double bet;
    private int elapsedTurns;
    private SaveFile save;

    public Game(TextChannel poker) {
        super(poker);

        responder = new Responder(poker, players);
    }

    @Override
    public boolean commandReceived(Member author, String message) {
        /*
         * Manage game state
         */
        if(RoleAuth.hasElevatedPrivileges(author)) {
            if(message.equals("open") && state == STATE.IDLE) {
                state = STATE.OPEN;
                responder.post("Game opened. Players may now join with \"join\"");
                save = new SaveFile();
                return true;
            }
            else if(message.equals("end") && state == STATE.IN_PROGRESS) {
                // transfer winnings
                for(Player p : players) {
                    p.setBank(p.getGameBank());
                }

                state = STATE.IDLE;
                responder.post("Game closed.");
                save.save(players);
                players.clear();
                turn = 0;
                firstTurnIndex = 0;
                pot = 0;
                bet = 0;
                return true;
            } else if(message.startsWith("start") && players.size() > 0 && state == STATE.OPEN && message.split("\\s+").length == 2) {
                state = STATE.IN_PROGRESS;
                responder.post("Game started, each player gets a $"+message.split("\\s+")[1]+" deposit");


                double deposit;

                try {
                    deposit = Double.parseDouble(message.split("\\s+")[1]);
                } catch (Exception e) {
                    deposit = 0;
                    responder.post("Invalid syntax. Defaulting deposit to $0.");
                }

                for(Player p : players) {
                    p.setGameBank(0);
                    p.deposit(deposit);
                }

                beginRound();
                return true;
            }
        }

        /*
         * Manage joining
         */
        if(message.equalsIgnoreCase("join") && state == STATE.OPEN) {
            // Check if the player is already on the team
            for(Player p : players) {
                if(p.matchesMember(author)) {
                    responder.post("You're already in the game.");
                    return true;
                }
            }

            // Add the player
            players.add(new Player(author.getGuild().getMembersByNickname("Sam", true).get(0), 0));
            players.add(save.search(author));
            responder.post(author.getNickname()+" joined! "+players.size()+" in the game!");
            return true;
        }

        /*
         * Manage leaving
         */
        if(message.equalsIgnoreCase("leave") && state != STATE.IDLE) {
            for(Player p : players) {
                if(p.matchesMember(author)) {
                    p.leave();
                    responder.post(author.getNickname()+" left the game.");
                    return true;
                }
            }
        }

        /*
         * Manage player turns
         */
        if(players.get(turn).matchesMember(author) && state == STATE.IN_PROGRESS) {
            System.out.println("here");

            if(message.startsWith("bet")) {
                if(!message.contains("\\s+") || message.split("\\s+").length != 2) {
                    responder.post("Invalid syntax, please use bet <amount>.");
                    return true;
                }

                try {
                    double bet = Double.parseDouble(message.split("\\s+")[1]);

                    // Check if minimum bet is met
                    if(bet < this.bet) {
                        responder.post("Minimum bet is $"+this.bet+". Try again.");
                        return true;
                    }

                    bet = players.get(turn).wager(bet);

                    pot += bet;
                    if(bet > this.bet) this.bet = bet; // update the max bet

                    responder.post(players.get(turn).getMember().getNickname()+" bet $"+round(bet)+". The pot is now $"+round(pot)+".");

                    if(players.get(turn).isAllIn()) responder.post(players.get(turn).getMember().getNickname()+" is ALL IN!");
                } catch(Exception e) {
                    responder.post("Invalid syntax, please use bet <amount>.");
                    return true;
                }
            }
            else if(message.equalsIgnoreCase("check")) {
                if(bet != 0) {
                    responder.post("You aren't allowed to check, the minimum bet is $"+this.bet);
                    return true;
                }
            }
            else if(message.equalsIgnoreCase("call")) {
                double bet = players.get(turn).wager(this.bet);

                pot += bet;
                if(bet > this.bet) this.bet = bet; // update the max bet

                responder.post(players.get(turn).getMember().getNickname()+" bet $"+round(bet)+". The pot is now $"+round(pot)+".");

                if(players.get(turn).isAllIn()) responder.post(players.get(turn).getMember().getNickname()+" is ALL IN!");

            }
            else if(message.equalsIgnoreCase("all in")) {
                double bet = players.get(turn).wager(players.get(turn).getGameBank());

                pot += bet;
                if(bet > this.bet) this.bet = bet; // update the max bet

                responder.post(players.get(turn).getMember().getNickname()+" bet $"+round(bet)+". The pot is now $"+round(pot)+".");
                responder.post(players.get(turn).getMember().getNickname()+" is ALL IN!");
            } else if(message.equalsIgnoreCase("fold")) {
                players.get(turn).setFolded(true);
            } else {
                return false;
            }

            turn();
            return true;
        }

        // Manage info
        if(state == STATE.IN_PROGRESS) {
            if(message.equalsIgnoreCase("banks")) {
                banks();
                return true;
            }
        }

        return false;
    }

    /**
     * Called at the beginning of each round
     */
    public void beginRound() {
        pot = 0;
        bet = 0;

        deck = Card.deck();

        drawn.clear();
        elapsedTurns = 0;

        for(Player p : players) {
            // Deal players cards
            p.dealHand(deck.remove(0), deck.remove(0));
            // Ante rules here TODO
            pot += p.withdraw(2);
        }

        firstTurnIndex = new Random().nextInt(players.size());

        responder.dmHands(); // send the users their hands

        turn();
    }

    /**
     * Called at the end of each round
     */
    public void endRound() {
       // HandEvaulator.evaluate(players, drawn);

        //new HandEvaluator(pot).evaluate(players, drawn);

        beginRound();
    }

    /**
     * Called at the beginning of each turn (before a player makes a move)
     *
     * This method sets up for the next turn
     */
    public void turn() {
        /*
         * Check if there is only one player who's not folded
         */
        int folded = 0;
        for(Player p : players) {
            if(p.isFolded()) folded++;
        }

        if(folded == 1) {
            endRound();
            return;
        }

        responder.post(players.get(turn).getMember().getNickname()+"'s turn. The bet is at $"+round(bet)+".");

        /*
         * Check if cards should be drawn,
         * 1) Everyone needs to have had at least one term
         * 2) Everyone's wager amount needs to be >= highest bet
         */
        boolean wagersMeetMinimum = true;
        for(Player p : players) {
            if(p.getWager() < this.bet) {
                wagersMeetMinimum = false;
                break;
            }
        }

        if(elapsedTurns >= players.size() && wagersMeetMinimum) {
            if(drawn.size() == 5) {
                endRound();
            } else if(drawn.size() == 0) {
                drawn.add(deck.remove(0));
                drawn.add(deck.remove(0));
                drawn.add(deck.remove(0));
                responder.postDrawn(drawn);
            } else {
                drawn.add(deck.remove(0));
                responder.postDrawn(drawn);
            }

            firstTurnIndex++;
            if(firstTurnIndex == players.size()) firstTurnIndex = 0;
            elapsedTurns = 0;
            turn = firstTurnIndex;
            return;
        }

        turn++;
        if(turn == players.size()) turn = 0;
        // Decide if cards need to be drawn OR round needs to be ended TODO

        elapsedTurns++;
    }

    /**
     * Outputs the information about money to the channel
     */
    public void banks() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Texas Hold-Em Standings");
        eb.setColor(Color.BLUE);
        for(Player p : players) {
            eb.addField(p.getMember().getNickname(), "Bank: $"+p.getGameBank()+(p.getWager() > 0 ? " Wager: $"+p.getWager() : ""), true);
        }
        eb.addBlankField(true);
        eb.addField("Pot", "$"+round(pot), true);

        poker.sendMessage(eb.build()).queue();
    }

    private double round(double value) {
        int scale = (int) Math.pow(10, 2);
        return (double) Math.round(value * scale) / scale;
    }
}
