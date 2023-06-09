package net.gigaclub.permissionsystem.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.gigaclub.permissionsystem.Main;
import net.gigaclub.permissionsystemapi.PermissionSystem;
import net.gigaclub.translation.Translation;
import net.gigaclub.translation.utils.ComponentHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GroupCommand implements CommandExecutor, TabCompleter {
    Translation t = Main.getTranslation();
    Gson gson = new Gson();
    JsonObject values;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //grepper minecraft paper get player by sender
        Player player = (Player) sender;
        //end grepper
        Translation t = Main.getTranslation();
        JsonObject params;

        if (args.length == 0) {
            t.sendMessage("group.no.parameters", player);
            return true;
        }
        PermissionSystem permissionSystem = Main.getPermissionSystem();
        JsonArray groups = permissionSystem.getAllGroups();
        switch (args[0]) {
            case "list":
                // change after translation rework to support lists
                t.sendMessage("group.list.group", player);
                for (JsonElement jsonElement : groups) {
                    JsonObject group = jsonElement.getAsJsonObject();
                    String groupName = group.get("name").getAsString();
                    JsonArray permissions = group.get("permissions").getAsJsonArray();
                    params = new JsonObject();
                    params.addProperty("group", groupName);
                    values = new JsonObject();
                    values.add("params", params);
                    List<String> perms = new ArrayList<>();
                    for (JsonElement permission : permissions) {
                        perms.add(permission.getAsString());
                    }
                    Component[] permComponents = perms.stream().map(s -> Component.text(s)).toArray(Component[]::new);
                    t.sendMessage("group.list", player, TagResolver.resolver("languages", Tag.inserting(ComponentHelper.join(permComponents, Component.text(", ")))));
                }
                break;
            case "add":
                if (args.length < 3) {
                    t.sendMessage("group.no.parameters", player);
                    return true;
                }
                String groupName = args[1].toLowerCase();
                String playerName = args[2];
                //grepper minecraft paper get player by name
                Player playerToAdd = Bukkit.getPlayer(playerName);
                //end grepper
                int groupId = 0;
                for (JsonElement jsonElement : groups) {
                    JsonObject group = jsonElement.getAsJsonObject();
                    if (group.get("name").getAsString().toLowerCase().equals(groupName)) {
                        groupId = group.get("id").getAsInt();
                        break;
                    }
                }
                assert playerToAdd != null;
                permissionSystem.setGroups(playerToAdd.getUniqueId().toString(), List.of(groupId));
                Main.setupGroups();
                t.sendMessage("group.add.success", player, Placeholder.parsed("playername", playerName), Placeholder.parsed("groupname", groupName));
                break;
            case "remove":
                if (args.length < 3) {
                    t.sendMessage("group.no.parameters", player);
                    return true;
                }
                String groupNameToRemove = args[1].toLowerCase();
                String playerNameToRemove = args[2];
                Player playerToRemove = Bukkit.getPlayer(playerNameToRemove);
                groupId = 0;
                for (JsonElement jsonElement : groups) {
                    JsonObject group = jsonElement.getAsJsonObject();
                    if (group.get("name").getAsString().toLowerCase().equals(groupNameToRemove)) {
                        groupId = group.get("id").getAsInt();
                        break;
                    }
                }
                assert playerToRemove != null;
                permissionSystem.removeGroups(playerToRemove.getUniqueId().toString(), List.of(groupId));
                Main.setupGroups();
                t.sendMessage("group.remove.success", player, Placeholder.parsed("playername", playerNameToRemove), Placeholder.parsed("groupname", groupNameToRemove));
                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        PermissionSystem permissionSystem = Main.getPermissionSystem();
        JsonArray groups = permissionSystem.getAllGroups();

        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("add");
            arguments.add("remove");
            arguments.add("list");
            return arguments;
        } else if (args.length == 2) {
            if (args[0].equals("add") || args[0].equals("remove")) {
                List<String> arguments = new ArrayList<>();
                for (JsonElement jsonElement : groups) {
                    JsonObject group = jsonElement.getAsJsonObject();
                    String groupName = group.get("name").getAsString();
                    arguments.add(groupName);
                }
                return arguments;
            }
        }
        return null;
    }
}
