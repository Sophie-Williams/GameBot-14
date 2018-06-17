package com.cpjd.modules;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

/*
 * Commands:
 * 1) ranks
 * 2) reset
 * 3)
 */
public class Rankings extends Module {
    public Rankings(TextChannel poker) {
        super(poker);
    }

    @Override
    public boolean commandReceived(Member author, String message) {
        return false;
    }
}
