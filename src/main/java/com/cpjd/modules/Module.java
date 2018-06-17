package com.cpjd.modules;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class Module {

    protected TextChannel poker;

    public Module(TextChannel poker) {
        this.poker = poker;
    }

    /**
     * Called by the API when a message is received from a text channel named "poker"
     * @return true if the command was processed, otherwise the next module is sent the commandReceived event.
     */
    public abstract boolean commandReceived(Member author, String message);

}
