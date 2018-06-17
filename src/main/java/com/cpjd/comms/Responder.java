package com.cpjd.comms;

import com.cpjd.models.Card;
import com.cpjd.models.NUMBER;
import com.cpjd.models.Player;
import com.cpjd.models.SUIT;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.*;
import java.util.ArrayList;

public class Responder {

    public ArrayList<Player> players;

    public Responder(ArrayList<Player> players) {
        this.players = players;
    }

    public void dmHands() {
        for(Player p : players) {

            Message m = new MessageBuilder().append("Dab").build();


            File file = new File("C:\\Users\\Will Davies\\Downloads\\cards\\2C.png");

            System.out.println(file.getAbsolutePath()+","+file.getAbsoluteFile().exists());

            //File f = new
           // System.out.println(f.getAbsolutePath());

            p.getUser().openPrivateChannel().queue((channel) ->
                    channel.sendFile(new Card(SUIT.SPADES, NUMBER.ACE).toFile(), m).queue());
        }
    }

}
