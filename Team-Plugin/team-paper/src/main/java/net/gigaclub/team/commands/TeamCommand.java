package net.gigaclub.team.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.gigaclub.team.Main;
import net.gigaclub.teamapi.Team;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class TeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        Translation t = Main.getTranslation();
        Team team = Main.getTeam();
        Gson gson = new Gson();
        int status;
        if(!(sender instanceof Player)) {
            Bukkit.getLogger().info("You´r not a Player  XD");
            return true;
        }
        if (args.length < 1) {
            t.sendMessage("team.command.to_less_arguments", player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length <= 2) {
                    status = team.createTeam(playerUUID, args[1]);
                    //grepper java switch
                    switch (status) {
                        case 0 -> t.sendMessage("team.command.create.success", player);
                        case 1 -> t.sendMessage("team.command.create.team_could_not_be_created", player);
                        case 2 -> t.sendMessage("team.command.create.team_with_this_name_already_exists", player);
                        case 3 -> t.sendMessage("team.command.create.user_has_no_access_to_create_a_team", player);
                        case 4 -> t.sendMessage("team.command.create.other_error", player);
                    }
                    //end grepper
                    break;
                }
                String description = this.getDescription(args, 2);
                status = team.createTeam(playerUUID, args[1], description);
                switch (status) {
                    case 0 -> t.sendMessage("team.command.create.success", player);
                    case 1 -> t.sendMessage("team.command.create.team_could_not_be_created", player);
                    case 2 -> t.sendMessage("team.command.create.team_with_this_name_already_exists", player);
                    case 3 -> t.sendMessage("team.command.create.user_has_no_access_to_create_a_team", player);
                    case 4 -> t.sendMessage("team.command.create.other_error", player);
                }
                break;
            case "edit":
                if (args.length == 2) {
                    t.sendMessage("team.command.to_less_arguments", player);
                    break;
                }
                switch (args[1].toLowerCase()) {
                    case "name" -> {
                        if (args.length == 3) {
                            t.sendMessage("team.command.to_less_arguments", player);
                            break;
                        }
                        status = team.editTeam(playerUUID, Integer.parseInt(args[2]), args[3]);
                        switch (status) {
                            case 0 -> t.sendMessage("team.command.edit.success", player);
                            case 1 -> t.sendMessage("team.command.edit.no_valid_team_found_for_this_user", player);
                            case 2 -> t.sendMessage("team.command.edit.other_error", player);
                        }
                    }
                    case "description" -> {
                        status = team.editTeam(playerUUID, Integer.parseInt(args[2]), "", this.getDescription(args, 3));
                        switch (status) {
                            case 0 -> t.sendMessage("team.command.edit.success", player);
                            case 1 -> t.sendMessage("team.command.edit.no_valid_team_found_for_this_user", player);
                            case 2 -> t.sendMessage("team.command.edit.other_error", player);
                        }
                    }
                }
                break;
            case "leave":
                status = team.leaveTeam(playerUUID, Integer.parseInt(args[1]));
                switch (status) {
                    case 0 -> t.sendMessage("team.command.leave.success", player);
                    case 1 -> t.sendMessage("team.command.leave.user_has_no_team", player);
                    case 2 -> t.sendMessage("team.command.leave.user_has_no_permission_to_leave_teams", player);
                    case 3 -> t.sendMessage("team.command.leave.other_error", player);
                }
                break;
            case "kick":
                int teamId = Integer.parseInt(args[1]);
                String playerToKick = Objects.requireNonNull(Bukkit.getPlayer(args[2])).getUniqueId().toString();
                status = team.kickMember(playerUUID, teamId, playerToKick);
                switch (status) {
                    case 0 -> t.sendMessage("team.command.kick.success", player);
                    case 1 -> t.sendMessage("team.command.kick.user_is_not_member_of_team", player);
                    case 2 -> t.sendMessage("team.command.kick.not_allowed_to_kick_owner", player);
                    case 3 -> t.sendMessage("team.command.kick.user_to_kick_does_not_exist", player);
                    case 4 -> t.sendMessage("team.command.kick.no_valid_team_found_for_this_user", player);
                    case 5 -> t.sendMessage("team.command.kick.other_error", player);
                }
                break;
            case "invite":
                //grepper java parse string to int
                teamId = Integer.parseInt(args[1]);
                //end grepper
                Player receiver = Bukkit.getPlayer(args[2]);
                if (receiver == null) {
                    t.sendMessage("team.command.invite.is_not_a_player", player);
                    break;
                }
                //grepper minecraft paper get uuid from player
                String receiverUUID = receiver.getUniqueId().toString();
                //end grepper
                status = team.inviteMember(playerUUID, teamId, receiverUUID);
                switch (status) {
                    case 0 -> {
                        t.sendMessage("team.command.invite.success", player);
                        JSONObject getTeam = team.getTeam(teamId);
                        String teamname = getTeam.getString("name");
                        JsonObject params = new JsonObject();
                        params.addProperty("teamname", teamname);
                        params.addProperty("receiver", receiver.getName());
                        JsonObject values = new JsonObject();
                        values.add("params", params);
                        t.sendMessage("team.command.invite.sender", player, values);
                        params = new JsonObject();
                        params.addProperty("teamname", teamname);
                        values = new JsonObject();
                        values.add("params", params);
                        t.sendMessage("team.command.invite.receiver", receiver, values);
                    }
                    case 1 -> t.sendMessage("team.command.invite.request_already_sent", player);
                    case 2 -> t.sendMessage("team.command.invite.user_is_already_member_of_this_team", player);
                    case 3 -> t.sendMessage("team.command.invite.user_to_invite_not_found", player);
                    case 4 -> t.sendMessage("team.command.invite.no_valid_team_found_for_the_user", player);
                    case 5 -> t.sendMessage("team.command.invite.other_error", player);
                }
                break;
            case "accept":
                teamId = Integer.parseInt(args[1]);
                status = team.acceptRequest(playerUUID, teamId);
                switch (status) {
                    case 0 -> t.sendMessage("team.command.accept.success", player);
                    case 1 -> t.sendMessage("team.command.accept.request_does_not_exist", player);
                    case 2 -> t.sendMessage("team.command.accept.team_does_not_exist", player);
                    case 3 -> t.sendMessage("team.command.accept.user_has_no_permission_to_accept_requests", player);
                    case 4 -> t.sendMessage("team.command.accept.other_error", player);
                }
                break;
            case "deny":
                teamId = Integer.parseInt(args[1]);
                status = team.denyRequest(playerUUID, teamId);
                switch (status) {
                    case 0 -> t.sendMessage("team.command.deny.success", player);
                    case 1 -> t.sendMessage("team.command.deny.request_does_not_exist", player);
                    case 2 -> t.sendMessage("team.command.deny.team_does_not_exist", player);
                    case 3 -> t.sendMessage("team.command.deny.user_has_no_permission_to_reject_requests", player);
                    case 4 -> t.sendMessage("team.command.deny.other_error", player);
                }
                break;
            case "list":
                JSONArray teamList = team.getTeamsByMember(playerUUID);
                JsonObject teamsList = new JsonObject();
                teamsList.add("teams", gson.toJsonTree(teamList));
                JsonObject values = new JsonObject();
                values.add("list", teamsList);
                t.sendMessage("team.command.list", player, values);
                break;
        }
        return true;
    }

    private String getDescription(String[] args, int at) {
        StringBuilder res = new StringBuilder();
        for (int i = at; i < args.length; i++) {
            res.append(args[i]).append(" ");
        }
        return res.toString();
    }

}
