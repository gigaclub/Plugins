package net.gigaclub.buildersystemserver.commands;

import com.google.gson.JsonObject;
import net.gigaclub.buildersystemserver.Main;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.jetbrains.annotations.NotNull;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(
        name = "setcenter", permission = "builder.system.server.setcenter"
))
@Permission(name = "builder.system.server.setcenter", defaultValue = PermissionDefault.TRUE)
public class SetCenterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        JsonObject builderSystemWorld = Main.builderSystem.getWorld(Main.worldId);
        JsonObject task = Main.builderSystem.getTask(builderSystemWorld.get("task_id").getAsInt());
        // just get one parameter because both should always be the same in the current logic
        int worldSize = task.get("build_width").getAsInt();
        if (worldSize <= 1)
            worldSize = 1;
        WorldBorder border = Main.getPlugin().world.getWorldBorder();
        border.setSize(worldSize);
        Location playerLocation = player.getLocation();
        border.setCenter(playerLocation);
        Main.getPlugin().translation.sendMessage("builder.system.server.center.set", player);
        return true;
    }
}
