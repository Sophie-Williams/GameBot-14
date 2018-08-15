package com.cpjd.poker;

import com.cpjd.models.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class GameResult {

    private ArrayList<Player> winners;
    private ArrayList<Player> losers;

}
