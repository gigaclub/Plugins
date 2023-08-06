package net.gigaclub.buildersystemserver.listener;

import com.google.gson.JsonObject;
import net.gigaclub.buildersystemserver.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void joinListener(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        JsonObject world = Main.builderSystem.getWorld(Main.worldId);
        String worldType = world.get("world_type").getAsString();
        if (worldType.contains("nether")) {
            for (World netherWorld : Bukkit.getWorlds()) {
                if (netherWorld.getName().contains("nether")) {
                    player.teleport(netherWorld.getSpawnLocation());
                    break;
                }
            }
        } else if (worldType.contains("end")) {
            for (World endWorld : Bukkit.getWorlds()) {
                if (endWorld.getName().contains("end")) {
                    player.teleport(endWorld.getSpawnLocation());
                    break;
                }
            }
        }
    }

}
