package net.gigaclub.auth.commands;


import net.gigaclub.auth.Main;
import net.gigaclub.auth.data.Data;
import net.gigaclub.translation.Translation;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.jetbrains.annotations.NotNull;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(
        name = "auth", permission = "auth.auth"
))
@Permission(name = "auth.auth", defaultValue = PermissionDefault.TRUE)
public class AuthCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player bukkitPlayer)) {
            sender.sendPlainMessage("This command should be executed as a user!");
            return true;
        }
        String playerUUID = bukkitPlayer.getUniqueId().toString();
        Translation t = Main.getTranslation();
        Data data = Main.getData();
        String authToken = data.generateAuthToken(playerUUID);
        t.sendMessage("auth.command.authtoken", bukkitPlayer, Placeholder.parsed("authtoken", authToken));
        return true;
    }
}
