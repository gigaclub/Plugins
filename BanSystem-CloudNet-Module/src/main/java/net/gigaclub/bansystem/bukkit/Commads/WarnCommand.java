package net.gigaclub.bansystem.bukkit.Commads;

import net.gigaclub.bansystem.bukkit.Anderes.Data;
import net.gigaclub.bansystem.bukkit.BukkitBanSystemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class WarnCommand implements CommandExecutor {
    //    0     1        2
    // /warn <Player_Name> <Grund_ID>
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Data data = BukkitBanSystemPlugin.getData();

        if (!(sender instanceof Player player)) {
            return false;
        }
        if (args.length == 0) {
            // open Gui

        } else if (args.length == 1) {
            player.sendMessage("Command angaben nicht coreckt");
        } else if (args.length == 2) {
            String username = args[0];
            try {
                if (data.MCplayerExists(username)) {
                    String[] User = data.getMCPlayerInfo(username);
                    if (data.checkIfPlayerExists(User[1])) {
                        //ADD USER uuid und ip TO BANN LISTs
                            

                    } else {
                        player.sendMessage("Spieler war noch nie auf dem netzwerk");

                    }
                }
            } catch (IOException e) {
                player.sendMessage("input is not a player");

            }


        }


        return false;
    }
}
