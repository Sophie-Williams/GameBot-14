package com.cpjd.main;

import com.cpjd.game.Game;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.File;

public class Bot extends ListenerAdapter {

    public static String CARD_DIRECTORY;

    private Game game;

    public static void main(String[] args) {

        CARD_DIRECTORY = args[0];

        // validate file
        File cardDirectory = new File(CARD_DIRECTORY);
        System.out.println("Loading card directory "+CARD_DIRECTORY);
        if(cardDirectory.exists()) {
            System.out.println("Card directory loaded.");
        } else {
            System.err.println("Card directory not found. Exiting...");
            return;
        }

        try {
            JDA jda = new JDABuilder(AccountType.BOT).setToken("NDM5NTQ1OTE4OTI5NTAyMjA4.DcUuzw.QQIm6VbAVJ7kGnGdRhHfiq6bVuY").buildBlocking();
            jda.addEventListener(new Bot());
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(game == null) {
            game = new Game(event.getGuild());
            event.getTextChannel().sendMessage("Weak Texas-Holdem Bot Started!").queue();
        } else {
            if(!event.getTextChannel().getName().equals("poker")) return;
            game.messageReceived(event.getTextChannel(), event.getAuthor(), event.getMessage().getRawContent(), event);
        }
    }
}
