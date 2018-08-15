package com.cpjd.modules;

import com.cpjd.comms.Responder;
import com.cpjd.models.Card;
import com.cpjd.models.Player;
import com.cpjd.poker.AnalyzeGame;
import com.cpjd.poker.GameResult;
import lombok.Getter;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;

/**
 * A round represents the time between dealing the cards and a player winning the money.
 * This class will handle that entire phase.
 *
 * Each round is consisted of several betting phases
 */
public class Round {

    private TurnProcessor tp; // Receives turns from the player whose turn it is

    private ArrayList<Player> players; // The list of players in the round
    private Responder responder; // For output

    /**
     * A deck, starting off at 52 cards, shuffled according to
     * atmospheric noise
     */
    private ArrayList<Card> deck;
    /**
     * The cards currently on the table
     */
    private ArrayList<Card> drawn;

    /**
     * This is what the bet is currently at. It resets when new cards are drawn
     */
    private double bet;

    /**
     * This is what the pot is currently at.
     */
    @Getter
    private double pot;

    /**
     * This represents the player whose turn it is
     */
    @Getter
    private Player currentTurn;

    private int elapsedTurns;

    /**
     * The index of the player that began the betting cycle
     */
    private int turnLeader;

    public interface RoundEndListener {
        void roundEnded(int newTurnLeader);
    }

    private RoundEndListener listener;

    public void setListener(RoundEndListener listener) {
        this.listener = listener;
    }

    public Round(Responder responder, ArrayList<Player> players, int turnLeader) {
        this.responder = responder;
        this.players = players;
        this.drawn = new ArrayList<>();
        tp = new TurnProcessor();
        this.turnLeader = turnLeader;
    }

    /**
     * Begins the round by shuffling the deck, dming players, and initializing the first round
     */
    public void begin() {
        currentTurn = players.get(turnLeader);

        deck = Card.deck();

        // Deal cards
        for(Player p : players) {
            p.setFolded(false);
            p.setCardCycleBet(0);

            p.dealHand(deck.remove(0), deck.remove(0));

            pot += p.withdraw(2);
        }

        responder.begin(2, currentTurn);

        responder.dmHands();

        beginTurn();
    }

    /**
     * Ends the round by finding a winner
     * @param cancelRound true to not process a winner
     */
    public void end(boolean cancelRound) {
        if(!cancelRound) {
            AnalyzeGame analyzeGame = new AnalyzeGame();
            GameResult result = analyzeGame.analyzeGame(players, drawn);

            // Handle the winners
            for(Player p : players) {
                p.setGameBank(p.getGameBank() - p.getWager());
            }

            ArrayList<Player> winners = result.getWinners();

            if(winners.size() == 1) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.magenta);
                embed.addField(winners.get(0).getMember().getNickname()+" won the pot of $"+(int)pot+"!", "", false);
                String info = "";
                for(Player p : players) {
                    info += p.getMember().getNickname() +"'s Hand: "+p.getCard1().toString()+", "+p.getCard2().toString() + "\n";
                }
                embed.addField("Hands", info, false);

                if(winners.size() > 1) {
                    // Display hand
                    for(Player p : winners) {
                        embed.addField(p.getMember().getNickname(), p.getCard1().toString()+", "+p.getCard2().toString()+"\n"+p.getValue().getCategory(), true);
                    }
                }
                responder.getPoker().sendMessage(embed.build()).queue();
            } else {
                String players = "";
                for(Player p : winners) {
                    players += p.getMember().getNickname()+", ";
                }
                responder.post("Tie! Winners: "+players+" each won $"+pot / winners.size()+".");
            }

