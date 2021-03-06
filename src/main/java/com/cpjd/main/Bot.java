package com.cpjd.main;

import com.cpjd.modules.Game;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.File;
import java.util.ArrayList;

public class Bot extends ListenerAdapter {

    public static String CARD_DIRECTORY;

    private Game game;

    public Bot(Guild guild) {
        game = new Game(guild.getTextChannelsByName("poker", true).get(0));
    }

    public static void main(String[] args) {
        CARD_DIRECTORY = args[0];

        if(args.length != 2) {
            System.err.println("Error: Too few or too many args.");
            return;
        }

        // validate file
        File cardDirectory = new File(CARD_DIRECTORY);
        System.out.println("Loading card directory "+CARD_DIRECTORY);

        if(cardDirectory.exists()) {
            System.out.println("Card directory loaded.");

            // Clear old files
            File[] temp = cardDirectory.listFiles();
            for(File f : temp) {
                if(!f.getName().contains("temp")) continue;
                f.delete();
            }

        } else {
            System.err.println("Card directory not found. Exiting...");
            return;
        }

        try {
            JDA jda = new JDABuilder(AccountType.BOT).setToken(args[1]).buildBlocking();
            jda.addEventListener(new Bot(jda.getGuilds().get(0)));
        } catch (Exception e) {
            System.err.println("Error logging in to Discord.");
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            if(event.getAuthor().isBot() || !event.getTextChannel().getName().equalsIgnoreCase("poker")) return;

            game.commandReceived(event.getMember(), event.getMessage().getRawContent());

             event.getMessage().delete().queue(); // delete the message to keep the channel clean

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error");
        }

    }
}
