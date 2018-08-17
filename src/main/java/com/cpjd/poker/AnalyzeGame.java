package com.cpjd.poker;

import com.cpjd.models.Card;
import com.cpjd.models.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AnalyzeGame {

    public static String lastGameResults = "No games played yet.";

    /**
     * Analyzes a poker game, this only generates a {@link GameResult} object representing the game's
     * ending state. The calling class should handle distribution of the pot
     * @param drawn The list of cards that were drawn, could be between 3 and 5
     * @return The state of the game, listing winners and losers
     */
    public GameResult analyzeGame(ArrayList<Player> playersList, ArrayList<Card> drawn) {
        ArrayList<Player> winners = new ArrayList<>();
        ArrayList<Player> losers = new ArrayList<>();

        ArrayList<Player> players = new ArrayList<>(playersList);

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
            GameResult gr = new GameResult(winners, losers);
            gr.setPlayersFolded(true);
            return gr;
        }

        // Looks like there are still players who haven't folded, so keep analyzing
        AnalyzeHand analyzeHand = new AnalyzeHand(drawn);

        // Assign hand values to all the players
        for(Player p : playersList) {
            p.setValue(analyzeHand.analyzePlayer(p));
        }

        // Now, sort players
        players.sort(Comparator.comparingInt(o -> o.getValue().getCategory().getValue()));

        // Determine highest, this will be the player at the end of the array
        HandValue winningCategory = players.get(players.size() - 1).getValue();

        // Remove all players who don't have an equivalent HandValue category
        for(int i = 0; i < players.size() - 1; i++) {
            if(!players.get(i).getValue().getCategory().equals(winningCategory.getCategory())) {
                losers.add(players.remove(i));
                i--;
            }
        }

        // Okay, next resort the array with HandValue.distinguish
        players.sort((o1, o2) -> HandValue.distinguish(o1.getValue(), o2.getValue()));
        Collections.reverse(players);

        HandValue winning = players.get(players.size() - 1).getValue();

        // Remove all that don't match
        for(int i = 0; i < players.size() - 1; i++) {
            if(HandValue.distinguish(players.get(i).getValue(), winning) < 0) {
                losers.add(players.remove(i));
                i--;
            }
        }

        // The remaining players are the winners!
        winners.addAll(players);

        // DEBUG
        StringBuilder results = new StringBuilder();
        for(Player p : playersList) {
            results.append("PLAYER=").append(p.getMember() == null ?  p.getDebugName() : p.getMember().getNickname()).append(" HAND=(").append(p.getCard1().toString()).append(", ")
                    .append(p.getCard2().toString()).append(") CPU-EVAL: ").append(p.getValue().toString()).append(" FOLDED=").append(p.isFolded())
                    .append(" WINNER=").append(winners.contains(p)).append("\n");
        }

        results.append("\nDrawn Cards\n");

        for(Card c : drawn) {
            results.append(c.toString()).append(", ");
        }

        lastGameResults = results.toString().substring(0, results.toString().length() - 2);

        System.out.println(lastGameResults);

        return new GameResult(winners, losers);
    }

}
