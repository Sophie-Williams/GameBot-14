package com.cpjd.poker;

import com.cpjd.models.Card;
import com.cpjd.models.Player;

import java.util.ArrayList;

public class AnalyzeGame {

    /**
     * Analyzes a poker game, this only generates a {@link GameResult} object representing the game's
     * ending state. The calling class should handle distribution of the pot
     * @param players The list of players that were in the round, including folded players
     * @param drawn The list of cards that were drawn, could be between 3 and 5
     * @return The state of the game, listing winners and losers
     */
    public GameResult analyzeGame(ArrayList<Player> players, ArrayList<Card> drawn) {
        ArrayList<Player> winners = new ArrayList<>();
        ArrayList<Player> losers = new ArrayList<>();

        // First things first, remove players that have folded from the players array
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).isFolded()) {
                losers.add(players.remove(i));
                i--;
            }
        }

        // Okay, if only one player is left, that means everyone else has folded
        if(players.size() == 1) {
            winners.add(players.get(0));
            return new GameResult(winners, losers);
        }

        // Looks like there are still players who haven't folded, so keep analyzing
        AnalyzeHand analyzeHand = new AnalyzeHand(drawn);

        // Assign hand values to all the players
        for(Player p : players) {
            p.setValue(analyzeHand.analyzePlayer(p));
        }

        // Now, sort players
        players.sort((o1, o2) -> HandValue.distinguish(o1.getValue(), o2.getValue()));

        // Determine highest, this will be the player at the end of the array
        HandValue winning = players.get(players.size() - 1).getValue();

        // Remove all players who don't have an equivalent HandValue
        for(int i = 0; i < players.size() - 1; i++) {
            if(!players.get(i).getValue().equals(winning)) {
                losers.add(players.remove(i));
                i--;
            }
        }

        // The remaining players are the winners!
        winners.addAll(players);

        return new GameResult(winners, losers);
    }

}
