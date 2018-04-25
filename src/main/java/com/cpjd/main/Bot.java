package com.cpjd.main;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class Bot {

    public static void main(String[] args) {
        try {
            JDA jda = new JDABuilder(AccountType.BOT).setToken("token").buildBlocking();
        } catch(Exception e) {
            System.out.println("Error");
        }
    }

}
