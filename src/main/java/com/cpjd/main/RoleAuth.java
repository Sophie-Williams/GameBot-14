package com.cpjd.main;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class RoleAuth {

    /**
     * These are the conditions that decide if a user is allowed to run admin level
     * bot commands, such as starting a game, or depositing money in player's accounts.
     * @param user the user to authenticate
     * @return true if the user has elevated privledges
     */
    public static boolean hasElevatedPrivledges(Member user) {
        return user.getNickname().equals("Will") || user.isOwner();
    }

}
