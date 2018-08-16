package com.cpjd.poker;

import com.cpjd.models.Player;
import lombok.Data;

import java.util.ArrayList;

@Data
public class GameResult {

    private ArrayList<Player> winners;
    private ArrayList<Player> losers;

    public GameResult(ArrayList<Player> winners, ArrayList<Player> losers) {
        this.winners = winners;
        this.losers = losers;
    }

    private boolean playersFolded; // true if the winner was determined because other players folded

}