            for(Player p : winners) {
                p.deposit(pot / winners.size());
            }
        }

        if(!cancelRound) {
            int newTurnLeader = turnLeader + 1;
            if(turnLeader == players.size()) turnLeader = 0;
            if(listener != null) listener.roundEnded(newTurnLeader);
        }
    }

    private void nextBettingPhase() {
        // Reset the cycle betting for each player
        bet = 0;
        for(Player p : players) {
            p.setCardCycleBet(0);
        }

        elapsedTurns = 0;

        // Check if the round needs to end
        if(drawn.size() == 5) {
            end(false);
            return;
        }

        // Draw cards
        if(drawn.size() == 0) {
            drawn.add(deck.remove(0));
            drawn.add(deck.remove(0));
            drawn.add(deck.remove(0));
            responder.postDrawn(drawn);
        } else {
            drawn.add(deck.remove(0));
            responder.postDrawn(drawn);
        }

        // Set the new turn leader
        currentTurn = players.get(turnLeader);

        beginTurn();
    }


    /**
     * Begins a user's turn, this will assume that {@link Round#currentTurn} is
     * now taking their turn
     */
    private void beginTurn() {
        // Determine if all the other players have folded
        boolean allFolded = true;
        for(Player p : players) {
            if(!p.isFolded() && !p.matchesMember(currentTurn.getMember())) {
                allFolded = false;
                break;
            }
        }
        if(allFolded) {
            end(false);
            return;
        }

        // Check if the player is folded
        if(currentTurn.isFolded() || currentTurn.isAllIn()) {
            nextTurn();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.blue);
        embed.setTitle(currentTurn.getMember().getNickname()+"'s turn.");
        //embed.addField("Use `bet <amount>`, `match`, `fold`, `check`, or `all in`.", "", false);
        embed.addField("Bank", "$"+(int)(currentTurn.getGameBank() - currentTurn.getWager()), true);
        //embed.addField("Min bet $","$"+(int)(bet - currentTurn.getCardCycleBet()), true);
        //embed.addField("Pot", "$"+(int)pot, true);
        embed.addField("Wagered ", "$"+(int)currentTurn.getWager(), true);
        embed.addField("Call", "$"+(int)((bet - currentTurn.getCardCycleBet())), true);
        responder.getPoker().sendMessage(embed.build()).queue();
    }

    public TurnProcessor turn() {
        return tp;
    }

    // wager show ante
    // flopped cards after a fold

    public class TurnProcessor {
        /**
         * Bets the specified amount
         * @param amount the amount to bet
         */
        public void bet(double amount) {
            if(amount < 0) return;

            // Verify the player has enough money
            if(currentTurn.getWager() + amount > currentTurn.getGameBank()) {
                if((bet - currentTurn.getCardCycleBet()) < (currentTurn.getGameBank() - currentTurn.getWager())) {
                    EmbedBuilder err = new EmbedBuilder();

                    err.setTitle("Insufficient funds.");

                    err.setColor(Color.red);
                    responder.getPoker().sendMessage(err.build()).queue();
                    return;
                }
            }

            if(amount < bet -  currentTurn.getCardCycleBet()) {
                if((bet - currentTurn.getCardCycleBet()) < (currentTurn.getGameBank() - currentTurn.getWager())) {
                    EmbedBuilder err = new EmbedBuilder();

                    err.setTitle("Your bet of $"+amount+" does not meet the minimum bet of $"+(bet - currentTurn.getCardCycleBet())+".");

                    err.setColor(Color.red);
                    responder.getPoker().sendMessage(err.build()).queue();
                    return;
                }
            }

            double wager = currentTurn.wager(amount);
            if(wager > bet) {
                bet = wager;
            }
            pot += wager;

            currentTurn.setCardCycleBet(currentTurn.getCardCycleBet() + wager);

            /*
             * Send some feedback.
             */
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.orange);
            if(wager != 0) {
                //if(!raised || oldBet == 0)
                    embed.setTitle(currentTurn.getMember().getNickname()+" bet $"+(int)wager+". Pot: $"+(int)pot+".");
               // else if(amount - oldBet - currentTurn.getCardCycleBet() != 0) embed.setTitle(currentTurn.getMember().getNickname()+" raised $"+(int)(amount - oldBet - currentTurn.getCardCycleBet())+". Pot: $"+(int)pot+".");
            }
            else embed.setTitle(currentTurn.getMember().getNickname()+" checked. Pot: $"+(int)pot+".");

            if(currentTurn.isAllIn()) embed.addField(currentTurn.getMember().getNickname()+" is all in!", "", false);

            responder.getPoker().sendMessage(embed.build()).queue();

            nextTurn();
        }

        public void match() {
            bet(bet - currentTurn.getCardCycleBet());
        }

        public void fold() {
            currentTurn.setFolded(true);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.orange);
            embed.setTitle(currentTurn.getMember().getNickname()+" folded.");
            responder.getPoker().sendMessage(embed.build()).queue();

            nextTurn();
        }

        public void allIn() {
            bet(currentTurn.getGameBank() - currentTurn.getWager());
        }

        public void check() {
            bet(0);
        }
    }

    /**
     * Proceeds to the next turn, call this after a user makes their play.
     * This method will decide whether to draw cards or issue more turns to the users
     */
    public void nextTurn() {
        elapsedTurns++;

        // set the next player
        int currentTurnIndex = players.indexOf(currentTurn);
        currentTurnIndex++;
        if(currentTurnIndex == players.size()) currentTurnIndex = 0;
        currentTurn = players.get(currentTurnIndex);

        // check if everyone is all in
        boolean allAllin = true;
        for(Player p : players) {
            if(!p.isAllIn() && !p.isFolded()) {
                allAllin = false;
                break;
            }
        }
        if(allAllin) {
            end(false);
            return;
        }

        // check if the next betting phase begins, this will happen when:
        // 1) every player has had at least 1 turn
        // 2) every player's betting cycle meets the current bet
        boolean minimumBetsMet = true;
        for(Player p : players) {
            if(p.isFolded()) continue;

            for(Player p2 : players) {
                if(p2.isFolded()) continue;

                if(p2.matchesMember(p.getMember())) continue;

                if(p.getCardCycleBet() != p2.getCardCycleBet()) {
                    minimumBetsMet = false;
                    break;
                }
            }
        }

        if(elapsedTurns >= players.size() && minimumBetsMet) {
            nextBettingPhase();
        } else {
            beginTurn();
        }
    }
}
