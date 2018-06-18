package com.cpjd.modules;

import com.cpjd.comms.Responder;
import com.cpjd.models.Card;
import com.cpjd.models.Player;
import com.cpjd.poker.GameEvaluator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Random;

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

    public Round(Responder responder, ArrayList<Player> players) {
        this.responder = responder;
        this.players = players;
        this.drawn = new ArrayList<>();
        tp = new TurnProcessor();
    }

    /**
     * Begins the round by shuffling the deck, dming players, and initializing the first round
     */
    public void begin() {
        turnLeader = new Random().nextInt(players.size());
        currentTurn = players.get(turnLeader);

        deck = Card.deck();

        // Deal cards
        for(Player p : players) {
            p.setFolded(false);

            p.dealHand(deck.remove(0), deck.remove(0));

            pot += p.withdraw(2);
        }

        responder.post("Everyone antes $2.");

        responder.dmHands();

        beginTurn();
    }

    /**
     * Ends the round by finding a winner
     * @param cancelRound true to not process a winner
     */
    public void end(boolean cancelRound) {
        if(!cancelRound) new GameEvaluator(responder, drawn, players, pot).evaluate();
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
        turnLeader++;
        if(turnLeader == players.size()) turnLeader = 0;
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

        responder.post(currentTurn.getMember().getNickname()+"'s turn. The bet is at $"+bet+". The pot is at $"+pot+" Use `bet <amount>`, `match`, `fold`, `check`, or `all in`.");
    }

    public TurnProcessor turn() {
        return tp;
    }

    public class TurnProcessor {
        /**
         * Bets the specified amount
         * @param amount the amount to bet
         */
        public void bet(double amount) {
            if(amount < bet - currentTurn.getWager()) {
                responder.post("Your bet of $"+amount+" does not meet the minimum bet of $"+(bet - currentTurn.getWager())+".");
                return;
            }

            double wager = currentTurn.wager(amount);
            if(wager > bet) bet = wager;
            pot += wager;

            currentTurn.setCardCycleBet(currentTurn.getCardCycleBet() + wager);

            if(wager != 0) responder.post(currentTurn.getMember().getNickname()+" bet $"+wager+".");
            else responder.post(currentTurn.getMember().getNickname()+" checked.");

            if(currentTurn.isAllIn()) responder.post(currentTurn.getMember().getNickname()+" is all in!");

            nextTurn();
        }

        public void match() {
            bet(bet - currentTurn.getWager());
        }

        public void fold() {
            currentTurn.setFolded(true);

            responder.post(currentTurn.getMember().getNickname()+" folded.");

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
            if(!p.isAllIn()) {
                allAllin = false;
                break;
            }
        }
        if(allAllin) {
            end(false);
            return;
        }

        if(currentTurn.isFolded() || currentTurn.isAllIn()) {
            nextTurn();
            return;
        }

        // check if the next betting phase begins, this will happen when:
        // 1) every player has had at least 1 turn
        // 2) every player's betting cycle meets the current bet
        boolean minimumBetsMet = true;
        for(Player p : players) {
            for(Player p2 : players) {
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
