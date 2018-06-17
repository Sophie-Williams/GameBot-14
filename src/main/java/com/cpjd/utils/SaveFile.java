package com.cpjd.utils;

import com.cpjd.main.Bot;
import com.cpjd.models.Player;
import net.dv8tion.jda.core.entities.User;

import java.io.*;
import java.util.ArrayList;

public class SaveFile {

    private ArrayList<String> lines;

    private File saveFile;

    public SaveFile() {
        lines = new ArrayList<>();

        saveFile = new File(Bot.CARD_DIRECTORY+File.separator+"save.save");

        if(!saveFile.exists()) {
            try {
                if(saveFile.createNewFile()) System.out.println("Save file created");
            } catch(IOException e) {
                System.err.println("Failed to create new save file!");
            }

        }

        try {
            FileReader fr = new FileReader(saveFile);
            BufferedReader br = new BufferedReader(fr);

            String line;
            while((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch(Exception e) {
            System.err.println("Error occurred while trying to read save file");
        }

    }

    public void save(ArrayList<Player> players) {
        ArrayList<String> toWrite = new ArrayList<>();

        for(Player p : players) {
            toWrite.add(p.getUser().getId() + " ," + String.valueOf(p.getBank()));
        }

        try {
            FileWriter fw = new FileWriter(saveFile);
            PrintWriter pw = new PrintWriter(fw);
            for(String s : toWrite) {
                pw.println(s);
            }

        } catch(Exception e) {
            System.err.println("Failed to write to save file");
        }

    }

    public Player load(User user) {
        for(String s : lines) {
            if(s.split(",")[0].equals(user.getId())) {
                return new Player(user, Double.parseDouble(s.split(",")[1]));
            }
        }

        return new Player(user, 0);
    }
}
