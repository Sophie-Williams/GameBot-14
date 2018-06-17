package com.cpjd.modules;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class General extends Module {
    public General(TextChannel poker) {
        super(poker);
    }

    @Override
    public boolean commandReceived(Member author, String message) {
        return false;
    }
}
