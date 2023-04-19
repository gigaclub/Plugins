package net.gigaclub.permissionsystem.commands;

import net.gigaclub.permissionsystem.Main;
import net.gigaclub.translation.Translation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SyncCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Translation t = Main.getTranslation();

        Main.setupGroups();
        t.sendMessage("group.sync.success", player);
        return true;
    }
}
