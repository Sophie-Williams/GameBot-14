package com.cpjd.poker;

import com.cpjd.comms.Responder;
import com.cpjd.models.Card;
import com.cpjd.models.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * GameEvaluator analyses a completed round of poker.
 *
 * It will do the following:
 * 1) Determine the winner
 * 2) Distribute the pot of money to the players' game-banks
 */
public class GameEvaluator {

    private Responder responder;
    private ArrayList<Player> players;
    private ArrayList<Card> drawn;
    private double pot;

    public GameEvaluator(Responder responder, ArrayList<Card> drawn, ArrayList<Player> players, double pot) {
        this.responder = responder;
        this.players = players;
        this.pot = pot;
        this.drawn = drawn;
    }

    public void evaluate() {
        // Firstly, check if everyone is folded
        int notFolded = 0;
        for(Player p : players) {
            if(p.isFolded()) notFolded++;
        }
        if(notFolded == 1) {
            for(Player p : players) {
                if(!p.isFolded()) {
                    winner(p);
                    break;
                }
            }
            return;
        }

        // A winner needs to be determined from the data above. To do this, each player will be assigned
        // a HandValue object that represents the value of the player's best 5 cards.
    }

    private void winner(ArrayList<Player> winners) {
        if(winners.size() == 1) {
            responder.post(winners.get(0).getMember().getNickname()+" won the pot of $"+pot+"!!!!");
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

    private void winner(Player ... winners) {
        winner(new ArrayList<>(Arrays.asList(winners)));
    }


}
